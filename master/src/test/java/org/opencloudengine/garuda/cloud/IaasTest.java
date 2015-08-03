package org.opencloudengine.garuda.cloud;

import com.amazonaws.services.ec2.model.Instance;
import org.junit.Before;
import org.junit.Test;
import org.opencloudengine.garuda.utils.PropertiesUtil;
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

    @Before
    public void setUp() throws IOException {
        Properties props = PropertiesUtil.loadProperties("/Users/swsong/Dropbox/System/auth/AwsCredentials.properties");
        accessKey = props.getProperty("accessKey");
        secretKey = props.getProperty("secretKey");
    }

    @Test
    public void testLaunch() {
        EC2IaaS iaas = new EC2IaaS(null, accessKey, secretKey, null);
        String instanceType = "t2.medium";
        String imageId = "ami-936d9d93";
        int volumeSize = 13;
        String group = "default";
        String keyPair = "aws-garuda";

        InstanceRequest request = new InstanceRequest(instanceType, imageId, volumeSize, group, keyPair);
        List<CommonInstance> list = iaas.launchInstance(request, "sang", 2);
        for(CommonInstance i : list) {
            logger.debug("- {}", i.getInstanceId());
            logger.debug("-- {}", i.as(Instance.class));
        }

        logger.debug("Wait..");
        iaas.waitUntilInstancesReady(list);
        logger.debug("Done!!");
    }

//    @Test
//    public void testDestroy() throws UnknownIaasProviderException, IOException {
//        String nodeId = "ap-northeast-1/i-cf2a883d";
//
//        IaasProvider iaasProvider = new IaasProvider("ec2", "test", accessKey, secretKey);
//        System.out.println("iaasProvider > " + iaasProvider);
//        Iaas iaas = iaasProvider.getIaas();
//        try {
//            iaas.computeService().destroyNode(nodeId);
//        }finally {
//            iaas.close();
//        }
//    }
//
//    @Test
//    public void testInstanceInfo() throws UnknownIaasProviderException, IOException {
//        String nodeId = "ap-northeast-1/i-cf2a883d";
//        Properties overrides = new Properties();
//        overrides.setProperty("jclouds.regions", "ap-northeast-1");
//        IaasProvider iaasProvider = new IaasProvider("ec2", "test", accessKey, secretKey);
//        System.out.println("iaasProvider > " + iaasProvider);
//        Iaas iaas = iaasProvider.getIaas();
//        try {
//            NodeMetadata d = iaas.computeService().getNodeMetadata(nodeId);
//            System.out.println(">> " + d);
//
//            System.out.println(String.format("NodeMetadata id[%s] group[%s]", d.getId(), d.getGroup()));
//        }finally {
//            iaas.close();
//        }
//    }


}
