package org.opencloudengine.garuda.api.rest.v1;

import org.opencloudengine.garuda.action.ActionRequest;
import org.opencloudengine.garuda.action.ActionStatus;
import org.opencloudengine.garuda.action.cluster.*;
import org.opencloudengine.garuda.cloud.ClusterService;
import org.opencloudengine.garuda.cloud.ClusterTopology;
import org.opencloudengine.garuda.mesos.MesosService;
import org.opencloudengine.garuda.service.common.ServiceManager;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.Map;


@Path("/v1/clusters")
public class ClustersAPI extends BaseAPI {

    private final ClusterService clusterService;

    public ClustersAPI() {
        super();
        clusterService = ServiceManager.getInstance().getService(ClusterService.class);
    }

    /**
     * Create cluster
     * definition       : definition id.
     * await (optional) : boolean. wait until action task is completed.
     */
    @POST
    @Path("/")
    public Response createCluster(Map<String, Object> data) throws Exception {

        if (data == null) {
            return getErrorMessageOkResponse("No data transferred.");
        }

        String clusterId = (String) data.get("id");
        if(clusterId == null) {
            return getErrorMessageOkResponse("ID must be set.");
        }
        String definitionId = (String) data.get("definition");
        if (definitionId == null) {
            return getErrorMessageOkResponse("Definition must be set.");
        }
        Boolean await = (Boolean) data.get("await");
        try {
            ActionRequest request = new CreateClusterActionRequest(clusterId, definitionId);
            ActionStatus actionStatus = actionService().request(request);
            if (await != null && await.booleanValue()) {
                actionStatus.waitForDone();
            }

            return Response.ok(actionStatus).build();
        } catch (Throwable t) {
            logger.error("", t);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(t).build();
        }
    }

    /**
     * Change clusters state
     * type             : start | stop | restart | destroy
     * definition       : definition id. Only needed when a type is 'create'.
     * await (optional) : boolean. wait until action task is completed.
     */
    @POST
    @Path("/{id}")
    public Response changeClusterState(@PathParam("id") String clusterId, Map<String, Object> data) throws Exception {

        if (data == null) {
            return getErrorMessageOkResponse("No data transferred.");
        }
        String type = (String) data.get("type");

        if (type == null) {
            return getErrorMessageOkResponse("Type must be set among 'start | stop | restart | destroy'");
        }

        Boolean await = (Boolean) data.get("await");
        try {
            ActionRequest request = null;
            if (type.equalsIgnoreCase("start")) {
                request = new StartClusterActionRequest(clusterId);
            } else if (type.equalsIgnoreCase("stop")) {
                request = new StopClusterActionRequest(clusterId);
            } else if (type.equalsIgnoreCase("restart")) {
                request = new RestartClusterActionRequest(clusterId);
            } else if (type.equalsIgnoreCase("destroy")) {
                request = new DestroyClusterActionRequest(clusterId);
            } else {
                return getErrorMessageOkResponse("Unknown type " + type + ". Choose type among 'start | stop | restart | destroy'");
            }
            ActionStatus actionStatus = actionService().request(request);
            if (await != null && await.booleanValue()) {
                actionStatus.waitForDone();
            }

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
            Collection<ClusterTopology> set = clusterService.getAllClusterTopology();
            return Response.ok(set).build();
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
            ClusterTopology topology = clusterService.getClusterTopology(clusterId);
            if (topology == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(topology).build();
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

    @GET
    @Path("/{id}/info")
    public Response getMarathonInfo(@PathParam("id") String clusterId) throws Exception {
        MesosService mesosService = ServiceManager.getInstance().getService(MesosService.class);
        try {
            return mesosService.getMarathonAPI().requestGetAPI(clusterId, "/info");
        } catch (Throwable t) {
            logger.error("", t);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(t).build();
        }
    }

    @GET
    @Path("/{id}/deployments")
    public Response getDeployments(@PathParam("id") String clusterId) throws Exception {
        try {
            MesosService mesosService = ServiceManager.getInstance().getService(MesosService.class);
            return mesosService.getMarathonAPI().requestGetAPI(clusterId, "/deployments");
        } catch (Throwable t) {
            logger.error("", t);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(t).build();
        }
    }

    @DELETE
    @Path("/{id}/deployments/{deploymentsId}")
    public Response deleteDeployments(@PathParam("id") String clusterId
            , @PathParam("deploymentsId") String deploymentsId) throws Exception {
        try {
            MesosService mesosService = ServiceManager.getInstance().getService(MesosService.class);
            return mesosService.getMarathonAPI().requestDeleteAPI(clusterId, "/deployments");
        } catch (Throwable t) {
            logger.error("", t);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(t).build();
        }
    }

    @POST
    @Path("/{id}/proxy")
    public Response applyProxyConfig(@PathParam("id") String clusterId) throws Exception {
        try {
            String configString = clusterService.getProxyAPI().notifyServiceChanged(clusterId, null);
            return Response.ok(configString).build();
        } catch (Throwable t) {
            logger.error("", t);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(t).build();
        }
    }
}
