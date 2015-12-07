package org.opencloudengine.garuda.beluga.cloud;

import com.amazonaws.services.ec2.model.GroupIdentifier;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Tag;
import com.google.common.collect.Multimap;
import org.jclouds.openstack.nova.v2_0.domain.Address;
import org.jclouds.openstack.nova.v2_0.domain.Server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    private String region;

    private String instanceType;
    private List<String> groupList;
    private IaasSpec iaasSpec;

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

            instanceType = i.getInstanceType();
            iaasSpec = IaasSpec.getSpec(IaasSpec.EC2_TYPE, instanceType);
            groupList = new ArrayList<>();
            for(GroupIdentifier id : i.getSecurityGroups()) {
                groupList.add(id.getGroupName());
            }
        } else if(instance instanceof Server) {
            Server s = (Server) instance;
            instanceId = s.getId();
            name = s.getName();
            Multimap<String, Address> addressMultimap = s.getAddresses();
            Collection<Address> addressCollection = addressMultimap.values();
            if(addressCollection.size() > 0) {
                privateIpAddress = addressCollection.iterator().next().getAddr();
                //FIXME
                // floating ip 를 연결해준다
                publicIpAddress = privateIpAddress;
            }
            state = s.getStatus().value();
            instanceType = s.getFlavor().getId();
            iaasSpec = IaasSpec.getSpec(IaasSpec.OPENSTACK_TYPE, instanceType);
            groupList = new ArrayList<>();
            //FIXME
            // 실제 그룹을 넣어준다.
            groupList.add("default");
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

    public String getInstanceType() {
        return instanceType;
    }

    public List<String> getGroupList() {
        return groupList;
    }

    public IaasSpec getIaasSpec() {
        return iaasSpec;
    }

    @Override
    public String toString() {
        return String.format("CommonInstance id[%s] %s", instanceId, instance.toString());
    }
}
