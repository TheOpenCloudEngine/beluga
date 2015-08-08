package org.opencloudengine.garuda.action.cluster;

import org.junit.Test;
import org.opencloudengine.garuda.action.ActionStatus;
import org.opencloudengine.garuda.action.BaseActionTest;
import org.opencloudengine.garuda.exception.GarudaException;

/**
 * 클러스터내 인스턴스를 정지시킨다.
 * Created by swsong on 2015. 8. 4..
 */
public class StopClusterActionTest extends BaseActionTest {

    @Test
    public void stopCluster() throws GarudaException, InterruptedException {
        String clusterId = "test-cluster";

        DestroyClusterActionRequest request = new DestroyClusterActionRequest(clusterId);
        DestroyClusterAction action = new DestroyClusterAction(request);
        ActionStatus status = action.getStatus();
        action.run();

        status.waitForDone();
        logger.info("#### Destroy {} Done.", clusterId);
    }
}
