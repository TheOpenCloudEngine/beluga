package org.opencloudengine.garuda.beluga.action.callback;

import org.opencloudengine.garuda.beluga.action.ActionRequest;
import org.opencloudengine.garuda.beluga.action.ActionStatus;
import org.opencloudengine.garuda.beluga.action.RunnableAction;

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
