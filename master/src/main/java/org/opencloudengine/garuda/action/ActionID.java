package org.opencloudengine.garuda.action;

/**
 * Created by swsong on 2015. 8. 4..
 */
public abstract class ActionId {

    public abstract String type();

    public abstract boolean compareUnique(ActionId id);

    public abstract RequestAction createRequestAction();


    @Override
    public boolean equals(Object id) {
        return compareUnique((ActionId) id);
    }
}
