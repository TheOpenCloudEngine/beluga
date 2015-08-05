package org.opencloudengine.garuda.utils;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class SshClientTest {
    private static Logger logger = LoggerFactory.getLogger(SshClientTest.class);

    private SshInfo sshInfo;

    @Before
    public void setUp() {
        String host = "192.168.33.10";
        String user = "ubuntu";
        String password = "ubuntu123";
        int port = 22;
        sshInfo = new SshInfo().withHost(host).withUser(user).withPort(port).withPassword(password);
    }
    @Test
    public void testSingleCommand() {
        String cmd = "ls -al";
        SshClient client = new SshClient();
        try {
            int ret = client.connect(sshInfo).runCommand("ubuntu1", cmd);
            System.out.println(ret);
        } finally {
            client.close();
        }
    }

    @Test
    public void testShellScriptFile() {
        File scriptFile = new File("production/resources/script/provision/install_master.sh");
        SshClient client = new SshClient();
        try {
            int ret = client.connect(sshInfo).runCommand("ubuntu1", scriptFile);
            System.out.println(ret);
        } catch (IOException e) {
            logger.error("", e);
        } finally {
            client.close();
        }
    }


}
