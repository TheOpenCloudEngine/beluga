package org.opencloudengine.garuda.mesos;

import org.opencloudengine.garuda.action.task.Task;
import org.opencloudengine.garuda.action.task.TaskResult;
import org.opencloudengine.garuda.action.task.Todo;
import org.opencloudengine.garuda.cloud.ClusterService;
import org.opencloudengine.garuda.cloud.ClusterTopology;
import org.opencloudengine.garuda.cloud.ClustersService;
import org.opencloudengine.garuda.cloud.CommonInstance;
import org.opencloudengine.garuda.env.Environment;
import org.opencloudengine.garuda.env.ScriptFileNames;
import org.opencloudengine.garuda.env.Settings;
import org.opencloudengine.garuda.exception.GarudaException;
import org.opencloudengine.garuda.mesos.docker.DockerAPI;
import org.opencloudengine.garuda.mesos.marathon.MarathonAPI;
import org.opencloudengine.garuda.service.AbstractClusterService;
import org.opencloudengine.garuda.service.ServiceException;
import org.opencloudengine.garuda.service.common.ClusterServiceManager;
import org.opencloudengine.garuda.service.common.ServiceManager;
import org.opencloudengine.garuda.settings.AccessInfo;
import org.opencloudengine.garuda.settings.ClusterDefinition;
import org.opencloudengine.garuda.utils.SshClient;
import org.opencloudengine.garuda.utils.SshInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by swsong on 2015. 8. 9..
 */
public class MesosAPI {
    protected static Logger logger = LoggerFactory.getLogger(MesosAPI.class);
    private static final String MARATHON_CONTAINER = "docker,mesos";

    private String clusterId;
    private ClusterService clusterService;
    private Environment environment;

    public MesosAPI(ClusterService clusterService, Environment environment) {
        this.clusterService = clusterService;
        this.clusterId = clusterService.getClusterId();
        this.environment = environment;
    }
    private int calculateQuorum(int size) {
        return size / 2 + 1; //과반수.
    }

    public void configureMesosMasterInstances(String definitionId) throws GarudaException {
        try {
            ClusterTopology topology = clusterService.getClusterTopology();
            if (topology.getMesosMasterList().size() > 0) {
                AccessInfo accessInfo = environment.settingManager().getClusterDefinition(definitionId).getAccessInfo();
                String userId = accessInfo.getUserId();
                String keyPairFile = accessInfo.getKeyPairFile();
                int timeout = accessInfo.getTimeout();

                if (topology.getMesosMasterList().size() > 0) {
                    final MesosMasterConfiguration conf = new MesosMasterConfiguration();

                    for (CommonInstance i : topology.getMesosMasterList()) {
                        conf.withZookeeperAddress(i.getPublicIpAddress());
                    }
                    final String mesosClusterName = "mesos-" + clusterId;
                    final int quorum = calculateQuorum(topology.getMesosMasterList().size());


                    Task masterTask = new Task("configure mesos-masters");

                    for (final CommonInstance master : topology.getMesosMasterList()) {
                        final String instanceName = master.getName();
                        final String ipAddress = master.getPublicIpAddress();
                        final SshInfo sshInfo = new SshInfo().withHost(ipAddress).withUser(userId).withPemFile(keyPairFile).withTimeout(timeout);
                        final File scriptFile = ScriptFileNames.getClusterScriptFile(environment, ScriptFileNames.CONFIGURE_MESOS_MASTER);

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

                    //
                    // reboot
                    // TODO 향후에는 각 데몬을 재시작하는 것으로 수정한다.
                    //
                    logger.debug("Reboot mesos-master : {}", topology.getMesosMasterList());
                    clusterService.rebootInstances(ClusterTopology.MESOS_MASTER_ROLE, true);
                    logger.debug("Reboot mesos-master Done.");
                }
            }
        } catch (Exception e) {
            throw new GarudaException("error while configure mesos master.", e);
        }
    }

    public void configureMesosSlaveInstances(String definitionId) throws GarudaException {
        try {
            ClusterTopology topology = clusterService.getClusterTopology();
            //
            //관리인스턴스는 하나만 존재한다고 가정한다.
            //
            CommonInstance managementInstance = topology.getManagementList().get(0);
            final String managementAddress = managementInstance.getPublicIpAddress();

            if (topology.getMesosSlaveList().size() > 0) {
                AccessInfo accessInfo = environment.settingManager().getClusterDefinition(definitionId).getAccessInfo();
                String userId = accessInfo.getUserId();
                String keyPairFile = accessInfo.getKeyPairFile();
                int timeout = accessInfo.getTimeout();
                final MesosSlaveConfiguration slaveConf = new MesosSlaveConfiguration();
                for (CommonInstance i : topology.getMesosMasterList()) {
                    slaveConf.withZookeeperAddress(i.getPublicIpAddress());
                }
                Task slaveTask = new Task("configure mesos-slave");
                for (final CommonInstance slave : topology.getMesosSlaveList()) {
                    final String instanceName = slave.getName();
                    final String ipAddress = slave.getPublicIpAddress();
                    final SshInfo sshInfo = new SshInfo().withHost(ipAddress).withUser(userId).withPemFile(keyPairFile).withTimeout(timeout);
                    final File scriptFile = ScriptFileNames.getClusterScriptFile(environment, ScriptFileNames.CONFIGURE_MESOS_SLAVE);

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

                //
                // reboot
                // TODO 향후에는 각 데몬을 재시작하는 것으로 수정한다.
                //
                logger.debug("Reboot mesos-slave : {}", topology.getMesosSlaveList());
                clusterService.rebootInstances(ClusterTopology.MESOS_SLAVE_ROLE, true);
                logger.debug("Reboot mesos-slave Done.");

            }
        } catch (Exception e) {
            logger.error("", e);
            throw new GarudaException("error while configure mesos slave.", e);
        }
    }
}
