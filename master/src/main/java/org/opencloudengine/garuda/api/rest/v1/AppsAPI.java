package org.opencloudengine.garuda.api.rest.v1;

import org.opencloudengine.garuda.action.ActionStatus;
import org.opencloudengine.garuda.action.webapp.DeployWebAppAction;
import org.opencloudengine.garuda.action.webapp.DeployWebAppActionRequest;
import org.opencloudengine.garuda.cloud.ClusterTopology;
import org.opencloudengine.garuda.env.DockerWebAppPorts;
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

    /**
     * 앱을 업로드하고, 전달된 설정대로 앱을 실행한다.
     * */
    @POST
    @Path("/{id}")
    public Response deployApp(@PathParam("clusterId") String clusterId, @PathParam("id") String appId, Map<String, Object> data) throws Exception {
        return deployOrUpdateApp(clusterId, appId, data, false);
    }

    /**
     * 앱을 새로 업로드하고, 변경된 설정으로 앱을 재시작한다.
     * 앱을 업로드하지 않을 경우, 설정만 변경되어 재시작된다.
     * 앱을 재시작하기 위해서는 mesos-slave에 도커컨데이너를 추가로 띄울 수 있는 여분의 리소스가 존재해야 한다.
     * minimumHealthCapacity=0.5, maximumOverCapacity=0.2
     * */
    @PUT
    @Path("/{id}")
    public Response updateApp(@PathParam("clusterId") String clusterId, @PathParam("id") String appId, Map<String, Object> data) throws Exception {
        return deployOrUpdateApp(clusterId, appId, data, true);
    }

    private Response deployOrUpdateApp(@PathParam("clusterId") String clusterId, @PathParam("id") String appId, Map<String, Object> data, boolean isUpdate) throws Exception {
        try {
            String webAppFile = (String) data.get("file");
            String webAppType = (String) data.get("type");
            Float cpus = null;
            if(data.get("cpus") != null) {
                cpus = Float.parseFloat(data.get("cpus").toString());
            }
            Float memory = null;
            if(data.get("memory") != null) {
                memory = Float.parseFloat(data.get("memory").toString());
            }
            Integer scale = (Integer) data.get("scale");

            DeployWebAppActionRequest request = new DeployWebAppActionRequest(clusterId, appId, webAppFile, webAppType, cpus, memory, scale, true);
            ActionStatus actionStatus = actionService().request(request);
            actionStatus.waitForDone();
            return Response.ok(actionStatus).build();
        } catch (Throwable t) {
            logger.error("", t);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(t).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteApp(@PathParam("clusterId") String clusterId, @PathParam("id") String appId) throws Exception {

        return null;
    }


}
