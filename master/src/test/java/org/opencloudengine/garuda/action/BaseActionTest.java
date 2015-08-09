package org.opencloudengine.garuda.action;

import org.junit.Before;
import org.opencloudengine.garuda.cloud.ClusterService;
import org.opencloudengine.garuda.env.Environment;
import org.opencloudengine.garuda.exception.GarudaException;
import org.opencloudengine.garuda.service.common.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class BaseActionTest {
    protected static Logger logger = LoggerFactory.getLogger(BaseActionTest.class);
    protected Environment environment;
    protected ServiceManager serviceManager;
    protected ClusterService clusterService;
    String home = "production";

    @Before
    public void setUp() throws GarudaException {

        environment = new Environment(home);
        environment.init();
        serviceManager = new ServiceManager(environment);
        serviceManager.asSingleton();

        serviceManager.registerService("cluster", ClusterService.class);
        clusterService = serviceManager.getService(ClusterService.class);
    }
}
