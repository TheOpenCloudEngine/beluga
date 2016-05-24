package org.opencloudengine.garuda.beluga.mesos.docker;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.opencloudengine.garuda.beluga.action.webapp.WebAppContextFile;
import org.opencloudengine.garuda.beluga.common.log.LogOutputStream;
import org.opencloudengine.garuda.beluga.common.progress.ProcessLogHandler;
import org.opencloudengine.garuda.beluga.env.Environment;
import org.opencloudengine.garuda.beluga.env.ScriptFileNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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

    public int buildWebAppDockerImage(String imageName, String webAppType, List<WebAppContextFile> webAppFileList, Float memory) throws IOException {
        String command = ScriptFileNames.getMergeWebAppImageScriptPath(environment, webAppType);
        DefaultExecutor executor = new DefaultExecutor();
        CommandLine cmdLine = CommandLine.parse(command);
        // new image name
        cmdLine.addArgument(imageName);
        cmdLine.addArgument(String.valueOf(memory.intValue()));
        // war,zip file path
        for (WebAppContextFile contextFile : webAppFileList) {
            cmdLine.addArgument(contextFile.getWebAppFile());
            cmdLine.addArgument(contextFile.getContext());
        }

        PumpStreamHandler streamHandler = new PumpStreamHandler(
                new ProcessLogHandler(logger), new ProcessLogHandler(logger)
        );
        executor.setStreamHandler(streamHandler);
        executor.setExitValue(0);
        logger.debug("Command : {}", cmdLine);


        int exitValue = executor.execute(cmdLine);
        logger.info("Build docker images process exit with {}", exitValue);
        return exitValue;
    }

    public int pushDockerImageToRegistry(String imageName) throws IOException {
        String command = ScriptFileNames.getPushImageToRegistryScriptPath(environment);
        DefaultExecutor executor = new DefaultExecutor();
        CommandLine cmdLine = CommandLine.parse(command);
        cmdLine.addArgument(imageName);

        PumpStreamHandler streamHandler = new PumpStreamHandler(
                new ProcessLogHandler(logger), new ProcessLogHandler(logger)
        );
        executor.setStreamHandler(streamHandler);
        executor.setExitValue(0);


        int exitValue = executor.execute(cmdLine);
        logger.info("Push docker images process exit with {}", exitValue);
        return exitValue;
    }

    public int terminalOpen(String containerName, String imageName, int bindPort, int containerPort) throws IOException {

        String command = ScriptFileNames.getTerminalOpenScriptPath(environment);

        DefaultExecutor executor = new DefaultExecutor();
        CommandLine cmdLine = CommandLine.parse(command);
        cmdLine.addArgument(containerName);
        cmdLine.addArgument(imageName);
        cmdLine.addArgument(Integer.toString(bindPort));
        cmdLine.addArgument(Integer.toString(containerPort));

        PumpStreamHandler streamHandler = new PumpStreamHandler(
                new ProcessLogHandler(logger), new ProcessLogHandler(logger)
        );
        executor.setStreamHandler(streamHandler);
        executor.setExitValue(0);

        int exitValue = executor.execute(cmdLine);
        logger.info("Terminal Open process exit with {}", exitValue);
        return exitValue;
    }

    public int terminalCommit(String containerName, String imageName, String cmd, int port) throws IOException {

        String command = ScriptFileNames.getTerminalCommitScriptPath(environment);

        DefaultExecutor executor = new DefaultExecutor();
        CommandLine cmdLine = CommandLine.parse(command);
        cmdLine.addArgument(containerName);
        cmdLine.addArgument(imageName);

        //배쉬 스크립트에서 ,__ 스트링을 화이트 스페이스로 치환하도록 한다.
        //TODO CMD 깔끔한 배쉬 스크립트 작성을 구현하도록 한다.
        String[] split = cmd.split(" ");
        String join = StringUtils.join(split, ",__");
        cmdLine.addArgument(join);

        cmdLine.addArgument(Integer.toString(port));

        PumpStreamHandler streamHandler = new PumpStreamHandler(
                new ProcessLogHandler(logger), new ProcessLogHandler(logger)
        );
        executor.setStreamHandler(streamHandler);
        executor.setExitValue(0);

        int exitValue = executor.execute(cmdLine);
        logger.info("Terminal Open process exit with {}", exitValue);
        return exitValue;
    }

    public String getMacHost() throws IOException {
        String command = ScriptFileNames.getMacHostScriptPath(environment);

        //ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CollectingLogOutputStream outputStream = new CollectingLogOutputStream();
        CommandLine cmdLine = CommandLine.parse(command);

        DefaultExecutor executor = new DefaultExecutor();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
        executor.setStreamHandler(streamHandler);
        executor.setExitValue(0);

        int exitValue = executor.execute(cmdLine);
        logger.info("Terminal Open process exit with {}", exitValue);

        List<String> lines = outputStream.getLines();
        String s = lines.get(lines.size() - 1);
        System.out.println(s);

        return (s);
    }

    public class CollectingLogOutputStream extends org.apache.commons.exec.LogOutputStream {
        private final List<String> lines = new LinkedList<String>();

        @Override
        protected void processLine(String line, int level) {
            lines.add(line);
        }

        public List<String> getLines() {
            return lines;
        }
    }
}
