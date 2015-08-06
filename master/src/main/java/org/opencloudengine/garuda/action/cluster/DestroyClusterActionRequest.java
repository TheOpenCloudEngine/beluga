package org.opencloudengine.garuda.action.cluster;

import org.opencloudengine.garuda.action.ActionRequest;
import org.opencloudengine.garuda.action.RunnableAction;

/**
 * Created by swsong on 2015. 8. 6..
 */
public class DestroyClusterActionRequest extends ActionRequest {
    private String clusterId;

    public DestroyClusterActionRequest(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getClusterId() {
        return clusterId;
    }

    @Override
    public boolean compareUnique(ActionRequest other) {
        if(other instanceof DestroyClusterActionRequest) {
            return clusterId.equalsIgnoreCase(((DestroyClusterActionRequest) other).clusterId);
        } else {
            return false;
        }
    }

    @Override
    public RunnableAction createAction() {
        return new DestroyClusterAction(this);
    }
}
