package org.opencloudengine.garuda.action;

import org.opencloudengine.garuda.env.SettingManager;
import org.opencloudengine.garuda.service.common.ServiceManager;

/**
 * Created by swsong on 2015. 8. 4..
 */
public abstract class RequestAction {

    protected ActionStatus status = new ActionStatus();
    private ActionResult result;

    protected SettingManager settingManager;
    protected ServiceManager serviceManager;

    public RequestAction() {
        settingManager = SettingManager.getInstance();
        serviceManager = ServiceManager.getInstance();
    }

    public ActionStatus getStatus() {
        return status;
    }

    public ActionResult getResult() {
        return result;
    }

    public void request(Object... params) {

        try {
            result = doAction(params);
        } catch (Throwable t) {
            status.setDone();
            result= new ActionResult().withError(t);
        }
    }

    protected abstract ActionResult doAction(Object... params) throws Exception;
}
