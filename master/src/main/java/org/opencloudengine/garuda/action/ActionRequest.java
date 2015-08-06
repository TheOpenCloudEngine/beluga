package org.opencloudengine.garuda.action;

/**
 * Created by swsong on 2015. 8. 4..
 */
public abstract class ActionRequest {

    private String actionId;

    public abstract boolean compareUnique(ActionRequest other);

    public abstract RunnableAction createAction();


    @Override
    public boolean equals(Object obj) {
        return compareUnique((ActionRequest) obj);
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }
}
