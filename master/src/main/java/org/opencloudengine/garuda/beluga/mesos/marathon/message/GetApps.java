package org.opencloudengine.garuda.beluga.mesos.marathon.message;

import org.opencloudengine.garuda.beluga.mesos.marathon.model.App;

import java.util.List;

/**
 * Created by swsong on 2015. 8. 9..
 */

public class GetApps {
    private List<App> apps;

    public List<App> getApps() {
        return apps;
    }

    public void setApps(List<App> apps) {
        this.apps = apps;
    }
}
