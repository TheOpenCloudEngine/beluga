package org.opencloudengine.garuda.beluga.cadvisor;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.opencloudengine.garuda.beluga.watcher.ContainerUsage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by swsong on 2015. 11. 27..
 */
public class CAdvisor1_3_API {
    protected static Logger logger = LoggerFactory.getLogger(CAdvisor1_3_API.class);

    private static final String API_1_3_CONTAINERS_DOCKER = "/api/v1.3/containers/docker";
    private static final String API_1_3_DOCKER = "/api/v1.3";

    public List<ContainerUsage> getSlaveContainerUsages(String slaveHost) {
        /*
        * 1. 먼저 dockerId 리스트를 얻는다.
        * */
        List<String> nameList = new ArrayList<>();
        Gson gson = new Gson();
        WebTarget containerDockertarget = getContainerDockerWebTarget(slaveHost);
        logger.debug("Get cAdvisor Docker Data >> {}", containerDockertarget.getUri());
        Response response = containerDockertarget.request(MediaType.APPLICATION_JSON_TYPE).get();
        if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
            String dockerListString = response.readEntity(String.class);
            if (dockerListString != null) {
                JsonElement el = gson.toJsonTree(dockerListString);
                JsonObject root = el.getAsJsonObject();
                JsonArray array = root.getAsJsonArray("subcontainers");
                for (int i = 0; i < array.size(); i++) {
                    String name = array.get(i).getAsJsonObject().get("name").getAsString();
                    nameList.add(name);
                }
            }
        }

        List<ContainerUsage> usageList = new ArrayList<>();
        for (String name : nameList) {
            WebTarget dockerTarget = getDockerWebTarget(slaveHost);
            dockerTarget.path(name);
            logger.debug("Get cAdvisor Stat Data >> {}", dockerTarget.getUri());
            response = dockerTarget.request(MediaType.APPLICATION_JSON_TYPE).get();
            String containerId = name.substring(8);
            String mesosTaskId = null;
            String appId = null;
            String image = null;
            float loadAverage = 0f;
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                String dockerStatString = response.readEntity(String.class);
                if (dockerStatString != null) {
                    JsonElement el = gson.toJsonTree(dockerStatString);
                    JsonObject root = el.getAsJsonObject();
                    JsonObject obj = root.getAsJsonObject(name);

                    JsonArray aliases = obj.getAsJsonArray("aliases");
                    for (int i = 0; i < aliases.size(); i++) {
                        String alias = aliases.get(i).getAsString();
                        if (alias.startsWith("mesos-")) {
                            mesosTaskId = alias;
                        }
                    }
                    JsonObject spec = obj.getAsJsonObject("spec");
                    image = spec.getAsJsonPrimitive("image").getAsString();
                    //cAdvisor는 무시한다.
                    if (image.startsWith("google/cadvisor")) {
                        continue;
                    }

                    appId = image;
                    int s = image.indexOf('/');
                    if (s > 0) {
                        appId = appId.substring(s);
                    }
                    int e = image.indexOf(':');
                    if (e > 0) {
                        appId = appId.substring(0, e);
                    }

                    JsonArray stats = obj.getAsJsonArray("stats");
                    if (stats.size() > 0) {
                        JsonObject stat = stats.get(0).getAsJsonObject();
                        loadAverage = stat.getAsJsonObject("cpu").getAsJsonPrimitive("load_average").getAsFloat();
                    }
                }
            }

            usageList.add(new ContainerUsage(containerId, appId, image, mesosTaskId, loadAverage));
        }

        return usageList;
    }

    private WebTarget getContainerDockerWebTarget(String host) {
        Client client = ClientBuilder.newClient();
        return client.target(host).path(API_1_3_CONTAINERS_DOCKER);
    }

    private WebTarget getDockerWebTarget(String host) {
        Client client = ClientBuilder.newClient();
        return client.target(host).path(API_1_3_DOCKER);
    }
}
