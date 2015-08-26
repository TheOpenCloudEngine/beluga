package org.opencloudengine.garuda.api.rest.v1;

import org.opencloudengine.garuda.action.ActionStatus;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/v1")
public class CommonAPI extends BaseAPI {

    public CommonAPI() {
        super();
    }

    /**
     * Get Action Status
     */
    @GET
    @Path("/actions/{id}")
    public Response getActionStatus(@PathParam("id") String actionId) throws Exception {
        ActionStatus actionStatus = actionService().getStatus(actionId);
        if (actionStatus == null) {
            return getErrorMessageOkResponse("Cannot find the action id : " + actionId);
        }
        return Response.ok(actionStatus).build();
    }

}
