package org.opencloudengine.garuda.action.webapp;

import org.opencloudengine.garuda.action.RunnableAction;
import org.opencloudengine.garuda.cloud.ClusterService;
import org.opencloudengine.garuda.cloud.ClusterTopology;
import org.opencloudengine.garuda.cloud.ClustersService;
import org.opencloudengine.garuda.env.DockerWebAppPorts;
import org.opencloudengine.garuda.exception.GarudaException;
import org.opencloudengine.garuda.mesos.MesosAPI;
import org.opencloudengine.garuda.mesos.docker.DockerAPI;
import org.opencloudengine.garuda.mesos.marathon.MarathonAPI;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by swsong on 2015. 8. 6..
 */
public class DeployWebAppAction extends RunnableAction<DeployWebAppActionRequest> {

    public DeployWebAppAction(DeployWebAppActionRequest actionRequest) {
        super(actionRequest);
        status.registerStep("Build webapp docker image.");
        status.registerStep("Push image to registry.");
        status.registerStep("Deploy to marathon.");
    }

    @Override
    protected void doAction() throws Exception {
        DeployWebAppActionRequest request = getActionRequest();
        String clusterId = request.getClusterId();
        String appId = request.getAppId();
        String webAppFile = request.getWebAppFile();
        String webAppType = request.getWebAppType();
        //
        //for marathon
        //
        Integer port = request.getPort();
        Float cpus = request.getCpus();
        Float memory = request.getMemory();
        Integer scale = request.getScale();
        Boolean isUpdate = request.getIsUpdate();

        // clusterId를 통해 인스턴스 주소를 받아온다.
        ClusterService clusterService = serviceManager.getService(ClustersService.class).getClusterService(clusterId);
        ClusterTopology topology = clusterService.getClusterTopology();
        if(topology == null) {
            // 그런 클러스터가 없다.
            throw new GarudaException("No such cluster: "+ clusterId);
        }

        String registryAddress = topology.getRegistryAddressPort();
        if(registryAddress == null) {
            throw new GarudaException("No registry instance in " + clusterId);
        }

        DockerAPI dockerAPI = serviceManager.getService(ClustersService.class).getClusterService(clusterId).getDockerAPI();
        boolean needImageBuild = webAppFile != null && webAppFile != null;
        /*
        * 1. Build Image
        * */
        String imageName = registryAddress + "/" + appId;
        status.walkStep();
        if(needImageBuild) {
            int exitValue = dockerAPI.buildWebAppDockerImage(imageName, webAppType, webAppFile);
            if(exitValue != 0) {
                throw new GarudaException(String.format("Error while build docker image : %s, %s, %s", imageName, webAppType, webAppFile));
            }
        }

        /*
         * 2 push image to registry
         * */
        status.walkStep();
        if(needImageBuild) {
            int exitValue = dockerAPI.pushDockerImageToRegistry(imageName);
            if(exitValue != 0) {
                throw new GarudaException(String.format("Error while push docker image : %s", imageName));
            }
        }

        /*
        * 3. Deploy to Marathon
        * */
        status.walkStep();
        List<Integer> usedPort = null;
        if(port == null) {
            usedPort = DockerWebAppPorts.getPortsByStackId(webAppType);
        } else {
            usedPort = new ArrayList<>();
            usedPort.add(port);
        }
        MarathonAPI marathonAPI = serviceManager.getService(ClustersService.class).getClusterService(clusterId).getMarathonAPI();
        Response response = null;
        if(isUpdate) {
            response = marathonAPI.deployDockerApp(appId, imageName, usedPort, cpus, memory, scale);
        } else {
            response = marathonAPI.updateDockerApp(appId, imageName, usedPort, cpus, memory, scale);
        }
        setResult(response);
    }
}
