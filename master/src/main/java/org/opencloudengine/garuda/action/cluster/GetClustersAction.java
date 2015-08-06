package org.opencloudengine.garuda.action.cluster;

import org.opencloudengine.garuda.action.RunnableAction;
import org.opencloudengine.garuda.cloud.ClusterService;
import org.opencloudengine.garuda.cloud.ClusterTopology;

import java.util.Collection;

/**
 * Created by swsong on 2015. 8. 6..
 */
public class GetClustersAction extends RunnableAction<GetClustersActionRequest> {

    public GetClustersAction(GetClustersActionRequest actionRequest) {
        super(actionRequest);
    }

    @Override
    protected void doAction() throws Exception {
        ClusterService clusterService = serviceManager.getService(ClusterService.class);
        Collection<ClusterTopology> set = clusterService.getAllClusterTopology();
        setResult(set);
    }
}
