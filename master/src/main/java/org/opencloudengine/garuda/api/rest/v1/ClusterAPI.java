package org.opencloudengine.garuda.api.rest.v1;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.opencloudengine.garuda.cloud.ClusterService;
import org.opencloudengine.garuda.service.common.ServiceManager;
import org.opencloudengine.garuda.utils.FileTransferUtil;
import org.opencloudengine.garuda.utils.SshClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Created by soo on 2015. 6. 8..
 */
@Path("/v1/clusters")
public class ClusterAPI {

    private static Logger logger = LoggerFactory.getLogger(ClusterAPI.class);

    private static final String EC2_SECRETKEY = "보안상 삭제";
    private static final String EC2_ACCESSKEY = "보안상 삭제";
    private static final String EC2_ENDPOINT = "ec2.ap-northeast-1.amazonaws.com";
    private static final String EC2_PEM_PATH = "/Users/soo/soo.pem";
    private static final String INSTALL_MASTER_FILE_PATH = "scripts/provisioning/installMaster.sh";
    private static final String INSTALL_SLAVE_FILE_PATH = "scripts/provisioning/installSlave.sh";
    private static final String INSTALL_REGISTRY_FILE_PATH = "scripts/provisioning/installRegistry.sh";
    private static final int MESES_MASTER_NUM = 3;
    private static final int MESES_SLAVE_NUM = 2;

    String zk = "zk://";
    String registryPath = "";

    public ClusterAPI() {
        super();
    }

    /**
     * Create cluster.
     */
    @POST
    @Path("/")
    public Response createCluster(@FormDataParam("clusterId") String clusterId
            , @FormDataParam("definition") String definitionId) throws Exception {

        ClusterService clusterService = ServiceManager.getInstance().getService(ClusterService.class);
        clusterService.createCluster(clusterId, definitionId);
        return Response.ok().build();
    }

    public void installRegistry(String registryIp) throws Exception {
        FileTransferUtil.send(INSTALL_REGISTRY_FILE_PATH, "ubuntu", "", registryIp, "/tmp", "installRegistry.sh", EC2_PEM_PATH);
        SshClient sshUtil = new SshClient();
//        sshUtil.sessionLogin(registryIp, "ubuntu", "", EC2_PEM_PATH);
        sshUtil.runCommand("chmod 755 /tmp/installRegistry.sh");
        sshUtil.runCommand("sh /tmp/installRegistry.sh");

        registryPath = String.format("%s:5000", registryIp);
    }

//    public void installMasters(List<Instance> masterIpList) throws Exception {
//        String zServers = "";
//        String cluster = "garuda";
//        String quorum = "2";
//        SshUtil sshUtil = new SshUtil();
//        String publicIp, privateIp;
//
//        //zk, zServer 등 쉘 스크립트 매개변수 만들기 위해서
//        for (int i = 0; i < MESES_MASTER_NUM; i++) {
//            if (i != (MESES_MASTER_NUM - 1)) {
//                zk += String.format("%s:2181,", masterIpList.get(i).getPublicIpAddress());
//            } else {
//                zk += String.format("%s:2181/mesos", masterIpList.get(i).getPublicIpAddress());
//            }
//            zServers += String.format("server.%s=%s:2188:3188 ", i + 1, masterIpList.get(i).getPublicIpAddress());
//        }
//
//        for (int i = 0; i < MESES_MASTER_NUM; i++) {
//            publicIp = masterIpList.get(i).getPublicIpAddress();
//            privateIp = masterIpList.get(i).getPrivateIpAddress();
//            FileTransferUtil.send(INSTALL_MASTER_FILE_PATH, "ubuntu", "", publicIp, "/tmp", "installMaster.sh", EC2_PEM_PATH);
//
//            sshUtil.sessionLogin(publicIp, "ubuntu", "", EC2_PEM_PATH);
//            sshUtil.runCommand("chmod 755 /tmp/installMaster.sh");
//            sshUtil.runCommand(String.format("sh /tmp/installMaster.sh %s %s %s %s %s %s %s", zk, cluster, publicIp, privateIp,
//                    quorum, i + 1, zServers));
//        }
//    }
//
//    public void installSlaves(List<Instance> slaveIpList) throws Exception {
//        String containerizers = "docker";
//        String publicIp, privateIp;
//        SshUtil sshUtil = new SshUtil();
//
//        Iterator iterator = slaveIpList.iterator();
//        while (iterator.hasNext()) {
//            Instance instance = (Instance) iterator.next();
//            publicIp = instance.getPublicIpAddress();
//            privateIp = instance.getPrivateIpAddress();
//
//            FileTransferUtil.send(INSTALL_SLAVE_FILE_PATH, "ubuntu", "", publicIp, "/tmp", "installSlave.sh", EC2_PEM_PATH);
//            sshUtil.sessionLogin(publicIp, "ubuntu", "", EC2_PEM_PATH);
//            sshUtil.runCommand("chmod 755 /tmp/installSlave.sh");
//            sshUtil.runCommand(String.format("sh /tmp/installSlave.sh %s %s %s %s %s", zk, publicIp, privateIp, containerizers, registryPath));
//        }
//
//    }
}
