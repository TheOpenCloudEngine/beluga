package org.opencloudengine.garuda.action.webapp;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by swsong on 2015. 8. 6..
 */
public class ExecTest {

    @Test
    public void test1() throws IOException {
        String command = "find /";

        DefaultExecutor executor = new DefaultExecutor();
        CommandLine cmdLine = CommandLine.parse(command);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();

        // handle error and output of the process and write them to the given
        // out stream
        PumpStreamHandler handler = new PumpStreamHandler(out, err, null);
        executor.setStreamHandler(handler);


        int exitValue = executor.execute(cmdLine);
        System.out.println("exitValue: " + exitValue);
        System.out.println("STDOUT: " + out.toString());
        System.out.println("Err: "+ err.toString());
    }
}
