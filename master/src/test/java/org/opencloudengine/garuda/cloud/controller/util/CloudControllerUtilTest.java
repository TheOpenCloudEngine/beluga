package org.opencloudengine.garuda.cloud.controller.util;

import org.junit.Test;
import org.opencloudengine.garuda.cloud.controller.domain.IaasProvider;
import org.opencloudengine.garuda.cloud.controller.domain.MemberContext;
import org.opencloudengine.garuda.cloud.controller.exception.InvalidIaasProviderException;
import org.opencloudengine.garuda.cloud.controller.iaases.Iaas;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by swsong on 2015. 7. 15..
 */
public class CloudControllerUtilTest {

    @Test
    public void createEC2() throws InvalidIaasProviderException {

        String identity = "AKIAJ34HUGNI2HQI53UQ";
        String credential = "l2GxfMJAF4/9eJJTMWoYkuzl6ge64oI4dftWtOX5";

        Map<String, String> properties = new HashMap<>();
        properties.put("instanceType", "t2.micro");
        properties.put("availabilityZone", "ap-northeast-1a");

        IaasProvider ec2Provider = new IaasProvider();
        ec2Provider.setClassName("org.opencloudengine.garuda.org.opencloudengine.cloud.controller.iaases.ec2.EC2Iaas");
        ec2Provider.setProperties(properties);
        ec2Provider.setName("myname");
        ec2Provider.setIdentity(identity);
        ec2Provider.setCredential(credential);
        ec2Provider.setType("ec2");
//        ec2Provider.setImage("ami-936d9d93");
        ec2Provider.setProvider("amazon");
        Iaas iaas = ec2Provider.getIaas();
        MemberContext memberContext= new MemberContext("clust1");
        memberContext.setInstanceId("inst1");
        iaas.startInstance(memberContext, null);
        System.out.println(iaas);

    }
}
