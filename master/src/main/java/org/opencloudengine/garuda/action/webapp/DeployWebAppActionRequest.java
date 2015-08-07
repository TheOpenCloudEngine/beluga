package org.opencloudengine.garuda.action.webapp;

import org.opencloudengine.garuda.action.ActionRequest;
import org.opencloudengine.garuda.action.RunnableAction;

/**
 * Created by swsong on 2015. 8. 6..
 */
public class DeployWebAppActionRequest extends ActionRequest {

    private String appId;
    private String webAppFile;
    private String webAppType;

    public DeployWebAppActionRequest(String appId, String webAppFile, String webAppType) {
        this.appId = appId;
        this.webAppFile = webAppFile;
        this.webAppType = webAppType;
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

    @Override
    public boolean compareUnique(ActionRequest other) {
        if(other instanceof DeployWebAppActionRequest) {
            return appId.equalsIgnoreCase(((DeployWebAppActionRequest) other).appId);
        } else {
            return false;
        }
    }

    @Override
    public RunnableAction createAction() {
        return new DeployWebAppAction(this);
    }
}
