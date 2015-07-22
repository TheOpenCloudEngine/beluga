package org.opencloudengine.garuda.cloud;

import org.jclouds.compute.domain.NodeMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 현재 생성되어 운영중인 클러스터의 형상을 담고있다.
 * Created by swsong on 2015. 7. 20..
 */
public class ClusterTopology {


    public static final String IAAS_TYPE_KEY = "iaasType";
    public static final String CLUSTER_ID_KEY = "clusterId";

    public static final String GARUDA_MASTER_ROLE = "garuda-master";
    public static final String PROXY_ROLE = "proxy";
    public static final String MESOS_MASTER_ROLE = "mesos-master";
    public static final String MESOS_SLAVE_ROLE = "mesos-slave";
    public static final String DOCKER_REGISTRY_ROLE = "docker-registry";
    public static final String MANAGEMENT_DB_ROLE = "management-db";
    public static final String SERVICE_NODES_ROLE = "service-nodes";

    private String clusterId;
    private String iaasType;

    private List<NodeMetadata> garudaMasterList;
    private List<NodeMetadata> proxyList;
    private List<NodeMetadata> mesosMasterList;
    private List<NodeMetadata> mesosSlaveList;
    private List<NodeMetadata> dockerRegistryList;
    private List<NodeMetadata> managementDbList;
    private List<NodeMetadata> serviceNodeList;


    public ClusterTopology(String clusterId, String iaasType) {
        this.clusterId = clusterId;
        this.iaasType = iaasType;

        garudaMasterList = new ArrayList<>();
        proxyList = new ArrayList<>();
        mesosMasterList = new ArrayList<>();
        mesosSlaveList = new ArrayList<>();
        dockerRegistryList = new ArrayList<>();
        managementDbList = new ArrayList<>();
        serviceNodeList = new ArrayList<>();
    }

    public String getClusterId() {
        return clusterId;
    }

    public String getIaasType() {
        return iaasType;
    }

    public List<NodeMetadata> getAllNodeList() {
        List<NodeMetadata> list = new ArrayList<>();
        list.addAll(garudaMasterList);
        list.addAll(proxyList);
        list.addAll(mesosMasterList);
        list.addAll(mesosSlaveList);
        list.addAll(dockerRegistryList);
        list.addAll(managementDbList);
        list.addAll(serviceNodeList);
        return list;
    }

    public List<NodeMetadata> getGarudaMasterList() {
        return garudaMasterList;
    }

    public List<NodeMetadata> getProxyList() {
        return proxyList;
    }

    public List<NodeMetadata> getMesosMasterList() {
        return mesosMasterList;
    }

    public List<NodeMetadata> getMesosSlaveList() {
        return mesosSlaveList;
    }

    public List<NodeMetadata> getDockerRegistryList() {
        return dockerRegistryList;
    }

    public List<NodeMetadata> getManagementDbList() {
        return managementDbList;
    }

    public List<NodeMetadata> getServiceNodeList() {
        return serviceNodeList;
    }

    public void addNode(String role, NodeMetadata nodeMetadata) {
        switch (role) {
            case GARUDA_MASTER_ROLE:
                garudaMasterList.add(nodeMetadata);
                break;
            case PROXY_ROLE:
                proxyList.add(nodeMetadata);
                break;
            case MESOS_MASTER_ROLE:
                mesosMasterList.add(nodeMetadata);
                break;
            case MESOS_SLAVE_ROLE:
                mesosSlaveList.add(nodeMetadata);
                break;
            case DOCKER_REGISTRY_ROLE:
                dockerRegistryList.add(nodeMetadata);
                break;
            case MANAGEMENT_DB_ROLE:
                managementDbList.add(nodeMetadata);
                break;
            case SERVICE_NODES_ROLE:
                serviceNodeList.add(nodeMetadata);
                break;
            default:
                throw new RuntimeException("no such role : " + role);
        }
    }

    @Override
    public String toString() {
        return storeProperties().toString();
    }

    public Properties storeProperties() {
        Properties props = new Properties();
        props.setProperty(CLUSTER_ID_KEY, clusterId);
        props.setProperty(IAAS_TYPE_KEY, iaasType);
        putProps(props, GARUDA_MASTER_ROLE, garudaMasterList);
        putProps(props, PROXY_ROLE, proxyList);
        putProps(props, MESOS_MASTER_ROLE, mesosMasterList);
        putProps(props, MESOS_SLAVE_ROLE, mesosSlaveList);
        putProps(props, DOCKER_REGISTRY_ROLE, dockerRegistryList);
        putProps(props, MANAGEMENT_DB_ROLE, managementDbList);
        putProps(props, SERVICE_NODES_ROLE, serviceNodeList);
        return props;
    }

    private void putProps(Properties props, String roleName, List<NodeMetadata> nodeList) {
        StringBuffer addressList = new StringBuffer();
        for(NodeMetadata d : nodeList) {
            String id = d.getId();
            if(addressList.length() > 0) {
                addressList.append(",");
            }
            addressList.append(id);
        }
        props.put(roleName, addressList.toString());
    }

}
