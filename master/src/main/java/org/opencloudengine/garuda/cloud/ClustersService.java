package org.opencloudengine.garuda.cloud;

import org.opencloudengine.garuda.env.Environment;
import org.opencloudengine.garuda.env.SettingManager;
import org.opencloudengine.garuda.env.Settings;
import org.opencloudengine.garuda.exception.GarudaException;
import org.opencloudengine.garuda.exception.InvalidRoleException;
import org.opencloudengine.garuda.exception.UnknownIaasProviderException;
import org.opencloudengine.garuda.service.AbstractService;
import org.opencloudengine.garuda.service.ServiceException;
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

    private Map<String, ClusterService> clusterMap;

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
                    ClusterService cluster = new ClusterService(clusterId, environment, settings);
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
        for(ClusterService clusterService : clusterMap.values()) {
            clusterService.stop();
        }
        clusterMap.clear();
        return true;
    }

    @Override
    protected boolean doClose() throws ServiceException {
        clusterMap = null;
        return true;
    }

    public ClusterService getClusterService(String clusterId) {
        return clusterMap.get(clusterId);
    }

    public void createCluster(String clusterId, String definitionId) throws GarudaException, ClusterExistException {
        createCluster(clusterId, definitionId, false);
    }

    public ClusterService createCluster(String clusterId, String definitionId, boolean waitUntilInstanceAvailable) throws GarudaException, ClusterExistException {

        if(clusterMap.containsKey(clusterId) || isClusterIdExistInSetting(clusterId)) {
            throw new ClusterExistException(String.format("Cluster %s is already exists.", clusterId));
        }

        ClusterService clusterService = null;
        try {
            clusterService = new ClusterService(clusterId, environment, settings).createCluster(definitionId, waitUntilInstanceAvailable);
        } catch (UnknownIaasProviderException e) {
            throw new GarudaException(e);
        }
        if (clusterService.start()) {
            addClusterIdToSetting(clusterId);
        }
        clusterMap.put(clusterId, clusterService);

        return clusterService;
    }

    public void destroyCluster(String clusterId) throws GarudaException {
        checkIfClusterExists(clusterId);
        ClusterService clusterService = clusterMap.get(clusterId);
        try {
            clusterService.destroyCluster();
        } catch (UnknownIaasProviderException e) {
            throw new GarudaException(e);
        }
        //
        // 설정관련 수정. cluster리스트에서 제거한다.
        //
        clusterMap.remove(clusterId);
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

    public Collection<ClusterTopology> getAllClusterTopology() {
        Collection<ClusterTopology> topologies = new HashSet<>();
        for(ClusterService clusterService : clusterMap.values()) {
            topologies.add(clusterService.getClusterTopology());
        }
        return topologies;
    }
}
