package org.opencloudengine.garuda.action.webapp;

import org.opencloudengine.garuda.action.ActionRequest;
import org.opencloudengine.garuda.action.RunnableAction;

import java.util.List;

/**
 * Created by swsong on 2015. 8. 6..
 */
public class DeployWebAppActionRequest extends ActionRequest {

    private String clusterId;
    private String appId;
    private Integer revision;
    private List<WebAppContextFile> webAppFileList;
    private String webAppType;
    private Integer port;
    private Float cpus;
    private Float memory;
    private Integer scale;
    private Boolean isUpdate;


    public DeployWebAppActionRequest(String clusterId, String appId, Integer revision, List<WebAppContextFile> webAppFileList, String webAppType, Integer port, Float cpus, Float memory, Integer scale) {
        this.clusterId = clusterId;
        this.appId = appId;
        this.revision = revision;
        this.webAppFileList = webAppFileList;
        this.webAppType = webAppType;
        this.port = port;
        this.cpus = cpus;
        this.memory = memory;
        this.scale = scale;
        isUpdate = false;
    }

    public DeployWebAppActionRequest(String clusterId, String appId, Integer revision, List<WebAppContextFile> webAppFileList, String webAppType, Integer port, Float cpus, Float memory, Integer scale, Boolean isUpdate) {
        this.clusterId = clusterId;
        this.appId = appId;
        this.revision = revision;
        this.webAppFileList = webAppFileList;
        this.webAppType = webAppType;
        this.port = port;
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

    public Integer getRevision() {
        return revision;
    }

    public List<WebAppContextFile> getWebAppFileList() {
        return webAppFileList;
    }

    public String getWebAppType() {
        return webAppType;
    }

    public Integer getPort() {
        return port;
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
