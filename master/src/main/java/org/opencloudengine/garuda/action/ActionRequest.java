package org.opencloudengine.garuda.action;

/**
 * Created by swsong on 2015. 8. 4..
 */
public abstract class ActionRequest {

    public abstract boolean compareUnique(ActionRequest id);

    public abstract RunnableAction createAction();


    @Override
    public boolean equals(Object id) {
        return compareUnique((ActionRequest) id);
    }
}
