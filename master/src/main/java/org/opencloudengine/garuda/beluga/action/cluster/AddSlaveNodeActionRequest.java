package org.opencloudengine.garuda.beluga.action.cluster;

import org.opencloudengine.garuda.beluga.action.ActionRequest;
import org.opencloudengine.garuda.beluga.action.RunnableAction;

/**
 * Created by swsong on 2015. 11. 27..
 */
public class AddSlaveNodeActionRequest extends ActionRequest {
    private String clusterId;
    private Integer incrementSize;


    public AddSlaveNodeActionRequest(String clusterId, Integer incrementSize) {
        this.clusterId = clusterId;
        this.incrementSize = incrementSize;
    }

    @Override
    public boolean compareUnique(ActionRequest other) {
        if(other instanceof AddSlaveNodeActionRequest) {
            return clusterId.equalsIgnoreCase(((AddSlaveNodeActionRequest) other).clusterId);
        } else {
            return false;
        }
    }

    @Override
    public RunnableAction createAction() {
        return new AddSlaveNodeAction(this);
    }

    public String getClusterId() {
        return clusterId;
    }

    public Integer getIncrementSize() {
        return incrementSize;
    }

    @Override
    public String toString() {
        return String.format("%s:%s:%s", getClass().getSimpleName(), clusterId, incrementSize);
    }
}
