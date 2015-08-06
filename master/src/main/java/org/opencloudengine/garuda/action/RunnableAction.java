package org.opencloudengine.garuda.action;

import org.opencloudengine.garuda.env.Environment;
import org.opencloudengine.garuda.env.SettingManager;
import org.opencloudengine.garuda.service.common.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by swsong on 2015. 8. 4..
 */
public abstract class RunnableAction<RequestType extends ActionRequest> implements Runnable {
    protected Logger logger = LoggerFactory.getLogger(RunnableAction.class);

    protected ActionStatus status;
    protected Environment environment;
    protected SettingManager settingManager;
    protected ServiceManager serviceManager;

    protected RequestType actionRequest;

    public RunnableAction(RequestType actionRequest) {
        this.actionRequest = actionRequest;
        status = new ActionStatus(actionRequest.getActionId(), getClass().getSimpleName());
        settingManager = SettingManager.getInstance();
        environment = settingManager.getEnvironment();
        serviceManager = ServiceManager.getInstance();
    }

    public ActionStatus getStatus() {
        return status;
    }

    public RequestType getActionRequest() {
        return actionRequest;
    }

    @Override
    public void run() {
        try {
            logger.info("### Start Action {}", status.getActionName(), status.getId());
            status.setStart();
            doAction();
            status.setComplete();
        } catch (Throwable t) {
            status.setError(t);
        } finally {
            logger.info("### Finished Action {}", status.getActionName(), status.getId());
        }
    }

    protected abstract void doAction() throws Exception;

    protected void setResult(Object obj) {
        status.setResult(obj);
    }
}
