package org.opencloudengine.garuda.beluga.action.serviceApp;

import org.opencloudengine.garuda.beluga.action.ActionRequest;
import org.opencloudengine.garuda.beluga.action.RunnableAction;

import java.util.Map;

/**
 * 주의 : 한번 띄우면 변경하지 않는다.
 * 변경시 새로운 인스턴스가 시작되므로, DB의 경우 데이터가 모두 날아간다.
 * Created by swsong on 2015. 8. 6..
 */
public class DeployDockerImageActionRequest extends ActionRequest {

    private String clusterId;
    private String appId;
    private String imageName;
    private String imageTag;
    private Integer port;
    private Float cpus;
    private Float memory;
    private Integer scale;
    private Map<String, Object> env;

    public DeployDockerImageActionRequest(String clusterId, String appId, String imageName, Integer port, Float cpus, Float memory, Integer scale, Map<String, Object> env) {
        this.clusterId = clusterId;
        this.appId = appId;
        this.imageName = imageName;
        this.port = port;
        this.cpus = cpus;
        this.memory = memory;
        this.scale = scale;
        this.env = env;
    }

    public String getClusterId() {
        return clusterId;
    }

    public String getAppId() {
        return appId;
    }

    public String getImageName() {
        return imageName;
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

    @Override
    public boolean compareUnique(ActionRequest other) {
        if(other instanceof DeployDockerImageActionRequest) {
            return clusterId.equalsIgnoreCase(((DeployDockerImageActionRequest) other).clusterId) && appId.equalsIgnoreCase(((DeployDockerImageActionRequest) other).appId);
        } else {
            return false;
        }
    }

    @Override
    public RunnableAction createAction() {
        return new DeployDockerImageAction(this);
    }
}
