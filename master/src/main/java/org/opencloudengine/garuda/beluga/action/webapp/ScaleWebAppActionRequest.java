//package org.opencloudengine.garuda.action.webapp;
//
//import org.opencloudengine.garuda.action.ActionRequest;
//import org.opencloudengine.garuda.action.RunnableAction;
//
///**
// * Created by swsong on 2015. 8. 6..
// */
//public class ScaleWebAppActionRequest extends ActionRequest {
//
//    private String clusterId;
//    private String appId;
//    private Integer scale;
//
//    public ScaleWebAppActionRequest(String clusterId, String appId, Integer scale) {
//        this.clusterId = clusterId;
//        this.appId = appId;
//        this.scale = scale;
//    }
//
//    public String getClusterId() {
//        return clusterId;
//    }
//
//    public void setClusterId(String clusterId) {
//        this.clusterId = clusterId;
//    }
//
//    public String getAppId() {
//        return appId;
//    }
//
//    public void setAppId(String appId) {
//        this.appId = appId;
//    }
//
//    public Integer getScale() {
//        return scale;
//    }
//
//    public void setScale(Integer scale) {
//        this.scale = scale;
//    }
//
//    @Override
//    public boolean compareUnique(ActionRequest other) {
//        if(other instanceof ScaleWebAppActionRequest) {
//            return clusterId.equalsIgnoreCase(((ScaleWebAppActionRequest) other).clusterId) && appId.equalsIgnoreCase(((ScaleWebAppActionRequest) other).appId);
//        } else {
//            return false;
//        }
//    }
//
//    @Override
//    public RunnableAction createAction() {
//        return new ScaleWebAppAction(this);
//    }
//}
