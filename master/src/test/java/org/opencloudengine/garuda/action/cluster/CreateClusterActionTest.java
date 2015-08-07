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
    public void createCluster() throws GarudaException, InterruptedException {
        String clusterId = "test-cluster";
        String definitionId = "ec2-real";

        logger.debug("clusterService Hash {}", clusterService);
        CreateClusterActionRequest request = new CreateClusterActionRequest(clusterId, definitionId);
        CreateClusterAction action = new CreateClusterAction(request);
        ActionStatus status = action.getStatus();
        action.run();

        while(!status.checkDone()) {
            System.out.println(String.format("%d / %d", status.getStep().getCurrentStep(), status.getStep().getTotalStep()));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {
            }
        }
        logger.info("#### Done.");
    }
}
