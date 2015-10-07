package org.opencloudengine.garuda.beluga.mesos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by swsong on 2015. 8. 5..
 */
/*
# @param 1 : zookeeper address		zk://192.168.2.44:2181,192.168.2.45:2181,192.168.2.46:2181/mesos
# @param 2 : mesos slave public ip
# @param 3 : mesos slave private ip
# @param 4 : mesos container. Comma separated list of containerizer. "docker" for docker, "mesos" for mesos
# @param 5 : docker registry address
* */
public class MesosSlaveConfiguration implements Cloneable {

    private static final String MESOS_ZK_FORMAT = "zk://%s/mesos";
    private static final String ZK_HOST_PORT_FORMAT = "%s:2181";

    private String mesosZookeeperAddress;
    private String hostName;
    private String privateIpAddress;
    private String dockerRegistryAddress;
    private List<String> zookeeperList;
    private Set<String> containerizerSet;

    public MesosSlaveConfiguration() {
        zookeeperList = new ArrayList<>();
        containerizerSet = new HashSet<>();
    }
    public MesosSlaveConfiguration withZookeeperAddress(String zookeeperAddress){
        this.zookeeperList.add(zookeeperAddress);
        return this;
    }
    public MesosSlaveConfiguration withHostName(String hostName){
        this.hostName = hostName;
        return this;
    }

    public MesosSlaveConfiguration withPrivateIpAddress(String privateIpAddress){
        this.privateIpAddress = privateIpAddress;
        return this;
    }

    public MesosSlaveConfiguration withDockerRegistryAddress(String dockerRegistryAddress){
        this.dockerRegistryAddress = dockerRegistryAddress;
        return this;
    }

    public MesosSlaveConfiguration withContainerizer(String containerizer){
        this.containerizerSet.add(containerizer);
        return this;
    }

    public String[] toParameter() {
        StringBuilder zkAddress = new StringBuilder();
        for(int i = 0; i < zookeeperList.size(); i++) {
            String address = zookeeperList.get(i);
            if(i > 0) {
                zkAddress.append(",");
            }
            zkAddress.append(String.format(ZK_HOST_PORT_FORMAT, address));
        }
        mesosZookeeperAddress = String.format(MESOS_ZK_FORMAT, zkAddress.toString());

        List<String> paramList = new ArrayList<>();
        paramList.add(mesosZookeeperAddress);
        paramList.add(hostName);
        paramList.add(privateIpAddress);
        StringBuilder containerizers = new StringBuilder();
        for(String s : containerizerSet) {
            if(containerizers.length() > 0) {
                containerizers.append(",");
            }
            containerizers.append(s);
        }
        paramList.add(String.valueOf(containerizers.toString()));
        paramList.add(String.valueOf(dockerRegistryAddress));

        return paramList.toArray(new String[0]);
    }

    @Override
    public MesosSlaveConfiguration clone() {
        MesosSlaveConfiguration c = new MesosSlaveConfiguration();
        c.mesosZookeeperAddress = this.mesosZookeeperAddress;
        c.hostName = this.hostName;
        c.privateIpAddress = this.privateIpAddress;
        c.dockerRegistryAddress = this.dockerRegistryAddress;
        c.zookeeperList = this.zookeeperList;
        c.containerizerSet = this.containerizerSet;
        return c;
    }
}
