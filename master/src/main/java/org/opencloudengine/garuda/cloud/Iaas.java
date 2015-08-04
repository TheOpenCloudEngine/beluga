package org.opencloudengine.garuda.cloud;

import java.util.Collection;
import java.util.List;

/**
 * Created by swsong on 2015. 8. 3..
 */
public interface IaaS {

    public List<CommonInstance> launchInstance(InstanceRequest request, String name, int scale);

    public void terminateInstance(String id);

    public List<CommonInstance> getRunningInstances(Collection<CommonInstance> instanceList);

    public void waitUntilInstancesReady(Collection<CommonInstance> instanceList);

    public void terminateInstances(Collection<CommonInstance> instanceList);

    public void terminateInstanceList(Collection<String> instanceIdList) ;

    public String provider();

    public void close();
}
