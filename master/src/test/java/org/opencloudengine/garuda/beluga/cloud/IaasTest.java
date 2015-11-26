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
 * Created by swsong on 2015. 7. 22..
 */
public class IaasTest {

    private static Logger logger = LoggerFactory.getLogger(IaasTest.class);
    String accessKey;
    String secretKey;

    String clusterId = "mytest";
    String endPoint = "ec2.ap-southeast-1.amazonaws.com";
    String instanceType = "t2.micro";
    String imageId = "ami-936d9d93";
    int volumeSize = 13;
    String group = "default";
    String keyPair = "beluga-aws";

    @Before
    public void setUp() throws IOException {
        Properties props = PropertiesUtil.loadProperties("/Users/swsong/Dropbox/System/auth/AwsCredentials.properties");
        accessKey = props.getProperty("accessKey");
        secretKey = props.getProperty("secretKey");
    }

    @Test
    public void testEC2Launch() {
        InstanceRequest request = new InstanceRequest(clusterId, instanceType, imageId, volumeSize, group, keyPair);
        EC2Iaas iaas = new EC2Iaas(endPoint, accessKey, secretKey, null);
        List<CommonInstance> list = iaas.launchInstance(request, "sang", 2, 1);
        for(CommonInstance i : list) {
            logger.debug("- {}", i.getInstanceId());
            logger.debug("-- {}", i.as(Instance.class));
        }

        logger.debug("Wait..");
        iaas.waitUntilInstancesRunning(list);
        logger.debug("Done!!");
    }

    @Test
    public void testEC2LaunchByCustomAMI() {
        String imageId = "ami-ba1fa3ba";
        int volumeSize = 10;
        InstanceRequest request = new InstanceRequest(clusterId, instanceType, imageId, volumeSize, group, keyPair);
        EC2Iaas iaas = new EC2Iaas(endPoint, accessKey, secretKey, null);
        List<CommonInstance> list = iaas.launchInstance(request, "slave", 1, 1);
        for(CommonInstance i : list) {
            logger.debug("- {}", i.getInstanceId());
            logger.debug("-- {}", i.as(Instance.class));
        }

        logger.debug("Wait..");
        iaas.waitUntilInstancesRunning(list);
        logger.debug("Done!!");
    }

    @Test
    public void testEC2LaunchDescribeTerminate() {
        InstanceRequest request = new InstanceRequest(clusterId, instanceType, imageId, volumeSize, group, keyPair);
        EC2Iaas iaas = new EC2Iaas(endPoint, accessKey, secretKey, null);
        List<CommonInstance> list = iaas.launchInstance(request, "sang", 2, 1);
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
