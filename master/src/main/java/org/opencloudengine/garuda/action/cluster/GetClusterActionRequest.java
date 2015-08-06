package org.opencloudengine.garuda.action.cluster;

import org.opencloudengine.garuda.action.ActionRequest;
import org.opencloudengine.garuda.action.RunnableAction;

/**
 * Created by swsong on 2015. 8. 6..
 */
public class GetClusterActionRequest extends ActionRequest {

    private String clusterId;

    public GetClusterActionRequest(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getClusterId() {
        return clusterId;
    }

    @Override
    public boolean compareUnique(ActionRequest other) {
        if(other instanceof GetClusterActionRequest) {
            return clusterId.equalsIgnoreCase(((GetClusterActionRequest) other).clusterId);
        } else {
            return false;
        }
    }

    @Override
    public RunnableAction createAction() {
        return new GetClusterAction(this);
    }
}
