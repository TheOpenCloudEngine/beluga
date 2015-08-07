package org.opencloudengine.garuda.action.cluster;

import org.junit.Test;
import org.opencloudengine.garuda.action.ActionStatus;
import org.opencloudengine.garuda.action.BaseActionTest;
import org.opencloudengine.garuda.exception.GarudaException;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class DestroyClusterActionTest extends BaseActionTest {

    @Test
    public void destroyCluster() throws GarudaException, InterruptedException {
        String clusterId = "test-cluster";

        DestroyClusterActionRequest request = new DestroyClusterActionRequest(clusterId);
        DestroyClusterAction action = new DestroyClusterAction(request);
        ActionStatus status = action.getStatus();
        action.run();

        status.waitForDone();
        logger.info("#### Destroy {} Done.", clusterId);
    }
}
