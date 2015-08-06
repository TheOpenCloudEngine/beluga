package org.opencloudengine.garuda.api.rest.v1;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.opencloudengine.garuda.action.ActionStatus;
import org.opencloudengine.garuda.action.cluster.CreateClusterActionRequest;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * 1. 클러스터 생성
 * uri : clusters
 * method : post
 * param : clusterId 클러스터명
 * param : definitionId 클러스터정의 설정아이디
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
        try {
            CreateClusterActionRequest actionId = new CreateClusterActionRequest(clusterId, definitionId);
            ActionStatus actionStatus = actionService().request(actionId);
            return Response.ok(actionStatus).build();
        } catch (Throwable t) {
            logger.error("", t);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
