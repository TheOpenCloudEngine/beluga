package org.opencloudengine.garuda.cloud;

import java.util.Collection;
import java.util.List;

/**
 * Created by swsong on 2015. 8. 3..
 */
public interface Iaas {

    public List<CommonInstance> launchInstance(InstanceRequest request, String name, int scale);

    public void terminateInstance(String id);

    public void updateInstancesInfo(List<CommonInstance> instanceList);

    public List<CommonInstance> getInstances(Collection<String> instanceList);

    public void waitUntilInstancesRunning(Collection<CommonInstance> instanceList);

    public void waitUntilInstancesStopped(Collection<CommonInstance> instanceList);

    public void terminateInstances(Collection<String> instanceIdList) ;

    public void stopInstances(Collection<String> instanceIdList) ;

    public void startInstances(Collection<String> instanceIdList) ;

    public void rebootInstances(Collection<String> instanceIdList);

    public String provider();

    public void close();


}
