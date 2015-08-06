package org.opencloudengine.garuda.action.cluster;

import org.opencloudengine.garuda.action.ActionRequest;
import org.opencloudengine.garuda.action.RunnableAction;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class CreateClusterActionRequest extends ActionRequest {
    private String clusterId;
    private String definitionId;

    public CreateClusterActionRequest(String clusterId, String definitionId) {
        this.clusterId = clusterId;
        this.definitionId = definitionId;
    }

    public String getClusterId() {
        return clusterId;
    }

    public String getDefinitionId() {
        return definitionId;
    }

    @Override
    public boolean compareUnique(ActionRequest other) {
        if(other instanceof CreateClusterActionRequest) {
            return clusterId.equalsIgnoreCase(((CreateClusterActionRequest) other).clusterId);
        } else {
            return false;
        }
    }

    @Override
    public RunnableAction createAction() {
        return new CreateClusterAction(this);
    }

    public String toString() {
        return String.format("%s:%s:%s", getClass().getSimpleName(), clusterId, definitionId);
    }
}
