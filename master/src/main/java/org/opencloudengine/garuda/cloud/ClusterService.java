package org.opencloudengine.garuda.cloud;

import org.opencloudengine.garuda.env.Environment;
import org.opencloudengine.garuda.env.SettingManager;
import org.opencloudengine.garuda.env.Settings;
import org.opencloudengine.garuda.exception.GarudaException;
import org.opencloudengine.garuda.exception.UnknownIaasProviderException;
import org.opencloudengine.garuda.service.AbstractService;
import org.opencloudengine.garuda.service.common.ServiceManager;
import org.opencloudengine.garuda.settings.ClusterDefinition;
import org.opencloudengine.garuda.settings.IaasProviderConfig;

import java.util.*;

/**
* Created by swsong on 2015. 7. 20..
*/
public class ClusterService extends AbstractService {

    private Map<String, ClusterTopology> clusterTopologyMap;

    private IaasProviderConfig iaasProviderConfig;

    private static final String CLUSTERS_KEY = "clusters";

    public ClusterService(Environment environment, Settings settings, ServiceManager serviceManager) {
        super(environment, settings, serviceManager);
    }

    @Override
    protected boolean doStart() throws GarudaException {

        clusterTopologyMap = new HashMap<>();
        iaasProviderConfig = environment.settingManager().getIaasProviderConfig();

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

        return true;
    }

    @Override
    protected boolean doStop() throws GarudaException {
        return true;
    }

    @Override
    protected boolean doClose() throws GarudaException {
        return true;
    }

    public ClusterTopology createCluster(String clusterId, String definitionId) throws UnknownIaasProviderException {
        return createCluster(clusterId, definitionId, false);
    }

    public ClusterTopology createCluster(String clusterId, String definitionId, boolean waitUntilInstanceAvailable) throws UnknownIaasProviderException {

        SettingManager settingManager = environment.settingManager();
        ClusterDefinition clusterDefinition = settingManager.getClusterDefinition(definitionId);
        String iaasProfile = clusterDefinition.getIaasProfile();
        IaasProvider iaasProvider = iaasProviderConfig.getIaasProvider(iaasProfile);

        ClusterTopology clusterTopology = new ClusterTopology(clusterId, iaasProfile);

        String keyPair = clusterDefinition.getKeyPair();

        IaaS iaas = iaasProvider.getIaas();
        try {
            List<ClusterDefinition.RoleDefinition> roleDefinitions = clusterDefinition.getRoleList();

            for (ClusterDefinition.RoleDefinition roleDefinition : roleDefinitions) {
                String role = roleDefinition.getRole();
                int size = roleDefinition.getDefaultSize();
                InstanceRequest request = new InstanceRequest(roleDefinition.getInstanceType(), roleDefinition.getImageId()
                        , roleDefinition.getDiskSize(), roleDefinition.getGroup(), keyPair);
                List<CommonInstance> instanceList = iaas.launchInstance(request, role, size);
                for(CommonInstance instance : instanceList) {
                    //토폴로지에 넣어준다.
                    clusterTopology.addNode(role, instance);
                    logger.debug("launched {}", instance);
                }
            }
        } finally {
            iaas.close();
        }
        //runtime 토폴로지에 넣어준다.
        clusterTopologyMap.put(clusterId, clusterTopology);
        //토폴로지 설정파일을 저장한다.
        storeClusterTopology(clusterTopology);
        //clusters 설정에서 cluster에 넣어준다.
        Settings settings = settingManager.getClustersConfig();
        settings.addStringToArray(CLUSTERS_KEY, clusterId);
        //clusters 설정파일을 저장한다.
        settingManager.storeClustersConfig(settings);

        if(waitUntilInstanceAvailable) {
            List<CommonInstance> list = clusterTopology.getAllNodeList();
            iaas.waitUntilInstancesReady(list);
        }

        return clusterTopology;
    }

    public void destroyCluster(String clusterId) throws UnknownIaasProviderException {

        ClusterTopology clusterTopology = clusterTopologyMap.get(clusterId);

        String iaasType = clusterTopology.getIaasProfile();

        IaasProvider iaasProvider = iaasProviderConfig.getIaasProvider(iaasType);

        //clusterTopology 내에 해당하는 살아있는 모든 노드 삭제.
        IaaS iaas = iaasProvider.getIaas();

        try {
            iaas.terminateInstances(clusterTopology.getAllNodeList());
        } finally {
            if(iaas != null) {
                iaas.close();
            }
        }
        clusterTopologyMap.remove(clusterId);
        SettingManager settingManager = environment.settingManager();
        //clusters 설정에서 cluster를 제거한다.
        Settings settings = settingManager.getClustersConfig();
        settings.removeStringFromArray(CLUSTERS_KEY, clusterId);
        //clusters 설정파일을 저장한다.
        settingManager.storeClustersConfig(settings);
    }

    public void loadCluster(String clusterId) throws UnknownIaasProviderException {
        Settings settings = environment.settingManager().getClusterTopologyConfig(clusterId);

        String iaasType = null;//settings.getString(ClusterTopology.IAAS_PROFILE_KEY);
        if(iaasType == null) {
            throw new UnknownIaasProviderException("provider is null.");
        }
        IaasProvider iaasProvider = iaasProviderConfig.getIaasProvider(iaasType);
        IaaS iaas = iaasProvider.getIaas();

        ClusterTopology clusterTopology = new ClusterTopology(clusterId, iaasType);
        try {
            loadRole(ClusterTopology.GARUDA_MASTER_ROLE, settings, iaas, clusterTopology);
            loadRole(ClusterTopology.PROXY_ROLE, settings, iaas, clusterTopology);
            loadRole(ClusterTopology.MESOS_MASTER_ROLE, settings, iaas, clusterTopology);
            loadRole(ClusterTopology.MESOS_SLAVE_ROLE, settings, iaas, clusterTopology);
            loadRole(ClusterTopology.DOCKER_REGISTRY_ROLE, settings, iaas, clusterTopology);
            loadRole(ClusterTopology.MANAGEMENT_DB_ROLE, settings, iaas, clusterTopology);
            loadRole(ClusterTopology.SERVICE_NODES_ROLE, settings, iaas, clusterTopology);
        } finally {
            iaas.close();
        }
        clusterTopologyMap.put(clusterId, clusterTopology);
    }

    private void storeClusterTopology(ClusterTopology clusterTopology){
        String clusterId = clusterTopology.getClusterId();
        Properties props = clusterTopology.storeProperties();
        environment.settingManager().storeClusterTopology(clusterId, props);
    }

    private void loadRole(String role, Settings settings, IaaS iaas, ClusterTopology clusterTopology) {
        String value = settings.getValue(role);
        if(value != null && value.trim().length() > 0) {
            String[] idArray = settings.getStringArray(role);
            List<String> idList = new ArrayList<String>();
            for(String id : idArray) {
                idList.add(id);
            }
            List<CommonInstance> list = iaas.getRunningInstances(idList);
            for(CommonInstance instance : list) {
                clusterTopology.addNode(role, instance);
            }
        }
    }

    public ClusterTopology getClusterTopology(String clusterId) {
        return clusterTopologyMap.get(clusterId);
    }

}
