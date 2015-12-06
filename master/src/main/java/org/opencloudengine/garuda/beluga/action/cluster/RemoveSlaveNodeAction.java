package org.opencloudengine.garuda.beluga.action.cluster;

import org.opencloudengine.garuda.beluga.action.RunnableAction;
import org.opencloudengine.garuda.beluga.cloud.ClusterService;
import org.opencloudengine.garuda.beluga.cloud.ClustersService;
import org.opencloudengine.garuda.beluga.cloud.CommonInstance;
import org.opencloudengine.garuda.beluga.mesos.MesosAPI;

import java.util.List;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class RemoveSlaveNodeAction extends RunnableAction<RemoveSlaveNodeActionRequest> {

    private static final int DELAY_BEFORE_CONFIGURATION = 60;//secs

    public RemoveSlaveNodeAction(RemoveSlaveNodeActionRequest request) {
        super(request);
        status.registerStep("Remove instance.");
        status.registerStep("Configure mesos-slave.");
    }

    @Override
    protected void doAction() throws Exception {
        RemoveSlaveNodeActionRequest request = getActionRequest();
        String clusterId = request.getClusterId();
        String instanceId = request.getInstanceId();

        ClustersService clustersService = serviceManager.getService(ClustersService.class);

        /*
        * Prepare instances
        * */
        status.walkStep();
        logger.debug("Remove Node..{} / {}", clusterId, instanceId);
        ClusterService clusterService = clustersService.getClusterService(clusterId);
        CommonInstance instance = clusterService.removeSlaveNode(instanceId);
        logger.debug("Remove Node.. Done.");
    }

}
