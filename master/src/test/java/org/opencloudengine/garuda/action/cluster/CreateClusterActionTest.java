package org.opencloudengine.garuda.action.cluster;

import org.junit.Test;
import org.opencloudengine.garuda.action.ActionStatus;
import org.opencloudengine.garuda.action.BaseActionTest;
import org.opencloudengine.garuda.exception.GarudaException;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class CreateClusterActionTest extends BaseActionTest {

    @Test
    public void createEC2DevelopmentCluster() throws GarudaException, InterruptedException {
        String clusterId = "test-cluster";
        String definitionId = "ec2-dev";

        CreateClusterActionRequest request = new CreateClusterActionRequest(clusterId, definitionId);
        CreateClusterAction action = new CreateClusterAction(request);
        ActionStatus status = action.getStatus();
        action.run();
        status.waitForDone();
    }

    @Test
    public void createEC2RealCluster() throws GarudaException, InterruptedException {
        String clusterId = "test-cluster";
        String definitionId = "ec2-real";

        CreateClusterActionRequest request = new CreateClusterActionRequest(clusterId, definitionId);
        CreateClusterAction action = new CreateClusterAction(request);
        ActionStatus status = action.getStatus();
        action.run();
        status.waitForDone();
    }
}
