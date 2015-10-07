package org.opencloudengine.garuda.beluga.action.cluster;

import org.opencloudengine.garuda.beluga.action.ActionRequest;
import org.opencloudengine.garuda.beluga.action.RunnableAction;

/**
 * Created by swsong on 2015. 8. 6..
 */
public class RestartClusterActionRequest extends ActionRequest {
    private String clusterId;

    public RestartClusterActionRequest(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getClusterId() {
        return clusterId;
    }

    @Override
    public boolean compareUnique(ActionRequest other) {
        if(other instanceof RestartClusterActionRequest) {
            return clusterId.equalsIgnoreCase(((RestartClusterActionRequest) other).clusterId);
        } else {
            return false;
        }
    }

    @Override
    public RunnableAction createAction() {
        return new RestartClusterAction(this);
    }
}
