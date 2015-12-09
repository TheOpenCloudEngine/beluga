package org.opencloudengine.garuda.beluga.docker;

import com.amazonaws.util.json.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.*;
import org.opencloudengine.garuda.beluga.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by swsong on 2015. 12. 9..
 */
public class DockerRemoteApi {
    protected static Logger logger = LoggerFactory.getLogger(DockerRemoteApi.class);
    private static final String API_CONTAINER_LIST = "/containers/json";
    private static final String API_CONTAINER_INSPECT = "/containers/%s/json";
    private static final String TARGET_FORMAT = "http://%s:4243";

    public Map<String, List<String>> getAppIdWithContainerIdMap(String host) {
        /*
        * 1. 먼저 container id 리스트를 얻는다.
        * */
        List<String> idList = new ArrayList<>();
        WebTarget containerListTarget = getContainerListWebTarget(host);
        logger.debug("Call {}", containerListTarget.getUri());
        Response response = containerListTarget.request(MediaType.APPLICATION_JSON_TYPE).get();
        if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
            String idListString = response.readEntity(String.class);
            try {
                if (idListString != null) {
                    JsonNode node = JsonUtil.toJsonNode(idListString);
                    for (int i = 0; i < node.size(); i++) {
                        String id = node.get(i).get("Id").asText();
                        idList.add(id);
                    }
                }
            } catch (IOException e) {
                logger.error("", e);
            }
        }

        /*
        * 2. Inspect결과의 환경변수에서 appId를 얻는다.
        * */
        Map<String, List<String>> appIdContainerIdMap = new HashMap<>();
        for(String containerId : idList) {
            WebTarget containerInspectTarget = getContainerInspectWebTarget(host, containerId);
            response = containerInspectTarget.request(MediaType.APPLICATION_JSON_TYPE).get();
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                String idListString = response.readEntity(String.class);
                try {
                    if (idListString != null) {
                        JsonNode node = JsonUtil.toJsonNode(idListString);
                        JsonNode envArray = node.get("Config").get("Env");
                        for (int i = 0; i < envArray.size(); i++) {
                            String value = envArray.get(i).asText();
                            if(value.startsWith("MARATHON_APP_ID")) {
                                String appId = value.substring("MARATHON_APP_ID=".length() + 1);
                                List<String> cidList = appIdContainerIdMap.get(appId);
                                if(cidList == null) {
                                    cidList = new ArrayList<>();
                                    appIdContainerIdMap.put(appId, cidList);
                                }
                                cidList.add(containerId);
                                logger.debug("Got AppId[{}] ContainerId[{}]", appId, containerId);
                                break;
                            }
                        }
                    }
                } catch (IOException e) {
                    logger.error("", e);
                }
            }
        }
        return appIdContainerIdMap;
    }
    private WebTarget getContainerListWebTarget(String host) {
        Client client = ClientBuilder.newClient();
        return client.target(String.format(TARGET_FORMAT, host)).path(API_CONTAINER_LIST);
    }

    private WebTarget getContainerInspectWebTarget(String host, String containerId) {
        Client client = ClientBuilder.newClient();
        return client.target(String.format(TARGET_FORMAT, host)).path(String.format(API_CONTAINER_INSPECT, containerId));
    }
}
