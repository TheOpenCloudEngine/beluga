package org.opencloudengine.garuda.utils;

import com.jcraft.jsch.*;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class SshClient {
    private static Logger logger = LoggerFactory.getLogger(SshClient.class);
    private Session session;
    private final static String DEFAULT_FILE_ENCODING = "utf-8";

    public SshClient connect(SshInfo sshInfo) {
        JSch jsch = new JSch();
        try {
            if (sshInfo.getPrivateKeyFile() != null) {
                jsch.addIdentity(sshInfo.getPrivateKeyFile());
            }
            session = jsch.getSession(sshInfo.getUser(), sshInfo.getHost(), sshInfo.getPort());
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

    public int runCommand(String label, File scriptFile) throws IOException {
        return runCommand(label, scriptFile, DEFAULT_FILE_ENCODING);
    }
    public int runCommand(String label, File scriptFile, String encoding) throws IOException {
        String script = FileUtils.readFileToString(scriptFile, encoding);
        return runCommand(label, script);
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

                InputStream stdIn = channel.getInputStream();
                InputStream errIn = channel.getErrStream();
                return consumeOutputStream(label, channel, stdIn, errIn);

            } finally {
                if (channel != null) {
                    channel.disconnect();
                }
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }


    private static int consumeOutputStream(String label, Channel channel, InputStream stdIn, InputStream errIn) {
        BufferedReader stdReader = new BufferedReader(new InputStreamReader(stdIn));
        BufferedReader errReader = new BufferedReader(new InputStreamReader(errIn));
        while (true) {
            while (true) {
                try {
                    String strLog = stdReader.readLine();
                    String errLog = errReader.readLine();
                    if (strLog == null && errLog == null) {
                        //EOF
                        break;
                    }
                    if(strLog != null) {
                        if (label != null) {
                            logger.info("[{}] {}", label, strLog);
                        } else {
                            logger.info(strLog);
                        }
                    }
                    if(errLog != null) {
                        if (label != null) {
                            logger.error("[{}] {}", label, errLog);
                        } else {
                            logger.error(errLog);
                        }
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
