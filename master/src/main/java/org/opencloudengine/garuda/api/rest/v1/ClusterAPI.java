package org.opencloudengine.garuda.api.rest.v1;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.opencloudengine.garuda.cloud.ClusterService;
import org.opencloudengine.garuda.service.common.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 */
@Path("/v1/clusters")
public class ClusterAPI {

    private static final Logger logger = LoggerFactory.getLogger(ClusterAPI.class);

    public ClusterAPI() {
        super();
    }

    /**
     * Create cluster.
     */
    @POST
    @Path("/")
    public Response createCluster(@FormDataParam("clusterId") String clusterId
            , @FormDataParam("definition") String definitionId) throws Exception {

        ClusterService clusterService = ServiceManager.getInstance().getService(ClusterService.class);
        clusterService.createCluster(clusterId, definitionId);
        return Response.ok().build();
    }

}
