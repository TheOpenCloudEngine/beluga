package org.opencloudengine.garuda.beluga.mesos.marathon;

import org.opencloudengine.garuda.beluga.cloud.ClusterService;
import org.opencloudengine.garuda.beluga.cloud.ClusterTopology;
import org.opencloudengine.garuda.beluga.env.Environment;
import org.opencloudengine.garuda.beluga.mesos.marathon.model.*;
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

    private String clusterId;
    private ClusterService clusterService;
    private Environment environment;

    public MarathonAPI(ClusterService clusterService, Environment environment) {
        this.clusterService = clusterService;
        this.clusterId = clusterService.getClusterId();
        this.environment = environment;
    }

    protected String chooseMarathonEndPoint() {
        // 여러개중 장애없는 것을 가져온다.
        ClusterTopology topology = clusterService.getClusterTopology();
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

    private WebTarget getWebTarget(String path) {
        Client client = ClientBuilder.newClient();
        return client.target(chooseMarathonEndPoint()).path(path);
    }

    public Response deployDockerApp(String appId, String imageName, List<Integer> usedPorts, Float cpus, Float memory, Integer scale) {
        App appRequest = createDockerTypeApp(appId, imageName, usedPorts, cpus, memory, scale, getDefaultUpgradeStrategy());

        WebTarget target = getWebTarget(API_PATH_APPS);
        return target.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(appRequest));
    }

    public Response updateDockerApp(String appId, String imageName, List<Integer> usedPorts, Float cpus, Float memory, Integer scale) {
        App appRequest = createDockerTypeApp(appId, imageName, usedPorts, cpus, memory, scale, null);

        WebTarget target = getWebTarget(API_PATH_APPS + SLASH + appId);
        return target.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.json(appRequest));
    }

//    public Response scaleApp(String appId, Integer scale) {
//        App appScaleRequest = new App();
//        appScaleRequest.setId(appId);
//        appScaleRequest.setInstances(scale);
//        WebTarget target = getWebTarget(API_PATH_APPS + SLASH + appId);
//        return target.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.json(appScaleRequest));
//    }

    private App createDockerTypeApp(String imageId, String imageName, List<Integer> usedPorts, Float cpus, Float memory, Integer scale, UpgradeStrategy upgradeStrategy) {
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
        if(imageName != null && portMappings != null) {
            Docker docker = new Docker();
            docker.setImage(imageName);
            docker.setForcePullImage(true);
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
        app.setUpgradeStrategy(upgradeStrategy);
        app.setBackoffSeconds(1);
        app.setBackoffFactor(1.15f);
        app.setMaxLaunchDelaySeconds(5 * 60); //5분안에 떠야한다.
        return app;
    }

    public Response deployCommandApp(String appId, String command, List<Integer> usedPorts, Float cpus, Float memory, Integer scale) {
        App appRequest = createCommandApp(appId, command, usedPorts, cpus, memory, scale);

        WebTarget target = getWebTarget(API_PATH_APPS);
        return target.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(appRequest));
    }

    public Response updateCommandApp(String appId, String command, List<Integer> usedPorts, Float cpus, Float memory, Integer scale) {
        App appRequest = createCommandApp(appId, command, usedPorts, cpus, memory, scale);

        WebTarget target = getWebTarget(API_PATH_APPS + SLASH + appId);
        return target.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.json(appRequest));
    }

    private App createCommandApp(String imageId, String command, List<Integer> usedPorts, Float cpus, Float memory, Integer scale) {
        App app = new App();
        app.setId(imageId);
        app.setCmd(command);
        app.setInstances(scale);
        app.setCpus(cpus);
        app.setMem(memory);
        app.setPorts(usedPorts);
        app.setUpgradeStrategy(getDefaultUpgradeStrategy());
        app.setBackoffSeconds(1);
        app.setBackoffFactor(1.15f);
        app.setMaxLaunchDelaySeconds(5 * 60); //5분안에 떠야한다.
        return app;
    }

    private UpgradeStrategy getDefaultUpgradeStrategy() {
        // 업그레이드 방식 설정
        // minimumHealthCapacity=0.5, maximumOverCapacity=0.2
        // 즉, 새로운 앱으로 재시작될때, 최소 1/2은 살아있는 상태를 유지. Rolling 과정에서 총갯수의 0.2정도는 초과해서 컨테이너가 생길수 있다.
        // scale이 5개라면 1개정도는 작업과정에서 잠깐 초과될수 있음.
        UpgradeStrategy upgradeStrategy = new UpgradeStrategy();
        upgradeStrategy.setMinimumHealthCapacity(1.0f);
        upgradeStrategy.setMaximumOverCapacity(0.0f);
        return upgradeStrategy;
    }
    /*
    * Marathon 의 GET API를 직접호출한다.
    * */
    public Response requestGetAPI(String path) {
        WebTarget target = getWebTarget(API_PATH_VERSION + path);
        return target.request(MediaType.APPLICATION_JSON_TYPE).get();
    }

    /*
    * Marathon 의 GET API를 직접호출한다.
    * */
    public String requestGetAPIasString(String path) {
        WebTarget target = getWebTarget(API_PATH_VERSION + path);
        return target.request(MediaType.APPLICATION_JSON_TYPE).get(String.class);
    }

    /*
    * Marathon 의 POST API를 직접호출한다.
    * */
    public Response requestPostAPI(String path, Map<String, Object> data) {
        WebTarget target = getWebTarget(API_PATH_VERSION + path);
        return target.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(data));
    }

    /*
    * Marathon 의 DELETE API를 직접호출한다.
    * */
    public Response requestDeleteAPI(String path) {
        WebTarget target = getWebTarget(API_PATH_VERSION + path);
        return target.request(MediaType.APPLICATION_JSON_TYPE).delete();
    }
}
