package org.opencloudengine.garuda.action.cluster;

import org.opencloudengine.garuda.action.RunnableAction;
import org.opencloudengine.garuda.cloud.ClusterService;
import org.opencloudengine.garuda.cloud.ClusterTopology;

/**
 * Created by swsong on 2015. 8. 6..
 */
public class GetClusterAction extends RunnableAction<GetClusterActionRequest> {

    public GetClusterAction(GetClusterActionRequest actionRequest) {
        super(actionRequest);
    }

    @Override
    protected void doAction() throws Exception {
        String clusterId = getActionRequest().getClusterId();
        ClusterService clusterService = serviceManager.getService(ClusterService.class);
        ClusterTopology topology = clusterService.getClusterTopology(clusterId);
        setResult(topology);
    }
}
