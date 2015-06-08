package org.opencloudengine.garuda.api.rest.v1;

import com.amazonaws.services.ec2.model.Instance;
import org.opencloudengine.garuda.utils.FileTransferUtil;
import org.opencloudengine.garuda.utils.SshUtil;
import org.opencloudengine.garuda.builder.EC2InstanceConfiguration;
import org.opencloudengine.garuda.builder.EC2Interface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 * Created by soo on 2015. 6. 8..
 */
@Path("/v1/clusters")
public class ClusterAPI {

    private static Logger logger = LoggerFactory.getLogger(ClusterAPI.class);

    private static final String EC2_SECRETKEY = "Hpn7rlRJ0ud9m4L3AM5TUeG+wsof2C1jZnKMfgSn";
    private static final String EC2_ACCESSKEY = "AKIAJDFB6F76TUZ5IFXA";
    private static final String EC2_ENDPOINT = "ec2.ap-northeast-1.amazonaws.com";
    private static final String EC2_PEM_PATH = "/Users/soo/soo.pem";
    private static final String INSTALL_MASTER_FILE_PATH = "scripts/provisioning/installMaster.sh";
    private static final String INSTALL_SLAVE_FILE_PATH = "scripts/provisioning/installSlave.sh";
    private static final String INSTALL_REGISTRY_FILE_PATH = "scripts/provisioning/installRegistry.sh";
    private static final int MESES_MASTER_NUM = 3;
    private static final int MESES_SLAVE_NUM = 2;

    String zk = "zk://";

    /**
     * Create cluster.
     * <p>
     * <p>
     * <p>
     * POST /v1/apps
     * <p>
     */
    @POST
    @Path("/")
    public Response createCluster() throws Exception {
        List<Instance> masterIpList = new ArrayList<Instance>();
        List<Instance> slaveIpList = new ArrayList<Instance>();
        String registryIp = null;

        Properties options = new Properties();
        options.put("awsAccessKey", EC2_ACCESSKEY);
        options.put("awsSecretKey", EC2_SECRETKEY);
        options.put("ec2EndPoint", EC2_ENDPOINT);

        EC2InstanceConfiguration ec2InstanceConfiguration = new EC2InstanceConfiguration();
        ec2InstanceConfiguration.setSecurityGroup("garudatest");
        ec2InstanceConfiguration.setEc2InstanceType("t2.micro");
        ec2InstanceConfiguration.setEc2ImageId("ami-936d9d93");
        ec2InstanceConfiguration.setEc2KeyPair("soo");

        EC2Interface ec2Interface = new EC2Interface(options, 10);
        try {
            ec2Interface.initializeEC2Client();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //도커 레지스트리 설치
//        Vector<Instance> instances = ec2Interface.launchEC2Instances(1, ec2InstanceConfiguration);
//        Iterator iterator = instances.iterator();
//        while (iterator.hasNext()) {
//            Instance instance = (Instance) iterator.next();
//            String id = instance.getInstanceId();
//            Instance instance1 = ec2Interface.getRunningInstance(id);
//            while (true) {
//                if (instance1.getPublicIpAddress() == null) {
//                    instance1 = ec2Interface.getRunningInstance(id);
//                } else {
//                    registryIp = instance1.getPublicIpAddress();
//                    break;
//                }
//            }
//        }
//        installRegistry(registryIp);

        //master 설치
        Vector<Instance> instances = ec2Interface.launchEC2Instances(MESES_MASTER_NUM, ec2InstanceConfiguration);
        Iterator iterator = instances.iterator();
        while (iterator.hasNext()) {
            Instance instance = (Instance) iterator.next();
            String id = instance.getInstanceId();
            while (true) {
                String state = ec2Interface.getInstanceStatus(id);
                if (state == "okok") {
                    masterIpList.add(instance);
                    break;
                }
            }
        }
        installMasters(masterIpList);

//        instances = ec2Interface.launchEC2Instances(MESES_SLAVE_NUM, ec2InstanceConfiguration);
//        iterator = instances.iterator();
//        while (iterator.hasNext()) {
//            Instance instance = (Instance) iterator.next();
//            String id = instance.getInstanceId();
//            Instance instance1 = ec2Interface.getRunningInstance(id);
//            while (true) {
//                if (instance1.getPublicIpAddress() == null) {
//                    instance1 = ec2Interface.getRunningInstance(id);
//                } else {
//                    slaveIpList.add(instance1);
//                    break;
//                }
//            }
//        }
//        installSlaves(slaveIpList);

        return Response.serverError().build();
    }

    public void installRegistry(String registryIp) {
        FileTransferUtil.send("docker/php5_apache2", "ubuntu", "", "52.69.70.194", "/tmp", "Dockerfile", "/Users/soo/soo.pem");
    }

    public void installMasters(List<Instance> masterIpList) throws Exception {
        String zServers = "";
        String cluster = "garuda";
        String quorum = "2";
        SshUtil sshUtil = new SshUtil();
        String publicIP, privateIP;

        //zk, zServer 등 쉘 스크립트 매개변수 만들기 위해서
        for (int i = 0; i < MESES_MASTER_NUM; i++) {
            if (i != (MESES_MASTER_NUM - 1)) {
                zk += String.format("%s:2181,", masterIpList.get(i).getPublicIpAddress());
            } else {
                zk += String.format("%s:2181/mesos", masterIpList.get(i).getPublicIpAddress());
            }
            zServers += String.format("server.%s=%s:2188:3188 ", i + 1, masterIpList.get(i).getPublicIpAddress());
        }

        Thread.sleep(10000);

        for (int i = 0; i < MESES_MASTER_NUM; i++) {
            publicIP = masterIpList.get(i).getPublicIpAddress();
            privateIP = masterIpList.get(i).getPrivateIpAddress();
            FileTransferUtil.send(INSTALL_MASTER_FILE_PATH, "ubuntu", "", publicIP, "/tmp", "installMaster.sh", EC2_PEM_PATH);

            sshUtil.sessionLogin(publicIP, "ubuntu", "", EC2_PEM_PATH);
            sshUtil.runCommand("chmod 755 /tmp/installMaster.sh");
            sshUtil.runCommand(String.format("sh /tmp/installMaster.sh %s %s %s %s %s %s %s", zk, cluster, publicIP, privateIP,
                    quorum, i + 1, zServers));
        }
    }

    public void installSlaves(List<Instance> slaveIpList) {

    }
}
