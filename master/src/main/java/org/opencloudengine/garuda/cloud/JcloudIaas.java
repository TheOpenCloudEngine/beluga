package org.opencloudengine.garuda.cloud;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
* Created by swsong on 2015. 7. 15..
*/
public abstract class JcloudIaas implements Iaas {
    private static final Logger logger = LoggerFactory.getLogger(JcloudIaas.class);
    private ComputeServiceContext context;
    private ComputeService computeService;

    public JcloudIaas(String providerType, String accessKey, String secretKey, Properties overrides) {

        context = ContextBuilder.newBuilder(providerType)
                .credentials(accessKey, secretKey)
                .overrides(overrides)
                .modules(ImmutableSet.<Module> of(new SLF4JLoggingModule(),
                        new SshjSshClientModule()))
                .buildView(ComputeServiceContext.class);
        computeService = context.getComputeService();

        Set<? extends Hardware> profiles = computeService.listHardwareProfiles();
        for(Hardware h : profiles) {
            logger.debug("###### {}", h);
        }
    }

    private Template initTemplate(InstanceRequest request) {
        TemplateBuilder templateBuilder = computeService.templateBuilder();
        templateBuilder.hardwareId(request.getInstanceType());
        templateBuilder.imageId(request.getImageId());
        Template template = templateBuilder.build();
        initTemplateOptions(request, template.getOptions());
        return template;
    }

    protected abstract void initTemplateOptions(InstanceRequest request, TemplateOptions templateOptions);


    @Override
    public List<CommonInstance> launchInstance(InstanceRequest request, String name, int scale) {
        Template template = initTemplate(request);
        try {
            computeService.createNodesInGroup(name, scale, template);
        } catch (RunNodesException e) {

        }

        return null;
    }

    @Override
    public void terminateInstance(String id) {

    }

    public ComputeService computeService() {
        return computeService;
    }

    public void close() {
        context.close();
    }

}
