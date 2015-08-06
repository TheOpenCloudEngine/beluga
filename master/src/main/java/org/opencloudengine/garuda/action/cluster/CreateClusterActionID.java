package org.opencloudengine.garuda.action.cluster;

import org.opencloudengine.garuda.action.ActionId;
import org.opencloudengine.garuda.action.RequestAction;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class CreateClusterActionId extends ActionId {
    private String clusterId;
    private String definitionId;

    public CreateClusterActionId(String clusterId, String definitionId) {
        this.clusterId = clusterId;
        this.definitionId = definitionId;
    }

    @Override
    public String type() {
        return "create-cluster";
    }

    @Override
    public boolean compareUnique(ActionId id) {
        if(id instanceof CreateClusterActionId) {
            return clusterId.equalsIgnoreCase(((CreateClusterActionId) id).clusterId);
        } else {
            return false;
        }
    }

    @Override
    public RequestAction createRequestAction() {
        return new CreateClusterAction(clusterId, definitionId);
    }

    public String toString() {
        return String.format("%s:%s:%s", type(), clusterId, definitionId);
    }
}
