package org.opencloudengine.garuda.api.rest.v1;

import org.opencloudengine.garuda.action.ActionRequest;
import org.opencloudengine.garuda.action.ActionStatus;
import org.opencloudengine.garuda.action.cluster.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Map;

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
     * Change clusters state
     * type             : create | start | stop | restart | destroy
     * definition       : definition id. Only needed when a type is 'create'.
     * await (optional) : boolean. wait until action task is completed.
     */
    @POST
    @Path("/{id}")
    public Response changeClusterState(@PathParam("id") String clusterId, Map<String, Object> data) throws Exception {

        if(data == null) {
            return getErrorMessageOkResponse("No data transferred.");
        }
        String type = (String) data.get("type");

        if(type == null) {
            return getErrorMessageOkResponse("Type must be set among 'create | start | stop | restart | destroy'");
        }

        Boolean await = (Boolean) data.get("await");
        try {
            ActionRequest request = null;
            if (type.equalsIgnoreCase("create")) {
                String definitionId = (String) data.get("definition");
                if(definitionId == null) {
                    return getErrorMessageOkResponse("Definition must be set when type is 'create'");
                }
                request = new CreateClusterActionRequest(clusterId, definitionId);
            } else if (type.equalsIgnoreCase("start")) {
                request = new StartClusterActionRequest(clusterId);
            } else if (type.equalsIgnoreCase("stop")) {
                request = new StopClusterActionRequest(clusterId);
            } else if (type.equalsIgnoreCase("restart")) {
                request = new RestartClusterActionRequest(clusterId);
            } else if (type.equalsIgnoreCase("destroy")) {
                request = new DestroyClusterActionRequest(clusterId);
            } else {
                return getErrorMessageOkResponse("Unknown type " + type + ". Choose type among 'create | start | stop | restart | destroy'");
            }
            ActionStatus actionStatus = actionService().request(request);
            if(await != null && await.booleanValue()) {
                actionStatus.waitForDone();
            }

            return Response.ok(actionStatus).build();
        } catch (Throwable t) {
            logger.error("", t);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(t).build();
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
            actionStatus.waitForDone();
            return Response.ok(actionStatus).build();
        } catch (Throwable t) {
            logger.error("", t);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(t).build();
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
            actionStatus.waitForDone();
            return Response.ok(actionStatus).build();
        } catch (Throwable t) {
            logger.error("", t);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(t).build();
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
            actionStatus.waitForDone();
            if(actionStatus.getResult() == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(actionStatus).build();
        } catch (Throwable t) {
            logger.error("", t);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(t).build();
        }
    }
}
