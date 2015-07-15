package org.opencloudengine.garuda.cloud.controller.domain;

/**
 * Created by swsong on 2015. 7. 15..
 */
public class IaasProviderTest {

    public void testEC2() {
        IaasProvider ec2Provider = new IaasProvider();
        ec2Provider.setClassName("org.apache.stratos.org.opencloudengine.cloud.controller.iaases.ec2.EC2Iaas");
    }
}
