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
    private Float cpus;
    private Float memory;
    private Integer scale;
    private Boolean isUpdate;


    public DeployWebAppActionRequest(String clusterId, String appId, String webAppFile, String webAppType, Float cpus, Float memory, Integer scale) {
        this.clusterId = clusterId;
        this.appId = appId;
        this.webAppFile = webAppFile;
        this.webAppType = webAppType;
        this.cpus = cpus;
        this.memory = memory;
        this.scale = scale;
        isUpdate = false;
    }

    public DeployWebAppActionRequest(String clusterId, String appId, String webAppFile, String webAppType, Float cpus, Float memory, Integer scale, Boolean isUpdate) {
        this.clusterId = clusterId;
        this.appId = appId;
        this.webAppFile = webAppFile;
        this.webAppType = webAppType;
        this.cpus = cpus;
        this.memory = memory;
        this.scale = scale;
        this.isUpdate = isUpdate;
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

    public Float getCpus() {
        return cpus;
    }

    public Float getMemory() {
        return memory;
    }

    public Integer getScale() {
        return scale;
    }

    /**
     * 이미 실행된 app에 설정을 변경하는 업데이트 작업인지 여부.
     * */
    public Boolean getIsUpdate() {
        return isUpdate;
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
