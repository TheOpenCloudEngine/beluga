package org.opencloudengine.cloud.garuda.master;

import java.util.Vector;

public abstract class AbstractLoadBalancerController {

    public abstract void startLoadBalancer() throws Exception;

    public abstract void reconfigure() throws Exception;

    public abstract void stopLoadBalancer() throws Exception;

    public abstract void generateConfiguration(Vector<Backend> backends)
            throws Exception;

}
