package org.opencloudengine.garuda.cloud;

import org.jclouds.compute.domain.NodeMetadata;
import org.junit.Before;
import org.junit.Test;
import org.opencloudengine.garuda.exception.UnknownIaasProviderException;
import org.opencloudengine.garuda.utils.PropertiesUtil;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by swsong on 2015. 7. 22..
 */
public class IaasTest {

    String accessKey;
    String secretKey;

    @Before
    public void setUp() throws IOException {
        Properties props = PropertiesUtil.loadProperties("/Users/swsong/Dropbox/System/auth/AwsCredentials.properties");
        accessKey = props.getProperty("accessKey");
        secretKey = props.getProperty("secretKey");
    }
    @Test
    public void testDestroy() throws UnknownIaasProviderException, IOException {
        String nodeId = "ap-northeast-1/i-cf2a883d";

        IaasProvider iaasProvider = new IaasProvider("ec2", "test", accessKey, secretKey);
        System.out.println("iaasProvider > " + iaasProvider);
        Iaas iaas = iaasProvider.getIaas();
        try {
            iaas.computeService().destroyNode(nodeId);
        }finally {
            iaas.close();
        }
    }

    @Test
    public void testInstanceInfo() throws UnknownIaasProviderException, IOException {
        String nodeId = "ap-northeast-1/i-cf2a883d";
        Properties overrides = new Properties();
        overrides.setProperty("jclouds.regions", "ap-northeast-1");
        IaasProvider iaasProvider = new IaasProvider("ec2", "test", accessKey, secretKey);
        System.out.println("iaasProvider > " + iaasProvider);
        Iaas iaas = iaasProvider.getIaas();
        try {
            NodeMetadata d = iaas.computeService().getNodeMetadata(nodeId);
            System.out.println(">> " + d);

            System.out.println(String.format("NodeMetadata id[%s] group[%s]", d.getId(), d.getGroup()));
        }finally {
            iaas.close();
        }
    }
}
