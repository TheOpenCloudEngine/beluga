package org.opencloudengine.garuda.cloud;

import org.jclouds.compute.options.TemplateOptions;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class OpenstackIaaS extends JcloudIaaS {
    public OpenstackIaaS(String providerType, String accessKey, String secretKey, Properties overrides) {
        super(providerType, accessKey, secretKey, overrides);
    }

    @Override
    protected void initTemplateOptions(InstanceRequest request, TemplateOptions templateOptions) {

    }

    @Override
    public List<CommonInstance> getRunningInstances(Collection<CommonInstance> instanceList) {
        return null;
    }

    @Override
    public void waitUntilInstancesReady(Collection<CommonInstance> instanceList) {

    }

    @Override
    public void terminateInstances(Collection<CommonInstance> list) {

    }

    @Override
    public void terminateInstanceList(Collection<String> instanceIdList) {

    }

    @Override
    public String provider() {
        return null;
    }
}
