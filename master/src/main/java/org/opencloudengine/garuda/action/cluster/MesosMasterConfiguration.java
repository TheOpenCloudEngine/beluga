package org.opencloudengine.garuda.action.cluster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by swsong on 2015. 8. 5..
 */

public class MesosMasterConfiguration implements Cloneable {
    private static final String MESOS_ZK_FORMAT = "zk://%s/mesos";
    private static final String ZK_HOST_PORT_FORMAT = "%s:2181";
    private static final String ZK_SERVER_FORMAT = "server.%d=%s:2888:3888";

    private String mesosZookeeperAddress;
    private String mesosClusterName;
    private String hostName;
    private String privateIpAddress;
    private int quorum;
    private int zookeeperId;
    private List<String> zookeeperList;

    public MesosMasterConfiguration() {
        zookeeperList = new ArrayList<>();
    }

    public MesosMasterConfiguration withMesosClusterName(String mesosClusterName){
        this.mesosClusterName = mesosClusterName;
        return this;
    }
    public MesosMasterConfiguration withHostName(String hostName){
        this.hostName = hostName;
        return this;
    }

    public MesosMasterConfiguration withPrivateIpAddress(String privateIpAddress){
        this.privateIpAddress = privateIpAddress;
        return this;
    }

    public MesosMasterConfiguration withQuorum(int quorum){
        this.quorum = quorum;
        return this;
    }
    public MesosMasterConfiguration withZookeeperId(int zookeeperId){
        this.zookeeperId = zookeeperId;
        return this;
    }
    public MesosMasterConfiguration withZookeeperAddress(String zookeeperAddress){
        this.zookeeperList.add(zookeeperAddress);
        return this;
    }

    public String[] toParameter() {
        StringBuilder zkAddress = new StringBuilder();
        List<String> zookeeperServerList = new ArrayList<>();
        for(int i = 0; i < zookeeperList.size(); i++) {
            String address = zookeeperList.get(i);
            if(i > 0) {
                zkAddress.append(",");
            }
            zkAddress.append(String.format(ZK_HOST_PORT_FORMAT, address));
            zookeeperServerList.add(String.format(ZK_SERVER_FORMAT, i + 1, address));
        }
        mesosZookeeperAddress = String.format(MESOS_ZK_FORMAT, zkAddress.toString());

        List<String> paramList = new ArrayList<>();
        paramList.add(mesosZookeeperAddress);
        paramList.add(mesosClusterName);
        paramList.add(hostName);
        paramList.add(privateIpAddress);
        paramList.add(String.valueOf(quorum));
        paramList.add(String.valueOf(zookeeperId));
        for(String zkServer : zookeeperServerList) {
            paramList.add(zkServer);
        }

        return paramList.toArray(new String[0]);
    }

    @Override
    public MesosMasterConfiguration clone() {
        MesosMasterConfiguration c = new MesosMasterConfiguration();
        c.mesosZookeeperAddress = this.mesosZookeeperAddress;
        c.mesosClusterName = this.mesosClusterName;
        c.hostName = this.hostName;
        c.privateIpAddress = this.privateIpAddress;
        c.quorum = this.quorum;
        c.zookeeperId = this.zookeeperId;
        c.zookeeperList = this.zookeeperList;
        return c;
    }

}
