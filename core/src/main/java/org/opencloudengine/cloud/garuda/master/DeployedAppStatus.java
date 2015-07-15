package org.opencloudengine.cloud.garuda.master;

public class DeployedAppStatus {

    private String appName;
    private String bundleName;
    private String version;
    private long deploymentTime;
    private final long updateTimestamp;

    public DeployedAppStatus() {
        appName = "";
        bundleName = "";
        version = "";
        deploymentTime = 0;
        updateTimestamp = System.currentTimeMillis();
    }

    public String getAppName() {
        return appName;
    }

    public String getBundleName() {
        return bundleName;
    }

    public String getVersion() {
        return version;
    }

    public long getDeploymentTime() {
        return deploymentTime;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setBundleName(String bundleName) {
        this.bundleName = bundleName;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setDeploymentTime(long deploymentTime) {
        this.deploymentTime = deploymentTime;
    }

    @Override
    public String toString() {
        return appName + " " + bundleName + " " + version + " "
                + deploymentTime;
    }

    public long getTimeSinceUpdate() {
        return System.currentTimeMillis() - updateTimestamp;
    }
}
