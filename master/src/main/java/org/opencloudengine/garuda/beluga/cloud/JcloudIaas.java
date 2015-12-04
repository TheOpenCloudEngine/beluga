//package org.opencloudengine.garuda.beluga.cloud;
//
//import com.google.common.collect.ImmutableSet;
//import com.google.inject.Module;
//import org.jclouds.ContextBuilder;
//import org.jclouds.compute.ComputeService;
//import org.jclouds.compute.ComputeServiceContext;
//import org.jclouds.compute.RunNodesException;
//import org.jclouds.compute.domain.Hardware;
//import org.jclouds.compute.domain.NodeMetadata;
//import org.jclouds.compute.domain.Template;
//import org.jclouds.compute.domain.TemplateBuilder;
//import org.jclouds.compute.options.TemplateOptions;
//import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
//import org.jclouds.openstack.cinder.v1.CinderApi;
//import org.jclouds.openstack.cinder.v1.CinderApiMetadata;
//import org.jclouds.openstack.keystone.v2_0.KeystoneApi;
//import org.jclouds.sshj.config.SshjSshClientModule;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Properties;
//import java.util.Set;
//
///**
//* Created by swsong on 2015. 7. 15..
//*/
//public abstract class JcloudIaas implements Iaas {
//    private static final Logger logger = LoggerFactory.getLogger(JcloudIaas.class);
//    private ComputeServiceContext context;
//    private ComputeService computeService;
//    private String endPoint;
//    public JcloudIaas(String providerType, String endPoint, String accessKey, String secretKey, Properties overrides) {
//
//        context = ContextBuilder.newBuilder(providerType)
//                .credentials(accessKey, secretKey)
//                .overrides(overrides)
//                .modules(ImmutableSet.<Module> of(new SLF4JLoggingModule(),
//                        new SshjSshClientModule()))
//                .buildView(ComputeServiceContext.class);
//
//        computeService = context.getComputeService();
//    }
//
//    private Template initTemplate(InstanceRequest request) {
//        TemplateBuilder templateBuilder = computeService.templateBuilder();
//        templateBuilder.hardwareId(request.getInstanceType());
//        templateBuilder.imageId(request.getImageId());
//        Template template = templateBuilder.build();
//        initTemplateOptions(request, template.getOptions());
//        return template;
//    }
//
//    protected abstract void initTemplateOptions(InstanceRequest request, TemplateOptions templateOptions);
//
//
//    @Override
//    public List<CommonInstance> launchInstance(InstanceRequest request, String name, int scale, int startIndex) {
//
//        //instanceType을 가리킴.
//        Set<? extends Hardware> profiles = computeService.listHardwareProfiles();
//        for(Hardware h : profiles) {
//            logger.debug("###### {}", h);
//        }
//
//        Template template = initTemplate(request);
//        try {
//            Set<? extends NodeMetadata> intanceSet = null;
//            if (scale > 0) {
//                intanceSet = computeService.createNodesInGroup(name, scale, template);
//            }
//
//        } catch (RunNodesException e) {
//
//        }
//        String endpoint = "http://10.0.1.251:5000/v2.0";
//        String tenantName = "demo";
//        String userName = "demo";
//        String identity = tenantName + ":"  + userName;
//        String password = "demopass";
//        String provider = "openstack-nova";
//        Iterable<Module> modules = ImmutableSet.<Module>of(new SLF4JLoggingModule());
//
//
//
//        List<CommonInstance> newInstances = new ArrayList<CommonInstance>();
//
//        String clusterId = request.getClusterId();
////        RunInstancesResult runInstancesResult = null;
////        if (scale > 0) {
////            RunInstancesRequest runRequest = new RunInstancesRequest();
////            runRequest.setImageId(request.getImageId());
////            runRequest.setInstanceType(request.getInstanceType());
////            runRequest.setKeyName(request.getKeyPair());
////            runRequest.setMaxCount(scale);
////            runRequest.setMinCount(scale);
////            runRequest.setSecurityGroups(request.getGroups());
////            EbsBlockDevice ebs = new EbsBlockDevice().withVolumeSize(request.getVolumeSize()).withVolumeType(VolumeType.Gp2);
////            BlockDeviceMapping m = new BlockDeviceMapping().withDeviceName(DEVICE_NAME).withEbs(ebs);
////            List<BlockDeviceMapping> blockDeviceMappingList = new ArrayList<>();
////            blockDeviceMappingList.add(m);
////            runRequest.setBlockDeviceMappings(blockDeviceMappingList);
////            runInstancesResult = client.runInstances(runRequest);
////        }
////
////        if (runInstancesResult != null) {
////            if(name != null) {
////                //tag request전에 1초정도 대기.
////                try {
////                    Thread.sleep(1000);
////                } catch (InterruptedException ignore) {
////                }
////
////                for (Instance instance : runInstancesResult.getReservation().getInstances()) {
////                    CreateTagsRequest createTagsRequest = new CreateTagsRequest();
////                    String tagName = null;
////                    if (startIndex > 1) {
////                        tagName = String.format("%s/%s-%d", clusterId, name, startIndex);
////                    } else {
////                        tagName = String.format("%s/%s", clusterId, name);
////                    }
////
////                    createTagsRequest.withResources(instance.getInstanceId()).withTags(new Tag("Name", tagName));
////                    client.createTags(createTagsRequest);
////                    newInstances.add(new CommonInstance(instance));
////                    startIndex++;
////                }
////            }
////        }
//
//        return newInstances;
//
//
//
////        return null;
//    }
//
//    @Override
//    public void terminateInstance(String id) {
//
//    }
//
//    public ComputeService computeService() {
//        return computeService;
//    }
//
//    public void close() {
//        context.close();
//    }
//
//}
