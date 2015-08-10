package org.opencloudengine.garuda.api.rest.v1;

import org.opencloudengine.garuda.action.ActionStatus;
import org.opencloudengine.garuda.action.webapp.DeployWebAppAction;
import org.opencloudengine.garuda.action.webapp.DeployWebAppActionRequest;
import org.opencloudengine.garuda.cloud.ClusterTopology;
import org.opencloudengine.garuda.mesos.MesosService;
import org.opencloudengine.garuda.mesos.marathon.model.App;
import org.opencloudengine.garuda.service.common.ServiceManager;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by swsong on 2015. 8. 7..
 */
@Path("/v1/{clusterId}/apps")
public class AppsAPI extends BaseAPI {

    private MesosService mesosService;

    public AppsAPI() {
        super();
        mesosService = ServiceManager.getInstance().getService(MesosService.class);
    }

    @GET
    @Path("/")
    public Response getApps(@PathParam("clusterId") String clusterId) throws Exception {
        try {
            List<App> apps = mesosService.getMarathonAPI().getApps(clusterId);
            return Response.ok(apps).build();
        } catch (Throwable t) {
            logger.error("", t);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(t).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getApp(@PathParam("clusterId") String clusterId, @PathParam("id") String appId) throws Exception {
        try {
            App app = mesosService.getMarathonAPI().getApp(clusterId, appId);
            return Response.ok(app).build();
        } catch (Throwable t) {
            logger.error("", t);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(t).build();
        }
    }

    @POST
    @Path("/{id}")
    public Response deployApp(@PathParam("clusterId") String clusterId, @PathParam("id") String appId, Map<String, Object> data) throws Exception {
        try {
            String webAppFile = null;
            String webAppType = null;
            Float cpus = null;
            Float memory = null;
            Integer scale = null;

            DeployWebAppActionRequest request = new DeployWebAppActionRequest(clusterId, appId, webAppFile, webAppType, cpus, memory, scale);
            ActionStatus actionStatus = actionService().request(request);
            actionStatus.waitForDone();
            return Response.ok(actionStatus).build();
        } catch (Throwable t) {
            logger.error("", t);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(t).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateApp(@PathParam("clusterId") String clusterId, @PathParam("id") String appId, Map<String, Object> data) throws Exception {

        return null;
    }

    @DELETE
    @Path("/{id}")
    public Response deleteApp(@PathParam("clusterId") String clusterId, @PathParam("id") String appId) throws Exception {

        return null;
    }


}
