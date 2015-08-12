package org.opencloudengine.garuda.cloud;

import org.opencloudengine.garuda.env.Environment;
import org.opencloudengine.garuda.env.SettingManager;
import org.opencloudengine.garuda.env.Settings;
import org.opencloudengine.garuda.exception.GarudaException;
import org.opencloudengine.garuda.exception.InvalidRoleException;
import org.opencloudengine.garuda.mesos.MesosService;
import org.opencloudengine.garuda.service.AbstractService;
import org.opencloudengine.garuda.service.ServiceException;
import org.opencloudengine.garuda.service.common.ClusterServiceManager;
import org.opencloudengine.garuda.service.common.ServiceManager;
import org.opencloudengine.garuda.settings.ClusterDefinition;
import org.opencloudengine.garuda.settings.IaasProviderConfig;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
* Created by swsong on 2015. 7. 20..
*/
public class ClustersService extends AbstractService {


    private IaasProviderConfig iaasProviderConfig;

    private static final String CLUSTERS_KEY = "clusters";

    private Map<String, Cluster> clusterMap;


    public ClustersService(Environment environment, Settings settings, ServiceManager serviceManager) {
        super(environment, settings, serviceManager);
    }

    @Override
    protected boolean doStart() throws ServiceException {

        iaasProviderConfig = environment.settingManager().getIaasProviderConfig();
        clusterMap = new ConcurrentHashMap<>();

        SettingManager settingManager = environment.settingManager();
        Settings settings = settingManager.getClustersConfig();
        String[] clusterList = settings.getStringArray(CLUSTERS_KEY);
        if(clusterList != null) {
            for (String clusterId : clusterList) {
                try {
                    Cluster cluster = new Cluster(clusterId, environment);
                    cluster.start();
                    clusterMap.put(clusterId, cluster);
                }catch(Throwable t) {
                    logger.error("error loading cluster topology.", t);
                }
            }
        }

        return true;
    }

    @Override
    protected boolean doStop() throws ServiceException {
        for(Cluster cluster : clusterMap.values()) {
            cluster.stop();
        }
        clusterMap.clear();
        return true;
    }

    @Override
    protected boolean doClose() throws ServiceException {
        clusterMap = null;
        return true;
    }

    public Cluster getCluster(String clusterId) {
        return clusterMap.get(clusterId);
    }

    public void createCluster(String clusterId, String definitionId) throws GarudaException, ClusterExistException {
        createCluster(clusterId, definitionId, false);
    }

    public void createCluster(String clusterId, String definitionId, boolean waitUntilInstanceAvailable) throws GarudaException, ClusterExistException {

        if(clusterMap.containsKey(clusterId) || isClusterIdExistInSetting(clusterId)) {
            throw new ClusterExistException(String.format("Cluster %s is already exists.", clusterId));
        }

        SettingManager settingManager = environment.settingManager();
        ClusterDefinition clusterDefinition = settingManager.getClusterDefinition(definitionId);
        String iaasProfile = clusterDefinition.getIaasProfile();
        IaasProvider iaasProvider = iaasProviderConfig.getIaasProvider(iaasProfile);

        Cluster cluster = Cluster.createCluster(iaasProvider, clusterDefinition);
        clusterMap.put(clusterId, cluster);

        addClusterIdToSetting(clusterId);

    }

    public void destroyCluster(String clusterId) throws GarudaException {
        checkIfClusterExists(clusterId);
        Cluster cluster = clusterMap.remove(clusterId);
        cluster.close();
        //
        // 설정관련 수정. cluster리스트에서 제거한다.
        //
        removeClusterIdFromSetting(clusterId);
    }

    private void checkIfClusterExists(String clusterId) throws GarudaException {
        if(! clusterMap.containsKey(clusterId)) {
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


    private void storeClusterTopology(ClusterTopology clusterTopology){
        String clusterId = clusterTopology.getClusterId();
        Properties props = clusterTopology.getProperties();
        environment.settingManager().storeClusterTopology(clusterId, props);
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

    public Collection<ClusterTopology> getAllClusterTopology() {
        Collection<ClusterTopology> topologies = new HashSet<>();
        for(Cluster cluster : clusterMap.values()) {
            topologies.add(cluster.services().getService(ClusterService.class).getClusterTopology());
        }
        return topologies;
    }

//    public Collection<ClusterTopology> getAllClusterTopology() {
//        return clusterTopologyMap.values();
//    }
//
//    public ClusterTopology getClusterTopology(String clusterId) {
//        return clusterTopologyMap.get(clusterId);
//    }

}
