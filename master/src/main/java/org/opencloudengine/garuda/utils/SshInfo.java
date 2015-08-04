package org.opencloudengine.garuda.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class SshInfo {
    private static Logger logger = LoggerFactory.getLogger(SshInfo.class);

    private String host;
    private int port = 22;
    private String userId;
    private String password;
    private String privateKeyFile;

    public SshInfo withHost(String host) {
        this.host = host;
        return this;
    }

    public SshInfo withPort(int port) {
        this.port = port;
        return this;
    }

    public SshInfo withUserId(String userId) {
        this.userId = userId;
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

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getPrivateKeyFile() {
        return privateKeyFile;
    }
}
