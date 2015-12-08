package org.opencloudengine.garuda.beluga.api.rest.v1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONObject;
import org.opencloudengine.garuda.beluga.action.ActionException;
import org.opencloudengine.garuda.beluga.action.ActionStatus;
import org.opencloudengine.garuda.beluga.action.serviceApp.DeployDockerImageActionRequest;
import org.opencloudengine.garuda.beluga.action.webapp.DeployWebAppActionRequest;
import org.opencloudengine.garuda.beluga.action.webapp.WebAppContextFile;
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
import java.util.*;

/**
 * Created by swsong on 2015. 8. 7..
 */
@Path("/v1/clusters/{clusterId}/resources")
public class ResourcesAPI extends BaseAPI {

    /**
     * 리소스 앱을 실행한다.
     * */
    @POST
    @Path("/")
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
            String env = (String) data.get("env");
            Map<String, String> envMap = new HashMap<>();
            if(env != null && env.length() > 0) {
                JSONObject json = new JSONObject(env);
                Iterator<String> iter = json.keys();
                while(iter.hasNext()) {
                    String key = iter.next();
                    envMap.put(key, json.getString(key));
                }
            }
            DeployDockerImageActionRequest request = new DeployDockerImageActionRequest(clusterId, resourceId, image, port, cpus, memory, scale, envMap);
            ActionStatus actionStatus = actionService().request(request);
            actionStatus.waitForDone();

            Object result = actionStatus.getResult();
            if(result instanceof Exception) {
                return Response.status(500).entity(((Exception)result).getMessage()).build();
            }
            return Response.ok().build();
        } catch (Throwable t) {
            logger.error("", t);
            throw t;
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
}
