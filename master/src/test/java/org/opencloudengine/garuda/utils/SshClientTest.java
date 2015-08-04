package org.opencloudengine.garuda.utils;

import org.junit.Test;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class SshClientTest {

    @Test
    public void test() {
        SshInfo sshInfo = new SshInfo().withHost("corp.fastcat.co").withUserId("fastcat").withPort(30122).withPemFile("/Users/swsong/.ssh/id_rsa");
        String cmd = "ls ";
        SshClient client = new SshClient();
        try {
            String output = client.connect(sshInfo).runCommand(cmd);
            System.out.println(output);
        } finally {
            client.close();
        }
    }
}
