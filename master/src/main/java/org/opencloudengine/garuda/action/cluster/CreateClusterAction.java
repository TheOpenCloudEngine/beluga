package org.opencloudengine.garuda.action.cluster;

import org.opencloudengine.garuda.action.ActionResult;
import org.opencloudengine.garuda.action.RequestAction;
import org.opencloudengine.garuda.action.task.Task;
import org.opencloudengine.garuda.action.task.TaskResult;
import org.opencloudengine.garuda.action.task.Todo;
import org.opencloudengine.garuda.cloud.ClusterService;
import org.opencloudengine.garuda.cloud.ClusterTopology;
import org.opencloudengine.garuda.cloud.CommonInstance;
import org.opencloudengine.garuda.common.ScriptFileNames;
import org.opencloudengine.garuda.settings.ClusterDefinition;
import org.opencloudengine.garuda.utils.SshClient;
import org.opencloudengine.garuda.utils.SshInfo;

import java.io.File;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class CreateClusterAction extends RequestAction {

    private static final int DELAY_BEFORE_CONFIGURATION = 60;//secs
    private static final String MARATHON_CONTAINER = "docker,mesos";

    public CreateClusterAction() {
        status.registerStep("Prepare instances.");
        status.registerStep("Install packages.");
        status.registerStep("Reboot instances.");
    }

    @Override
    protected ActionResult doAction(Object... params) throws Exception {
        String clusterId = (String) params[0];
        String definitionId = (String) params[1];

        ClusterService clusterService = serviceManager.getService(ClusterService.class);

        status.startStep();
        /*
        * Prepare instances
        * */
        //create instances and wait until available
        logger.debug("Create Cluster..");
        ClusterTopology topology = clusterService.createCluster(clusterId, definitionId, true);
        logger.debug("Create Cluster.. Done.");
//        ClusterTopology topology = clusterService.getClusterTopology(clusterId);
        status.walkStep();

        logger.debug("Wait {} secs before configuration", DELAY_BEFORE_CONFIGURATION);
        Thread.sleep(DELAY_BEFORE_CONFIGURATION);


        //
        //관리인스턴스는 하나만 존재한다고 가정한다.
        //
//        CommonInstance managementInstance= topology.getManagementList().get(0);
//        final String managementAddress = managementInstance.getPublicIpAddress();//나중에 private IP로 바꾼다.
        final String managementAddress = "localhost";

        /*
        * Configure packages
        * */
        ClusterDefinition clusterDefinition = settingManager.getClusterDefinition(definitionId);
        String userId = clusterDefinition.getUserId();
        String keyPairFile = clusterDefinition.getKeyPairFile();
        int timeout = clusterDefinition.getTimeout();

        //
        // 1.1 mesos-master
        //
        if(topology.getMesosMasterList().size() > 0) {
            final MesosMasterConfiguration conf = new MesosMasterConfiguration();

            for (CommonInstance i : topology.getMesosMasterList()) {
                conf.withZookeeperAddress(i.getPublicIpAddress());
            }
            final String mesosClusterName = "mesos-" + clusterId;
            final int quorum = topology.getMesosMasterList().size() / 2 + 1; //과반수.


            Task masterTask = new Task("configure mesos-masters");

            for (final CommonInstance master : topology.getMesosMasterList()) {
                final String instanceName = master.getName();
                final String ipAddress = master.getPublicIpAddress();
                final SshInfo sshInfo = new SshInfo().withHost(ipAddress).withUser(userId).withPemFile(keyPairFile).withTimeout(timeout);
                final File scriptFile = ScriptFileNames.getFile(environment, ScriptFileNames.ConfigureMesosMaster);

                masterTask.addTodo(new Todo() {
                    @Override
                    public Object doing() throws Exception {
                        int seq = sequence() + 1;
                        logger.info("[{}/{}] Configure instance {} ({}) ..", seq, taskSize(), instanceName, ipAddress);
                        MesosMasterConfiguration mesosConf = conf.clone();
                        SshClient sshClient = new SshClient();
                        try {
                            sshClient.connect(sshInfo);
                            mesosConf.withMesosClusterName(mesosClusterName).withQuorum(quorum).withZookeeperId(seq);
                            mesosConf.withHostName(master.getPublicIpAddress()).withPrivateIpAddress(master.getPrivateIpAddress());
                            int retCode = sshClient.runCommand(instanceName, scriptFile, mesosConf.toParameter());
                            logger.info("[{}/{}] Configure instance {} ({}) Done. RET = {}", seq, taskSize(), instanceName, ipAddress, retCode);
                        } finally {
                            if (sshClient != null) {
                                sshClient.close();
                            }
                        }
                        return null;
                    }
                });
            }

            masterTask.start();

            TaskResult masterTaskResult = masterTask.waitAndGetResult();
            if (masterTaskResult.isSuccess()) {
                logger.info("{} is success.", masterTask.getName());
            }
//          clusterService.removeClusterIdFromSetting(clusterId);


            //
            // 1.2 Reboot mesos master
            //
            logger.debug("Reboot mesos-master");
            clusterService.rebootInstances(topology, topology.getMesosMasterList(), true);
            logger.debug("Reboot mesos-master Done.");
        }

        //
        // 2.1 mesos-slave
        //
        if(topology.getMesosSlaveList().size() > 0) {
            final MesosSlaveConfiguration slaveConf = new MesosSlaveConfiguration();
            for (CommonInstance i : topology.getMesosMasterList()) {
                slaveConf.withZookeeperAddress(i.getPublicIpAddress());
            }
            Task slaveTask = new Task("configure mesos-slave");
            for (final CommonInstance slave : topology.getMesosSlaveList()) {
                final String instanceName = slave.getName();
                final String ipAddress = slave.getPublicIpAddress();
                final SshInfo sshInfo = new SshInfo().withHost(ipAddress).withUser(userId).withPemFile(keyPairFile).withTimeout(timeout);
                final File scriptFile = ScriptFileNames.getFile(environment, ScriptFileNames.ConfigureMesosSlave);

                slaveTask.addTodo(new Todo() {
                    @Override
                    public Object doing() throws Exception {
                        int seq = sequence() + 1;
                        logger.info("[{}/{}] Configure instance {} ({}) ..", seq, taskSize(), instanceName, ipAddress);
                        MesosSlaveConfiguration mesosConf = slaveConf.clone();
                        SshClient sshClient = new SshClient();
                        try {
                            sshClient.connect(sshInfo);
                            mesosConf.withHostName(slave.getPublicIpAddress()).withPrivateIpAddress(slave.getPrivateIpAddress());
                            mesosConf.withContainerizer(MARATHON_CONTAINER).withDockerRegistryAddress(managementAddress);
                            int retCode = sshClient.runCommand(instanceName, scriptFile, mesosConf.toParameter());
                            logger.info("[{}/{}] Configure instance {} ({}) Done. RET = {}", seq, taskSize(), instanceName, ipAddress, retCode);
                        } finally {
                            if (sshClient != null) {
                                sshClient.close();
                            }
                        }
                        return null;
                    }
                });
            }

            slaveTask.start();

            TaskResult slaveTaskResult = slaveTask.waitAndGetResult();
            if (slaveTaskResult.isSuccess()) {
                logger.info("{} is success.", slaveTask.getName());
            }

            status.walkStep();

            /*
             * REBOOT
             */
            logger.debug("Reboot mesos-slave");
            clusterService.rebootInstances(topology, topology.getMesosSlaveList(), true);
            logger.debug("Reboot mesos-slave Done.");
            status.walkStep();

        }
        return new ActionResult().withResult(true);
    }

}
