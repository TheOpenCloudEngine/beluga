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
        ClusterTopology topology = clusterService.createCluster(clusterId, definitionId, true);
//        ClusterTopology topology = clusterService.getClusterTopology(clusterId);
        status.walkStep();

        /*
        * Configure packages
        * */
        ClusterDefinition clusterDefinition = settingManager.getClusterDefinition(definitionId);
        String userId = clusterDefinition.getUserId();
        String keyPairFile = clusterDefinition.getKeyPairFile();
        int timeout = clusterDefinition.getTimeout();
        //
        // 1. mesos-master
        //
        final MesosMasterConfiguration conf = new MesosMasterConfiguration();

        for (CommonInstance i : topology.getMesosMasterList()) {
            conf.withZookeeperAddress(i.getPublicIpAddress());
        }
        final String mesosClusterName = "mesos-" + clusterId;
        final int quorum = topology.getMesosMasterList().size() / 2 + 1; //과반수.

        logger.debug("Wait {} secs before configuration", DELAY_BEFORE_CONFIGURATION);
        Thread.sleep(DELAY_BEFORE_CONFIGURATION);

        Task task = new Task("configure mesos-masters");

        for (final CommonInstance i : topology.getMesosMasterList()) {
            final String instanceName = i.getName();
            final String ipAddress = i.getPublicIpAddress();
            final SshInfo sshInfo = new SshInfo().withHost(ipAddress).withUser(userId).withPemFile(keyPairFile).withTimeout(timeout);
            final File scriptFile = ScriptFileNames.getFile(environment, ScriptFileNames.ConfigureMesosMaster);

            task.addTodo(new Todo() {
                @Override
                public Object doing() throws Exception {
                    int seq = sequence() + 1;
                    logger.info("[{}/{}] Configure instance {} ({}) ..", seq, taskSize(), instanceName, ipAddress);
                    MesosMasterConfiguration mesosConf = conf.clone();
                    SshClient sshClient = new SshClient();
                    try {
                        sshClient.connect(sshInfo);
                        mesosConf.withMesosClusterName(mesosClusterName).withQuorum(quorum).withZookeeperId(seq);
                        mesosConf.withHostName(i.getPublicIpAddress()).withPrivateIpAddress(i.getPrivateIpAddress());
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

        task.start();

        TaskResult taskResult = task.waitAndGetResult();
        if(taskResult.isSuccess()) {
           logger.info("{} is success.", task.getName());
        } else {
            clusterService.removeClusterIdFromSetting(clusterId);
        }
        status.walkStep();



        /*
         * REBOOT
         */
        status.walkStep();

        return new ActionResult().withResult(true);
    }

}
