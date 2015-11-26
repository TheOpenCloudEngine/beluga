package org.opencloudengine.garuda.beluga.action.cluster;

import org.opencloudengine.garuda.beluga.action.ActionRequest;
import org.opencloudengine.garuda.beluga.action.RunnableAction;

/**
 * Created by swsong on 2015. 11. 27..
 */
public class RemoveSlaveNodeActionRequest extends ActionRequest {
    private String clusterId;
    private Integer decrementSize;


    public RemoveSlaveNodeActionRequest(String clusterId, Integer decrementSize) {
        this.clusterId = clusterId;
        this.decrementSize = decrementSize;
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

    public Integer getDecrementSize() {
        return decrementSize;
    }

    @Override
    public String toString() {
        return String.format("%s:%s:%s", getClass().getSimpleName(), clusterId, decrementSize);
    }
}
