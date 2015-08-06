package org.opencloudengine.garuda.action;

import org.opencloudengine.garuda.env.Environment;
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

    protected Environment environment;
    protected SettingManager settingManager;
    protected ServiceManager serviceManager;

    protected ActionId actionId;

    public RequestAction(ActionId actionId) {
        settingManager = SettingManager.getInstance();
        environment = settingManager.getEnvironment();
        serviceManager = ServiceManager.getInstance();
    }

    public ActionStatus getStatus() {
        return status;
    }

    public ActionId getActionId() {
        return actionId;
    }
//    public ActionResult getResult() {
//        return result;
//    }

    public void run() {

    }

    public ActionStatus request() {

        try {
            doAction();
        } catch (Throwable t) {
//            result = new ActionResult().withError(t);

        } finally {
            status.setDone();
        }
        return status;
    }

    protected abstract void doAction() throws Exception;

}
