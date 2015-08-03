//package org.opencloudengine.garuda.jcloud;
//
//import com.google.common.collect.ImmutableSet;
//import com.google.inject.Module;
//import org.jclouds.ContextBuilder;
//import org.jclouds.compute.ComputeService;
//import org.jclouds.compute.ComputeServiceContext;
//import org.jclouds.compute.RunNodesException;
//import org.jclouds.compute.domain.*;
//import org.jclouds.compute.options.TemplateOptions;
//import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
//import org.jclouds.sshj.config.SshjSshClientModule;
//import org.opencloudengine.garuda.cloud.InstanceRequest;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.Properties;
//import java.util.Set;
//
///**
// * Created by swsong on 2015. 7. 15..
// */
//public abstract class Iaas {
//    private static final Logger logger = LoggerFactory.getLogger(Iaas.class);
//    private ComputeServiceContext context;
//    private ComputeService computeService;
//
//    public Iaas(String providerType, String accessKey, String secretKey, Properties overrides) {
//
//        context = ContextBuilder.newBuilder(providerType)
//                .credentials(accessKey, secretKey)
//                .overrides(overrides)
//                .modules(ImmutableSet.<Module> of(new SLF4JLoggingModule(),
//                        new SshjSshClientModule()))
//                .buildView(ComputeServiceContext.class);
//        computeService = context.getComputeService();
//
//        Set<? extends Hardware> profiles = computeService.listHardwareProfiles();
//        for(Hardware h : profiles) {
//            logger.debug("###### {}", h);
//        }
//    }
//
//    private Template initTemplate(InstanceRequest request) {
//        TemplateBuilder templateBuilder = computeService.templateBuilder();
////        templateBuilder.os64Bit(true).osFamily(OsFamily.UBUNTU).osVersionMatches("14.04");
//
////        templateBuilder.locationId("us-west-2").imageId("us-west-2/ami-5189a661");//.hardwareId(InstanceType.T2_MICRO);
////        templateBuilder.hardwareId(request.getInstanceType());
////        templateBuilder.hardwareId(InstanceType.M3_MEDIUM);
////        templateBuilder.imageId(request.getImageId()); //"ap-northeast-1/ami-936d9d93"
////        templateBuilder.osFamily(OsFamily.UBUNTU)
////                .osVersionMatches("14.04").os64Bit(true).locationId("ap-northeast-1").hardwareId(request.getInstanceType());
//        Template template = templateBuilder.build();
//        // specify your own groups which already have the correct rules applied
//        initTemplateOptions(request, template.getOptions());
////        return template;
//        return null;
//    }
//
//    protected abstract void initTemplateOptions(InstanceRequest request, TemplateOptions templateOptions);
//
//
//    public Set<? extends NodeMetadata> launchInstance(InstanceRequest request, String prefixId, int scale) throws RunNodesException {
//        Template template = initTemplate(request);
//        return computeService.createNodesInGroup(prefixId, scale, template);
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
