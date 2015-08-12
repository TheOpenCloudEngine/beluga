package org.opencloudengine.garuda.proxy;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.opencloudengine.garuda.cloud.ClusterService;
import org.opencloudengine.garuda.cloud.ClusterTopology;
import org.opencloudengine.garuda.cloud.ClustersService;
import org.opencloudengine.garuda.cloud.CommonInstance;
import org.opencloudengine.garuda.common.util.JsonUtils;
import org.opencloudengine.garuda.env.ClusterPorts;
import org.opencloudengine.garuda.env.Environment;
import org.opencloudengine.garuda.env.Path;
import org.opencloudengine.garuda.env.SettingManager;
import org.opencloudengine.garuda.mesos.MesosAPI;
import org.opencloudengine.garuda.mesos.marathon.MarathonAPI;
import org.opencloudengine.garuda.service.common.ServiceManager;
import org.opencloudengine.garuda.utils.SshClient;
import org.opencloudengine.garuda.utils.SshInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by swsong on 2015. 8. 10..
 */
public class HAProxyAPI {

    protected static Logger logger = LoggerFactory.getLogger(HAProxyAPI.class);

    protected static final String RESTART_COMMAND = "sudo haproxy -f /etc/haproxy/haproxy.cfg -p /var/run/haproxy.pid -sf $(cat /var/run/haproxy.pid)";
    protected static final String CONFIG_NAME = "haproxy.cfg";
    protected static final String TMP_CONFIG_FILE = "/tmp/" + CONFIG_NAME;
    protected static final String COPY_CONFIG_COMMAND = "sudo cp /tmp/" + CONFIG_NAME + " /etc/haproxy/" + CONFIG_NAME;
    protected static final String CONFIG_TEMPLATE_NAME = CONFIG_NAME + ".vm";
    protected static final String ENCODING = "utf-8";

    private static final String FRONTEND_LIST = "frontendList";
    private static final String BACKEND_LIST = "backendList";

    private String clusterId;
    private String templateFilePath;
    private Queue<String> proxyUpdateQueue;
    /*
     * 디플로이가 진행중일때까지 유지하는 Q.
     * 디플로이가 서비스중으로 변경까지는 시간이 걸리기 때문에, 서비스로 변경되면 Q에서 지워주고, proxy 설정을 업데이트 한다.
     */
    private Set<String> deploymentSet;

    public HAProxyAPI(String clusterId, Environment environment) {
        this.clusterId = clusterId;
        templateFilePath = environment.filePaths().configPath().file().getAbsolutePath();
        this.proxyUpdateQueue = new ConcurrentLinkedQueue<>();
        this.deploymentSet = new ConcurrentHashSet<>();
    }

    /**
     * 클러스터 토폴로지에 변화가 생기겼을 경우 즉시 업데이트 한다.
     * */
    public void notifyTopologyChanged() {
        logger.debug("Proxy notified Topology Changed : {}", clusterId);
        updateProxyConfig();
    }
    /**
     * 서비스가 추가/변경 되었을때 호출된다.
     * Deploy 과정을 거치므로, 즉시 업데이트 하지 않고 deploymentSet 에 넣어둔뒤, 사라지는지 확인한다.
     * */
    public void notifyServiceChanged(String deploymentsId) {
        logger.debug("Proxy notified Service Changed : {}", clusterId);
        deploymentSet.add(deploymentsId);
    }

    public String updateProxyConfig() {
        VelocityContext context = new VelocityContext();
        List<Frontend> frontendList = new ArrayList<>();
        List<Backend> backendList = new ArrayList<>();

        //1. topology구성도로 context에 값을 넣어준다.
        fillTopologyToContext(frontendList, backendList);

        //2. marathon을 통해 app별 listening 상태를 받아와서 context에 넣어준다.
        fillServiceToContext(frontendList, backendList);

        context.put(FRONTEND_LIST, frontendList);
        context.put(BACKEND_LIST, backendList);

        String configString = makeConfigString(context);
        proxyUpdateQueue.offer(configString);
        return configString;
    }

    protected void fillTopologyToContext(List<Frontend> frontendList, List<Backend> backendList) {
        ClusterService clusterService = ServiceManager.getInstance().getService(ClustersService.class).getClusterService(clusterId);
        ClusterTopology topology = clusterService.getClusterTopology();

        if (topology.getMesosMasterList().size() > 0) {
            Frontend adminFrontend = new Frontend("admin").withIp("*").withPort(ClusterPorts.PROXY_ADMIN_PORT).withMode("http");
            /*
            * Marathon frontend
            * */
            Frontend.ACL acl = new Frontend.ACL("url_marathon").withCriterion("hdr_beg(host)").withValue("marathon.").withBackendName("marathon-be");
            adminFrontend.withAcl(acl);
            /*
            * Mesos frontend
            * */
            for (int i = 0; i < topology.getMesosMasterList().size(); i++) {
                int seq = i + 1;
                String mesosName = "mesos";
                if (i > 0) {
                    mesosName += (seq +".");
                } else {
                    mesosName += ".";
                }
                Frontend.ACL acl2 = new Frontend.ACL("url_mesos" + i).withCriterion("hdr_beg(host)").withValue(mesosName);
                acl2.withBackendName("mesos-be-" + i);
                adminFrontend.withAcl(acl2);
            }
            frontendList.add(adminFrontend);

            /*
            * Marathon backend
            * */
            Backend marathonBackend = new Backend("marathon-be").withMode("http").withBalance("roundrobin");
            for (int i = 0; i < topology.getMesosMasterList().size(); i++) {
                CommonInstance instance = topology.getMesosMasterList().get(i);
                Backend.Server server = new Backend.Server("marathon-be-" + i).withIp(instance.getPrivateIpAddress()).withPort(ClusterPorts.MARATHON_PORT);
                marathonBackend.withServer(server);
            }
            backendList.add(marathonBackend);
            /*
            * Mesos backend
            * */
            for (int i = 0; i < topology.getMesosMasterList().size(); i++) {
                Backend mesosBackend = new Backend("mesos-be-"+i).withMode("http").withBalance("roundrobin");
                CommonInstance instance = topology.getMesosMasterList().get(i);
                Backend.Server server = new Backend.Server("mesos-be-" + i +"-0").withIp(instance.getPrivateIpAddress()).withPort(ClusterPorts.MESOS_PORT);
                mesosBackend.withServer(server);
                backendList.add(mesosBackend);
            }
        }
    }

    protected void fillServiceToContext(List<Frontend> frontendList, List<Backend> backendList) {
        MarathonAPI marathonAPI = ServiceManager.getInstance().getService(ClustersService.class).getClusterService(clusterId).getMarathonAPI();
        String appsString = marathonAPI.requestGetAPIasString("/tasks");

        JsonNode taskList = JsonUtils.toJsonNode(appsString).get("tasks");
        if (taskList != null) {
            Map<String, List<HostPort>> taskEndpointMap = new HashMap<>();
            for (final JsonNode task : taskList) {
                String appId = task.get("appId").asText();
                appId = appId.substring(1);
                List<HostPort> list = taskEndpointMap.get(appId);
                if(list == null) {
                    list = new ArrayList<>();
                    taskEndpointMap.put(appId, list);
                }
                String host = task.get("host").asText();
                for(JsonNode port : task.get("ports")){
                    list.add(new HostPort(host, port.asInt()));
                }
            }
            Frontend serviceFrontend = new Frontend("service").withIp("*").withPort(ClusterPorts.PROXY_SERVICE_PORT).withMode("http");
            for(String appId : taskEndpointMap.keySet()) {
                Frontend.ACL acl = new Frontend.ACL("url_" + appId).withCriterion("hdr_beg(host)").withValue(appId+".").withBackendName(appId+"-be");
                serviceFrontend.withAcl(acl);
            }

            frontendList.add(serviceFrontend);

            for(Map.Entry<String, List<HostPort>> e : taskEndpointMap.entrySet()) {
                String appId = e.getKey();
                Backend serviceBackend = new Backend(appId+"-be").withMode("http").withBalance("roundrobin");
                int i = 0;
                for(HostPort hp : e.getValue()){
                    Backend.Server server = new Backend.Server(appId + "-be-" + i++).withIp(hp.getHost()).withPort(hp.getPort());
                    serviceBackend.withServer(server);
                }
                backendList.add(serviceBackend);
            }
        }
    }

    protected String makeConfigString(VelocityContext context) {
        VelocityEngine engine = new VelocityEngine();
        engine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, templateFilePath);
        engine.init();

        Template template = engine.getTemplate(CONFIG_TEMPLATE_NAME, ENCODING);
        StringWriter stringWriter = new StringWriter();

        template.merge(context, stringWriter);
        return stringWriter.toString();
    }

    public void applyProxyConfig(SshInfo sshInfo) throws IOException {
        String tempDir = SettingManager.getInstance().getSystemSettings().getString("temp.dir.path");
        Path tempDirPath = SettingManager.getInstance().getEnvironment().filePaths().path(tempDir);

        int sizeToRemove = proxyUpdateQueue.size();
        String configString = null;
        for (int i = 0; i < sizeToRemove; i++) {
            configString = proxyUpdateQueue.poll();
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
    }
    public void checkDeploymentsAndApply() {

        Set<String> removed = null;
        //marathon에 던져봐서 존재하는지 확인한다.
        MarathonAPI marathonAPI = null;
        String deployString = marathonAPI.requestGetAPIasString("/deployments");
        JsonNode deployList = JsonUtils.toJsonNode(deployString);
        Set<String> runningSet = new HashSet<>();
        for(JsonNode deployments : deployList) {
            runningSet.add(deployments.get("id").asText());
        }
        Iterator<String> iter = deploymentSet.iterator();
        int removeCount = 0;
        while(iter.hasNext()) {
            String key = iter.next();
            if(!runningSet.contains(key)) {
                iter.remove();
                logger.debug("## Deployments {} is now running.", key);
                removeCount++;
            }
        }
        // 없어지면 proxy 를 업데이트 한다.
        if(removeCount > 0) {
            updateProxyConfig();
        }
    }


    class HostPort {
        private String host;
        private int port;
        public HostPort(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }
    }
}