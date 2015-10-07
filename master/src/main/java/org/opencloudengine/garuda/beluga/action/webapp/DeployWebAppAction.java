package org.opencloudengine.garuda.beluga.action.webapp;

import org.opencloudengine.garuda.beluga.action.ActionException;
import org.opencloudengine.garuda.beluga.action.RunnableAction;
import org.opencloudengine.garuda.beluga.cloud.ClusterService;
import org.opencloudengine.garuda.beluga.cloud.ClusterTopology;
import org.opencloudengine.garuda.beluga.cloud.ClustersService;
import org.opencloudengine.garuda.beluga.env.DockerWebAppPorts;
import org.opencloudengine.garuda.beluga.exception.GarudaException;
import org.opencloudengine.garuda.beluga.mesos.docker.DockerAPI;
import org.opencloudengine.garuda.beluga.mesos.marathon.MarathonAPI;

import javax.ws.rs.core.Response;
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
        Integer revision = request.getRevision();
        List<WebAppContextFile> webAppFileList = request.getWebAppFileList();
        String webAppType = request.getWebAppType();
        //
        //for marathon
        //
//        Integer port = request.getPort();
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

        DockerAPI dockerAPI = clusterService.getDockerAPI();
        boolean needImageBuild = webAppFileList != null && webAppFileList.size() > 0;
        /*
        * 1. Build Image
        * */
        String imageName = registryAddress + "/" + appId + ":" + (revision != null ? revision : "latest");
        status.walkStep();
        if(needImageBuild) {
            int exitValue = dockerAPI.buildWebAppDockerImage(imageName, webAppType, webAppFileList, memory);
            if(exitValue != 0) {
                throw new GarudaException(String.format("Error while build docker image : %s, %s", imageName, webAppType));
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
        List<Integer>  usedPort = DockerWebAppPorts.getPortsByStackId(webAppType);
        MarathonAPI marathonAPI = clusterService.getMarathonAPI();
        Response response = null;
        if(isUpdate) {
            response = marathonAPI.updateDockerApp(appId, imageName, usedPort, cpus, memory, scale);
        } else {
            response = marathonAPI.deployDockerApp(appId, imageName, usedPort, cpus, memory, scale);
        }
        if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
            setResult(response);
        } else if(response.getStatus() == Response.Status.CONFLICT.getStatusCode()) {
            throw new ActionException("App is already running.");
        } else {
            throw new ActionException("error while deploy to marathon : [" + response.getStatus() + "] " + response.getStatusInfo());
        }
    }
}
