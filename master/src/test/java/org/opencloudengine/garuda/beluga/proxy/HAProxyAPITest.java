package org.opencloudengine.garuda.beluga.proxy;

import org.junit.Test;
import org.opencloudengine.garuda.beluga.action.BaseActionTest;
import org.opencloudengine.garuda.beluga.cloud.ClusterService;
import org.opencloudengine.garuda.beluga.env.Environment;

/**
 * Created by swsong on 2015. 8. 11..
 */
public class HAProxyAPITest extends BaseActionTest {

    @Test
    public void makeConfigWithTopology() {
        String homePath = "/Users/swsong/Projects/beluga/production";
        String clusterId = "test-cluster";
        Environment environment = new Environment(homePath);
        ClusterService clusterService = new ClusterService(clusterId, environment, environment.settingManager().getSystemSettings().getSubSettings("cluster"));
        HAProxyAPI api = new HAProxyAPI(clusterService, environment);
        String deploymentId = "asdfghjkl";
        api.notifyServiceChanged(deploymentId);
    }
}
