package org.opencloudengine.garuda.api.rest.v1;

import org.opencloudengine.garuda.service.ActionService;
import org.opencloudengine.garuda.service.common.ServiceManager;
import org.opencloudengine.garuda.utils.StringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;

/**
 * API 호출에 필요한 사항들을 구현해놓았다.
 *
 * Created by swsong on 2015. 8. 6..
 */
public abstract class BaseAPI {
    protected static final Logger logger = LoggerFactory.getLogger(BaseAPI.class);

    private ActionService actionService;

    protected BaseAPI() {
        actionService = ServiceManager.getInstance().getService(ActionService.class);
    }

    protected ActionService actionService() {
        return actionService;
    }

    public Response getErrorMessageOkResponse(String message) {
        Object entity = new StringMap(1).with("error", message);
        return Response.ok().entity(entity).build();
    }
}
