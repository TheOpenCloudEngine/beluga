package org.opencloudengine.garuda.api.rest.v1;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.opencloudengine.garuda.action.ActionStatus;
import org.opencloudengine.garuda.action.webapp.DeployWebAppAction;
import org.opencloudengine.garuda.action.webapp.DeployWebAppActionRequest;
import org.opencloudengine.garuda.cloud.ClusterTopology;
import org.opencloudengine.garuda.env.DockerWebAppPorts;
import org.opencloudengine.garuda.env.SettingManager;
import org.opencloudengine.garuda.exception.GarudaException;
import org.opencloudengine.garuda.mesos.MesosService;
import org.opencloudengine.garuda.mesos.marathon.model.App;
import org.opencloudengine.garuda.service.common.ServiceManager;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.URLDecoder;
import java.nio.channels.FileLock;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by swsong on 2015. 8. 7..
 */
@Path("/v1/clusters/{clusterId}")
public class AppsAPI extends BaseAPI {

    private MesosService mesosService;

    public AppsAPI() {
        super();
        mesosService = ServiceManager.getInstance().getService(MesosService.class);
    }

    @GET
    @Path("/apps")
    public Response getApps(@PathParam("clusterId") String clusterId) throws Exception {
        try {
            return mesosService.getMarathonAPI().requestGetAPI(clusterId, "/apps");
        } catch (Throwable t) {
            logger.error("", t);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(t).build();
        }
    }

    @GET
    @Path("/apps/{id}")
    public Response getApp(@PathParam("clusterId") String clusterId, @PathParam("id") String appId) throws Exception {
        try {
            return mesosService.getMarathonAPI().requestGetAPI(clusterId, "/apps/" + appId);
        } catch (Throwable t) {
            logger.error("", t);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(t).build();
        }
    }

    /**
     * 앱 파일을 업로드한다.
     */
    @POST
    @Path("/apps/{id}/file")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@PathParam("clusterId") String clusterId, @PathParam("id") String appId,
                               @FormDataParam("file") InputStream uploadedInputStream,
                               @FormDataParam("file") FormDataContentDisposition fileDetail) throws GarudaException {
        String uploadDir = SettingManager.getInstance().getSystemSettings().getString("upload.dir.path");
        org.opencloudengine.garuda.env.Path appUploadPath = SettingManager.getInstance().getEnvironment().filePaths().path(uploadDir);
        File fileHome = appUploadPath.path(clusterId, appId).file();
        if (!fileHome.exists()) {
            fileHome.mkdirs();
        }
        String fileName = URLDecoder.decode(fileDetail.getFileName());
        File file = new File(fileHome, fileName);
        if (file.exists()) {
            file.delete();
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException ignore) {
        }

        OutputStream out = null;
        try {
            int read = 0;
            byte[] bytes = new byte[1024];

            out = new FileOutputStream(file);
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
        } catch (Throwable t) {
            logger.error("", t);
            throw new GarudaException("File upload failed. ");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignore) {
                }
            }
        }
        Map<String, String> result = new HashMap<>();
        result.put("filePath", file.getAbsolutePath());
        return Response.ok(result).build();
    }

    /**
     * 전달된 설정대로 앱을 실행한다.
     */
    @POST
    @Path("/apps/{id}")
    public Response deployApp(@PathParam("clusterId") String clusterId, @PathParam("id") String appId, Map<String, Object> data) throws Exception {
        return deployOrUpdateApp(clusterId, appId, data, false);
    }

    /**
     * 변경된 설정으로 앱을 재시작한다.
     * 앱을 업로드하지 않을 경우, 설정만 변경되어 재시작된다.
     * 앱을 재시작하기 위해서는 mesos-slave에 도커컨데이너를 추가로 띄울 수 있는 여분의 리소스가 존재해야 한다.
     * minimumHealthCapacity=0.5, maximumOverCapacity=0.2
     */
    @PUT
    @Path("/apps/{id}")
    public Response updateApp(@PathParam("clusterId") String clusterId, @PathParam("id") String appId, Map<String, Object> data) throws Exception {
        return deployOrUpdateApp(clusterId, appId, data, true);
    }

    private Response deployOrUpdateApp(@PathParam("clusterId") String clusterId, @PathParam("id") String appId, Map<String, Object> data, boolean isUpdate) throws Exception {
        try {
            String webAppFile = (String) data.get("file");
            String webAppType = (String) data.get("type");
            Float cpus = null;
            if (data.get("cpus") != null) {
                cpus = Float.parseFloat(data.get("cpus").toString());
            }
            Float memory = null;
            if (data.get("memory") != null) {
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

    @POST
    @Path("/apps/{id}/restart")
    public Response restartApp(@PathParam("clusterId") String clusterId, @PathParam("id") String appId) throws Exception {
        return mesosService.getMarathonAPI().requestPostAPI(clusterId, "/apps/" + appId + "restart", null);
    }

    @DELETE
    @Path("/apps/{id}")
    public Response deleteApp(@PathParam("clusterId") String clusterId, @PathParam("id") String appId) throws Exception {
        return mesosService.getMarathonAPI().requestDeleteAPI(clusterId, "/apps/" + appId);
    }

    @GET
    @Path("/deployments")
    public Response getDeployments(@PathParam("clusterId") String clusterId) throws Exception {
        return mesosService.getMarathonAPI().requestGetAPI(clusterId, "/deployments");
    }

    @DELETE
    @Path("/deployments/{deploymentsId}")
    public Response deleteDeployments(@PathParam("clusterId") String clusterId
            , @PathParam("deploymentsId") String deploymentsId) throws Exception {
        return mesosService.getMarathonAPI().requestDeleteAPI(clusterId, "/deployments");
    }

}
