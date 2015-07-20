package org.opencloudengine.garuda.cloud;

import org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.ec2.domain.InstanceType;

/**
 * Created by swsong on 2015. 7. 15..
 */
public class EC2Iaas extends Iaas {
    public EC2Iaas(String providerType, String accessKey, String secretKey) {
        super(providerType, accessKey, secretKey);
    }

    @Override
    protected void initTemplateOptions(InstanceRequest request, TemplateOptions templateOptions) {
        templateOptions.as(AWSEC2TemplateOptions.class).securityGroups(request.getGroup()).keyPair(request.getKeyPair());
        templateOptions.as(AWSEC2TemplateOptions.class).mapNewVolumeToDeviceName("/dev/sda1", request.getVolumeSize(), true);
    }

    public static void main(String[] args) throws RunNodesException {
        String providerType = args[0];
        String accessKey = args[1];
        String secretKey = args[2];

        EC2Iaas test = new EC2Iaas(providerType, accessKey, secretKey);
        InstanceRequest request = new InstanceRequest(InstanceType.T2_MICRO, "ap-northeast-1/ami-936d9d93", 10, "default", "aws-garuda");
        test.launchInstance(request, "test", 1);
    }

}
