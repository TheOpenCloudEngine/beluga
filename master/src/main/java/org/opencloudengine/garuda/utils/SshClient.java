package org.opencloudengine.garuda.utils;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class SshClient {
    private static Logger logger = LoggerFactory.getLogger(SshClient.class);
    private Session session;

    public SshClient connect(SshInfo sshInfo) {
        JSch jsch = new JSch();
        try {
            if (sshInfo.getPrivateKeyFile() != null) {
                jsch.addIdentity(sshInfo.getPrivateKeyFile());
            }
            session = jsch.getSession(sshInfo.getUserId(), sshInfo.getHost(), sshInfo.getPort());
            if (sshInfo.getPassword() != null) {
                session.setPassword(sshInfo.getPassword());
            }
            session.setUserInfo(new DefaultUserInfo());
            session.connect();
        }catch(Throwable t) {
            throw new RuntimeException(t);
        }
        return this;
    }


    public void close() {
        if(session != null) {
            session.disconnect();
        }
    }

    public String runCommand(String command) {

        try {
            String output = null;

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            try {
                channel.setCommand(command);
                channel.setInputStream(null);
                channel.connect();

                InputStream in = channel.getInputStream();
                output = setInAndOutStream(channel, in);

            } finally {
                if (channel != null) {
                    channel.disconnect();
                }
            }
            return output;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private static String setInAndOutStream(Channel channel, InputStream in) throws IOException, JSchException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder outPutResult = new StringBuilder();
        int exitStatus = -100;
        String output = null;
        while (true) {
            while (true) {
                try {
                    String result = br.readLine();
                    if (result == null) {
                        //EOF
                        break;
                    }
                    outPutResult.append(result);
                    outPutResult.append("\n");
                } catch (Exception ex) {
                    logger.error("", ex);
                    break;
                }
            }
            if (channel.isClosed()) {
                exitStatus = channel.getExitStatus();
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception ee) {
            }
        }
        return outPutResult.toString();
    }

    public static class DefaultUserInfo implements UserInfo, UIKeyboardInteractive {
        public String getPassword() {
            return null;
        }

        public boolean promptYesNo(String str) {
            return true;
        }

        public String getPassphrase() {
            return null;
        }

        public boolean promptPassphrase(String message) {
            return false;
        }

        public boolean promptPassword(String message) {
            return false;
        }

        public void showMessage(String message) {
        }

        public String[] promptKeyboardInteractive(String destination,
                                                  String name,
                                                  String instruction,
                                                  String[] prompt,
                                                  boolean[] echo) {
            return null;
        }
    }

}
