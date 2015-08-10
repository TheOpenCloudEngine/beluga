package org.opencloudengine.garuda.cloud;

import org.opencloudengine.garuda.env.Environment;
import org.opencloudengine.garuda.env.SettingManager;
import org.opencloudengine.garuda.env.Settings;
import org.opencloudengine.garuda.exception.GarudaException;
import org.opencloudengine.garuda.exception.InvalidRoleException;
import org.opencloudengine.garuda.exception.UnknownIaasProviderException;
import org.opencloudengine.garuda.proxy.HAProxyAPI;
import org.opencloudengine.garuda.proxy.ProxyUpdateWorker;
import org.opencloudengine.garuda.service.AbstractService;
import org.opencloudengine.garuda.service.ServiceException;
import org.opencloudengine.garuda.service.common.ServiceManager;
import org.opencloudengine.garuda.settings.ClusterDefinition;
import org.opencloudengine.garuda.settings.IaasProviderConfig;
import org.opencloudengine.garuda.utils.SshInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
* Created by swsong on 2015. 7. 20..
*/
public class ClusterService extends AbstractService {

    private Map<String, ClusterTopology> clusterTopologyMap;

    private IaasProviderConfig iaasProviderConfig;

    private static final String CLUSTERS_KEY = "clusters";

    private Map<String, ProxyUpdateWorker> clusterProxyUpdateWorkerMap;
    private Map<String, Queue<String>> clusterProxyUpdateQueueMap;
    private HAProxyAPI haProxyAPI;

    public ClusterService(Environment environment, Settings settings, ServiceManager serviceManager) {
        super(environment, settings, serviceManager);
    }

    @Override
    protected boolean doStart() throws ServiceException {

        clusterTopologyMap = new HashMap<>();
        iaasProviderConfig = environment.settingManager().getIaasProviderConfig();
        clusterProxyUpdateWorkerMap = new ConcurrentHashMap<>();
        clusterProxyUpdateQueueMap = new ConcurrentHashMap<>();

        SettingManager settingManager = environment.settingManager();
        Settings settings = settingManager.getClustersConfig();
        String[] clusterList = settings.getStringArray(CLUSTERS_KEY);
        if(clusterList != null) {
            for (String clusterId : clusterList) {
                try {
                    loadCluster(clusterId);
                }catch(Throwable t) {
                    logger.error("error loading cluster topology.", t);
                }
            }
        }

        haProxyAPI = new HAProxyAPI(environment, clusterProxyUpdateQueueMap);

        return true;
    }

    private ProxyUpdateWorker loadProxyWorker(ClusterTopology topology) {
        if(topology.getProxyList().size() == 0) {
            return null;
        }
        String clusterId = topology.getClusterId();
        String definitionId = topology.getDefinitionId();
        String proxyIpAddress = topology.getProxyList().get(0).getPrivateIpAddress();
        ClusterDefinition clusterDefinition = environment.settingManager().getClusterDefinition(definitionId);
        String userId = clusterDefinition.getUserId();
        String keyPairFile = clusterDefinition.getKeyPairFile();
        int timeout = clusterDefinition.getTimeout();
        final SshInfo sshInfo = new SshInfo().withHost(proxyIpAddress).withUser(userId).withPemFile(keyPairFile).withTimeout(timeout);
        Queue<String> queue = new ConcurrentLinkedQueue<>();
        ProxyUpdateWorker proxyUpdateWorker = new ProxyUpdateWorker(clusterId, sshInfo, queue);
        proxyUpdateWorker.start();
        clusterProxyUpdateQueueMap.put(clusterId, queue);
        clusterProxyUpdateWorkerMap.put(clusterId, proxyUpdateWorker);
        return proxyUpdateWorker;
    }

    private void unloadProxyWorker(String clusterId) {
        ProxyUpdateWorker worker = clusterProxyUpdateWorkerMap.remove(clusterId);
        if(worker != null) {
            worker.interrupt();
        }
        Queue<String> queue = clusterProxyUpdateQueueMap.remove(clusterId);
        if(queue != null) {
            queue.clear();
        }
    }

    @Override
    protected boolean doStop() throws ServiceException {
        for(String clusterId : clusterTopologyMap.keySet()) {
            unloadProxyWorker(clusterId);
        }
        clusterTopologyMap.clear();
        return true;
    }

    @Override
    protected boolean doClose() throws ServiceException {
        clusterTopologyMap = null;
        clusterProxyUpdateWorkerMap = null;
        clusterProxyUpdateQueueMap = null;
        return true;
    }

    public HAProxyAPI getProxyAPI(){
        return haProxyAPI;
    }

    public ClusterTopology createCluster(String clusterId, String definitionId) throws GarudaException, ClusterExistException {
        return createCluster(clusterId, definitionId, false);
    }

    public ClusterTopology createCluster(String clusterId, String definitionId, boolean waitUntilInstanceAvailable) throws GarudaException, ClusterExistException {

        if(clusterTopologyMap.containsKey(clusterId) || isClusterIdExistInSetting(clusterId)) {
            throw new ClusterExistException(String.format("Cluster %s is already exists.", clusterId));
        }
        SettingManager settingManager = environment.settingManager();
        ClusterDefinition clusterDefinition = settingManager.getClusterDefinition(definitionId);
        String iaasProfile = clusterDefinition.getIaasProfile();
        IaasProvider iaasProvider = iaasProviderConfig.getIaasProvider(iaasProfile);

        ClusterTopology clusterTopology = new ClusterTopology(clusterId, definitionId, iaasProfile);

        String keyPair = clusterDefinition.getKeyPair();

        IaaS iaas = null;
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
            try {
                destroyCluster(clusterTopology);
            } catch (UnknownIaasProviderException e1) {
                throw new GarudaException(e);
            }
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

        //runtime 토폴로지에 넣어준다.
        clusterTopologyMap.put(clusterId, clusterTopology);
        //토폴로지 설정파일을 저장한다.
        storeClusterTopology(clusterTopology);
        addClusterIdToSetting(clusterId);
        //프록시 업데이트 감지를 시작한다.
        loadProxyWorker(clusterTopology);

        return clusterTopology;
    }

    public void destroyCluster(String clusterId) throws GarudaException {
        checkIfClusterExists(clusterId);
        ClusterTopology clusterTopology = clusterTopologyMap.get(clusterId);
        try {
            destroyCluster(clusterTopology);
        } catch (UnknownIaasProviderException e) {
            throw new GarudaException(e);
        }
    }

    private void destroyCluster(ClusterTopology clusterTopology) throws UnknownIaasProviderException {

        String iaasProfile = clusterTopology.getIaasProfile();

        IaasProvider iaasProvider = iaasProviderConfig.getIaasProvider(iaasProfile);

        //clusterTopology 내에 해당하는 살아있는 모든 노드 삭제.
        IaaS iaas = iaasProvider.getIaas();

        try {
            iaas.terminateInstances(IaasUtils.getIdList(clusterTopology.getAllNodeList()));
        } finally {
            if(iaas != null) {
                iaas.close();
            }
        }

        String clusterId = clusterTopology.getClusterId();
        clusterTopologyMap.remove(clusterId);
        unloadProxyWorker(clusterId);
        //clusterId 제거.
        removeClusterIdFromSetting(clusterId);
        //topology.cluster 설정파일 삭제.
        environment.settingManager().deleteClusterTopology(clusterId);
    }

    public void stopCluster(String clusterId) throws GarudaException {
        checkIfClusterExists(clusterId);
        ClusterTopology clusterTopology = clusterTopologyMap.get(clusterId);
        try {
            stopCluster(clusterTopology);
        } catch (UnknownIaasProviderException e) {
            throw new GarudaException(e);
        }
    }

    private void stopCluster(ClusterTopology clusterTopology) throws UnknownIaasProviderException {

        String clusterId = clusterTopology.getClusterId();
        unloadProxyWorker(clusterId);

        String iaasProfile = clusterTopology.getIaasProfile();
        IaasProvider iaasProvider = iaasProviderConfig.getIaasProvider(iaasProfile);

        //clusterTopology 내에 해당하는 살아있는 모든 노드 삭제.
        IaaS iaas = iaasProvider.getIaas();
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

    public void startCluster(String clusterId) throws GarudaException {
        checkIfClusterExists(clusterId);
        ClusterTopology clusterTopology = clusterTopologyMap.get(clusterId);
        try {
            startCluster(clusterTopology);
        } catch (UnknownIaasProviderException e) {
            throw new GarudaException(e);
        }
    }

    private void startCluster(ClusterTopology clusterTopology) throws UnknownIaasProviderException {
        String iaasProfile = clusterTopology.getIaasProfile();

        IaasProvider iaasProvider = iaasProviderConfig.getIaasProvider(iaasProfile);

        //clusterTopology 내에 해당하는 살아있는 모든 노드 삭제.
        IaaS iaas = iaasProvider.getIaas();

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
        loadProxyWorker(clusterTopology);
    }

    public void restartCluster(String clusterId) throws GarudaException {
        checkIfClusterExists(clusterId);
        ClusterTopology clusterTopology = clusterTopologyMap.get(clusterId);
        try {
            restartCluster(clusterTopology);
        } catch (UnknownIaasProviderException e) {
            throw new GarudaException(e);
        }
    }

    private void restartCluster(ClusterTopology clusterTopology) throws UnknownIaasProviderException {

        String clusterId = clusterTopology.getClusterId();
        unloadProxyWorker(clusterId);

        String iaasProfile = clusterTopology.getIaasProfile();
        IaasProvider iaasProvider = iaasProviderConfig.getIaasProvider(iaasProfile);

        //clusterTopology 내에 해당하는 살아있는 모든 노드 삭제.
        IaaS iaas = iaasProvider.getIaas();
        List<CommonInstance> allNodeList = clusterTopology.getAllNodeList();
        try {
            iaas.rebootInstances(IaasUtils.getIdList(allNodeList));
            iaas.waitUntilInstancesRunning(allNodeList);
        } finally {
            if(iaas != null) {
                iaas.close();
            }
        }
        // 상태 정보를 업데이트 한다.
        iaas.updateInstancesInfo(allNodeList);

        loadProxyWorker(clusterTopology);
    }

    private void checkIfClusterExists(String clusterId) throws GarudaException {
        if(! clusterTopologyMap.containsKey(clusterId)) {
            throw new GarudaException("Cluster not found : " + clusterId);
        }
    }

    private boolean isClusterIdExistInSetting(String clusterId) {
        SettingManager settingManager = environment.settingManager();
        Settings settings = settingManager.getClustersConfig();
        String[] list = settings.getStringArray(CLUSTERS_KEY);
        if(list != null) {
            for(String id : list) {
                if(clusterId.equalsIgnoreCase(id)){
                    return true;
                }
            }
        }
        return false;
    }

    private void addClusterIdToSetting(String clusterId) {
        SettingManager settingManager = environment.settingManager();
        Settings settings = settingManager.getClustersConfig();
        settings.addStringToArray(CLUSTERS_KEY, clusterId);
        //clusters 설정파일을 저장한다.
        settingManager.storeClustersConfig(settings);
    }
    public void removeClusterIdFromSetting(String clusterId) {
        SettingManager settingManager = environment.settingManager();
        Settings settings = settingManager.getClustersConfig();
        settings.removeStringFromArray(CLUSTERS_KEY, clusterId);
        //clusters 설정파일을 저장한다.
        settingManager.storeClustersConfig(settings);
    }
    public ClusterTopology loadCluster(String clusterId) throws UnknownIaasProviderException, InvalidRoleException, GarudaException {
        logger.info("Load cluster {}..", clusterId);
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
        IaaS iaas = iaasProvider.getIaas();

        ClusterTopology clusterTopology = new ClusterTopology(clusterId, definitionId, iaasProfile);
        try {
            loadRole(ClusterTopology.GARUDA_MASTER_ROLE, settings, iaas, clusterTopology);
            loadRole(ClusterTopology.PROXY_ROLE, settings, iaas, clusterTopology);
            loadRole(ClusterTopology.MESOS_MASTER_ROLE, settings, iaas, clusterTopology);
            loadRole(ClusterTopology.MESOS_SLAVE_ROLE, settings, iaas, clusterTopology);
            loadRole(ClusterTopology.MANAGEMENT_DB_REGISTRY_ROLE, settings, iaas, clusterTopology);
            loadRole(ClusterTopology.SERVICE_NODES_ROLE, settings, iaas, clusterTopology);
        } finally {
            iaas.close();
        }
        clusterTopologyMap.put(clusterId, clusterTopology);
        loadProxyWorker(clusterTopology);
        return clusterTopology;
    }

    private void storeClusterTopology(ClusterTopology clusterTopology){
        String clusterId = clusterTopology.getClusterId();
        Properties props = clusterTopology.getProperties();
        environment.settingManager().storeClusterTopology(clusterId, props);
    }

    private void loadRole(String role, Settings settings, IaaS iaas, ClusterTopology clusterTopology) throws InvalidRoleException {
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

    public Collection<ClusterTopology> getAllClusterTopology() {
        return clusterTopologyMap.values();
    }

    public ClusterTopology getClusterTopology(String clusterId) {
        return clusterTopologyMap.get(clusterId);
    }

    public void rebootInstances(ClusterTopology clusterTopology, List<CommonInstance> instanceList, boolean waitUntilInstanceAvailable) throws UnknownIaasProviderException {
        String iaasProfile = clusterTopology.getIaasProfile();

        IaasProvider iaasProvider = iaasProviderConfig.getIaasProvider(iaasProfile);

        IaaS iaas = iaasProvider.getIaas();
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

    private void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException ignore) {
        }
    }
}
