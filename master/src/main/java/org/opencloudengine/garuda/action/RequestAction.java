package org.opencloudengine.garuda.action;

import org.opencloudengine.garuda.env.SettingManager;
import org.opencloudengine.garuda.service.common.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by swsong on 2015. 8. 4..
 */
public abstract class RequestAction {
    protected Logger logger = LoggerFactory.getLogger(RequestAction.class);
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

    public ActionStatus request(Object... params) {

        try {
            result = doAction(params);
        } catch (Throwable t) {
            result = new ActionResult().withError(t);
        } finally {
            status.setDone();
        }
        return status;
    }

    protected abstract ActionResult doAction(Object... params) throws Exception;

}
