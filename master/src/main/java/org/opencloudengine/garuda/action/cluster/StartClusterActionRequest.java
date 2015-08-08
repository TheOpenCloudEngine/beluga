package org.opencloudengine.garuda.action.cluster;

import org.opencloudengine.garuda.action.ActionRequest;
import org.opencloudengine.garuda.action.RunnableAction;

/**
 * Created by swsong on 2015. 8. 6..
 */
public class StartClusterActionRequest extends ActionRequest {
    private String clusterId;

    public StartClusterActionRequest(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getClusterId() {
        return clusterId;
    }

    @Override
    public boolean compareUnique(ActionRequest other) {
        if(other instanceof StartClusterActionRequest) {
            return clusterId.equalsIgnoreCase(((StartClusterActionRequest) other).clusterId);
        } else {
            return false;
        }
    }

    @Override
    public RunnableAction createAction() {
        return new StartClusterAction(this);
    }
}
