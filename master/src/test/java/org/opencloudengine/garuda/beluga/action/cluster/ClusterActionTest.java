package org.opencloudengine.garuda.beluga.action.cluster;

import org.junit.Test;
import org.opencloudengine.garuda.beluga.action.ActionStatus;
import org.opencloudengine.garuda.beluga.action.BaseActionTest;
import org.opencloudengine.garuda.beluga.exception.GarudaException;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class ClusterActionTest extends BaseActionTest {

    String clusterId = "test-cluster";
    String domainName = "fastcatsearch.com";

    @Test
    public void createEC2DevelopmentCluster() throws GarudaException, InterruptedException {
        String definitionId = "ec2-dev";
        CreateClusterActionRequest request = new CreateClusterActionRequest(clusterId, definitionId, domainName);
        CreateClusterAction action = new CreateClusterAction(request);
        ActionStatus status = action.getStatus();
        action.run();
        status.waitForDone();
    }

    @Test
    public void createEC2RealCluster() throws GarudaException, InterruptedException {
        String definitionId = "ec2-real";

        CreateClusterActionRequest request = new CreateClusterActionRequest(clusterId, definitionId, domainName);
        CreateClusterAction action = new CreateClusterAction(request);
        ActionStatus status = action.getStatus();
        action.run();
        status.waitForDone();
    }

    @Test
    public void stopCluster() throws GarudaException, InterruptedException {
        DestroyClusterActionRequest request = new DestroyClusterActionRequest(clusterId);
        DestroyClusterAction action = new DestroyClusterAction(request);
        ActionStatus status = action.getStatus();
        action.run();

        status.waitForDone();
        logger.info("#### Destroy {} Done.", clusterId);
    }

    @Test
    public void startCluster() throws GarudaException, InterruptedException {
        StartClusterActionRequest request = new StartClusterActionRequest(clusterId);
        StartClusterAction action = new StartClusterAction(request);
        ActionStatus status = action.getStatus();
        action.run();

        status.waitForDone();
        logger.info("#### Start {} Done.", clusterId);
    }

    @Test
    public void restartCluster() throws GarudaException, InterruptedException {
        RestartClusterActionRequest request = new RestartClusterActionRequest(clusterId);
        RestartClusterAction action = new RestartClusterAction(request);
        ActionStatus status = action.getStatus();
        action.run();

        status.waitForDone();
        logger.info("#### Restart {} Done.", clusterId);
    }

    @Test
    public void destroyCluster() throws GarudaException, InterruptedException {
        DestroyClusterActionRequest request = new DestroyClusterActionRequest(clusterId);
        DestroyClusterAction action = new DestroyClusterAction(request);
        ActionStatus status = action.getStatus();
        action.run();

        status.waitForDone();
        logger.info("#### Destroy {} Done.", clusterId);
    }
}
