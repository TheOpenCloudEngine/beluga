package org.opencloudengine.garuda.cloud;

import com.amazonaws.services.ec2.model.Instance;

/**
 * Created by swsong on 2015. 8. 3..
 */
public class CommonInstance {

    private String instanceId;
    private String publicIpAddress;
    private String privateIpAddress;

    private Object instance;

    public CommonInstance(Object instance) {
        if(instance instanceof Instance) {
            Instance i = (Instance) instance;
            instanceId = i.getInstanceId();
            publicIpAddress = i.getPublicIpAddress();
            privateIpAddress = i.getPrivateIpAddress();
        }
        this.instance = instance;
    }

    public <T> T as(Class<T> t) {
        return (T) instance;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getPublicIpAddress() {
        return publicIpAddress;
    }

    public String getPrivateIpAddress() {
        return privateIpAddress;
    }

    @Override
    public String toString() {
        return String.format("CommonInstance id[%s] %s", instanceId, instance.toString());
    }
}
