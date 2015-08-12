package org.opencloudengine.garuda.action.cluster;

import org.opencloudengine.garuda.action.RunnableAction;
import org.opencloudengine.garuda.cloud.ClusterService;
import org.opencloudengine.garuda.cloud.ClustersService;

/**
 * 클러스터를 삭제한다.
 * Created by swsong on 2015. 8. 6..
 */
public class DestroyClusterAction extends RunnableAction<DestroyClusterActionRequest> {

    public DestroyClusterAction(DestroyClusterActionRequest actionRequest) {
        super(actionRequest);
        status.registerStep("destroy instances.");
    }

    @Override
    protected void doAction() throws Exception {
        String clusterId = getActionRequest().getClusterId();
        ClustersService clustersService = serviceManager.getService(ClustersService.class);
        status.walkStep();
        clustersService.destroyCluster(clusterId);
    }
}
