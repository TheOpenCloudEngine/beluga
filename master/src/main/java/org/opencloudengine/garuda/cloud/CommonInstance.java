package org.opencloudengine.garuda.cloud;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Tag;

/**
 * Created by swsong on 2015. 8. 3..
 */
public class CommonInstance {

    private String instanceId;
    private String name;
    private String publicIpAddress;
    private String privateIpAddress;
    private String state;
    private Object instance;

    public CommonInstance(String instanceId) {
        this.instanceId = instanceId;
        state = "not-exist";
    }

    public CommonInstance(Object instance) {
        update(instance);
    }

    public void update(Object instance) {
        if (instance instanceof Instance) {
            Instance i = (Instance) instance;
            instanceId = i.getInstanceId();
            publicIpAddress = i.getPublicIpAddress();
            privateIpAddress = i.getPrivateIpAddress();
            for (Tag tag : i.getTags()) {
                if (tag.getKey().equalsIgnoreCase("name")) {
                    this.name = tag.getValue();
                    break;
                }
            }
            state = i.getState().getName();
        }
        this.instance = instance;
    }

    public <T> T as(Class<T> t) {
        return (T) instance;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getName() {
        return name;
    }

    public String getPublicIpAddress() {
        return publicIpAddress;
    }

    public String getPrivateIpAddress() {
        return privateIpAddress;
    }

    public String getState() {
        return state;
    }

    @Override
    public String toString() {
        return String.format("CommonInstance id[%s] %s", instanceId, instance.toString());
    }
}
