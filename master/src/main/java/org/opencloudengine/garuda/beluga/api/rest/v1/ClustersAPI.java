package org.opencloudengine.garuda.beluga.api.rest.v1;

import org.opencloudengine.garuda.beluga.action.ActionRequest;
import org.opencloudengine.garuda.beluga.action.ActionStatus;
import org.opencloudengine.garuda.beluga.action.cluster.*;
import org.opencloudengine.garuda.beluga.cloud.ClusterService;
import org.opencloudengine.garuda.beluga.cloud.ClusterTopology;
import org.opencloudengine.garuda.beluga.cloud.ClustersService;
import org.opencloudengine.garuda.beluga.service.common.ServiceManager;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.Map;


@Path("/v1/clusters")
public class ClustersAPI extends BaseAPI {

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
        if (clusterId == null) {
            return getErrorMessageOkResponse("ID must be set.");
        }
        String definitionId = (String) data.get("definition");
        if (definitionId == null) {
            return getErrorMessageOkResponse("Definition must be set.");
        }
        String domainName = (String) data.get("domain");
        if (domainName == null) {
            return getErrorMessageOkResponse("Domain must be set.");
        }
        Boolean await = (Boolean) data.get("await");
        try {
            ActionRequest request = new CreateClusterActionRequest(clusterId, definitionId, domainName);
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
            return getErrorMessageOkResponse("Type must be set among 'start | stop | restart | destroy | add | remove'");
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
            } else if (type.equalsIgnoreCase("add")) {
                int size = (int) data.get("size");
                String role = (String) data.get("role");
                if (role.equalsIgnoreCase("mesos-slave")) {
                    request = new AddSlaveNodeActionRequest(clusterId, size);
                }
            } else if (type.equalsIgnoreCase("remove")) {
                String role = (String) data.get("role");
                String instanceId = (String) data.get("instanceId");
                if (role.equalsIgnoreCase("mesos-slave")) {
                    request = new RemoveSlaveNodeActionRequest(clusterId, instanceId);
                }
            } else {
                return getErrorMessageOkResponse("Unknown type " + type + ". Choose type among 'start | stop | restart | destroy | add | remove'");
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
     * Get one clusters info.
     */
    @GET
    @Path("/{id}")
    public Response getCluster(@PathParam("id") String clusterId) throws Exception {
        try {
            ClusterService clusterService = clusterService(clusterId);
            if (clusterService == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            ClusterTopology topology = clusterService.getClusterTopology();
            return Response.ok(topology).build();
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
            ClustersService clustersService = ServiceManager.getInstance().getService(ClustersService.class);
            Collection<ClusterTopology> set = clustersService.getAllClusterTopology();
            return Response.ok(set).build();
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
        try {
            return marathonAPI(clusterId).requestGetAPI("/info");
        } catch (Throwable t) {
            logger.error("", t);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(t).build();
        }
    }

    @GET
    @Path("/{id}/deployments")
    public Response getDeployments(@PathParam("id") String clusterId) throws Exception {
        try {
            return marathonAPI(clusterId).requestGetAPI("/deployments");
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
            return marathonAPI(clusterId).requestDeleteAPI("/deployments/" + deploymentsId);
        } catch (Throwable t) {
            logger.error("", t);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(t).build();
        }
    }

    @POST
    @Path("/{id}/proxy")
    public Response applyProxyConfig(@PathParam("id") String clusterId) throws Exception {
        try {
            String configString = clusterService(clusterId).getProxyAPI().notifyTopologyChanged();
            return Response.ok(configString).build();
        } catch (Throwable t) {
            logger.error("", t);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(t).build();
        }
    }

    @GET
    @Path("/{id}/domain")
    public Response getDomainName(@PathParam("id") String clusterId) throws Exception {
        String domain = clusterService(clusterId).getDomainName();
        return Response.ok(domain).build();
    }

    @POST
    @Path("/{id}/increaseSlave")
    public Response modifySlaveNode(@PathParam("id") String clusterId, Map<String, Object> data) throws Exception {
        int size = 1;
        Integer incrementSize = (Integer) data.get("incrementSize");
        if(incrementSize != null) {
            size = incrementSize;
        }
        clusterService(clusterId).addSlaveNode(size);
        return Response.ok().build();
    }

}
