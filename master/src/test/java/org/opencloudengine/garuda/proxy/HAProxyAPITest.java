package org.opencloudengine.garuda.proxy;

import org.junit.Test;
import org.opencloudengine.garuda.action.BaseActionTest;
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
        HAProxyAPI api = new HAProxyAPI(clusterId, environment);
        String deploymentId = "asdfghjkl";
        api.notifyServiceChanged(deploymentId);
    }
}
