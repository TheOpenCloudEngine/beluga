package org.opencloudengine.garuda.action.cluster;

import org.junit.Test;
import org.opencloudengine.garuda.action.ActionResult;
import org.opencloudengine.garuda.action.ActionStatus;
import org.opencloudengine.garuda.action.BaseActionTest;
import org.opencloudengine.garuda.exception.GarudaException;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class CreateClusterActionTest extends BaseActionTest {

    @Test
    public void createCluster() throws GarudaException, InterruptedException {
        String clusterId = "test-cluster2";
        String definitionId = "ec2-real";

        logger.debug("clusterService Hash {}", clusterService);
        CreateClusterAction action = new CreateClusterAction();
        ActionStatus status = action.request(clusterId, definitionId);

        while(!status.isDone()) {
            System.out.println(String.format("%d / %d", status.getStep(), status.getTotalStep()));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {
            }
        }
        logger.info("#### Done.");
        ActionResult result = action.getResult();
        logger.info("#### Result = {}", result.getResult());
        logger.info("#### Result error = {} // {}", result.getErrorMessage(), result.getException());
    }
}
