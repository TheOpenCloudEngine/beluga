package org.opencloudengine.garuda.settings;

/**
 * Created by swsong on 2015. 8. 12..
 */
public class AccessInfo {

    private String userId;
    private String keyPairFile;
    private int timeout;

    public AccessInfo(String userId, String keyPairFile, int timeout) {
        this.userId = userId;
        this.keyPairFile = keyPairFile;
        this.timeout = timeout;
    }

    public String getUserId() {
        return userId;
    }

    public String getKeyPairFile() {
        return keyPairFile;
    }

    public int getTimeout() {
        return timeout;
    }
}
