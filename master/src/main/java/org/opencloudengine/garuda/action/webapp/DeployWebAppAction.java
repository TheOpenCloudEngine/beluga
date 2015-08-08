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
import org.opencloudengine.garuda.controller.mesos.marathon.model.apps.createapp.req.Container;
import org.opencloudengine.garuda.controller.mesos.marathon.model.apps.createapp.req.CreateApp;
import org.opencloudengine.garuda.controller.mesos.marathon.model.apps.createapp.req.Docker;
import org.opencloudengine.garuda.controller.mesos.marathon.model.apps.createapp.req.PortMapping;
import org.opencloudengine.garuda.env.ClusterPorts;
import org.opencloudengine.garuda.env.ScriptFileNames;
import org.opencloudengine.garuda.exception.GarudaException;
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
        status.registerStep("Deploy to marathon");
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
        int webAppPort = request.getWebAppPort();
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

        List<CommonInstance> list = topology.getManagementList();
        if(list.size() == 0) {
            throw new GarudaException("No registry instance in " + clusterId);
        }
        String registryAddress = list.get(0).getPrivateIpAddress() + ":" + ClusterPorts.REGISTRY_PORT;
        //FIXME 개발시 테스트를 하니, 레지스트리 서버에 public으로 접근해야함..향후 private으로 변경.
        String registryPublicAddress = list.get(0).getPublicIpAddress() + ":" + ClusterPorts.REGISTRY_PORT;

        list = topology.getMesosMasterList();
        if(list.size() == 0) {
            throw new GarudaException("No marathon instance in " + clusterId);
        }
        //FIXME 3개일 경우 자동으로 리다이렉트 되는지 확인필요.
        String marathonAddress = list.get(0).getPublicIpAddress() + ":" + ClusterPorts.MARATHON_PORT;

        /*
        * 1. Merge Image
        *
        * */
        //TODO 자동으로 버전이 올라가도록..
        String newImageName = appId;

        status.walkStep();

        String command = ScriptFileNames.getMergeWebAppScriptPath(environment, webAppType);
        DefaultExecutor executor = new DefaultExecutor();
        CommandLine cmdLine = CommandLine.parse(command);
        // registry address
        cmdLine.addArgument(registryPublicAddress);
        // war,zip file path
        cmdLine.addArgument(webAppFile);
        // new image name
        cmdLine.addArgument(newImageName);

        Logger appLogger = AppLoggerFactory.createLogger(environment, appId);
        InfoLogOutputStream outLog = new InfoLogOutputStream(appLogger);
        ErrorLogOutputStream errLog = new ErrorLogOutputStream(appLogger);
        PumpStreamHandler streamHandler = new PumpStreamHandler(outLog, errLog, null);
        executor.setStreamHandler(streamHandler);
        executor.setExitValue(0);
        int exitValue = executor.execute(cmdLine);

        appLogger.info("Build and push docker images for [{}] process exit with {}", appId, exitValue);

        /*
        * 2. Deploy to Marathon
        * */
        status.walkStep();
        //이름에 주소가 포함되므로, 동일한 registryAddress를 적어주어야 이름을 찾을 수 있다.
        CreateApp createApp = createApp(registryPublicAddress, newImageName, webAppPort, cpus, memory, scale);
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://"+marathonAddress).path("/v2/apps");
        //PUT하면 업데이트
//        Response response = target.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(createApp));
        Response response = target.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.json(createApp));
        logger.debug("response status : {}", response.getStatusInfo());
        logger.debug("response entity : {}", response.getEntity());
    }


    private CreateApp createApp(String registryAddress, String imageId, int containerPort, float cpus, float memory, int scale) {
        List<PortMapping> portMappings = new ArrayList<>();
        PortMapping portMapping = new PortMapping();
        portMapping.setContainerPort(containerPort);
        //서비스 포트는 자동할당이다.
        portMapping.setServicePort(0);
        portMappings.add(portMapping);

        Docker docker = new Docker();
        docker.setImage(String.format("%s/%s", registryAddress, imageId));
        docker.setNetwork("BRIDGE");
        docker.setPrivileged(true);
        docker.setPortMappings(portMappings);

        Container container = new Container();
        container.setDocker(docker);
        container.setType("DOCKER");

        CreateApp createApp = new CreateApp();
        createApp.setId(imageId);
        createApp.setContainer(container);
        createApp.setInstances(scale);
        createApp.setCpus(cpus);
        createApp.setMem(memory);

        return createApp;
    }
}
