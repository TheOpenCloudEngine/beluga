package org.opencloudengine.garuda.api.rest.v1;

import org.opencloudengine.garuda.action.ActionStatus;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/v1/actions")
public class CommonAPI extends BaseAPI {

    public CommonAPI() {
        super();
    }

    /**
     * Get Action Status
     */
    @GET
    @Path("/{actionId}")
    public Response checkStatus(@PathParam("actionId") String actionId) throws Exception {
        ActionStatus actionStatus = actionService().getStatus(actionId);
        if(actionStatus == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(actionStatus).build();
    }

}
