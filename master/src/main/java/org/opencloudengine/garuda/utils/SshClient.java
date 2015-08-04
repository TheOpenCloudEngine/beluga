package org.opencloudengine.garuda.utils;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
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

    public int runCommand(String command) {
        return runCommand(null, command);
    }
    public int runCommand(String label, String command) {

        try {
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            try {
                channel.setCommand(command);
                channel.setInputStream(null);
                channel.connect();

                InputStream in = channel.getInputStream();
                return consumeOutputStream(label, channel, in);

            } finally {
                if (channel != null) {
                    channel.disconnect();
                }
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

//    public String runCommand(String command) {
//
//        try {
//            String output = null;
//
//            ChannelExec channel = (ChannelExec) session.openChannel("exec");
//            try {
//                channel.setCommand(command);
//                channel.setInputStream(null);
//                channel.connect();
//
//                InputStream in = channel.getInputStream();
//                output = consumeOutputStream(channel, in);
//
//            } finally {
//                if (channel != null) {
//                    channel.disconnect();
//                }
//            }
//            return output;
//        } catch (Throwable t) {
//            throw new RuntimeException(t);
//        }
//    }



    private static int consumeOutputStream(String label, Channel channel, InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        while (true) {
            while (true) {
                try {
                    String result = br.readLine();
                    if (result == null) {
                        //EOF
                        break;
                    }
                    if(label != null) {
                        logger.info("[{}] {}", label, result);
                    } else {
                        logger.info(result);
                    }
                } catch (Exception ex) {
                    if(label != null) {
                        logger.error("[{}] {}", label, ex);
                    } else {
                        logger.error("{}", ex.toString());
                    }
                    break;
                }
            }
            if (channel.isClosed()) {
                int exitStatus = channel.getExitStatus();
                if(label != null) {
                    logger.info("[{}] Exit code {}", label, exitStatus);
                } else {
                    logger.info("Exit code {}", exitStatus);
                }
                return exitStatus;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception ignore) {
            }
        }
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
