package org.opencloudengine.garuda.proxy;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.opencloudengine.garuda.cloud.ClusterService;
import org.opencloudengine.garuda.cloud.ClusterTopology;
import org.opencloudengine.garuda.cloud.CommonInstance;
import org.opencloudengine.garuda.env.ClusterPorts;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by swsong on 2015. 8. 10..
 */
public class HAProxyAPI {
    protected static Logger logger = LoggerFactory.getLogger(HAProxyAPI.class);

    protected static final String RESTART_COMMAND = "sudo haproxy -f /etc/haproxy/haproxy.cfg -p /var/run/haproxy.pid -sf $(cat /var/run/haproxy.pid)";
    protected static final String CONFIG_NAME = "haproxy.cfg";
    protected static final String CONFIG_FILE = "/etc/haproxy/" + CONFIG_NAME;
    protected static final String CONFIG_TEMPLATE_NAME = CONFIG_NAME + ".template";
    protected static final String ENCODING = "utf-8";

    private String templateFilePath;
    private Map<String, Queue<String>> clusterConfigQueueMap;

    public HAProxyAPI(Environment environment, Map<String, Queue<String>> queueMap) {
        templateFilePath = environment.filePaths().configPath().path(CONFIG_TEMPLATE_NAME).file().getAbsolutePath();
        this.clusterConfigQueueMap = queueMap;
    }

    public String onChangeCluster(String clusterId) {
        if(!clusterConfigQueueMap.containsKey(clusterId)) {
            return null;
        }
        VelocityContext context = new VelocityContext();
        ClusterService clusterService = ServiceManager.getInstance().getService(ClusterService.class);

        //1. topology구성도로 context에 값을 넣어준다.
        ClusterTopology topology = clusterService.getClusterTopology(clusterId);

        List<Frontend> frontendList = new ArrayList<>();
        List<Backend> backendList = new ArrayList<>();
        if (topology.getMesosMasterList().size() > 0) {
            /* front-end */
            Frontend frontend = new Frontend("marathon").withIp("*").withPort(8080).withMode("http");
            Frontend.ACL acl = new Frontend.ACL("url_marathon").withCriterion("hdr_beg(host)").withValue("marathon.");
            acl.withBackendName("marathon-be");
            frontendList.add(frontend);

            /* back-end */
            Backend backend = new Backend("marathon-be").withMode("").withBalance("roundrobin");
            List<Backend.Server> serverList = new ArrayList<>();
            for (int i = 0; i < topology.getMesosMasterList().size(); i++) {
                CommonInstance instance = topology.getMesosMasterList().get(i);
                Backend.Server server = new Backend.Server("marathon-be-" + i).withIp(instance.getPrivateIpAddress()).withPort(ClusterPorts.MARATHON_PORT);
                serverList.add(server);
            }
            backend.setServerList(serverList);
            backendList.add(backend);
        }

        //TODO 2. marathon을 통해 app별 listening 상태를 받아와서




        VelocityEngine engine = new VelocityEngine();
        engine.init();

        Template template = engine.getTemplate(templateFilePath, ENCODING);
        StringWriter stringWriter = new StringWriter();

        template.merge(context, stringWriter);
        String configString = stringWriter.toString();

        Queue<String> configQueue = clusterConfigQueueMap.get(clusterId);
        configQueue.offer(configString);
        return configString;
    }

    private void restartProxy(String clusterId) {

    }

}