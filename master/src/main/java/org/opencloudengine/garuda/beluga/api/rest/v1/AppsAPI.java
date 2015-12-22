package org.opencloudengine.garuda.beluga.api.rest.v1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.opencloudengine.garuda.beluga.action.ActionException;
import org.opencloudengine.garuda.beluga.action.ActionStatus;
import org.opencloudengine.garuda.beluga.action.serviceApp.DeployDockerImageActionRequest;
import org.opencloudengine.garuda.beluga.action.webapp.DeployWebAppActionRequest;
import org.opencloudengine.garuda.beluga.action.webapp.WebAppContextFile;
import org.opencloudengine.garuda.beluga.cloud.watcher.AutoScaleRule;
import org.opencloudengine.garuda.beluga.cloud.watcher.CloudWatcher;
import org.opencloudengine.garuda.beluga.common.util.JsonUtils;
import org.opencloudengine.garuda.beluga.env.SettingManager;
import org.opencloudengine.garuda.beluga.exception.BelugaException;
import org.opencloudengine.garuda.beluga.proxy.HAProxyAPI;
import org.opencloudengine.garuda.beluga.utils.JsonUtil;

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
                               @FormDataParam("file") FormDataContentDisposition fileDetail) throws BelugaException {
        String tempDir = SettingManager.getInstance().getSystemSettings().getString("temp.dir.path");
        org.opencloudengine.garuda.beluga.env.Path uploadPath = SettingManager.getInstance().getEnvironment().filePaths().path(tempDir);
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
            throw new BelugaException("File upload failed. ");
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
     * 리소스 앱을 실행한다.
     * */
    @POST
    @Path("/resources")
    public Response deployResourceApp(@PathParam("clusterId") String clusterId, Map<String, Object> data) throws Exception {
        /*
         * 리소스 앱을 구동한다.
         */
        try {
            Integer port = (Integer) data.get("port");
            Float cpus = null;
            if (data.get("cpus") != null) {
                cpus = Float.parseFloat(data.get("cpus").toString());
            }
            Float memory = null;
            if (data.get("memory") != null) {
                memory = Float.parseFloat(data.get("memory").toString());
            }

            Integer scale = 1; //리소스는 무조건 한개로 고정.
            String resourceId = (String) data.get("id");
            String image = (String) data.get("image");

            DeployDockerImageActionRequest request = new DeployDockerImageActionRequest(clusterId, resourceId, image, port, cpus, memory, scale, null);
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
            Map<String, String> env = (Map<String, String>) data.get("beluga_env");
            List<Integer> ports = null;
            if(port != null) {
                ports = new ArrayList<>();
                ports.add(port);
            }
            Response response = null;
            if (!isUpdate) {
                response = marathonAPI(clusterId).deployCommandApp(appId, command, ports, cpus, memory, scale, env);
            } else {
                response = marathonAPI(clusterId).updateCommandApp(appId, command, ports, cpus, memory, scale, env);
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
            /*
             * Webapp의 port는 app이 정할 수 있는것이 아니므로 environment에 정해져 있는 포트를 연다.
             */
            Integer port = null;
            Integer revision = (Integer) data.get("revision");
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
            String autoScaleConf = (String) data.get("autoScaleConf");


            /*
            * 오토스케일링 rule로 저장한다.
            * */
            if(autoScaleConf != null) {
                AutoScaleRule autoScaleRule = JsonUtil.json2Object(autoScaleConf, AutoScaleRule.class);
                logger.debug("###[{}/{}] autoScaleRule > {}", clusterId, appId, autoScaleRule);

                CloudWatcher cloudWatcher = clusterService(clusterId).getCloudWatcher();
                if (autoScaleRule.isInUse() != null && autoScaleRule.isInUse()) {
                    clusterService(clusterId).getCloudWatcher().updateAutoScaleRule(appId, autoScaleRule);
                } else {
                    cloudWatcher.updateAutoScaleRule(appId, null);
                }
            }

            /*
            * 중요!!! 여기서 리소스 노드와 연동할수 있는 환경변수를 넣어준다.
            * */
            Map<String, String> env = makeEnvVariables(clusterId, data);

            List<WebAppContextFile> webAppFileList = new ArrayList<>();

            if(webAppContext != null && webAppFile != null) {
                webAppFileList.add(new WebAppContextFile(webAppFile, webAppContext));
            }
            if(webAppContext2 != null && webAppFile2 != null) {
                webAppFileList.add(new WebAppContextFile(webAppFile2, webAppContext2));
            }
            DeployWebAppActionRequest request = new DeployWebAppActionRequest(clusterId, appId, revision, webAppFileList, webAppType, port, cpus, memory, scale, env, isUpdate);
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

    private Map<String, String> makeEnvVariables(String clusterId, Map<String, Object> data) {
        Map<String, String> env = new HashMap<>();
        // 환경변수준비.
        List<String> resourceList = (List<String>) data.get("resourceList");
        if(resourceList == null) {
            return null;
        }
        for(String resourceId : resourceList) {
            // marathon으로 resourceId를 조회하면서 task의 host, port를 알아내어 환경변수로 셋팅한다.
            String resourceHost = null;
            String resourcePort = null;
            try {
                Response response = marathonAPI(clusterId).requestGetAPI("/apps/" + resourceId);
                if (response.getStatus() == 200) {
                    String json = response.readEntity(String.class);
                    JsonNode root = JsonUtil.toJsonNode(json);
                    JsonNode app = root.get("app");
                    ArrayNode tasks = (ArrayNode) app.get("tasks");
                    if (tasks != null) {
                        JsonNode task = tasks.get(0);
                        if (task != null) {
                            resourceHost = task.get("host").asText();
                            ArrayNode ports = (ArrayNode) task.get("ports");
                            if (ports.size() > 0) {
                                resourcePort = String.valueOf(ports.get(0).asInt());
                            }
                        }
                    }
                }
            } catch (Throwable t) {
                logger.error("", t);
            }

            if (resourceHost != null && resourcePort != null) {
                //환경변수로 $OOO_HOST, $OOO_PORT 를 사용할 수 있게 해준다.
                String envHostKey = resourceId + ".host";
                String envPortKey = resourceId + ".port";

                env.put(envHostKey, resourceHost);
                env.put(envPortKey, resourcePort);

                String webAppType = (String) data.get("type");
                //Java 어플리케이션의 경우 JAVA_OPTS로 넣어주어야 System.getProperties로 받아올 수 있다.
                //java opts
                String javaOptsString = env.get("JAVA_OPTS");
                if (javaOptsString == null) {
                    javaOptsString = "";
                }
                if (javaOptsString.length() > 0) {
                    javaOptsString += " ";
                }
                javaOptsString += ("-D" + envHostKey + "=" + resourceHost + " -D" + envPortKey + "=" + resourcePort);
                env.put("JAVA_OPTS", javaOptsString);
            }

        }
        return env;
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
            //App 및 하위 서비스 노드 삭제
            response = marathonAPI(clusterId).requestDeleteAPI("/apps/" + appId);
            response = marathonAPI(clusterId).requestDeleteAPI("/groups/" + appId + "-resources");

            // 삭제되었으면 haproxy에서 지워준다.
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

        String deploymentId = (String) entity.get("deploymentId");
        if(deploymentId != null) {
            HAProxyAPI haProxyAPI = clusterService(clusterId).getProxyAPI();
            haProxyAPI.notifyServiceChanged(deploymentId);
        }
    }

}
