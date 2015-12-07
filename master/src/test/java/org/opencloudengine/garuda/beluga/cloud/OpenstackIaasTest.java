package org.opencloudengine.garuda.beluga.cloud;

import com.amazonaws.services.ec2.model.Instance;
import org.junit.Before;
import org.junit.Test;
import org.opencloudengine.garuda.beluga.utils.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Created by swsong on 2015. 12. 4..
 */
public class OpenstackIaasTest {

    private static Logger logger = LoggerFactory.getLogger(OpenstackIaasTest.class);
    String accessKey = "demo:demo";
    String secretKey = "demopass";

    String clusterId = "sample";
    String endPoint = "http://10.0.1.251:5000/v2.0";
    String instanceType = "2";
    String imageId = "44c83f44-f95d-41f1-9d14-06639de892fa";
    int volumeSize = 10;
    String group = "default";
    String keyPair = "beluga-openstack";
    String[] networks = new String[] {"ff2e5579-2cfa-4a67-832f-4fc2a6085de9"};
    String nodeName = "ndoe";

    @Test
    public void testLaunch() {

        InstanceRequest request = new InstanceRequest(clusterId, instanceType, imageId, volumeSize, group, keyPair, networks);
        OpenstackIaas iaas = new OpenstackIaas(endPoint, accessKey, secretKey, null);
        List<CommonInstance> list = iaas.launchInstance(request, nodeName, 2, 1);
        for(CommonInstance i : list) {
            logger.debug("- {}", i.getInstanceId());
            logger.debug("-- {}", i.as(Instance.class));
        }

        logger.debug("Wait..");
        iaas.waitUntilInstancesRunning(list);
        logger.debug("Done!!");
    }

    @Test
    public void testLaunchByCustomAMI() {
        int volumeSize = 10;
        InstanceRequest request = new InstanceRequest(clusterId, instanceType, imageId, volumeSize, group, keyPair, networks);
        OpenstackIaas iaas = new OpenstackIaas(endPoint, accessKey, secretKey, null);
        List<CommonInstance> list = iaas.launchInstance(request, nodeName, 1, 1);
        for(CommonInstance i : list) {
            logger.debug("- {}", i.getInstanceId());
            logger.debug("-- {}", i.as(Instance.class));
        }

        logger.debug("Wait..");
        iaas.waitUntilInstancesRunning(list);
        logger.debug("Done!!");
    }

    @Test
    public void testLaunchDescribeTerminate() {
        InstanceRequest request = new InstanceRequest(clusterId, instanceType, imageId, volumeSize, group, keyPair, networks);
        OpenstackIaas iaas = new OpenstackIaas(endPoint, accessKey, secretKey, null);
        List<CommonInstance> list = iaas.launchInstance(request, nodeName, 2, 1);
        for(CommonInstance i : list) {
            logger.debug("- {}", i.getInstanceId());
            logger.debug("-- {}", i.as(Instance.class));
        }

        logger.debug("Wait..");
        iaas.waitUntilInstancesRunning(list);
        logger.debug("Launch Done!!");



        logger.debug("Terminate!");
        iaas.terminateInstances(IaasUtils.getIdList(list));


    }
}
