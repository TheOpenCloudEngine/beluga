package org.opencloudengine.garuda.beluga.action;

import org.junit.Before;
import org.opencloudengine.garuda.beluga.cloud.ClustersService;
import org.opencloudengine.garuda.beluga.env.Environment;
import org.opencloudengine.garuda.beluga.exception.GarudaException;
import org.opencloudengine.garuda.beluga.service.common.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class BaseActionTest {
    protected static Logger logger = LoggerFactory.getLogger(BaseActionTest.class);
    protected Environment environment;
    protected ServiceManager serviceManager;
    protected ClustersService clustersService;
    String home = "/Users/swsong/Projects/garuda/production";

    @Before
    public void setUp() throws GarudaException {

        environment = new Environment(home);
        environment.init();
        serviceManager = new ServiceManager(environment);
        serviceManager.asSingleton();

        serviceManager.registerService("cluster", ClustersService.class);
        clustersService = serviceManager.getService(ClustersService.class);
    }
}
