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
public class AddSlaveNodeAction extends RunnableAction<AddSlaveNodeActionRequest> {

    private static final int DELAY_BEFORE_CONFIGURATION = 60;//secs

    public AddSlaveNodeAction(AddSlaveNodeActionRequest request) {
        super(request);
        status.registerStep("Create instance.");
        status.registerStep("Configure mesos-slave.");
    }

    @Override
    protected void doAction() throws Exception {
        AddSlaveNodeActionRequest request = getActionRequest();
        String clusterId = request.getClusterId();
        Integer incrementSize = request.getIncrementSize();

        ClustersService clustersService = serviceManager.getService(ClustersService.class);

        /*
        * Prepare instances
        * */
        status.walkStep();
        logger.debug("Create Node..");
        ClusterService clusterService = clustersService.getClusterService(clusterId);
        List<CommonInstance> instanceList = clusterService.addSlaveNode(incrementSize);
        logger.debug("Create Node.. Done.");

        logger.debug("Wait {} secs before configuration", DELAY_BEFORE_CONFIGURATION);
        Thread.sleep(DELAY_BEFORE_CONFIGURATION);

        MesosAPI mesosAPI = clusterService.getMesosAPI();
        //
        // Configure mesos-slave
        //
        status.walkStep();
        mesosAPI.configureMesosSlaveInstances(instanceList);
    }

}
