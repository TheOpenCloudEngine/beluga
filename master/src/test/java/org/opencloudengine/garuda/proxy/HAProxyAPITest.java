package org.opencloudengine.garuda.proxy;

import org.junit.Test;
import org.opencloudengine.garuda.action.BaseActionTest;
import org.opencloudengine.garuda.cloud.ClusterService;
import org.opencloudengine.garuda.env.Environment;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by swsong on 2015. 8. 11..
 */
public class HAProxyAPITest extends BaseActionTest {

    @Test
    public void makeConfigWithTopology() {
        String homePath = "/Users/swsong/Projects/garuda/production";
        String clusterId = "test-cluster";
        Environment environment = new Environment(homePath);
        ClusterService clusterService = new ClusterService(clusterId, environment, environment.settingManager().getSystemSettings().getSubSettings("cluster"));
        HAProxyAPI api = new HAProxyAPI(clusterService, environment);
        String deploymentId = "asdfghjkl";
        api.notifyServiceChanged(deploymentId);
    }
}
