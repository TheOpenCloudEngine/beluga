package org.opencloudengine.garuda.cloud;

/**
 * Created by swsong on 2015. 7. 15..
 */
public class InstanceRequest {
    private String instanceType;
    private String imageId;
    private int volumeSize;
    private String group;
    private String keyPair;

    public InstanceRequest(String instanceType, String imageId, int volumeSize, String group, String keyPair) {
        this.instanceType = instanceType;
        this.imageId = imageId;
        this.volumeSize = volumeSize;
        this.group = group;
        this.keyPair = keyPair;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public int getVolumeSize() {
        return volumeSize;
    }

    public void setVolumeSize(int volumeSize) {
        this.volumeSize = volumeSize;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getKeyPair() {
        return keyPair;
    }

    public void setKeyPair(String keyPair) {
        this.keyPair = keyPair;
    }
}
