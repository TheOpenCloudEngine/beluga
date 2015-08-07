package org.opencloudengine.garuda.action.webapp;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.opencloudengine.garuda.action.RunnableAction;
import org.opencloudengine.garuda.common.log.AppLoggerFactory;
import org.opencloudengine.garuda.common.log.ErrorLogOutputStream;
import org.opencloudengine.garuda.common.log.InfoLogOutputStream;
import org.opencloudengine.garuda.controller.mesos.marathon.model.apps.createapp.req.Container;
import org.opencloudengine.garuda.controller.mesos.marathon.model.apps.createapp.req.CreateApp;
import org.opencloudengine.garuda.controller.mesos.marathon.model.apps.createapp.req.Docker;
import org.opencloudengine.garuda.controller.mesos.marathon.model.apps.createapp.req.PortMapping;
import org.opencloudengine.garuda.env.ScriptFileNames;
import org.opencloudengine.garuda.env.SettingManager;
import org.opencloudengine.garuda.env.Settings;
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
        String appId = request.getAppId();
        String webAppFile = request.getWebAppFile();
        String webAppType = request.getWebAppType();

        //
        //for marathon
        //
        int webAppPort = 80; //Stack별로 정해져 있다. 80고정.
        float cpus = 0.1f;
        float memory = 32;
        int scale = 1;

        String registryIPAddress = "";

        String marathonAddress = "";
        /*
        * 1. Merge Image
        *
        * */

        String newImageName = appId;

        status.walkStep();
        String command = ScriptFileNames.getMergeWebAppScriptPath(environment, webAppType);
        DefaultExecutor executor = new DefaultExecutor();
        CommandLine cmdLine = CommandLine.parse(command);
        // registry address
        cmdLine.addArgument(registryIPAddress);
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

        appLogger.info("Build docker images process exit with {}", appId, exitValue);
        /*
        * 2. Deploy to Marathon
        * */

        status.walkStep();
        CreateApp createApp = createApp(newImageName, webAppPort, cpus, memory, scale);
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(marathonAddress).path("/v2/apps");
        Response response = target.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(createApp));
    }


    //TODO 여기 코드들은 Action으로 옮겨야함.
    private CreateApp createApp(String imageId, int containerPort, float cpus, float memory, int scale) {
        List<PortMapping> portMappings = new ArrayList<>();
        PortMapping portMapping = new PortMapping();
        portMapping.setContainerPort(containerPort);
        //서비스 포트는 자동할당이다.
        portMapping.setServicePort(0);
        portMappings.add(portMapping);

        Settings settings = SettingManager.getInstance().getSystemSettings();
        String registryAddress = settings.getString("registry.address");

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
