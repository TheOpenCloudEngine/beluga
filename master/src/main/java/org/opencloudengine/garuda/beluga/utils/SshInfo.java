package org.opencloudengine.garuda.beluga.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class SshInfo {
    private static Logger logger = LoggerFactory.getLogger(SshInfo.class);

    private String host;
    private int port = 22;
    private String user;
    private String password;
    private String privateKeyFile;
    private int timeout = 60;

    public SshInfo withHost(String host) {
        this.host = host;
        return this;
    }

    public SshInfo withPort(int port) {
        this.port = port;
        return this;
    }

    public SshInfo withUser(String user) {
        this.user = user;
        return this;
    }

    public SshInfo withPassword(String password) {
        this.password = password;
        return this;
    }

    public SshInfo withPemFile(String pemFilePath) {
       this.privateKeyFile = pemFilePath;
        return this;
    }

    //초단위.
    public SshInfo withTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getPrivateKeyFile() {
        return privateKeyFile;
    }

    public int getTimeout() {
        return timeout;
    }

    public int getTimeoutInMillis() {
        return timeout * 1000;
    }

    @Override
    public String toString() {
        return String.format("[%s] user[%s] host[%s] port[%s] privateKeyFile[%s] timeout[%s]", getClass().getSimpleName(), user, host, port, privateKeyFile, timeout);
    }
}
