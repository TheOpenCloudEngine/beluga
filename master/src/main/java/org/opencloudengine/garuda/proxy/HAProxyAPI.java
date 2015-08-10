package org.opencloudengine.garuda.proxy;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.opencloudengine.garuda.cloud.ClusterService;
import org.opencloudengine.garuda.cloud.ClusterTopology;
import org.opencloudengine.garuda.env.Environment;
import org.opencloudengine.garuda.env.Path;
import org.opencloudengine.garuda.env.SettingManager;
import org.opencloudengine.garuda.service.common.ServiceManager;
import org.opencloudengine.garuda.utils.SshClient;
import org.opencloudengine.garuda.utils.SshInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.StringWriter;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by swsong on 2015. 8. 10..
 */
public class HAProxyAPI {
    protected static Logger logger = LoggerFactory.getLogger(HAProxyAPI.class);

    private static final String RESTART_COMMAND = "sudo haproxy -f /etc/haproxy/haproxy.cfg -p /var/run/haproxy.pid -sf $(cat /var/run/haproxy.pid)";
    private static final String CONFIG_NAME = "haproxy.cfg";
    private static final String CONFIG_FILE = "/etc/haproxy/" + CONFIG_NAME;
    private static final String CONFIG_TEMPLATE_NAME = CONFIG_NAME + ".template";
    private static final String ENCODING = "utf-8";

    private String templateFilePath;
    private Map<String, Queue<String>> clusterConfigQueueMap;
    private Object lock = new Object();

    private String domain;
    private String ipAddress;
    private SshInfo sshInfo;

    public HAProxyAPI(Environment environment, String domain, String ipAddress, SshInfo sshInfo) {
        templateFilePath = environment.filePaths().configPath().path(CONFIG_TEMPLATE_NAME).file().getAbsolutePath();
        clusterConfigQueueMap = new ConcurrentHashMap<>();
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.sshInfo = sshInfo;
    }

    public void onChangeCluster(String clusterId) {
        VelocityContext context = new VelocityContext();
        ClusterService clusterService = ServiceManager.getInstance().getService(ClusterService.class);

        //TODO 1. topology구성도로 context에 값을 넣어준다.
        ClusterTopology topology = clusterService.getClusterTopology(clusterId);
        topology.getMesosMasterList();




        //TODO 2. marathon을 통해 app별 listening 상태를 받아와서

        VelocityEngine engine = new VelocityEngine();
        engine.init();

        Template template = engine.getTemplate(templateFilePath, ENCODING);
        StringWriter stringWriter = new StringWriter();

        template.merge(context, stringWriter);
        String configString = stringWriter.toString();
        Queue<String> configQueue = null;
        synchronized (lock) {
            configQueue = clusterConfigQueueMap.get(clusterId);
            if (configQueue == null) {
                configQueue = new LinkedBlockingQueue<>();
                clusterConfigQueueMap.put(clusterId, configQueue);
            }
        }

        configQueue.offer(configString);
    }

    private void restartProxy(String clusterId) {

    }

    class ConfigUpdateWorker extends Thread {
        private Path tempDirPath;

        public ConfigUpdateWorker() {
            String tempDir = SettingManager.getInstance().getSystemSettings().getString("temp.dir.path");
            tempDirPath = SettingManager.getInstance().getEnvironment().filePaths().path(tempDir);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    for(Map.Entry<String, Queue<String>> entry : clusterConfigQueueMap.entrySet()) {
                        String clusterId = entry.getKey();
                        Queue<String> configQueue = entry.getValue();
                        int sizeToRemove = configQueue.size();
                        String configString = null;
                        for (int i = 0; i < sizeToRemove; i++) {
                            configString = configQueue.poll();
                        }

                        if (configString != null) {
                            //0. save to file
                            File fileHome = tempDirPath.path(clusterId).file();
                            if (!fileHome.exists()) {
                                fileHome.mkdirs();
                            }
                            File configFile = new File(fileHome, CONFIG_NAME);
                            FileUtils.write(configFile, configString, ENCODING);

                            SshClient sshClient = new SshClient();
                            try {
                                sshClient.connect(sshInfo);

                                //1. Send config to proxy server
                                sshClient.sendFile(configFile.getAbsolutePath(), CONFIG_FILE, false);

                                //2. Restart proxy
                                sshClient.runCommand("proxy update worker", RESTART_COMMAND);
                            } finally {
                                if (sshClient != null) {
                                    sshClient.close();
                                }
                            }
                        }
                        Thread.sleep(1000);
                    }
                } catch (Throwable t) {
                    logger.error("ConfigUpdateWorker error : ", t);
                }
            }
        }
    }

}