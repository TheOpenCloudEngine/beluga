package org.opencloudengine.garuda.action.cluster;

import org.opencloudengine.garuda.action.ActionResult;
import org.opencloudengine.garuda.action.RequestAction;
import org.opencloudengine.garuda.cloud.ClusterService;
import org.opencloudengine.garuda.cloud.ClusterTopology;
import org.opencloudengine.garuda.cloud.CommonInstance;
import org.opencloudengine.garuda.exception.UnknownIaasProviderException;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class CreateClusterAction extends RequestAction {

    public CreateClusterAction() {
        status.registerStep("Prepare instances.");
        status.registerStep("Install packages.");
        status.registerStep("Reboot instances.");
    }
    @Override
    protected ActionResult doAction(Object... params) {
        String clusterId = (String) params[0];
        String definitionId = (String) params[1];

        status.startStep();

        /*
        * Prepare instances
        * */
        ClusterService clusterService = serviceManager.createService("cluster", ClusterService.class);
        //클러스터가 이미 존재하는지 확인.
        if(clusterService.getClusterTopology(clusterId) != null) {
            return new ActionResult().withError("Cluster %s is already exist.", clusterId);
        }
        status.walkStep();

        /*
        * Install packages
        * */
        try {
            //create instances and wait until available
            ClusterTopology topology = clusterService.createCluster(clusterId, definitionId, true);

            for( CommonInstance i : topology.getMesosMasterList()) {

            }


        } catch (UnknownIaasProviderException e) {
            e.printStackTrace();
        }

        status.walkStep();

        /*
         * REBOOT
         */
        status.walkStep();

        return new ActionResult().withResult(true);
    }
}
