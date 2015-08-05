package org.opencloudengine.garuda.action.cluster;

import org.opencloudengine.garuda.action.ActionResult;
import org.opencloudengine.garuda.action.RequestAction;
import org.opencloudengine.garuda.cloud.ClusterService;
import org.opencloudengine.garuda.cloud.ClusterTopology;
import org.opencloudengine.garuda.cloud.CommonInstance;
import org.opencloudengine.garuda.settings.ClusterDefinition;
import org.opencloudengine.garuda.utils.SshClient;
import org.opencloudengine.garuda.utils.SshInfo;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class CreateClusterAction extends RequestAction {

    public CreateClusterAction() {
        status.registerStep("Prepare instances.");
        status.registerStep("Install packages.");
        status.registerStep("Reboot instances.");
    }
    @Override
    protected ActionResult doAction(Object... params) throws Exception {
        String clusterId = (String) params[0];
        String definitionId = (String) params[1];

        ClusterService clusterService = serviceManager.createService("cluster", ClusterService.class);
        //클러스터가 이미 존재하는지 확인.
        if(clusterService.getClusterTopology(clusterId) != null) {
            return new ActionResult().withError("Cluster %s is already exist.", clusterId);
        }

        status.startStep();
        /*
        * Prepare instances
        * */
        //create instances and wait until available
        ClusterTopology topology = clusterService.createCluster(clusterId, definitionId, true);
        status.walkStep();

        /*
        * Install packages
        * */
        ClusterDefinition clusterDefinition = settingManager.getClusterDefinition(definitionId);
        String userId = clusterDefinition.getUserId();
        String keyPairFile = clusterDefinition.getKeyPairFile();
        for( CommonInstance i : topology.getMesosMasterList()) {
            String instanceId = i.getInstanceId();
            String ipAddress = i.getPublicIpAddress();
            SshInfo sshInfo = new SshInfo().withHost(ipAddress).withUser(userId).withPemFile(keyPairFile);
            SshClient sshClient = new SshClient();
            sshClient.connect(sshInfo);
//            sshClient.runCommand(instanceId, command);
        }



        status.walkStep();

        /*
         * REBOOT
         */
        status.walkStep();

        return new ActionResult().withResult(true);
    }
}
