package org.opencloudengine.garuda.mesos.marathon;

import org.opencloudengine.garuda.cloud.ClusterService;
import org.opencloudengine.garuda.cloud.ClusterTopology;
import org.opencloudengine.garuda.env.Environment;
import org.opencloudengine.garuda.mesos.marathon.message.GetApp;
import org.opencloudengine.garuda.mesos.marathon.message.GetApps;
import org.opencloudengine.garuda.mesos.marathon.model.*;
import org.opencloudengine.garuda.service.common.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by swsong on 2015. 8. 10..
 */
public class MarathonAPI {

    private static final Logger logger = LoggerFactory.getLogger(MarathonAPI.class);

    private static final String API_PATH_VERSION = "/v2";
    private static final String API_PATH_APPS = "/v2/apps";
    private static final String SLASH = "/";

    private ClusterService clusterService;

    public MarathonAPI() {
        this.clusterService = ServiceManager.getInstance().getService(ClusterService.class);
    }

    protected String chooseMarathonEndPoint(String clusterId) {
        // 여러개중 장애없는 것을 가져온다.
        ClusterTopology topology = clusterService.getClusterTopology(clusterId);
        List<String> list = topology.getMarathonEndPoints();
        if(list == null) {
            return null;
        }
        Client client = ClientBuilder.newClient();
        for(String endPoint : list) {
            String response = client.target(endPoint).path("/ping").request(MediaType.TEXT_PLAIN).get(String.class);
            if("pong".equalsIgnoreCase(response.trim())) {
                return endPoint;
            }
        }
        return null;
    }

    private WebTarget getWebTarget(String clusterId, String path) {
        Client client = ClientBuilder.newClient();
        return client.target(chooseMarathonEndPoint(clusterId)).path(path);
    }

    public List<App> getApps(String clusterId) {
        WebTarget target = getWebTarget(clusterId, API_PATH_APPS);
        GetApps getApps = target.request(MediaType.APPLICATION_JSON_TYPE).get(GetApps.class);
        return getApps.getApps();
    }

    public App getApp(String clusterId, String appId) {
        WebTarget target = getWebTarget(clusterId, API_PATH_APPS + SLASH + appId);
        GetApp getApp = target.request(MediaType.APPLICATION_JSON_TYPE).get(GetApp.class);
        return getApp.getApp();
    }

    public App deployDockerApp(String clusterId, String appId, String imageName, Integer[] usedPorts, Float cpus, Float memory, Integer scale) {
        App appRequest = createDockerTypeApp(appId, imageName, usedPorts, cpus, memory, scale);

        WebTarget target = getWebTarget(clusterId, API_PATH_APPS);
        GetApp getApp = target.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(appRequest), GetApp.class);
        return getApp.getApp();
    }

    public App updateDockerApp(String clusterId, String appId, String imageName, Integer[] usedPorts, Float cpus, Float memory, Integer scale) {
        App appRequest = createDockerTypeApp(appId, imageName, usedPorts, cpus, memory, scale);

        WebTarget target = getWebTarget(clusterId, API_PATH_APPS);
        GetApp getApp = target.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.json(appRequest), GetApp.class);
        return getApp.getApp();
    }

    private App createDockerTypeApp(String imageId, String imageName, Integer[] usedPorts, Float cpus, Float memory, Integer scale) {
        List<PortMapping> portMappings = null;
        if(usedPorts != null) {
            portMappings = new ArrayList<>();
            for (int port : usedPorts) {
                PortMapping portMapping = new PortMapping();
                portMapping.setContainerPort(port);
                //서비스 포트는 자동할당이다.
                portMapping.setServicePort(0);
                portMappings.add(portMapping);
            }
        }
        Container container = null;
        if(imageName != null) {
            Docker docker = new Docker();
            docker.setImage(imageName);
            docker.setNetwork("BRIDGE");
            docker.setPrivileged(true);
            docker.setPortMappings(portMappings);
            container = new Container();
            container.setDocker(docker);
            container.setType("DOCKER");
        }

        App app = new App();
        app.setId(imageId);
        app.setContainer(container);
        app.setInstances(scale);
        app.setCpus(cpus);
        app.setMem(memory);

        // 업그레이드 방식 설정
        // minimumHealthCapacity=0.5, maximumOverCapacity=0.2
        // 즉, 새로운 앱으로 재시작될때, 최소 1/2은 살아있는 상태를 유지. Rolling 과정에서 총갯수의 0.2정도는 초과해서 컨테이너가 생길수 있다.
        // scale이 5개라면 1개정도는 작업과정에서 잠깐 초과될수 있음.
        UpgradeStrategy upgradeStrategy = new UpgradeStrategy();
        upgradeStrategy.setMinimumHealthCapacity(0.5f);
        upgradeStrategy.setMaximumOverCapacity(0.2f);
        app.setUpgradeStrategy(upgradeStrategy);
        return app;
    }

    /*
    * Marathon 의 GET API를 직접호출한다.
    * */
    public Response requestGetAPI(String clusterId, String path) {
        WebTarget target = getWebTarget(clusterId, API_PATH_VERSION + path);
        return target.request(MediaType.APPLICATION_JSON_TYPE).get();
    }

    /*
    * Marathon 의 POST API를 직접호출한다.
    * */
    public Response requestPostAPI(String clusterId, String path, Map<String, Object> data) {
        WebTarget target = getWebTarget(clusterId, API_PATH_VERSION + path);
        return target.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(data));
    }

    /*
    * Marathon 의 DELETE API를 직접호출한다.
    * */
    public Response requestDeleteAPI(String clusterId, String path) {
        WebTarget target = getWebTarget(clusterId, API_PATH_VERSION + path);
        return target.request(MediaType.APPLICATION_JSON_TYPE).delete();
    }
}
