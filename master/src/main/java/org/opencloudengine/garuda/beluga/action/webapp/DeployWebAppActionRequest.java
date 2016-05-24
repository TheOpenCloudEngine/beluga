package org.opencloudengine.garuda.beluga.action.webapp;

import org.opencloudengine.garuda.beluga.action.ActionRequest;
import org.opencloudengine.garuda.beluga.action.RunnableAction;

import java.util.List;
import java.util.Map;

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
    private Map<String, Object> env;
    private Boolean isUpdate;


    public DeployWebAppActionRequest(String clusterId, String appId, Integer revision, List<WebAppContextFile> webAppFileList, String webAppType, Integer port, Float cpus, Float memory, Integer scale, Map<String, Object> env) {
        this.clusterId = clusterId;
        this.appId = appId;
        this.revision = revision;
        this.webAppFileList = webAppFileList;
        this.webAppType = webAppType;
        this.port = port;
        this.cpus = cpus;
        this.memory = memory;
        this.scale = scale;
        this.env = env;
        isUpdate = false;
    }

    public DeployWebAppActionRequest(String clusterId, String appId, Integer revision, List<WebAppContextFile> webAppFileList, String webAppType, Integer port, Float cpus, Float memory, Integer scale, Map<String, Object> env, Boolean isUpdate) {
        this.clusterId = clusterId;
        this.appId = appId;
        this.revision = revision;
        this.webAppFileList = webAppFileList;
        this.webAppType = webAppType;
        this.port = port;
        this.cpus = cpus;
        this.memory = memory;
        this.scale = scale;
        this.env = env;
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

    public Map<String, Object> getEnv() {
        return env;
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
