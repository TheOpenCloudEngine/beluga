package org.opencloudengine.garuda.beluga.action.cluster;

import org.opencloudengine.garuda.beluga.action.ActionRequest;
import org.opencloudengine.garuda.beluga.action.RunnableAction;

/**
 * Created by swsong on 2015. 11. 27..
 */
public class RemoveSlaveNodeActionRequest extends ActionRequest {
    private String clusterId;
    private String instanceId;


    public RemoveSlaveNodeActionRequest(String clusterId, String instanceId) {
        this.clusterId = clusterId;
        this.instanceId = instanceId;
    }

    @Override
    public boolean compareUnique(ActionRequest other) {
        if(other instanceof RemoveSlaveNodeActionRequest) {
            return clusterId.equalsIgnoreCase(((RemoveSlaveNodeActionRequest) other).clusterId);
        } else {
            return false;
        }
    }

    @Override
    public RunnableAction createAction() {
        return new RemoveSlaveNodeAction(this);
    }

    public String getClusterId() {
        return clusterId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    @Override
    public String toString() {
        return String.format("%s:%s:%s", getClass().getSimpleName(), clusterId, instanceId);
    }
}
