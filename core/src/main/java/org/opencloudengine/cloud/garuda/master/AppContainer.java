package org.opencloudengine.cloud.garuda.master;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class AppContainer {

    private final HashMap<String, App> apps = new HashMap<String, App>();

    public AppContainer() {

    }

    public void addApp(App app) {
        apps.put(app.getAppName(), app);
    }

    public void removeApp(App app) {
        if (apps.containsKey(app.getAppName())) {
            apps.remove(app.getAppName());
        }
    }

    public void removeApp(String appName) {
        if (apps.containsKey(appName)) {
            apps.remove(appName);
        }
    }

    public App getApp(String appName) {
        return apps.get(appName);
    }

    public Collection<App> getApps() {
        return apps.values();
    }

    public Set<String> getAppNames() {
        return apps.keySet();
    }
}
