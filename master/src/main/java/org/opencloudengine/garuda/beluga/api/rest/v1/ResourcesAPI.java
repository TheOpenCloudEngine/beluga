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

            DeployDockerImageActionRequest request = new DeployDockerImageActionRequest(clusterId, resourceId, image, port, cpus, memory, scale, null);
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
