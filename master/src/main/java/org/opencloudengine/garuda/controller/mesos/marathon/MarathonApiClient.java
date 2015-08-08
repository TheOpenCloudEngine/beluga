//package org.opencloudengine.garuda.controller.mesos.marathon;
//
//import org.opencloudengine.garuda.controller.mesos.marathon.model.apps.createapp.req.PortMapping;
//import org.springframework.stereotype.Component;
//
//import org.apache.curator.framework.recipes.cache.PathChildrenCache;
//import org.opencloudengine.garuda.controller.mesos.*;
//import org.opencloudengine.garuda.controller.mesos.marathon.model.apps.createapp.req.Container;
//import org.opencloudengine.garuda.controller.mesos.marathon.model.apps.createapp.req.CreateApp;
//import org.opencloudengine.garuda.controller.mesos.marathon.model.apps.createapp.req.Docker;
//import org.opencloudengine.garuda.controller.mesos.marathon.model.apps.createapp.res.CreateAppRes;
//import org.opencloudengine.garuda.controller.mesos.marathon.model.apps.getapps.res.App;
//import org.opencloudengine.garuda.controller.mesos.marathon.model.apps.putapp.req.PutAppReq;
//import org.opencloudengine.garuda.controller.mesos.marathon.model.apps.putapp.res.PutAppRes;
//import org.opencloudengine.garuda.controller.mesos.marathon.model.groups.creategroup.req.CreateGroup;
//import org.opencloudengine.garuda.controller.mesos.marathon.model.groups.getgroups.res.GetGroups;
//import org.opencloudengine.garuda.controller.mesos.marathon.model.groups.getgroups.res.Group;
//import org.opencloudengine.garuda.controller.mesos.marathon.model.task.gettasks.res.GetTasks;
//import org.opencloudengine.garuda.controller.mesos.marathon.model.task.gettasks.res.Task;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//import javax.ws.rs.client.*;
//import javax.ws.rs.core.GenericType;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * Marathon API 사용을 구현한 클래스이다.
// * - webResourceMap 에는 Marathon 노드들에 대한 컨넥션 객체들이 들어있다.
// * - Marathon 클러스터에 참여한 노드가 제거되면 해당 Map에서도 제거하고 추가되면 해당 Map에 추가하여 API가 항상 사용가능하도록 한다.
// * Marathon API 참조 : https://mesosphere.github.io/marathon/docs/rest-api.html
// *
// * @author Seong Jong, Jeon
// * @since 1.0
// */
//@Component("marathonApiClient")
//public class MarathonApiClient {
//    private static final Logger logger = LoggerFactory.getLogger(MarathonApiClient.class);
//
//    @Autowired
//    private MesosApiManager mesosApiManager;
//
//    //Web Client
//    private Map<String, Client> webClientMap = new ConcurrentHashMap<>();
//    private Map<String, WebTarget> webResourceMap = new ConcurrentHashMap<>();
//
//    //zookeeper monitor setting
//    private PathChildrenCache cache;
//    private final String MARATHON_ZNODE = "/marathon/leader";
//
//    @PostConstruct
//    public void init() throws Exception {
//        //marathon node monitoring
//        cache = new PathChildrenCache(mesosApiManager.getClient(), MARATHON_ZNODE, true);
//        cache.start();
//        new MesosServerMonitor("marathon", this, cache);
//    }
//
//    @PreDestroy
//    public void destroy() {
//        if (cache != null) {
////            IOUtils.closeQuietly(cache);
//        }
//
//        if (webClientMap != null) {
//            Iterator<String> iter = webClientMap.keySet().iterator();
//            while (iter.hasNext()) {
//                webClientMap.get(iter.next()).close();
//            }
//        }
//    }
//
//    /**
//     * 인자로 받은 Marathon Host에 연결한다.
//     *
//     * @param host
//     */
//    @Override
//    public void createNodeConnection(String host) {
//        if (webResourceMap.get(host) == null) {
////            String marathonApiUri = String.format("http://%s", host);
////            WebResourceConfigBuilder builder = new WebResourceConfigBuilder();
////            builder.withMaxTotalConnections(6000)
////                    .withReadTimeout(10)
////                    .withUri(marathonApiUri);
////
////            WebResourceConfig webResourceConfig = builder.build();
////            Client webClient = WebResourceClient.getWebClient(webResourceConfig);
////            WebTarget webResource = webClient.target(webResourceConfig.getUri());
////
////            webClientMap.put(host, webClient);
////            webResourceMap.put(host, webResource);
////            logger.info("Connect to marathon node - {}, available nodes:{}", new Object[]{marathonApiUri, webResourceMap.size()});
//        }
//    }
//
//    /**
//     * Marathon Cluster 에서 제외된 노드를 제거한다.
//     *
//     * @param host
//     */
//    @Override
//    public void closeResource(String host) {
//        Client webClient = webClientMap.remove(host);
//        webResourceMap.remove(host);
//
//        if (webClient != null) {
//            webClient.close();
//            logger.info("Disconnect to marathon node - {}, available nodes:{}", new Object[]{host, webResourceMap.size()});
//        }
//    }
//
//    private WebTarget getWebResource() {
//        if (webResourceMap == null || webResourceMap.size() == 0) {
//            throw new RuntimeException("Not exists web resource");
//        }
//
//        Iterator<String> iter = webResourceMap.keySet().iterator();
//        return webResourceMap.get(iter.next());
//    }
//
//    /**
//     * POST /v2/apps: Create and start a new app
//     *
//     * @param createApp
//     * @return
//     */
//    public CreateAppRes createApp(CreateApp createApp, boolean force) {
//        WebTarget resource = getWebResource().path("/v2/apps").queryParam("force", force);
//
//        return resource.request(MediaType.APPLICATION_JSON_TYPE)
//                .accept(MediaType.APPLICATION_JSON)
//                .post(Entity.json(createApp), new GenericType<CreateAppRes>() {
//                });
//    }
//
//    /**
//     * GET /v2/apps: List all running apps
//     *
//     * @return
//     */
//    public List<App> searchAppList() {
//        WebTarget resource = getWebResource().path("/v2/apps");
//
//        Map<String, List<App>> result = resource.request()
//                .accept(MediaType.APPLICATION_JSON)
//                .get(new GenericType<Map<String, List<App>>>() {
//                });
//
//        return result.get("apps");
//    }
//
//    /**
//     * GET /v2/apps/{appId}: List the app appId
//     *
//     * @param appId
//     * @return
//     */
//    public App searchApp(String appId) {
//        WebTarget resource = getWebResource().path("/v2/apps/" + appId);
//
//        Map<String, App> result = resource.request()
//                .accept(MediaType.APPLICATION_JSON)
//                .get(new GenericType<Map<String, App>>() {
//                });
//
//        return result.get("app");
//    }
//
//    /**
//     * PUT /v2/apps/{appId}: Change config of the app appId
//     *
//     * @param putAppReq
//     * @param appId
//     * @param force
//     * @return
//     */
//    public PutAppRes modifyApp(PutAppReq putAppReq, String appId, boolean force) {
//        WebTarget resource = getWebResource().path("/v2/apps/" + appId).queryParam("force", force);
//
//        return resource.request()
//                .accept(MediaType.APPLICATION_JSON)
//                .put(Entity.json(putAppReq), new GenericType<PutAppRes>() {
//                });
//    }
//
//    /**
//     * POST /v2/apps/{appId}/restart: Rolling restart of all tasks of the given app
//     *
//     * @param appId
//     * @param force
//     * @return
//     */
//    public PutAppRes restartApp(String appId, boolean force) {
//        WebTarget resource = getWebResource().path("/v2/apps/" + appId + "/restart").queryParam("force", force);
//
//        return resource.request()
//                .accept(MediaType.APPLICATION_JSON)
//                .post(Entity.json(null), new GenericType<PutAppRes>() {
//                });
//    }
//
//    /**
//     * DELETE /v2/apps/{appId}: Destroy app appId
//     */
//    public PutAppRes removeApp(String appId) {
//        WebTarget resource = getWebResource().path("/v2/apps/" + appId);
//
//        return resource.request()
//                .accept(MediaType.APPLICATION_JSON)
//                .delete(new GenericType<PutAppRes>() {
//                });
//    }
//
//    /**
//     * GET /v2/apps/{appId}/tasks: List running tasks for app appId
//     *
//     * @param appId
//     * @return
//     */
//    public List<Task> searchTasksWithAppId(String appId) {
//        WebTarget resource = getWebResource().path("/v2/apps/" + appId + "/tasks");
//
//        GetTasks result = resource.request()
//                .accept(MediaType.APPLICATION_JSON)
//                .get(new GenericType<GetTasks>() {
//                });
//
//        return result.getTasks();
//    }
//
//    /**
//     * GET /v2/tasks: List all running tasks
//     *
//     * @return
//     */
//    public List<Task> searchTasks() {
//        WebTarget resource = getWebResource().path("/v2/tasks");
//
//        GetTasks result = resource.request()
//                .accept(MediaType.APPLICATION_JSON)
//                .get(new GenericType<GetTasks>() {
//                });
//
//        return result.getTasks();
//    }
//
//    /**
//     * DELETE /v2/apps/{appId}/tasks: kill tasks belonging to app appId
//     *
//     * @param host
//     * @param scale
//     * @return
//     */
//    public List<Task> removeTasksWithAppId(String appId, String host, boolean scale) {
//        WebTarget resource = getWebResource().path("/v2/apps/" + appId + "/tasks")
//                .queryParam(host == null ? "none" : host)
//                .queryParam("scale", scale);
//
//        GetTasks result = resource.request()
//                .accept(MediaType.APPLICATION_JSON)
//                .delete(new GenericType<GetTasks>() {
//                });
//
//        return result.getTasks();
//    }
//
//    /**
//     * DELETE /v2/apps/{appId}/tasks/{taskId}: Kill the task taskId that belongs to the application ap
//     *
//     * @param appId
//     * @param taskId
//     * @param scale
//     * @return
//     */
//    public Task removeTasksWithTaskId(String appId, String taskId, boolean scale) {
//        WebTarget resource = getWebResource().path("/v2/apps/" + appId + "/tasks/" + taskId)
//                .queryParam("scale", scale);
//
//        return resource.request()
//                .accept(MediaType.APPLICATION_JSON)
//                .delete(new GenericType<Task>() {
//                });
//    }
//
//    /**
//     * GET /v2/groups: List all groups
//     *
//     * @return
//     */
//    public GetGroups searchGroups() {
//        WebTarget resource = getWebResource().path("/v2/groups");
//
//        GetGroups result = resource.request()
//                .accept(MediaType.APPLICATION_JSON)
//                .get(new GenericType<GetGroups>() {
//                });
//
//        return result;
//    }
//
//    /**
//     * GET /v2/groups/{groupId}: List the group with the specified ID
//     *
//     * @param groupId
//     */
//    public Group searchGroupWithGroupId(String groupId) {
//        WebTarget resource = getWebResource().path("/v2/groups/" + groupId);
//
//        return resource.request()
//                .accept(MediaType.APPLICATION_JSON)
//                .get(new GenericType<Group>() {
//                });
//    }
//
//    /**
//     * POST /v2/groups: Create and start a new groups
//     * todo dependency 및 group 안에 group이 들어갈 수 있으므로 해당 부분에 맞게 파라미터 변경 필요
//     *
//     * @param createGroup
//     * @return
//     */
//    public PutAppRes createGroups(CreateGroup createGroup) {
//        WebTarget resource = getWebResource().path("/v2/groups");
//        return resource.request(MediaType.APPLICATION_JSON_TYPE)
//                .accept(MediaType.APPLICATION_JSON)
//                .post(Entity.json(createGroup), new GenericType<PutAppRes>() {
//                });
//    }
//
//    /**
//     * DELETE /v2/groups/{groupId}: Destroy a group
//     *
//     * @param groupId
//     * @return
//     */
//    public PutAppRes removeGroup(String groupId, boolean force) {
//        WebTarget resource = getWebResource().path("/v2/groups/" + groupId).queryParam("force", force);
//        return resource.request(MediaType.APPLICATION_JSON_TYPE)
//                .accept(MediaType.APPLICATION_JSON)
//                .delete(new GenericType<PutAppRes>() {
//                });
//    }
//
//}