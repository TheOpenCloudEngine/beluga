package org.opencloudengine.garuda.utils;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class SshClient {

    private static Logger logger = LoggerFactory.getLogger(SshClient.class);
    private static final String DEFAULT_WORKING_PATH = "/tmp/";
    private static final String DEFAULT_ARG_BRACE = "\"";
    private static final int MAX_TRIES = 10;

    private Session session;
    private int timeout;
    private SshInfo sshInfo;

    public SshClient connect(SshInfo sshInfo) {
        this.sshInfo = sshInfo;
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
            logger.debug("Connect {}", sshInfo);
            timeout = sshInfo.getTimeoutInMillis();
            session.setTimeout(timeout);
            tryConnect(session);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        return this;
    }

    private void tryConnect(Object obj) throws JSchException {
        for (int i = 0; i < MAX_TRIES; i++) {
            try {
                logger.debug("Try connect {} times : {}", i + 1, obj);
                if (obj instanceof Channel) {
                    ((Channel) obj).connect(timeout);
                } else if (obj instanceof Session) {
                    ((Session) obj).connect(timeout);
                }
                logger.debug("Connect Success : {}", obj);
                break;
            } catch (Exception e) {
                if (i == MAX_TRIES - 1) {
                    throw e;
                }
                logger.error("connection fail : {}", e.getMessage());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignore) {
                }
                continue;
            }
        }
    }

    public void close() {
        if (session != null) {
            session.disconnect();
        }
    }

    public int runCommand(String label, File scriptFile) throws IOException {
        return runCommand(label, scriptFile, null);
    }

    public int runCommand(String label, File scriptFile, String... args) throws IOException {
        String destFile = DEFAULT_WORKING_PATH + scriptFile.getName();
        sendFile(scriptFile.getAbsolutePath(), destFile, true);

        return runCommand(label, destFile, args);
    }

    public int runCommand(String label, String command) {
        return runCommand(label, command, null);
    }

    public int runCommand(String label, String command, String... args) {

        try {
            final ChannelExec channel = (ChannelExec) session.openChannel("exec");
            try {

                if (args != null) {
                    command = makeCommandLine(command, DEFAULT_ARG_BRACE, args);
                }
                logger.debug("[{}] Send command {}", sshInfo.getHost(), command);
                channel.setCommand(command);
                tryConnect(channel);
//                channel.setErrStream(System.err);
//                channel.setOutputStream(System.out);
//                InputStream stdIn = channel.getInputStream();
//                InputStream errIn = channel.getErrStream();
//                return consumeOutputStream(label, channel, stdIn, errIn);
                return 0;

            } finally {
                if (channel != null) {
                    channel.disconnect();
                }
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private String makeCommandLine(String command, String brace, String... args) {
        StringBuilder commandBuilder = new StringBuilder();
        commandBuilder.append(command);
        if (args != null) {
            commandBuilder.append(" ");
            for (String arg : args) {
                commandBuilder.append(brace).append(arg).append(brace);
                commandBuilder.append(" ");
            }
        }
        return commandBuilder.toString();
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
                    if (strLog != null) {
                        if (label != null) {
                            logger.info("[{}] {}", label, strLog);
                        } else {
                            logger.info(strLog);
                        }
                    }
                    if (errLog != null) {
                        if (label != null) {
                            logger.error("[{}] {}", label, errLog);
                        } else {
                            logger.error(errLog);
                        }
                    }
                } catch (Exception ex) {
                    if (label != null) {
                        logger.error("[{}] {}", label, ex);
                    } else {
                        logger.error("{}", ex.toString());
                    }
                    break;
                }
            }
            if (channel.isClosed()) {
                int exitStatus = channel.getExitStatus();
                if (label != null) {
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

    public void sendFile(String source, String dest, boolean executable) {
        try {
            ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
            try {
                tryConnect(channel);
                logger.debug("[{}] Send file {} to {}", sshInfo.getHost(), source, dest);
                channel.put(source, dest);
                if (executable) {
                    channel.chmod(Integer.parseInt("755", 8), dest);
                }
            } finally {
                if (channel != null) {
                    channel.disconnect();
                }
            }

        } catch (Throwable t) {
            throw new RuntimeException(t);
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
