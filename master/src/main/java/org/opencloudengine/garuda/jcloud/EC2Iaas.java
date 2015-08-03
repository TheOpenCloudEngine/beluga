//package org.opencloudengine.garuda.jcloud;
//
//import org.jclouds.compute.options.TemplateOptions;
//import org.jclouds.ec2.compute.options.EC2TemplateOptions;
//import org.opencloudengine.garuda.cloud.InstanceRequest;
//
//import java.util.Properties;
//
///**
// * Created by swsong on 2015. 7. 15..
// */
//public class EC2Iaas extends Iaas {
//    public EC2Iaas(String providerType, String accessKey, String secretKey, Properties overrides) {
//        super(providerType, accessKey, secretKey, overrides);
//    }
//
//    @Override
//    protected void initTemplateOptions(InstanceRequest request, TemplateOptions templateOptions) {
//        templateOptions.as(EC2TemplateOptions.class).securityGroups(request.getGroup()).keyPair(request.getKeyPair());
//        templateOptions.as(EC2TemplateOptions.class).mapNewVolumeToDeviceName("/dev/sda1", request.getVolumeSize(), true);
//    }
//}
