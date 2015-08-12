package org.opencloudengine.garuda.cloud;

import org.opencloudengine.garuda.env.Environment;
import org.opencloudengine.garuda.env.SettingManager;
import org.opencloudengine.garuda.env.Settings;
import org.opencloudengine.garuda.exception.GarudaException;
import org.opencloudengine.garuda.exception.InvalidRoleException;
import org.opencloudengine.garuda.exception.UnknownIaasProviderException;
import org.opencloudengine.garuda.mesos.MesosAPI;
import org.opencloudengine.garuda.mesos.docker.DockerAPI;
import org.opencloudengine.garuda.mesos.marathon.MarathonAPI;
import org.opencloudengine.garuda.proxy.HAProxyAPI;
import org.opencloudengine.garuda.proxy.ProxyUpdateWorker;
import org.opencloudengine.garuda.service.AbstractClusterService;
import org.opencloudengine.garuda.service.ServiceException;
import org.opencloudengine.garuda.settings.ClusterDefinition;
import org.opencloudengine.garuda.settings.IaasProviderConfig;
import org.opencloudengine.garuda.utils.SshInfo;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
* Created by swsong on 2015. 7. 20..
*/
public class ClusterService extends AbstractClusterService {

    private IaasProviderConfig iaasProviderConfig;
    private ClusterTopology clusterTopology;

    private ProxyUpdateWorker proxyUpdateWorker;
    private Queue<String> proxyUpdateQueue;
    private HAProxyAPI haProxyAPI;
    private MesosAPI mesosAPI;
    private MarathonAPI marathonAPI;
    private DockerAPI dockerAPI;

    public ClusterService(String clusterId, Environment environment, Settings settings) {
        super(clusterId, environment, settings);
        iaasProviderConfig = environment.settingManager().getIaasProviderConfig();
        mesosAPI = new MesosAPI(clusterId, environment);
        marathonAPI = new MarathonAPI(clusterId, environment);
        dockerAPI = new DockerAPI(environment);
    }

    @Override
    protected boolean doStart() throws ServiceException {

        try {
            logger.info("Starting cluster {}..", clusterId);
            loadClusterTopology();
            startInstances();
        } catch (Exception e) {
            logger.error("error while starting cluster service : " + clusterId, e);
            return false;
        }

        if(loadProxyWorker()){
            haProxyAPI = new HAProxyAPI(clusterId, environment, proxyUpdateQueue);
        }
        return true;
    }

    @Override
    protected boolean doStop() throws ServiceException {
        logger.info("Stopping cluster {}..", clusterId);

        unloadProxyWorker();

        try {
            stopInstances();
        } catch (Exception e) {
            logger.error("error while stopping cluster service : " + clusterId, e);
            return false;
        }
        return true;
    }

    @Override
    protected boolean doClose() throws ServiceException {
        return true;
    }

    protected boolean destroyCluster() throws UnknownIaasProviderException {
        logger.info("Destroying cluster {}..", clusterId);
        unloadProxyWorker();
        terminateInstances();
        //topology.cluster 설정파일 삭제.
        deleteClusterTopologyConfig();
        return true;
    }

    protected ClusterService createCluster(String definitionId, boolean waitUntilInstanceAvailable) throws GarudaException, UnknownIaasProviderException {
        SettingManager settingManager = environment.settingManager();
        ClusterDefinition clusterDefinition = settingManager.getClusterDefinition(definitionId);
        String iaasProfile = clusterDefinition.getIaasProfile();
        IaasProvider iaasProvider = iaasProviderConfig.getIaasProvider(iaasProfile);

        ClusterTopology clusterTopology = new ClusterTopology(clusterId, definitionId, iaasProfile);
        createInstances(clusterTopology, clusterDefinition, iaasProvider, waitUntilInstanceAvailable);
        storeClusterTopologyConfig();
        return this;
    }

    private void createInstances(ClusterTopology clusterTopology, ClusterDefinition clusterDefinition, IaasProvider iaasProvider, boolean waitUntilInstanceAvailable) throws UnknownIaasProviderException, GarudaException {
        String keyPair = clusterDefinition.getKeyPair();
        Iaas iaas = null;
        try {
            iaas = iaasProvider.getIaas();
            List<ClusterDefinition.RoleDefinition> roleDefinitions = clusterDefinition.getRoleList();

            for (ClusterDefinition.RoleDefinition roleDefinition : roleDefinitions) {
                String role = roleDefinition.getRole();
                int size = roleDefinition.getDefaultSize();
                InstanceRequest request = new InstanceRequest(roleDefinition.getInstanceType(), roleDefinition.getImageId()
                        , roleDefinition.getDiskSize(), roleDefinition.getGroup(), keyPair);
                List<CommonInstance> instanceList = iaas.launchInstance(request, role, size);

                for (CommonInstance instance : instanceList) {
                    //토폴로지에 넣어준다.
                    clusterTopology.addNode(role, instance);
                }
            }
        } catch (Exception e) {
            terminateInstances();
            throw new GarudaException(e);
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

    private void startInstances() throws UnknownIaasProviderException {
        String iaasProfile = clusterTopology.getIaasProfile();
        IaasProvider iaasProvider = iaasProviderConfig.getIaasProvider(iaasProfile);

        //clusterTopology 내에 해당하는 살아있는 모든 노드 삭제.
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

    protected void terminateInstances() throws UnknownIaasProviderException {
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

    private boolean loadProxyWorker() {
        if(clusterTopology.getProxyList().size() == 0) {
            return false;
        }
        String clusterId = clusterTopology.getClusterId();
        String definitionId = clusterTopology.getDefinitionId();
        String proxyIpAddress = clusterTopology.getProxyList().get(0).getPublicIpAddress();
        ClusterDefinition clusterDefinition = environment.settingManager().getClusterDefinition(definitionId);
        String userId = clusterDefinition.getUserId();
        String keyPairFile = clusterDefinition.getKeyPairFile();
        int timeout = clusterDefinition.getTimeout();
        final SshInfo sshInfo = new SshInfo().withHost(proxyIpAddress).withUser(userId).withPemFile(keyPairFile).withTimeout(timeout);
        proxyUpdateQueue = new ConcurrentLinkedQueue<>();
        proxyUpdateWorker = new ProxyUpdateWorker(clusterId, sshInfo, proxyUpdateQueue);
        proxyUpdateWorker.start();
        return true;
    }

    private boolean unloadProxyWorker() {
        if(proxyUpdateWorker != null) {
            proxyUpdateWorker.interrupt();
        }
        if(proxyUpdateQueue != null) {
            proxyUpdateQueue.clear();
        }
        return true;
    }

    public void loadClusterTopology() throws UnknownIaasProviderException, InvalidRoleException, GarudaException {
        Settings settings = environment.settingManager().getClusterTopologyConfig(clusterId);
        if(settings == null) {
            throw new GarudaException("Cluster topology config not found : " + clusterId);
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

    private void loadRole(String role, Settings settings, Iaas iaas, ClusterTopology clusterTopology) throws InvalidRoleException {
        String value = settings.getValue(role);
        if(value != null && value.trim().length() > 0) {
            String[] idArray = settings.getStringArray(role);
            List<String> idList = new ArrayList<String>();
            for(String id : idArray) {
                idList.add(id);
            }
            List<CommonInstance> list = iaas.getInstances(idList);
            for(CommonInstance instance : list) {
                clusterTopology.addNode(role, instance);
            }
        }
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
}
