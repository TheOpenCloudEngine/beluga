package org.opencloudengine.garuda.proxy;

import org.apache.velocity.VelocityContext;
import org.junit.Test;
import org.opencloudengine.garuda.action.BaseActionTest;
import org.opencloudengine.garuda.env.Environment;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
        Map<String, Queue<String>> clusterConfigQueueMap = new HashMap<>();
        clusterConfigQueueMap.put(clusterId, new LinkedBlockingQueue<String>());
        HAProxyAPI api = new HAProxyAPI(environment, clusterConfigQueueMap);
        String configString = api.onChangeCluster(clusterId);
        System.out.println(configString);
    }
}
