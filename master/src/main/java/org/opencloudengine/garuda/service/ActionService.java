package org.opencloudengine.garuda.service;

import org.opencloudengine.garuda.action.ActionID;
import org.opencloudengine.garuda.action.ActionResult;
import org.opencloudengine.garuda.action.ActionStatus;
import org.opencloudengine.garuda.env.Environment;
import org.opencloudengine.garuda.env.Settings;
import org.opencloudengine.garuda.exception.GarudaException;
import org.opencloudengine.garuda.service.common.ServiceManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class ActionService extends AbstractService {

    private Map<ActionID, ActionStatus> runningActionStatusMap;
    private Map<ActionID, ActionResult> actionResultMap;



    public ActionService(Environment environment, Settings settings, ServiceManager serviceManager) {
        super(environment, settings, serviceManager);
    }

    @Override
    protected boolean doStart() throws GarudaException {
        runningActionStatusMap = new ConcurrentHashMap<>();
        actionResultMap = new ConcurrentHashMap<>();

        return true;
    }

    @Override
    protected boolean doStop() throws GarudaException {
        return false;
    }

    @Override
    protected boolean doClose() throws GarudaException {
        return false;
    }
}
