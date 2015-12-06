package org.opencloudengine.garuda.beluga.watcher;

/**
 * Created by swsong on 2015. 11. 27..
 */
public class ContainerUsage {

    private String containerName;
    private String appId;
    private String image;
    private String mesosTaskId;
    private float loadAverage;

    public ContainerUsage(String containerName, String appId, String image, String mesosTaskId, float loadAverage) {
        this.containerName = containerName;
        this.appId = appId;
        this.image = image;
        this.mesosTaskId = mesosTaskId;
        this.loadAverage = loadAverage;
    }

    public String getContainerName() {
        return containerName;
    }

    public String getAppId() {
        return appId;
    }

    public String getImage() {
        return image;
    }

    public String getMesosTaskId() {
        return mesosTaskId;
    }

    public float getLoadAverage() {
        return loadAverage;
    }
}
