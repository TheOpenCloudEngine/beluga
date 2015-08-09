package org.opencloudengine.garuda.action.cluster;

import org.opencloudengine.garuda.action.RunnableAction;
import org.opencloudengine.garuda.cloud.ClusterService;
import org.opencloudengine.garuda.cloud.ClusterTopology;
import org.opencloudengine.garuda.mesos.MesosService;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class CreateClusterAction extends RunnableAction<CreateClusterActionRequest> {

    private static final int DELAY_BEFORE_CONFIGURATION = 60;//secs

    public CreateClusterAction(CreateClusterActionRequest request) {
        super(request);
        status.registerStep("Create instances.");
        status.registerStep("Configure mesos-master1.");
        status.registerStep("Configure mesos-slave.");
    }

    @Override
    protected void doAction() throws Exception {
        CreateClusterActionRequest request = getActionRequest();
        String clusterId = request.getClusterId();
        String definitionId = request.getDefinitionId();

        ClusterService clusterService = serviceManager.getService(ClusterService.class);
        MesosService mesosService = serviceManager.getService(MesosService.class);
        /*
        * Prepare instances
        * */
        //create instances and wait until available
        status.walkStep();
        logger.debug("Create Cluster..");
        ClusterTopology topology = clusterService.createCluster(clusterId, definitionId, true);
        logger.debug("Create Cluster.. Done.");
//        ClusterTopology topology = clusterService.getClusterTopology(clusterId);

        logger.debug("Wait {} secs before configuration", DELAY_BEFORE_CONFIGURATION);
        Thread.sleep(DELAY_BEFORE_CONFIGURATION);

        // 1.1 mesos-master1
        //
        status.walkStep();
        mesosService.configureMesosMasterInstances(clusterId, definitionId);

        //
        // 2.1 mesos-slave
        //
        status.walkStep();
        mesosService.configureMesosSlaveInstances(clusterId, definitionId);
    }

}
