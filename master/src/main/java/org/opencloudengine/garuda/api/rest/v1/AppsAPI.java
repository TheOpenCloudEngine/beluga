package org.opencloudengine.garuda.api.rest.v1;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Map;

/**
 * Created by swsong on 2015. 8. 7..
 */
@Path("/v1/apps")
public class AppsAPI {

    @GET
    @Path("/")
    public Response getApps() throws Exception {


        return null;
    }


    @POST
    @Path("/{id}")
    public Response deployApp(@PathParam("id") String appId, Map<String, Object> data) throws Exception {

        return null;
    }

    @PUT
    @Path("/{id}")
    public Response updateApp(@PathParam("id") String appId, Map<String, Object> data) throws Exception {

        return null;
    }

    @DELETE
    @Path("/{id}")
    public Response deleteApp(@PathParam("id") String appId) throws Exception {

        return null;
    }


}
