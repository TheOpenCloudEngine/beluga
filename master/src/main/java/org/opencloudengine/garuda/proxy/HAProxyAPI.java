package org.opencloudengine.garuda.proxy;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.opencloudengine.garuda.cloud.ClusterService;
import org.opencloudengine.garuda.cloud.ClusterTopology;
import org.opencloudengine.garuda.cloud.CommonInstance;
import org.opencloudengine.garuda.common.util.JsonUtils;
import org.opencloudengine.garuda.env.ClusterPorts;
import org.opencloudengine.garuda.env.Environment;
import org.opencloudengine.garuda.mesos.MesosService;
import org.opencloudengine.garuda.service.common.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.util.*;

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

    private String templateFilePath;
    private Map<String, Queue<String>> clusterConfigQueueMap;

    public HAProxyAPI(Environment environment, Map<String, Queue<String>> queueMap) {
        templateFilePath = environment.filePaths().configPath().file().getAbsolutePath();
        this.clusterConfigQueueMap = queueMap;
    }

    public String onChangeCluster(String clusterId) {
        logger.debug("Proxy onChangeCluster : {}", clusterId);
        if(!clusterConfigQueueMap.containsKey(clusterId)) {
            return null;
        }
        VelocityContext context = new VelocityContext();
        List<Frontend> frontendList = new ArrayList<>();
        List<Backend> backendList = new ArrayList<>();

        //1. topology구성도로 context에 값을 넣어준다.
        fillTopologyToContext(frontendList, backendList, clusterId);

        //2. marathon을 통해 app별 listening 상태를 받아와서 context에 넣어준다.
        fillServiceToContext(frontendList, backendList, clusterId);

        context.put(FRONTEND_LIST, frontendList);
        context.put(BACKEND_LIST, backendList);

        String configString = makeConfigString(context);
        Queue<String> configQueue = clusterConfigQueueMap.get(clusterId);
        configQueue.offer(configString);
        return configString;
    }

    protected void fillTopologyToContext(List<Frontend> frontendList, List<Backend> backendList, String clusterId) {
        ClusterService clusterService = ServiceManager.getInstance().getService(ClusterService.class);
        ClusterTopology topology = clusterService.getClusterTopology(clusterId);

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

    protected void fillServiceToContext(List<Frontend> frontendList, List<Backend> backendList, String clusterId) {
        MesosService mesosService = ServiceManager.getInstance().getService(MesosService.class);
        String appsString = mesosService.getMarathonAPI().requestGetAPIasString(clusterId, "/tasks");

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