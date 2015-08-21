package org.opencloudengine.garuda.api.rest.v1;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.opencloudengine.garuda.action.ActionException;
import org.opencloudengine.garuda.action.ActionService;
import org.opencloudengine.garuda.action.ActionStatus;
import org.opencloudengine.garuda.action.webapp.DeployWebAppActionRequest;
import org.opencloudengine.garuda.action.webapp.WebAppContextFile;
import org.opencloudengine.garuda.common.util.JsonUtils;
import org.opencloudengine.garuda.env.SettingManager;
import org.opencloudengine.garuda.exception.GarudaException;
import org.opencloudengine.garuda.proxy.HAProxyAPI;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by swsong on 2015. 8. 7..
 */
@Path("/v1/clusters/{clusterId}/apps")
public class AppsAPI extends BaseAPI {

    @GET
    @Path("/")
    public Response getApps(@PathParam("clusterId") String clusterId) throws Exception {
        try {
            return marathonAPI(clusterId).requestGetAPI("/apps");
        } catch (Throwable t) {
            logger.error("", t);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(t).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getApp(@PathParam("clusterId") String clusterId, @PathParam("id") String appId) throws Exception {
        try {
            return marathonAPI(clusterId).requestGetAPI("/apps/" + appId);
        } catch (Throwable t) {
            logger.error("", t);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(t).build();
        }
    }

    @GET
    @Path("/{id}/tasks")
    public Response getTasks(@PathParam("clusterId") String clusterId, @PathParam("id") String appId) throws Exception {
        try {
            return marathonAPI(clusterId).requestGetAPI("/apps/" + appId + "/tasks");
        } catch (Throwable t) {
            logger.error("", t);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(t).build();
        }
    }

    /**
     * 앱 파일을 업로드한다.
     */
    @POST
    @Path("/{id}/file")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@PathParam("clusterId") String clusterId, @PathParam("id") String appId,
                               @FormDataParam("file") InputStream uploadedInputStream,
                               @FormDataParam("file") FormDataContentDisposition fileDetail) throws GarudaException {
        String tempDir = SettingManager.getInstance().getSystemSettings().getString("temp.dir.path");
        org.opencloudengine.garuda.env.Path uploadPath = SettingManager.getInstance().getEnvironment().filePaths().path(tempDir);
        File fileHome = uploadPath.path(clusterId, appId).file();
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
    @Path("/")
    public Response deployApp(@PathParam("clusterId") String clusterId, Map<String, Object> data) throws Exception {
        String appId = (String) data.get("id");
        String command = (String) data.get("cmd");
        if (command != null) {
            return runApp(clusterId, appId, data, false);
        }
        return deployOrUpdateApp(clusterId, appId, data, false);
    }

    /**
     * 변경된 설정으로 앱을 재시작한다.
     * 앱을 업로드하지 않을 경우, 설정만 변경되어 재시작된다.
     * 앱을 재시작하기 위해서는 mesos-slave에 도커컨데이너를 추가로 띄울 수 있는 여분의 리소스가 존재해야 한다.
     * minimumHealthCapacity=0.5, maximumOverCapacity=0.2
     */
    @PUT
    @Path("/{id}")
    public Response updateApp(@PathParam("clusterId") String clusterId, @PathParam("id") String appId, Map<String, Object> data) throws Exception {
        String command = (String) data.get("cmd");
        if (command != null) {
            return runApp(clusterId, appId, data, true);
        }
        return deployOrUpdateApp(clusterId, appId, data, true);
    }

    private Response runApp(String clusterId, String appId, Map<String, Object> data, Boolean isUpdate) {
        try {
            String command = (String) data.get("cmd");
            Integer port = (Integer) data.get("port");
            Float cpus = null;
            if (data.get("cpus") != null) {
                cpus = Float.parseFloat(data.get("cpus").toString());
            }
            Float memory = null;
            if (data.get("memory") != null) {
                memory = Float.parseFloat(data.get("memory").toString());
            }
            Integer scale = (Integer) data.get("scale");
            List<Integer> ports = null;
            if(port != null) {
                ports = new ArrayList<>();
                ports.add(port);
            }
            Response response = null;
            if (!isUpdate) {
                response = marathonAPI(clusterId).deployCommandApp(appId, command, ports, cpus, memory, scale);
            } else {
                response = marathonAPI(clusterId).updateCommandApp(appId, command, ports, cpus, memory, scale);
            }
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                //OK
            } else if(response.getStatus() == Response.Status.CONFLICT.getStatusCode()) {
                throw new ActionException("App is already running.");
            } else {
                throw new ActionException("error while deploy to marathon : [" + response.getStatus() + "] " + response.getStatusInfo());
            }

            Map<String, Object> entity = parseMarathonResponse(response);
            notifyDeployment(clusterId, entity);
            return Response.ok().build();
        } catch (Throwable t) {
            logger.error("", t);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(t).build();
        }
    }


    private Response deployOrUpdateApp(String clusterId, String appId, Map<String, Object> data, boolean isUpdate) throws Exception {
        try {
//            Integer port = (Integer) data.get("port");
            /*
             * Webapp의 port는 app이 정할 수 있는것이 아니므로 environment에 정해져 있는 포트를 연다.
             */
            Integer port = null;
            String webAppContext = (String) data.get("context");
            String webAppFile = (String) data.get("file");
            String webAppContext2 = (String) data.get("context2");
            String webAppFile2 = (String) data.get("file2");
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

            List<WebAppContextFile> webAppFileList = new ArrayList<>();

            if(webAppContext != null && webAppFile != null) {
                webAppFileList.add(new WebAppContextFile(webAppFile, webAppContext));
            }
            if(webAppContext2 != null && webAppFile2 != null) {
                webAppFileList.add(new WebAppContextFile(webAppFile2, webAppContext2));
            }
            DeployWebAppActionRequest request = new DeployWebAppActionRequest(clusterId, appId, webAppFileList, webAppType, port, cpus, memory, scale, isUpdate);
            ActionStatus actionStatus = actionService().request(request);
            actionStatus.waitForDone();

            Object result = actionStatus.getResult();
            if(result instanceof Response) {
                Map<String, Object> entity = parseMarathonResponse((Response) result);
                notifyDeployment(clusterId, entity);
            } else if(result instanceof Exception) {
                return Response.status(500).entity(((Exception)result).getMessage()).build();
            }
            return Response.ok().build();
        } catch (Throwable t) {
            logger.error("", t);
            throw t;
        }
    }

    @POST
    @Path("/{id}/restart")
    public Response restartApp(@PathParam("clusterId") String clusterId, @PathParam("id") String appId) throws Exception {

        // 포트가 바뀌므로, haproxy를 업데이트 한다.
        Response response = null;
        try {
            response = marathonAPI(clusterId).requestPostAPI("/apps/" + appId + "/restart", null);
            Map<String, Object> entity = parseMarathonResponse(response);
            notifyDeployment(clusterId, entity);
            return Response.ok().build();
        } catch (Throwable t) {
            logger.error("", t);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(t).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteApp(@PathParam("clusterId") String clusterId, @PathParam("id") String appId) throws Exception {

        Response response = null;
        try {
            // 삭제되었으면 haproxy에서 지워준다.
            response = marathonAPI(clusterId).requestDeleteAPI("/apps/" + appId);
            Map<String, Object> entity = parseMarathonResponse(response);
            notifyDeployment(clusterId, entity);
            return Response.ok().build();
        } catch (Throwable t) {
            logger.error("", t);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(t).build();
        }
    }

    private Map<String, Object> parseMarathonResponse(Response response) {
        if(response == null) {
            return null;
        }
        String json = response.readEntity(String.class);
        if(json == null) {
            return null;
        }
        return JsonUtils.unmarshal(json);
    }

    private void notifyDeployment(String clusterId, Map<String, Object> entity){
        if(entity == null) {
            return;
        }
        List<Map<String, Object>> deployments = (List<Map<String, Object>>) entity.get("deployments");
        if(deployments != null) {
            HAProxyAPI haProxyAPI = clusterService(clusterId).getProxyAPI();
            for (Map<String, Object> deployment : deployments) {
                haProxyAPI.notifyServiceChanged((String) deployment.get("id"));
            }
        }
    }

}
