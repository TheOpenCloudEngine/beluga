package org.opencloudengine.garuda.beluga.cloud;

import org.jclouds.compute.options.TemplateOptions;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class OpenstackIaas extends JcloudIaas {
    public OpenstackIaas(String endPoint, String accessKey, String secretKey, Properties overrides) {
        super("openstack", endPoint, accessKey, secretKey, overrides);
    }

    @Override
    protected void initTemplateOptions(InstanceRequest request, TemplateOptions templateOptions) {

    }

    @Override
    public void updateInstancesInfo(List<CommonInstance> instanceList) {

    }

    @Override
    public List<CommonInstance> getInstances(Collection<String> instanceList) {
        return null;
    }

    @Override
    public void waitUntilInstancesRunning(Collection<CommonInstance> instanceList) {

    }

    @Override
    public void waitUntilInstancesStopped(Collection<CommonInstance> instanceList) {

    }

    @Override
    public void terminateInstances(Collection<String> instanceIdList) {

    }

    @Override
    public void stopInstances(Collection<String> instanceIdList) {

    }

    @Override
    public void startInstances(Collection<String> instanceIdList) {

    }

    @Override
    public void rebootInstances(Collection<String> instanceIdList) {

    }

    @Override
    public String provider() {
        return IaasProvider.OPENSTACK_TYPE;
    }
}
