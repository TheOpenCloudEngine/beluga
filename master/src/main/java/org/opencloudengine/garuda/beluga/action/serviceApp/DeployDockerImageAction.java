package org.opencloudengine.garuda.beluga.action.serviceApp;

import org.opencloudengine.garuda.beluga.action.ActionException;
import org.opencloudengine.garuda.beluga.action.RunnableAction;
import org.opencloudengine.garuda.beluga.cloud.ClusterService;
import org.opencloudengine.garuda.beluga.cloud.ClusterTopology;
import org.opencloudengine.garuda.beluga.cloud.ClustersService;
import org.opencloudengine.garuda.beluga.env.DockerWebAppPorts;
import org.opencloudengine.garuda.beluga.exception.BelugaException;
import org.opencloudengine.garuda.beluga.mesos.docker.DockerAPI;
import org.opencloudengine.garuda.beluga.mesos.marathon.MarathonAPI;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

/**
 * dockerhub에 저장되어 있는 도커이미지를 디플로이한다.
 * 이미지를 만들필요없이, dockerhub에서 직접가져다 쓴다.
 * Created by swsong on 2015. 8. 6..
 */
public class DeployDockerImageAction extends RunnableAction<DeployDockerImageActionRequest> {

    public DeployDockerImageAction(DeployDockerImageActionRequest actionRequest) {
        super(actionRequest);
    }

    @Override
    protected void doAction() throws Exception {
        DeployDockerImageActionRequest request = getActionRequest();
        String clusterId = request.getClusterId();
        //
        //for marathon
        //
        String appId = request.getAppId();
        String imageName = request.getImageName();
        String imageTag = request.getImageTag();
        Float cpus = request.getCpus();
        Float memory = request.getMemory();
        Integer scale = 1;
        Map<String, String> env = request.getEnv();

        // clusterId를 통해 인스턴스 주소를 받아온다.
        ClusterService clusterService = serviceManager.getService(ClustersService.class).getClusterService(clusterId);
        ClusterTopology topology = clusterService.getClusterTopology();
        if(topology == null) {
            // 그런 클러스터가 없다.
            throw new BelugaException("No such cluster: "+ clusterId);
        }

        String registryAddress = topology.getRegistryAddressPort();
        if(registryAddress == null) {
            throw new BelugaException("No registry instance in " + clusterId);
        }

        String dockerImageName = imageName +  ":" + (imageTag == null ? "latest" : imageTag);

        DockerAPI dockerAPI = clusterService.getDockerAPI();

        /*
        * Deploy to Marathon
        * */
        List<Integer> usedPort = DockerWebAppPorts.getPortsByStackId(imageName);
        MarathonAPI marathonAPI = clusterService.getMarathonAPI();
        Response response = marathonAPI.deployDockerApp(appId, dockerImageName, usedPort, cpus, memory, scale, env);
        if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
            setResult(response);
        } else if(response.getStatus() == Response.Status.CONFLICT.getStatusCode()) {
            throw new ActionException("App is already running.");
        } else {
            throw new ActionException("error while deploy to marathon : [" + response.getStatus() + "] " + response.getStatusInfo());
        }
    }
}
