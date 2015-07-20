package org.opencloudengine.garuda.cloud;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.sshj.config.SshjSshClientModule;

import java.util.Properties;
import java.util.Set;

/**
 * Created by swsong on 2015. 7. 15..
 */
public abstract class Iaas {

    private ComputeServiceContext context;
    private ComputeService computeService;

    public Iaas(String providerType, String accessKey, String secretKey) {

        Properties overrides = new Properties();
        context = ContextBuilder.newBuilder(providerType)
                .credentials(accessKey, secretKey)
                .overrides(overrides)
                .modules(ImmutableSet.<Module> of(new SLF4JLoggingModule(),
                        new SshjSshClientModule()))
                .buildView(ComputeServiceContext.class);
        computeService = context.getComputeService();
    }

    private Template initTemplate(InstanceRequest request) {
        TemplateBuilder templateBuilder = computeService.templateBuilder();
        templateBuilder.hardwareId(request.getInstanceType());
        templateBuilder.imageId(request.getImageId()); //"ap-northeast-1/ami-936d9d93"
        Template template = templateBuilder.build();
        // specify your own groups which already have the correct rules applied
        initTemplateOptions(request, template.getOptions());
        return template;
    }

    protected abstract void initTemplateOptions(InstanceRequest request, TemplateOptions templateOptions);


    public Set<? extends NodeMetadata> launchInstance(InstanceRequest request, String prefixId, int scale) throws RunNodesException {
        Template template = initTemplate(request);
        return computeService.createNodesInGroup(prefixId, scale, template);
    }

    public ComputeService computeService() {
        return computeService;
    }

    public void close() {
        context.close();
    }

}
