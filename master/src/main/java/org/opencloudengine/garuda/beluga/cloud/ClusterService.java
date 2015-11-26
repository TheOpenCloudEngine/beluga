package org.opencloudengine.garuda.beluga.cloud;

import org.opencloudengine.garuda.beluga.env.Environment;
import org.opencloudengine.garuda.beluga.env.SettingManager;
import org.opencloudengine.garuda.beluga.env.Settings;
import org.opencloudengine.garuda.beluga.exception.BelugaException;
import org.opencloudengine.garuda.beluga.exception.InvalidRoleException;
import org.opencloudengine.garuda.beluga.exception.UnknownIaasProviderException;
import org.opencloudengine.garuda.beluga.mesos.MesosAPI;
import org.opencloudengine.garuda.beluga.mesos.docker.DockerAPI;
import org.opencloudengine.garuda.beluga.mesos.marathon.MarathonAPI;
import org.opencloudengine.garuda.beluga.proxy.HAProxyAPI;
import org.opencloudengine.garuda.beluga.service.AbstractClusterService;
import org.opencloudengine.garuda.beluga.service.ServiceException;
import org.opencloudengine.garuda.beluga.settings.ClusterDefinition;
import org.opencloudengine.garuda.beluga.settings.IaasProviderConfig;
import org.opencloudengine.garuda.beluga.utils.SshInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
* Created by swsong on 2015. 7. 20..
*/
public class ClusterService extends AbstractClusterService {
    private static final String CLUSTERS_KEY = "clusters";
    private static final String DOMAIN_KEY = "domain";
    private static final long PROXY_CHECK_PERIOD = 500;
    private static final long DEPLOY_CHECK_PERIOD = 500;
    private IaasProviderConfig iaasProviderConfig;
    private ClusterTopology clusterTopology;

    private MesosAPI mesosAPI;
    private MarathonAPI marathonAPI;
    private DockerAPI dockerAPI;
    private HAProxyAPI haProxyAPI;
    private ProxyUpdateWorker proxyUpdateWorker;
    private DeploymentsCheckWorker deploymentsCheckWorker;

    private String domainName;

    public ClusterService(String clusterId, Environment environment, Settings settings) {
        super(clusterId, environment, settings);
        iaasProviderConfig = environment.settingManager().getIaasProviderConfig();
        mesosAPI = new MesosAPI(this, environment);
        marathonAPI = new MarathonAPI(this, environment);
        dockerAPI = new DockerAPI(environment);
        SettingManager settingManager = environment.settingManager();
        Settings clustersConfig = settingManager.getClustersConfig();
        domainName = clustersConfig.getString(clusterId + "." + DOMAIN_KEY);
    }



    // TODO
    // 클러스터 생성시에 만들어진 ip는 재시작하면 ip가 바뀌어서 다시 ip설정을 하고, mesos process를 재시작해야한다.
    // 일단 restart는 없애자.
    // 서비스 자체의 start,stop과
    // instance의 start,stop은 달리 처리되야 한다.


    public String getDomainName() {
        return domainName;
    }

    @Override
    protected boolean doStart() throws ServiceException {

        try {
            logger.info("Starting cluster {}..", clusterId);
            if(clusterTopology == null) {
                loadClusterTopology();
                updateInstances();
            }

            //TODO 기존의 topology와 받아온 IP가 다르면 configure를 다시한다.

            //configure Mesos with new IP
            String definitionId = clusterTopology.getDefinitionId();

            //TODO 다시 store to topology...

            loadProxyWorker();
            loadDeploymentsCheckWorker();
        } catch (Exception e) {
            logger.error("error while starting cluster service : " + clusterId, e);
            return false;
        }
        return true;
    }

    @Override
    protected boolean doStop() throws ServiceException {
        unloadProxyWorker();
        unloadDeploymentsCheckWorker();
        return true;
    }

    @Override
    protected boolean doClose() throws ServiceException {
        return true;
    }

    public boolean shutdownCluster() {
        logger.info("Shutdown cluster {}..", clusterId);

        unloadProxyWorker();
        unloadDeploymentsCheckWorker();
        try {
            stopInstances();
        } catch (Exception e) {
            logger.error("error while stopping cluster service : " + clusterId, e);
            return false;
        }
        return true;
    }
    protected boolean destroyCluster() {
        logger.info("Destroying cluster {}..", clusterId);
        unloadProxyWorker();
        try {
            terminateInstances();
        } catch (Exception e) {
            logger.error("error while stopping cluster service : " + clusterId, e);
            return false;
        }
        //topology.cluster 설정파일 삭제.
        deleteClusterTopologyConfig();
        return true;
    }

    protected ClusterService createCluster(String definitionId, String domainName, boolean waitUntilInstanceAvailable) throws BelugaException, UnknownIaasProviderException {
        this.domainName = domainName;
        SettingManager settingManager = environment.settingManager();
        ClusterDefinition clusterDefinition = settingManager.getClusterDefinition(definitionId);
        String iaasProfile = clusterDefinition.getIaasProfile();
        IaasProvider iaasProvider = iaasProviderConfig.getIaasProvider(iaasProfile);

        ClusterTopology clusterTopology = new ClusterTopology(clusterId, definitionId, iaasProfile);
        createInstances(clusterTopology, clusterDefinition, iaasProvider, waitUntilInstanceAvailable);
        storeClusterTopologyConfig();

        addClusterIdToSetting(clusterId, domainName);
        return this;
    }

    private void addClusterIdToSetting(String clusterId, String domainName) {
        SettingManager settingManager = environment.settingManager();
        Settings settings = settingManager.getClustersConfig();
        settings.addStringToArray(CLUSTERS_KEY, clusterId);
        settings.properties().put(clusterId + "." + DOMAIN_KEY, domainName);
        //clusters 설정파일을 저장한다.
        settingManager.storeClustersConfig(settings);
    }

    private void createInstances(ClusterTopology clusterTopology, ClusterDefinition clusterDefinition, IaasProvider iaasProvider, boolean waitUntilInstanceAvailable) throws UnknownIaasProviderException, BelugaException {
        String keyPair = clusterDefinition.getKeyPair();
        String clusterId = clusterTopology.getClusterId();
        Iaas iaas = null;
        try {
            iaas = iaasProvider.getIaas();
            List<ClusterDefinition.RoleDefinition> roleDefinitions = clusterDefinition.getRoleList();

            for (ClusterDefinition.RoleDefinition roleDefinition : roleDefinitions) {
                String role = roleDefinition.getRole();
                int size = roleDefinition.getDefaultSize();
                InstanceRequest request = new InstanceRequest(clusterId, roleDefinition.getInstanceType(), roleDefinition.getImageId()
                        , roleDefinition.getDiskSize(), roleDefinition.getGroup(), keyPair);
                List<CommonInstance> instanceList = iaas.launchInstance(request, role, size, 1);

                for (CommonInstance instance : instanceList) {
                    //토폴로지에 넣어준다.
                    clusterTopology.addNode(role, instance);
                }
            }
        } catch (Exception e) {
            logger.error("", e);
            terminateInstances();
            throw new BelugaException(e);
        } finally {
            iaas.close();
        }

        if(waitUntilInstanceAvailable) {
            List<CommonInstance> list = clusterTopology.getAllNodeList();
            //Wait until available
            iaas.waitUntilInstancesRunning(list);
            //Fetch latest instance information
            iaas.updateInstancesInfo(list);
        }
        this.clusterTopology = clusterTopology;
    }

    public List<CommonInstance> addSlaveNode(int incrementSize) throws BelugaException {
        String SLAVE_ROLE = "mesos-slave";
        SettingManager settingManager = environment.settingManager();
        String definitionId = clusterTopology.getDefinitionId();
        ClusterDefinition clusterDefinition = settingManager.getClusterDefinition(definitionId);
        String iaasProfile = clusterDefinition.getIaasProfile();
        IaasProvider iaasProvider = iaasProviderConfig.getIaasProvider(iaasProfile);
        Iaas iaas = null;
        int startIndex = clusterTopology.getMesosSlaveList().size() + 1;
        List<CommonInstance> instanceList = null;
        try {
            iaas = iaasProvider.getIaas();
            List<ClusterDefinition.RoleDefinition> roleDefinitions = clusterDefinition.getRoleList();
            String keyPair = clusterDefinition.getKeyPair();
            ClusterDefinition.RoleDefinition roleDefinition = null;
            for (ClusterDefinition.RoleDefinition definition : roleDefinitions) {
                if (definition.getRole().equalsIgnoreCase(SLAVE_ROLE)) {
                    roleDefinition = definition;
                    break;
                }
            }

            InstanceRequest request = new InstanceRequest(clusterId, roleDefinition.getInstanceType(), roleDefinition.getImageId()
                    , roleDefinition.getDiskSize(), roleDefinition.getGroup(), keyPair);
            instanceList = iaas.launchInstance(request, SLAVE_ROLE, incrementSize, startIndex);
            for (CommonInstance instance : instanceList) {
                //토폴로지에 넣어준다.
                clusterTopology.addNode(SLAVE_ROLE, instance);
            }
        } catch (Exception e) {
            logger.error("", e);
            throw new BelugaException(e);
        } finally {
            iaas.close();
        }

        //Wait until available
        iaas.waitUntilInstancesRunning(instanceList);
        //Fetch latest instance information
        iaas.updateInstancesInfo(instanceList);

        storeClusterTopologyConfig();
        return instanceList;
    }

    public CommonInstance removeSlaveNode(String instanceId) throws BelugaException, UnknownIaasProviderException {
        List<CommonInstance> list = clusterTopology.getMesosSlaveList();
        List<String> instanceIdList = new ArrayList<>();
        CommonInstance obj = null;
        for(CommonInstance instance : list) {
            if(instance.getInstanceId().equalsIgnoreCase(instanceId)) {
                instanceIdList.add(instanceId);
                obj = instance;
            }
        }
        list.remove(obj);
        terminateInstance(instanceIdList);
        storeClusterTopologyConfig();
        return obj;
    }

    private void startInstances() throws UnknownIaasProviderException {
        String iaasProfile = clusterTopology.getIaasProfile();
        IaasProvider iaasProvider = iaasProviderConfig.getIaasProvider(iaasProfile);
        Iaas iaas = iaasProvider.getIaas();

        List<CommonInstance> allNodeList = clusterTopology.getAllNodeList();
        try {
            iaas.startInstances(IaasUtils.getIdList(allNodeList));
            iaas.waitUntilInstancesRunning(allNodeList);
        } finally {
            if(iaas != null) {
                iaas.close();
            }
        }
        // 상태 정보를 업데이트 한다.
        iaas.updateInstancesInfo(allNodeList);
    }

    public void updateInstances() throws UnknownIaasProviderException {
        String iaasProfile = clusterTopology.getIaasProfile();
        IaasProvider iaasProvider = iaasProviderConfig.getIaasProvider(iaasProfile);
        Iaas iaas = iaasProvider.getIaas();
        List<CommonInstance> allNodeList = clusterTopology.getAllNodeList();
        // 상태 정보를 업데이트 한다.
        iaas.updateInstancesInfo(allNodeList);
    }

    private void stopInstances() throws UnknownIaasProviderException {
        String iaasProfile = clusterTopology.getIaasProfile();
        IaasProvider iaasProvider = iaasProviderConfig.getIaasProvider(iaasProfile);

        //clusterTopology 내에 해당하는 살아있는 모든 노드 삭제.
        Iaas iaas = iaasProvider.getIaas();
        List<CommonInstance> allNodeList = clusterTopology.getAllNodeList();
        try {
            iaas.stopInstances(IaasUtils.getIdList(allNodeList));
            iaas.waitUntilInstancesStopped(allNodeList);
        } finally {
            if(iaas != null) {
                iaas.close();
            }
        }

        // 상태 정보를 업데이트 한다.
        iaas.updateInstancesInfo(clusterTopology.getAllNodeList());
    }
    public void rebootInstances(boolean waitUntilInstanceAvailable) throws UnknownIaasProviderException {
        String iaasProfile = clusterTopology.getIaasProfile();
        IaasProvider iaasProvider = iaasProviderConfig.getIaasProvider(iaasProfile);
        Iaas iaas = iaasProvider.getIaas();
        try {
            List<CommonInstance> instanceList = clusterTopology.getAllNodeList();
            iaas.rebootInstances(IaasUtils.getIdList(instanceList));
            sleep(5);
            if(waitUntilInstanceAvailable) {
                //Wait until available
                iaas.waitUntilInstancesRunning(instanceList);
                //Fetch latest instance information
                iaas.updateInstancesInfo(instanceList);
            }
        } finally {
            if(iaas != null) {
                iaas.close();
            }
        }
    }
    public void rebootInstances(String role, boolean waitUntilInstanceAvailable) throws UnknownIaasProviderException, InvalidRoleException {
        String iaasProfile = clusterTopology.getIaasProfile();
        IaasProvider iaasProvider = iaasProviderConfig.getIaasProvider(iaasProfile);
        Iaas iaas = iaasProvider.getIaas();
        try {
            List<CommonInstance> instanceList = clusterTopology.getInstancesByRole(role);
            iaas.rebootInstances(IaasUtils.getIdList(instanceList));
            sleep(5);
            if(waitUntilInstanceAvailable) {
                //Wait until available
                iaas.waitUntilInstancesRunning(instanceList);
                //Fetch latest instance information
                iaas.updateInstancesInfo(instanceList);
            }
        } finally {
            if(iaas != null) {
                iaas.close();
            }
        }
    }

    //특정 인스턴스만 재부팅한다.
    public void rebootInstances(List<CommonInstance> instanceList, boolean waitUntilInstanceAvailable) throws UnknownIaasProviderException, InvalidRoleException {
        String iaasProfile = clusterTopology.getIaasProfile();
        IaasProvider iaasProvider = iaasProviderConfig.getIaasProvider(iaasProfile);
        Iaas iaas = iaasProvider.getIaas();
        try {
            iaas.rebootInstances(IaasUtils.getIdList(instanceList));
            sleep(5);
            if(waitUntilInstanceAvailable) {
                //Wait until available
                iaas.waitUntilInstancesRunning(instanceList);
                //Fetch latest instance information
                iaas.updateInstancesInfo(instanceList);
            }
        } finally {
            if(iaas != null) {
                iaas.close();
            }
        }
    }

    protected void terminateInstances() throws UnknownIaasProviderException {
        if(clusterTopology == null) {
            return;
        }
        String iaasProfile = clusterTopology.getIaasProfile();
        IaasProvider iaasProvider = iaasProviderConfig.getIaasProvider(iaasProfile);

        //clusterTopology 내에 해당하는 살아있는 모든 노드 삭제.
        Iaas iaas = iaasProvider.getIaas();
        try {
            iaas.terminateInstances(IaasUtils.getIdList(clusterTopology.getAllNodeList()));
        } finally {
            if(iaas != null) {
                iaas.close();
            }
        }
    }

    protected void terminateInstance(List<String> instanceIdList) throws UnknownIaasProviderException {
        if(clusterTopology == null) {
            return;
        }
        String iaasProfile = clusterTopology.getIaasProfile();
        IaasProvider iaasProvider = iaasProviderConfig.getIaasProvider(iaasProfile);

        //clusterTopology 내에 해당하는 살아있는 모든 노드 삭제.
        Iaas iaas = iaasProvider.getIaas();
        try {
            iaas.terminateInstances(instanceIdList);
        } finally {
            if(iaas != null) {
                iaas.close();
            }
        }
    }

    private boolean loadProxyWorker() {
        if(clusterTopology.getProxyList().size() == 0) {
            return false;
        }
        String definitionId = clusterTopology.getDefinitionId();
        String proxyIpAddress = clusterTopology.getProxyList().get(0).getPublicIpAddress();
        ClusterDefinition clusterDefinition = environment.settingManager().getClusterDefinition(definitionId);
        String userId = clusterDefinition.getUserId();
        String keyPairFile = clusterDefinition.getKeyPairFile();
        int timeout = clusterDefinition.getTimeout();
        haProxyAPI = new HAProxyAPI(this, environment);
        final SshInfo sshInfo = new SshInfo().withHost(proxyIpAddress).withUser(userId).withPemFile(keyPairFile).withTimeout(timeout);
        proxyUpdateWorker = new ProxyUpdateWorker(sshInfo);
        proxyUpdateWorker.start();
        return true;
    }

    private boolean unloadProxyWorker() {
        if(proxyUpdateWorker != null) {
            proxyUpdateWorker.interrupt();
        }
        return true;
    }

    private void loadDeploymentsCheckWorker() {
        deploymentsCheckWorker = new DeploymentsCheckWorker();
        deploymentsCheckWorker.start();
    }
    private void unloadDeploymentsCheckWorker() {
        if(deploymentsCheckWorker != null) {
            deploymentsCheckWorker.interrupt();
        }
    }

    public void loadClusterTopology() throws UnknownIaasProviderException, InvalidRoleException, BelugaException {
        Settings settings = environment.settingManager().getClusterTopologyConfig(clusterId);
        if(settings == null) {
            throw new BelugaException("Cluster topology config not found : " + clusterId);
        }
        String definitionId = settings.getString(ClusterTopology.DEFINITION_ID_KEY);
        if(definitionId == null) {
            throw new UnknownIaasProviderException("definition is null.");
        }
        String iaasProfile = settings.getString(ClusterTopology.IAAS_PROFILE_KEY);
        if(iaasProfile == null) {
            throw new UnknownIaasProviderException("provider is null.");
        }
        IaasProvider iaasProvider = iaasProviderConfig.getIaasProvider(iaasProfile);
        Iaas iaas = iaasProvider.getIaas();

        ClusterTopology clusterTopology = new ClusterTopology(clusterId, definitionId, iaasProfile);
        try {
            clusterTopology.loadRoles(settings, iaas);
        } finally {
            iaas.close();
        }
        this.clusterTopology = clusterTopology;
    }

    private void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException ignore) {
        }
    }

    private void storeClusterTopologyConfig(){
        Properties props = clusterTopology.getProperties();
        environment.settingManager().storeClusterTopology(clusterId, props);
    }
    private void deleteClusterTopologyConfig() {
        //topology.cluster 설정파일 삭제.
        environment.settingManager().deleteClusterTopology(clusterId);
    }

    public IaasProvider getIaasProvider() {
        String iaasProfile = clusterTopology.getIaasProfile();
        return iaasProviderConfig.getIaasProvider(iaasProfile);
    }

    public ClusterDefinition getClusterDefinition() {
        String definitionId = clusterTopology.getDefinitionId();
        return environment.settingManager().getClusterDefinition(definitionId);
    }
    public ClusterTopology getClusterTopology() {
        return clusterTopology;
    }

    public HAProxyAPI getProxyAPI(){
        return haProxyAPI;
    }

    public MesosAPI getMesosAPI() {
        return mesosAPI;
    }

    public MarathonAPI getMarathonAPI() {
        return marathonAPI;
    }

    public DockerAPI getDockerAPI() {
        return dockerAPI;
    }

    class ProxyUpdateWorker extends Thread {
        private SshInfo sshInfo;
        public ProxyUpdateWorker(SshInfo sshInfo) {
            this.sshInfo = sshInfo;
        }
        @Override
        public void run() {
            logger.info("Start ProxyUpdateWorker.");
            while (!this.isInterrupted()) {
                try {
                    haProxyAPI.applyProxyConfig(sshInfo);
                } catch (Exception e) {
                    logger.error("", e);
                }
                try {
                    Thread.sleep(PROXY_CHECK_PERIOD);
                } catch (InterruptedException ignore) {
                }
            }
        }
    }

    class DeploymentsCheckWorker extends Thread {
        @Override
        public void run() {
            logger.info("Start DeploymentsCheckWorker.");
            while (!this.isInterrupted()) {
                try {
                    haProxyAPI.checkDeploymentsAndApply();
                } catch (Exception e) {
                    logger.error("", e);
                }
                try {
                    Thread.sleep(DEPLOY_CHECK_PERIOD);
                } catch (InterruptedException ignore) {
                }
            }
        }
    }
}
