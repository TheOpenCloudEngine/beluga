package org.opencloudengine.garuda.action.callback;

import org.opencloudengine.garuda.action.ActionRequest;
import org.opencloudengine.garuda.action.ActionStatus;
import org.opencloudengine.garuda.action.RunnableAction;

import java.util.Map;

/**
 * Created by swsong on 2015. 8. 6..
 */
public class RemoveRequestMapCallback implements ActionCallback {

    private Map<ActionRequest, ActionStatus> map;

    public RemoveRequestMapCallback(Map<ActionRequest, ActionStatus> map) {
        this.map = map;
    }

    @Override
    public void callback(RunnableAction action) {
        map.remove(action.getActionRequest());
    }
}
