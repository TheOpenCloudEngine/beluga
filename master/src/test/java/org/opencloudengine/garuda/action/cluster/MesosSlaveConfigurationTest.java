package org.opencloudengine.garuda.action.cluster;

import org.junit.Test;
import org.opencloudengine.garuda.mesos.MesosSlaveConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by swsong on 2015. 8. 5..
 */
public class MesosSlaveConfigurationTest {

    protected static Logger logger = LoggerFactory.getLogger(MesosSlaveConfigurationTest.class);

    @Test
    public void create() {
        final MesosSlaveConfiguration mesosConf = new MesosSlaveConfiguration();
        mesosConf.withZookeeperAddress("10.0.1.10");
        mesosConf.withZookeeperAddress("10.0.1.11");
        mesosConf.withHostName("10.0.1.200").withPrivateIpAddress("10.0.1.200");
        mesosConf.withContainerizer("docker").withDockerRegistryAddress("10.0.1.5");
        logger.debug("{} {}", "slave", mesosConf.toParameter());
    }

    @Test
    public void createClone() {
        final MesosSlaveConfiguration conf = new MesosSlaveConfiguration();
        conf.withZookeeperAddress("10.0.1.10");
        conf.withZookeeperAddress("10.0.1.11");
        MesosSlaveConfiguration mesosConf = conf.clone();
        mesosConf.withHostName("10.0.1.200").withPrivateIpAddress("10.0.1.200");
        mesosConf.withContainerizer("docker").withDockerRegistryAddress("10.0.1.5");
        logger.debug("{} {}", "slave", mesosConf.toParameter());
    }
}
