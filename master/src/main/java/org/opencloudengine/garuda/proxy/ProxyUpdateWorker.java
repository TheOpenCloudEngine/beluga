package org.opencloudengine.garuda.proxy;

import org.apache.commons.io.FileUtils;
import org.opencloudengine.garuda.env.Path;
import org.opencloudengine.garuda.env.SettingManager;
import org.opencloudengine.garuda.utils.SshClient;
import org.opencloudengine.garuda.utils.SshInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Queue;

/**
 * Created by swsong on 2015. 8. 10..
 */
public class ProxyUpdateWorker extends Thread {
    protected static Logger logger = LoggerFactory.getLogger(ProxyUpdateWorker.class);

    private Path tempDirPath;
    private String clusterId;
    private SshInfo sshInfo;
    private Queue<String> configQueue;

    public ProxyUpdateWorker(String clusterId, SshInfo sshInfo, Queue<String> queue) {
        this.clusterId = clusterId;
        this.sshInfo = sshInfo;
        String tempDir = SettingManager.getInstance().getSystemSettings().getString("temp.dir.path");
        tempDirPath = SettingManager.getInstance().getEnvironment().filePaths().path(tempDir);
        configQueue = queue;
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            try {
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
                    File configFile = new File(fileHome, HAProxyAPI.CONFIG_NAME);
                    FileUtils.write(configFile, configString, HAProxyAPI.ENCODING);

                    SshClient sshClient = new SshClient();
                    try {
                        sshClient.connect(sshInfo);

                        //1. Send config to proxy server
                        sshClient.sendFile(configFile.getAbsolutePath(), HAProxyAPI.TMP_CONFIG_FILE, false);
                        //2. Copy config to tmp dir
                        sshClient.runCommand("proxy config copy", HAProxyAPI.COPY_CONFIG_COMMAND);
                        //3. Restart proxy
                        sshClient.runCommand("proxy update worker", HAProxyAPI.RESTART_COMMAND);
                    } finally {
                        if (sshClient != null) {
                            sshClient.close();
                        }
                    }
                }
                Thread.sleep(1000);
            } catch (Throwable t) {
                logger.error("ConfigUpdateWorker error : ", t);
            }
        }
    }
}
