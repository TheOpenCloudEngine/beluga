package org.opencloudengine.garuda.api.rest.v1;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 * Created by swsong on 2015. 8. 7..
 */
@Path("/v1/apps")
public class AppsAPI {

    @POST
    @Path("/{id}")
    public Response deployApp(@PathParam("id") String appId) throws Exception {

        return null;
    }


}
