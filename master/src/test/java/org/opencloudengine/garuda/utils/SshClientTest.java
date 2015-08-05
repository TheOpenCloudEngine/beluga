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
    public void testInstallScriptFile() {
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

    @Test
    public void testSendFile() {
        SshClient client = new SshClient();
        String source = "production/resources/script/provision/install_master.sh";
        String dest = "/tmp/install_master.sh";
        try {
            client.connect(sshInfo).sendFile(source, dest, true);
        } finally {
            client.close();
        }
    }
/**
 * # @param 1 : zookeeper address		zk://localhost:2181/mesos
 # @param 2 : mesos cluster name
 # @param 3 : mesos master public ip
 # @param 4 : mesos master private ip
 # @param 5 : quorum		3
 # @param 6 : zookeeper id
 # @param 7 : zookeeper address 1  server.1=192.168.2.44:2888:3888
 # @param 8 : zookeeper address 2  server.2=192.168.2.45:2888:3888
 # @param 9 : zookeeper address 3  server.3=192.168.2.46:2888:3888
 * */
    @Test
    public void testScriptFileWithParams() {
        File scriptFile = new File("production/resources/script/provision/configure_master.sh");
        String[] args = new String[] {
            "zk://localhost:2181/mesos"
                ,"test-cluster"
                ,sshInfo.getHost()
                ,sshInfo.getHost()
                ,"3"
                ,"1"
                ,"server.1=localhost:2888:3888"
                ,"server.2=localhost:2888:3888"
                ,"server.3=localhost:2888:3888"
        };
        SshClient client = new SshClient();
        try {
            int ret = client.connect(sshInfo).runCommand("ubuntu1", scriptFile, args);
            System.out.println(ret);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
    }
}
