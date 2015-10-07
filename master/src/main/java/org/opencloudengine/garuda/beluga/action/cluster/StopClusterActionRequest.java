package org.opencloudengine.garuda.beluga.action.cluster;

import org.opencloudengine.garuda.beluga.action.ActionRequest;
import org.opencloudengine.garuda.beluga.action.RunnableAction;

/**
 * Created by swsong on 2015. 8. 6..
 */
public class StopClusterActionRequest extends ActionRequest {
    private String clusterId;

    public StopClusterActionRequest(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getClusterId() {
        return clusterId;
    }

    @Override
    public boolean compareUnique(ActionRequest other) {
        if(other instanceof StopClusterActionRequest) {
            return clusterId.equalsIgnoreCase(((StopClusterActionRequest) other).clusterId);
        } else {
            return false;
        }
    }

    @Override
    public RunnableAction createAction() {
        return new StopClusterAction(this);
    }
}
