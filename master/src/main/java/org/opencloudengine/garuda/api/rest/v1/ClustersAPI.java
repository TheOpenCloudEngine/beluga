package org.opencloudengine.garuda.api.rest.v1;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.opencloudengine.garuda.action.ActionStatus;
import org.opencloudengine.garuda.action.cluster.CreateClusterActionRequest;
import org.opencloudengine.garuda.action.cluster.DestroyClusterActionRequest;
import org.opencloudengine.garuda.action.cluster.GetClusterActionRequest;
import org.opencloudengine.garuda.action.cluster.GetClustersActionRequest;

import javax.ws.rs.*;
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
            CreateClusterActionRequest request = new CreateClusterActionRequest(clusterId, definitionId);
            ActionStatus actionStatus = actionService().request(request);
            return Response.ok(actionStatus).build();
        } catch (Throwable t) {
            logger.error("", t);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Destroy cluster.
     */
    @DELETE
    @Path("/{id}")
    public Response destroyCluster(@PathParam("id") String clusterId) throws Exception {
        try {
            DestroyClusterActionRequest request = new DestroyClusterActionRequest(clusterId);
            ActionStatus actionStatus = actionService().request(request);
            actionStatus.waitUntilDone();
            return Response.ok(actionStatus).build();
        } catch (Throwable t) {
            logger.error("", t);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all clusters info.
     */
    @GET
    @Path("/")
    public Response getClusters() throws Exception {
        try {
            GetClustersActionRequest request = new GetClustersActionRequest();
            ActionStatus actionStatus = actionService().request(request);
            actionStatus.waitUntilDone();
            return Response.ok(actionStatus).build();
        } catch (Throwable t) {
            logger.error("", t);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get one clusters info.
     */
    @GET
    @Path("/{id}")
    public Response getCluster(@PathParam("id") String clusterId) throws Exception {
        try {
            GetClusterActionRequest request = new GetClusterActionRequest(clusterId);
            ActionStatus actionStatus = actionService().request(request);
            actionStatus.waitUntilDone();
            if(actionStatus.getResult() == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(actionStatus).build();
        } catch (Throwable t) {
            logger.error("", t);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
