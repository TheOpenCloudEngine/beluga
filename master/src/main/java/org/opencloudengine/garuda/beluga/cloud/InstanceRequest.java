package org.opencloudengine.garuda.beluga.cloud;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by swsong on 2015. 7. 15..
 */
public class InstanceRequest {
    private String clusterId;
    private String instanceType;
    private String imageId;
    private int volumeSize;
    private Set<String> groups;
    private String keyPair;
    private String[] networks;

    public InstanceRequest(String clusterId, String instanceType, String imageId, int volumeSize, String group
            , String keyPair, String[] networks) {
        this.clusterId = clusterId;
        this.instanceType = instanceType;
        this.imageId = imageId;
        this.volumeSize = volumeSize;
        this.groups = new HashSet<>();
        groups.add(group);
        this.keyPair = keyPair;
        this.networks = networks;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
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

    public Collection<String> getGroups() {
        return groups;
    }

    public void addGroup(String group) {
        this.groups.add(group);
    }

    public String getKeyPair() {
        return keyPair;
    }

    public void setKeyPair(String keyPair) {
        this.keyPair = keyPair;
    }

    public String[] getNetworks() {
        return networks;
    }

    public void setNetworks(String[] networks) {
        this.networks = networks;
    }
}
