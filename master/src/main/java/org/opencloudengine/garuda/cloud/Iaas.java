package org.opencloudengine.garuda.cloud;

import java.util.List;

/**
 * Created by swsong on 2015. 8. 3..
 */
public interface IaaS {

    public static final String EC2_PROVIDER = "ec2";
    public static final String OPENSTACK_PROVIDER = "openstack";

    public List<CommonInstance> launchInstance(InstanceRequest request, String prefixId, int scale);

    public void terminateInstance(String id);

    public String provider();


}
