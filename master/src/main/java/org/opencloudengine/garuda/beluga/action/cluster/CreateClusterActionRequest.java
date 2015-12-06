package org.opencloudengine.garuda.beluga.action.cluster;

import org.opencloudengine.garuda.beluga.action.ActionRequest;
import org.opencloudengine.garuda.beluga.action.RunnableAction;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class CreateClusterActionRequest extends ActionRequest {
    private String clusterId;
    private String definitionId;
    private String domainName;

    public CreateClusterActionRequest(String clusterId, String definitionId, String domainName) {
        this.clusterId = clusterId;
        this.definitionId = definitionId;
        this.domainName = domainName;

    }

    public String getClusterId() {
        return clusterId;
    }

    public String getDefinitionId() {
        return definitionId;
    }

    public String getDomainName() {
        return domainName;
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

    @Override
    public String toString() {
        return String.format("%s:%s:%s", getClass().getSimpleName(), clusterId, definitionId);
    }
}
