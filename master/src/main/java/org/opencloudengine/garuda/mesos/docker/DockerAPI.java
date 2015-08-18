package org.opencloudengine.garuda.mesos.docker;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.opencloudengine.garuda.common.log.ErrorLogOutputStream;
import org.opencloudengine.garuda.common.log.InfoLogOutputStream;
import org.opencloudengine.garuda.env.Environment;
import org.opencloudengine.garuda.env.ScriptFileNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by swsong on 2015. 8. 10..
 */
public class DockerAPI {

    private static final Logger logger = LoggerFactory.getLogger(DockerAPI.class);

    private static final String DOCKER_EXEC = "docker";
    private static final String DOCKER_PUSH_COMMAND = "push";

    private Environment environment;

    public DockerAPI(Environment environment) {
        this.environment = environment;
    }

    public int buildWebAppDockerImage(String imageName, String webAppType, String webAppFile, Float memory) throws IOException {
        String command = ScriptFileNames.getMergeWebAppImageScriptPath(environment, webAppType);
        DefaultExecutor executor = new DefaultExecutor();
        CommandLine cmdLine = CommandLine.parse(command);
        // new image name
        cmdLine.addArgument(imageName);
        // war,zip file path
        cmdLine.addArgument(webAppFile);
        cmdLine.addArgument(String.valueOf(memory.intValue()));

//        Logger appLogger = AppLoggerFactory.createLogger(environment, appId);
        InfoLogOutputStream outLog = new InfoLogOutputStream(logger);
        ErrorLogOutputStream errLog = new ErrorLogOutputStream(logger);
        PumpStreamHandler streamHandler = new PumpStreamHandler(outLog, errLog, null);
        executor.setStreamHandler(streamHandler);
        executor.setExitValue(0);
        int exitValue = executor.execute(cmdLine);
        logger.info("Build docker images process exit with {}", exitValue);
        return exitValue;
    }

    public int pushDockerImageToRegistry(String imageName) throws IOException {
        DefaultExecutor executor = new DefaultExecutor();
        CommandLine cmdLine = CommandLine.parse(DOCKER_EXEC);
        cmdLine.addArgument(DOCKER_PUSH_COMMAND);
        // new image name
        cmdLine.addArgument(imageName);

//        Logger appLogger = AppLoggerFactory.createLogger(environment, appId);
        InfoLogOutputStream outLog = new InfoLogOutputStream(logger);
        ErrorLogOutputStream errLog = new ErrorLogOutputStream(logger);
        PumpStreamHandler streamHandler = new PumpStreamHandler(outLog, errLog, null);
        executor.setStreamHandler(streamHandler);
        executor.setExitValue(0);
        int exitValue = executor.execute(cmdLine);
        logger.info("Push docker images process exit with {}", exitValue);
        return exitValue;
    }
}
