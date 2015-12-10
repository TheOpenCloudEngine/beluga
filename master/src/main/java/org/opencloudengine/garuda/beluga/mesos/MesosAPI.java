package org.opencloudengine.garuda.beluga.mesos;

import org.opencloudengine.garuda.beluga.action.task.Task;
import org.opencloudengine.garuda.beluga.action.task.TaskResult;
import org.opencloudengine.garuda.beluga.action.task.Todo;
import org.opencloudengine.garuda.beluga.cloud.ClusterService;
import org.opencloudengine.garuda.beluga.cloud.ClusterTopology;
import org.opencloudengine.garuda.beluga.cloud.CommonInstance;
import org.opencloudengine.garuda.beluga.env.Environment;
import org.opencloudengine.garuda.beluga.env.ScriptFileNames;
import org.opencloudengine.garuda.beluga.exception.BelugaException;
import org.opencloudengine.garuda.beluga.settings.AccessInfo;
import org.opencloudengine.garuda.beluga.utils.SshClient;
import org.opencloudengine.garuda.beluga.utils.SshInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.List;

/**
 * Created by swsong on 2015. 8. 9..
 */
public class MesosAPI {
    protected static Logger logger = LoggerFactory.getLogger(MesosAPI.class);
    private static final String MARATHON_CONTAINER = "docker,mesos";
    private static final String API_PATH_SLAVES = "/master/slaves";
    private String clusterId;
    private ClusterService clusterService;
    private Environment environment;

    public MesosAPI(ClusterService clusterService, Environment environment) {
        this.clusterService = clusterService;
        this.clusterId = clusterService.getClusterId();
        this.environment = environment;
    }

    /*
    * Mesos 의 GET API를 직접호출한다.
    * */
    public Response requestSlaves() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(chooseMesosEndPoint()).path(API_PATH_SLAVES);
        return target.request(MediaType.APPLICATION_JSON_TYPE).get();
    }
    protected String chooseMesosEndPoint() {
        // 여러개중 장애없는 것을 가져온다.
        ClusterTopology topology = clusterService.getClusterTopology();
        List<String> list = topology.getMesosMasterEndPoints();
        if (list == null) {
            return null;
        }
        ///FIXME 제일 처음것을 가져온다.
        return list.get(0);
    }

    private int calculateQuorum(int size) {
        return size / 2 + 1; //과반수.
    }

    public void configureMesosMasterInstances(String definitionId) throws BelugaException {
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
                }
            }
        } catch (Exception e) {
            throw new BelugaException("error while configure mesos master.", e);
        }
    }

    public void configureMesosSlaveInstances(String definitionId) throws BelugaException {
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
            }
        } catch (Exception e) {
            logger.error("", e);
            throw new BelugaException("error while configure mesos slave.", e);
        }
    }

    public void configureMesosSlaveInstances(List<CommonInstance> mesosSlaveList) throws BelugaException {
        try {
            ClusterTopology topology = clusterService.getClusterTopology();
            String definitionId = topology.getDefinitionId();
            //
            //관리인스턴스는 하나만 존재한다고 가정한다.
            //
            CommonInstance managementInstance = topology.getManagementList().get(0);
            final String managementAddress = managementInstance.getPublicIpAddress();

            if (mesosSlaveList.size() > 0) {
                AccessInfo accessInfo = environment.settingManager().getClusterDefinition(definitionId).getAccessInfo();
                String userId = accessInfo.getUserId();
                String keyPairFile = accessInfo.getKeyPairFile();
                int timeout = accessInfo.getTimeout();
                final MesosSlaveConfiguration slaveConf = new MesosSlaveConfiguration();
                for (CommonInstance i : topology.getMesosMasterList()) {
                    slaveConf.withZookeeperAddress(i.getPublicIpAddress());
                }
                Task slaveTask = new Task("configure mesos-slave");
                for (final CommonInstance slave : mesosSlaveList) {
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
            }
        } catch (Exception e) {
            logger.error("", e);
            throw new BelugaException("error while configure mesos slave.", e);
        }
    }
}
