package org.opencloudengine.garuda.utils;

import org.junit.Test;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class SshClientTest {

    @Test
    public void test() {
        SshInfo sshInfo = new SshInfo().withHost("corp.fastcat.co").withUserId("fastcat").withPort(30122).withPemFile("/Users/swsong/.ssh/id_rsa");
        String cmd = "ls -al";
        SshClient client = new SshClient();
        try {
            int ret = client.connect(sshInfo).runCommand("dev", cmd);
            System.out.println(ret);
        } finally {
            client.close();
        }
    }
}
