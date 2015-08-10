package org.opencloudengine.garuda.api.rest.v1;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.Map;

/**
 * Created by swsong on 2015. 8. 10..
 */
@Path("/v1/proxy")
public class ProxyAPI extends BaseAPI {

    @POST
    @Path("/{id}")
    public Response updateConfiguration(@PathParam("id") String clusterId, Map<String, Object> data) throws Exception {

        return null;
    }
}
