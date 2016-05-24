package org.opencloudengine.garuda.beluga.api.rest.v1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.opencloudengine.garuda.beluga.action.ActionException;
import org.opencloudengine.garuda.beluga.action.ActionStatus;
import org.opencloudengine.garuda.beluga.action.serviceApp.DeployDockerImageActionRequest;
import org.opencloudengine.garuda.beluga.action.terminal.TerminalCommitActionRequest;
import org.opencloudengine.garuda.beluga.action.terminal.TerminalOpenActionRequest;
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
@Path("/v1/clusters/{clusterId}/terminal")
public class TerminalAPI extends BaseAPI {

    /**
     * 터미널을 부팅한다.
     */
    @POST
    @Path("/boot")
    public Response bootTerminal(@PathParam("clusterId") String clusterId, Map<String, Object> data) throws Exception {
        /*
         * 리소스 앱을 구동한다.
         */
        try {
            String image = (String) data.get("image");
            String container = (String) data.get("container");
            String host = (String) data.get("host");

            TerminalOpenActionRequest request = new TerminalOpenActionRequest(clusterId, image, container, host);
            ActionStatus actionStatus = actionService().request(request);
            actionStatus.waitForDone();

            Object result = actionStatus.getResult();
            if (result instanceof Map) {
                Map<String, Object> entity = (Map) result;
            } else if (result instanceof Exception) {
                return Response.status(500).entity(((Exception) result).getMessage()).build();
            }
            return Response.ok().entity(JsonUtils.marshal(result)).build();
        } catch (Throwable t) {
            logger.error("", t);
            throw t;
        }
    }

    /**
     * 터미널을 커밋한다.
     */
    @POST
    @Path("/commit")
    public Response commitTerminal(@PathParam("clusterId") String clusterId, Map<String, Object> data) throws Exception {
        /*
         * 리소스 앱을 구동한다.
         */
        try {
            String image = (String) data.get("image");
            String container = (String) data.get("container");
            String cmd = (String) data.get("cmd");
            int port = (int) data.get("port");

            TerminalCommitActionRequest request = new TerminalCommitActionRequest(clusterId, image, container, cmd, port);
            ActionStatus actionStatus = actionService().request(request);
            actionStatus.waitForDone();

            Object result = actionStatus.getResult();
            if (result instanceof Map) {
                Map<String, Object> entity = (Map) result;
            } else if (result instanceof Exception) {
                return Response.status(500).entity(((Exception) result).getMessage()).build();
            }
            return Response.ok().entity(JsonUtils.marshal(result)).build();
        } catch (Throwable t) {
            logger.error("", t);
            throw t;
        }
    }
}
