package org.opencloudengine.garuda.action.webapp;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.opencloudengine.garuda.action.RunnableAction;
import org.opencloudengine.garuda.cloud.ClusterService;
import org.opencloudengine.garuda.cloud.ClusterTopology;
import org.opencloudengine.garuda.cloud.CommonInstance;
import org.opencloudengine.garuda.common.log.AppLoggerFactory;
import org.opencloudengine.garuda.common.log.ErrorLogOutputStream;
import org.opencloudengine.garuda.common.log.InfoLogOutputStream;
import org.opencloudengine.garuda.env.ClusterPorts;
import org.opencloudengine.garuda.env.DockerWebAppPorts;
import org.opencloudengine.garuda.env.ScriptFileNames;
import org.opencloudengine.garuda.exception.GarudaException;
import org.opencloudengine.garuda.mesos.MesosService;
import org.opencloudengine.garuda.mesos.marathon.model.App;
import org.opencloudengine.garuda.mesos.marathon.model.Container;
import org.opencloudengine.garuda.mesos.marathon.model.Docker;
import org.opencloudengine.garuda.mesos.marathon.model.PortMapping;
import org.slf4j.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
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
        float cpus = request.getCpus();
        float memory = request.getMemory();
        int scale = request.getScale();

        // clusterId를 통해 인스턴스 주소를 받아온다.
        ClusterService clusterService = serviceManager.getService(ClusterService.class);
        ClusterTopology topology = clusterService.getClusterTopology(clusterId);
        if(topology == null) {
            // 그런 클러스터가 없다.
            throw new GarudaException("No such cluster: "+ clusterId);
        }

        String registryAddress = topology.getRegistryAddressPort();
        if(registryAddress == null) {
            throw new GarudaException("No registry instance in " + clusterId);
        }

        MesosService mesosService = serviceManager.getService(MesosService.class);

        /*
        * 1. Build Image
        * */
        //TODO 자동으로 버전이 올라가도록.. :tag 추가.
        String imageName = registryAddress + "/" + appId;

        status.walkStep();
        int exitValue = mesosService.buildWebAppDockerImage(imageName, webAppType, webAppFile);

        /*
         * 2 push image to registry
         * */
        status.walkStep();
        exitValue = mesosService.pushDockerImageToRegistry(imageName);

        /*
        * 3. Deploy to Marathon
        * */
        status.walkStep();
        Integer[] usedPort = DockerWebAppPorts.getPortsByStackId(webAppType);
        App appResponse = mesosService.deployDockerAppToMarathon(clusterId, appId, imageName, usedPort, cpus, memory, scale);

    }
}
