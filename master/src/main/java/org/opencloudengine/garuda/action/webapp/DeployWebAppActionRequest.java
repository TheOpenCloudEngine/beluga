package org.opencloudengine.garuda.action.webapp;

import org.opencloudengine.garuda.action.ActionRequest;
import org.opencloudengine.garuda.action.RunnableAction;

/**
 * Created by swsong on 2015. 8. 6..
 */
public class DeployWebAppActionRequest extends ActionRequest {

    private String clusterId;
    private String appId;
    private String webAppFile;
    private String webAppType;
    private int webAppPort;
    private float cpus;
    private float memory;
    private int scale;

    public DeployWebAppActionRequest(String clusterId, String appId, String webAppFile, String webAppType, int webAppPort, float cpus, float memory, int scale) {
        this.clusterId = clusterId;
        this.appId = appId;
        this.webAppFile = webAppFile;
        this.webAppType = webAppType;
        this.webAppPort = webAppPort;
        this.cpus = cpus;
        this.memory = memory;
        this.scale = scale;
    }

    public String getClusterId() {
        return clusterId;
    }

    public String getAppId() {
        return appId;
    }

    public String getWebAppFile() {
        return webAppFile;
    }

    public String getWebAppType() {
        return webAppType;
    }

    public int getWebAppPort() {
        return webAppPort;
    }

    public float getCpus() {
        return cpus;
    }

    public float getMemory() {
        return memory;
    }

    public int getScale() {
        return scale;
    }

    @Override
    public boolean compareUnique(ActionRequest other) {
        if(other instanceof DeployWebAppActionRequest) {
            return clusterId.equalsIgnoreCase(((DeployWebAppActionRequest) other).clusterId) && appId.equalsIgnoreCase(((DeployWebAppActionRequest) other).appId);
        } else {
            return false;
        }
    }

    @Override
    public RunnableAction createAction() {
        return new DeployWebAppAction(this);
    }
}
