package org.opencloudengine.garuda.proxy;

import org.apache.velocity.VelocityContext;
import org.junit.Test;
import org.opencloudengine.garuda.action.BaseActionTest;
import org.opencloudengine.garuda.env.Environment;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by swsong on 2015. 8. 11..
 */
public class HAProxyAPITest extends BaseActionTest {

    @Test
    public void makeConfigWithTopology() {
        String homePath = "/Users/swsong/Projects/garuda/production";
        String clusterId = "test-cluster";
        Environment environment = new Environment(homePath);

        HAProxyAPI api = new HAProxyAPI(environment, null);
        VelocityContext context = new VelocityContext();
        api.fillTopologyToContext(context, clusterId);
        String configString = api.makeConfigString(context);
        System.out.println(configString);
    }
}
