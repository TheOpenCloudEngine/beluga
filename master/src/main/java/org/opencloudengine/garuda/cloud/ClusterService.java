package org.opencloudengine.garuda.cloud;

import org.jclouds.compute.RunNodesException;
import org.opencloudengine.garuda.env.Environment;
import org.opencloudengine.garuda.env.Settings;
import org.opencloudengine.garuda.exception.GarudaException;
import org.opencloudengine.garuda.exception.UnknownIaasProviderException;
import org.opencloudengine.garuda.service.AbstractService;
import org.opencloudengine.garuda.service.common.ServiceManager;
import org.opencloudengine.garuda.settings.ClusterConfig;
import org.opencloudengine.garuda.settings.IaasProviderConfig;

import java.util.List;

/**
 * Created by swsong on 2015. 7. 20..
 */
public class ClusterService extends AbstractService {


    private ClusterConfig clusterConfig;
    private IaasProviderConfig iaasProviderConfig;

    private List<ClusterConfig.RoleDefinition> roleDefinitions;

    public ClusterService(Environment environment, Settings settings, ServiceManager serviceManager) {
        super(environment, settings, serviceManager);
    }

    @Override
    protected boolean doStart() throws GarudaException {

        clusterConfig = environment.settingManager().getClusterConfig();
        iaasProviderConfig = environment.settingManager().getIaasProviderConfig();
        roleDefinitions = clusterConfig.getRoleList();

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

    public void initCluster(String iaasProviderId) throws UnknownIaasProviderException, RunNodesException {
        IaasProvider iaasProvider = iaasProviderConfig.getProviderMap().get(iaasProviderId);

        String keyPair = clusterConfig.getKeyPair();
        for (ClusterConfig.RoleDefinition roleDefinition : roleDefinitions) {
            String role = roleDefinition.getRole();
            int size = roleDefinition.getDefaultSize();
            InstanceRequest request = new InstanceRequest(roleDefinition.getInstanceType(), roleDefinition.getImageId()
                    , roleDefinition.getDiskSize(), roleDefinition.getGroup(), keyPair);
            iaasProvider.getIaas().launchInstance(request, role, size);
        }

    }

    public void destroyCluster() {

    }


}
