package org.opencloudengine.garuda.action.cluster;

import org.opencloudengine.garuda.action.RunnableAction;
import org.opencloudengine.garuda.cloud.ClustersService;
import org.opencloudengine.garuda.mesos.MesosAPI;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class CreateClusterAction extends RunnableAction<CreateClusterActionRequest> {

    private static final int DELAY_BEFORE_CONFIGURATION = 60;//secs

    public CreateClusterAction(CreateClusterActionRequest request) {
        super(request);
        status.registerStep("Create instances.");
        status.registerStep("Configure mesos-master.");
        status.registerStep("Configure mesos-slave.");
    }

    @Override
    protected void doAction() throws Exception {
        CreateClusterActionRequest request = getActionRequest();
        String clusterId = request.getClusterId();
        String definitionId = request.getDefinitionId();
        String domainName = request.getDomainName();

        ClustersService clustersService = serviceManager.getService(ClustersService.class);

        /*
        * Prepare instances
        * */
        //create instances and wait until available
        status.walkStep();
        logger.debug("Create Cluster..");
        clustersService.createCluster(clusterId, definitionId, domainName, true);
        logger.debug("Create Cluster.. Done.");

        logger.debug("Wait {} secs before configuration", DELAY_BEFORE_CONFIGURATION);
        Thread.sleep(DELAY_BEFORE_CONFIGURATION);

        MesosAPI mesosAPI = clustersService.getClusterService(clusterId).getMesosAPI();
        // 1.1 mesos-master1
        //
        status.walkStep();
        mesosAPI.configureMesosMasterInstances(definitionId);

        //
        // 2.1 mesos-slave
        //
        status.walkStep();
        mesosAPI.configureMesosSlaveInstances(definitionId);
    }

}
