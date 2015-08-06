package org.opencloudengine.garuda.action.webapp;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.opencloudengine.garuda.action.RunnableAction;

import java.util.List;

/**
 * Created by swsong on 2015. 8. 6..
 */
public class BuildWebAppImageAction extends RunnableAction<BuildWebAppImageActionRequest> {

    public BuildWebAppImageAction(BuildWebAppImageActionRequest actionRequest) {
        super(actionRequest);
    }

    @Override
    protected void doAction() throws Exception {
        String command = "";
        List<String> args = null;
        DefaultExecutor executor = new DefaultExecutor();
        CommandLine cmdLine = CommandLine.parse(command);
        for (String arg : args) {
            cmdLine.addArgument(arg);
        }
        executor.execute(cmdLine);
    }
}
