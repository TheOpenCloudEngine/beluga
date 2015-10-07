//package org.opencloudengine.garuda.action.webapp;
//
//import org.opencloudengine.garuda.action.ActionException;
//import org.opencloudengine.garuda.action.RunnableAction;
//import org.opencloudengine.garuda.cloud.ClusterService;
//import org.opencloudengine.garuda.cloud.ClusterTopology;
//import org.opencloudengine.garuda.cloud.ClustersService;
//import org.opencloudengine.garuda.exception.GarudaException;
//import org.opencloudengine.garuda.mesos.marathon.MarathonAPI;
//
//import javax.ws.rs.core.Response;
//
///**
// * Created by swsong on 2015. 8. 6..
// */
//public class ScaleWebAppAction extends RunnableAction<ScaleWebAppActionRequest> {
//
//    public ScaleWebAppAction(ScaleWebAppActionRequest actionRequest) {
//        super(actionRequest);
//    }
//
//    @Override
//    protected void doAction() throws Exception {
//        ScaleWebAppActionRequest request = getActionRequest();
//        String clusterId = request.getClusterId();
//        String appId = request.getAppId();
//        Integer scale = request.getScale();
//
//        // clusterId를 통해 인스턴스 주소를 받아온다.
//        ClusterService clusterService = serviceManager.getService(ClustersService.class).getClusterService(clusterId);
//        ClusterTopology topology = clusterService.getClusterTopology();
//        if(topology == null) {
//            // 그런 클러스터가 없다.
//            throw new GarudaException("No such cluster: "+ clusterId);
//        }
//
//        String registryAddress = topology.getRegistryAddressPort();
//        if(registryAddress == null) {
//            throw new GarudaException("No registry instance in " + clusterId);
//        }
//
//        /*
//        * Call to Marathon
//        * */
//        MarathonAPI marathonAPI = clusterService.getMarathonAPI();
//        Response response = null;
//        response = marathonAPI.scaleDockerApp(appId, scale);
//        if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
//            setResult(response);
//        } else if(response.getStatus() == Response.Status.CONFLICT.getStatusCode()) {
//            throw new ActionException("App is already running.");
//        } else {
//            throw new ActionException("error while deploy to marathon : [" + response.getStatus() + "] " + response.getStatusInfo());
//        }
//    }
//}
