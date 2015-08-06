package org.opencloudengine.garuda.api.rest.v1;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.opencloudengine.garuda.action.ActionStatus;
import org.opencloudengine.garuda.action.cluster.CreateClusterAction;
import org.opencloudengine.garuda.action.cluster.CreateClusterActionId;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * 1. 클러스터 생성
 * uri : clusters
 * method : post
 * param : clusterId 클러스터명
 * param : clusterId 클러스터명
 *
 */
@Path("/v1/clusters")
public class ClustersAPI extends BaseAPI {

    public ClustersAPI() {
        super();
    }

    /**
     * Create cluster.
     */
    @POST
    @Path("/")
    public Response createCluster(@FormDataParam("id") String clusterId
            , @FormDataParam("definition") String definitionId) throws Exception {

        CreateClusterActionId actionId = new CreateClusterActionId(clusterId, definitionId);
        ActionStatus actionStatus = actionService().request(actionId);

        return Response.ok().build();
    }

}
