package org.opencloudengine.garuda.beluga.docker;

import com.fasterxml.jackson.databind.JsonNode;
import org.opencloudengine.garuda.beluga.cloud.watcher.ContainerUsage;
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
 * Created by swsong on 2015. 11. 27..
 */
public class CAdvisor1_3_API {
    protected static Logger logger = LoggerFactory.getLogger(CAdvisor1_3_API.class);

    private static final String API_1_3_DOCKER = "/api/v1.3/docker";
    private static final String TARGET_FORMAT = "http://%s:8080";

    public Map<String, List<ContainerUsage>> getAppIdWithDockerContainerUsages(String host, Map<String, List<String>> appIdContainerIdMap) {

        Map<String, List<ContainerUsage>> map = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : appIdContainerIdMap.entrySet()) {

            String appId = entry.getKey();
            List<String> idList = entry.getValue();

            for(String containerId : idList) {
                WebTarget dockerTarget = getDockerWebTarget(host);
                dockerTarget.path(containerId);
                logger.trace("Get cAdvisor Stat Data >> {}", dockerTarget.getUri());
                Response response = dockerTarget.request(MediaType.APPLICATION_JSON_TYPE).get();
                double loadAverage = 0f;
                double cpuPercent = 0f;
                double memoryPercent = 0f;
                long maxMemory = 0;
                long usedMemory = 0;
                if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                    String dockerStatString = response.readEntity(String.class);
                    try {
                        if (dockerStatString != null) {
                            JsonNode obj = JsonUtil.toJsonNode(dockerStatString).get("/docker/" + containerId);
                            JsonNode stats = obj.get("stats");
                            if (stats.size() >= 5) {
                                JsonNode oldestStat = stats.get(stats.size() - 5);
                                JsonNode latestStat = stats.get(stats.size() - 1);

                                loadAverage = latestStat.get("cpu").get("load_average").asDouble(0);
                                long usedCpuClock = latestStat.get("cpu").get("usage").get("total").asLong(0)
                                        - oldestStat.get("cpu").get("usage").get("total").asLong(0);
                                long numCores = latestStat.get("cpu").get("usage").get("per_cpu_usage").size();
                                //5초간의 차이로 cpu를 계산.
                                cpuPercent = usedCpuClock * 100L / (numCores * 1000000000L * 4L); //5초사이 이므로 간격은 4이다.
                                maxMemory = obj.get("spec").get("memory").get("limit").asLong(1);
                                usedMemory = latestStat.get("memory").get("usage").asLong(0);
                                memoryPercent = usedMemory * 100L / maxMemory;
                            }
                        }
                    } catch (IOException e) {
                        logger.error("", e);
                    }
                }

                ContainerUsage containerUsage = new ContainerUsage(appId, containerId, loadAverage, cpuPercent, memoryPercent, maxMemory, usedMemory);
                List<ContainerUsage> usageList = map.get(appId);
                if (usageList == null) {
                    usageList = new ArrayList<>();
                    map.put(appId, usageList);
                }
                usageList.add(containerUsage);
                logger.trace("Got {}", containerUsage);
            }
        }

        return map;
    }

    private WebTarget getDockerWebTarget(String host) {
        Client client = ClientBuilder.newClient();
        return client.target(String.format(TARGET_FORMAT, host)).path(API_1_3_DOCKER);
    }
}
