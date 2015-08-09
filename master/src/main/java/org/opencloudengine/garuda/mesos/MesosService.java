package org.opencloudengine.garuda.mesos;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.opencloudengine.garuda.action.task.Task;
import org.opencloudengine.garuda.action.task.TaskResult;
import org.opencloudengine.garuda.action.task.Todo;
import org.opencloudengine.garuda.cloud.ClusterService;
import org.opencloudengine.garuda.cloud.ClusterTopology;
import org.opencloudengine.garuda.cloud.CommonInstance;
import org.opencloudengine.garuda.common.log.ErrorLogOutputStream;
import org.opencloudengine.garuda.common.log.InfoLogOutputStream;
import org.opencloudengine.garuda.env.Environment;
import org.opencloudengine.garuda.env.ScriptFileNames;
import org.opencloudengine.garuda.env.Settings;
import org.opencloudengine.garuda.exception.GarudaException;
import org.opencloudengine.garuda.mesos.marathon.message.GetApp;
import org.opencloudengine.garuda.mesos.marathon.model.App;
import org.opencloudengine.garuda.mesos.marathon.model.Container;
import org.opencloudengine.garuda.mesos.marathon.model.Docker;
import org.opencloudengine.garuda.mesos.marathon.model.PortMapping;
import org.opencloudengine.garuda.service.AbstractService;
import org.opencloudengine.garuda.service.ServiceException;
import org.opencloudengine.garuda.service.common.ServiceManager;
import org.opencloudengine.garuda.settings.ClusterDefinition;
import org.opencloudengine.garuda.utils.SshClient;
import org.opencloudengine.garuda.utils.SshInfo;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by swsong on 2015. 8. 9..
 */
public class MesosService extends AbstractService {
    private static final String MARATHON_CONTAINER = "docker,mesos";
    private ClusterService clusterService;

    public MesosService(Environment environment, Settings settings, ServiceManager serviceManager) {
        super(environment, settings, serviceManager);
    }

    @Override
    protected boolean doStart() throws ServiceException {
        clusterService = serviceManager.getService(ClusterService.class);
        return false;
    }

    @Override
    protected boolean doStop() throws ServiceException {
        return false;
    }

    @Override
    protected boolean doClose() throws ServiceException {
        return false;
    }

    private int calculateQuorum(int size) {
        return size / 2 + 1; //과반수.
    }

    public void configureMesosMasterInstances(String clusterId, String definitionId) throws GarudaException {

        try {
            ClusterTopology topology = clusterService.getClusterTopology(clusterId);
            if (topology.getMesosMasterList().size() > 0) {
                ClusterDefinition clusterDefinition = environment.settingManager().getClusterDefinition(definitionId);
                String userId = clusterDefinition.getUserId();
                String keyPairFile = clusterDefinition.getKeyPairFile();
                int timeout = clusterDefinition.getTimeout();

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
                    clusterService.rebootInstances(topology, topology.getMesosMasterList(), true);
                    logger.debug("Reboot mesos-master Done.");
                }
            }
        } catch (Exception e) {
            throw new GarudaException("error while configure mesos master.", e);
        }
    }

    public void configureMesosSlaveInstances(String clusterId, String definitionId) throws GarudaException {

        try {
            ClusterTopology topology = clusterService.getClusterTopology(clusterId);
            //
            //관리인스턴스는 하나만 존재한다고 가정한다.
            //
            CommonInstance managementInstance = topology.getManagementList().get(0);
            final String managementAddress = managementInstance.getPublicIpAddress();

            if (topology.getMesosSlaveList().size() > 0) {
                ClusterDefinition clusterDefinition = environment.settingManager().getClusterDefinition(definitionId);
                String userId = clusterDefinition.getUserId();
                String keyPairFile = clusterDefinition.getKeyPairFile();
                int timeout = clusterDefinition.getTimeout();
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
                clusterService.rebootInstances(topology, topology.getMesosSlaveList(), true);
                logger.debug("Reboot mesos-slave Done.");

            }
        } catch (Exception e) {
            throw new GarudaException("error while configure mesos slave.", e);
        }
    }

    public int buildWebAppDockerImage(String imageName, String webAppType, String webAppFile) throws IOException {
        String command = ScriptFileNames.getMergeWebAppImageScriptPath(environment, webAppType);
        DefaultExecutor executor = new DefaultExecutor();
        CommandLine cmdLine = CommandLine.parse(command);
        // war,zip file path
        cmdLine.addArgument(webAppFile);
        // new image name
        cmdLine.addArgument(imageName);

//        Logger appLogger = AppLoggerFactory.createLogger(environment, appId);
        InfoLogOutputStream outLog = new InfoLogOutputStream(logger);
        ErrorLogOutputStream errLog = new ErrorLogOutputStream(logger);
        PumpStreamHandler streamHandler = new PumpStreamHandler(outLog, errLog, null);
        executor.setStreamHandler(streamHandler);
        executor.setExitValue(0);
        int exitValue = executor.execute(cmdLine);
        logger.info("Build and push docker images process exit with {}", exitValue);
        return exitValue;
    }

    public int pushDockerImageToRegistry(String imageName) throws IOException {
        String command = ScriptFileNames.getPushImageToRegistryScriptPath(environment);
        DefaultExecutor executor = new DefaultExecutor();
        CommandLine cmdLine = CommandLine.parse(command);
        // new image name
        cmdLine.addArgument(imageName);

//        Logger appLogger = AppLoggerFactory.createLogger(environment, appId);
        InfoLogOutputStream outLog = new InfoLogOutputStream(logger);
        ErrorLogOutputStream errLog = new ErrorLogOutputStream(logger);
        PumpStreamHandler streamHandler = new PumpStreamHandler(outLog, errLog, null);
        executor.setStreamHandler(streamHandler);
        executor.setExitValue(0);
        int exitValue = executor.execute(cmdLine);
        logger.info("Build and push docker images process exit with {}", exitValue);
        return exitValue;
    }


    private String getMarathonAPIAddres() {
        // 여러개중 장애없는 것을 가져온다.

        //TODO

        return "";
    }
    public WebTarget getMarathonWebTarget(String path) {
        Client client = ClientBuilder.newClient();
        return client.target(getMarathonAPIAddres()).path(path);
    }

    public void deployDockerAppToMarathon(String appId, String imageName, int[] usedPorts, float cpus, float memory, int scale) {
        App appRequest = createDockerTypeApp(appId, imageName, usedPorts, cpus, memory, scale);

        WebTarget target = getMarathonWebTarget("/v2/apps");
        GetApp getApp = target.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(appRequest), GetApp.class);
        App appResponse = getApp.getApp();
    }

    public void updateDockerAppToMarathon(String appId, String imageName, int[] usedPorts, float cpus, float memory, int scale) {
        App appRequest = createDockerTypeApp(appId, imageName, usedPorts, cpus, memory, scale);

        WebTarget target = getMarathonWebTarget("/v2/apps");
        GetApp getApp = target.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.json(appRequest), GetApp.class);
        App appResponse = getApp.getApp();
    }

    private App createDockerTypeApp(String imageId, String imageName, int[] usedPorts, float cpus, float memory, int scale) {
        List<PortMapping> portMappings = new ArrayList<>();
        if(usedPorts != null) {
            for (int port : usedPorts) {
                PortMapping portMapping = new PortMapping();
                portMapping.setContainerPort(port);
                //서비스 포트는 자동할당이다.
                portMapping.setServicePort(0);
                portMappings.add(portMapping);
            }
        }
        Docker docker = new Docker();
        docker.setImage(imageName);
        docker.setNetwork("BRIDGE");
        docker.setPrivileged(true);
        docker.setPortMappings(portMappings);

        Container container = new Container();
        container.setDocker(docker);
        container.setType("DOCKER");

        App createApp = new App();
        createApp.setId(imageId);
        createApp.setContainer(container);
        createApp.setInstances(scale);
        createApp.setCpus(cpus);
        createApp.setMem(memory);

        return createApp;
    }
}
