package org.opencloudengine.garuda.beluga.cloud.watcher;

import org.opencloudengine.garuda.beluga.cloud.ClusterService;
import org.opencloudengine.garuda.beluga.cloud.CommonInstance;
import org.opencloudengine.garuda.beluga.docker.CAdvisor1_3_API;
import org.opencloudengine.garuda.beluga.docker.DockerRemoteApi;
import org.opencloudengine.garuda.beluga.mesos.marathon.MarathonAPI;
import org.opencloudengine.garuda.beluga.service.ServiceException;
import org.opencloudengine.garuda.beluga.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.util.*;

/**
 * 실행중인 docker app들에 대해서 리소스 사용률을 체크하고 필요시 scale out을 결정한다
 * docker remote api 와 cAdvisor를 함께 사용하여 리소스를 받아오는데,
 * cAdvisor는 컨테이너 내부의 환경변수를 제공하지 않기 때문에, remote api를 사용하여 <컨테이너ID:App ID> 쌍을 매핑해야 한다.
 *
 * Created by swsong on 2015. 7. 15..
 */
public class CloudWatcher {
    private static Logger logger = LoggerFactory.getLogger(CloudWatcher.class);

    private ClusterService clusterService;

    private CAdvisor1_3_API cAdvisorAPI;
    private DockerRemoteApi dockerRemoteApi;

    private TimerTask cloudResourceWatcher;

    private Timer timer;

    private Map<String, AutoScaleRule> autoScaleConfigMap;

    //조건에 부합하던 마지막 시간. 0이면 부합하지 않았던 것을 의미한다.
    private long appScaleOutLastTimestamp;
    private long appScaleInLastTimestamp;

    private Map<String, List<ContainerUsage>> allAppContainerUsageMap;
    private Map<String, ContainerUsage> appContainerUsageMap;

    public CloudWatcher(ClusterService clusterService) {
        this.clusterService = clusterService;
        cAdvisorAPI = new CAdvisor1_3_API();
        dockerRemoteApi = new DockerRemoteApi();
    }

    public boolean start() throws ServiceException {
        allAppContainerUsageMap = new HashMap<>();
        appContainerUsageMap = new HashMap<>();
        //오토 스케일-인/아웃.
        autoScaleConfigMap = new HashMap<>();
        appScaleOutLastTimestamp = 0;
        appScaleInLastTimestamp = 0;
        timer = new Timer();
        cloudResourceWatcher = new CloudResourceWatcher();
        //2초후에 5초간격.
        timer.schedule(cloudResourceWatcher, 2 * 1000, 5 * 1000);
        return true;
    }

    public boolean stop() throws ServiceException {
        timer.cancel();
        autoScaleConfigMap.clear();
        return true;
    }

    public void updateAutoScaleConfig(String appId, AutoScaleRule autoScaleConfig) {
        autoScaleConfigMap.put(appId, autoScaleConfig);
    }

    class CloudResourceWatcher extends TimerTask {

        @Override
        public void run() {
            /*
            * 모든 slave노드에서 앱 사용률을 가져온다.
            * webapp과 resouceApp이 섞여있다.
            * */
            try {
                for (CommonInstance slaveInstance : clusterService.getClusterTopology().getMesosSlaveList()) {
//                String host = slaveInstance.getPrivateIpAddress();
                    String host = slaveInstance.getPublicIpAddress();

                    Map<String, List<String>> appIdContainerIdMap = dockerRemoteApi.getAppIdWithContainerIdMap(host);
                    Map<String, List<ContainerUsage>> appContainerUsageMap = cAdvisorAPI.getAppIdWithDockerContainerUsages(host, appIdContainerIdMap);
                    for (Map.Entry<String, List<ContainerUsage>> entry : appContainerUsageMap.entrySet()) {
                        String appId = entry.getKey();
                        List<ContainerUsage> usageList = entry.getValue();
                        List<ContainerUsage> allUsageList = allAppContainerUsageMap.get(appId);
                        if (allUsageList == null) {
                            allAppContainerUsageMap.put(appId, usageList);
                        } else {
                            allUsageList.addAll(usageList);
                        }
                    }
                }

            }catch(Throwable t) {
                logger.error("error while resource watching..", t);
            }

            for(Map.Entry<String, List<ContainerUsage>> entry : allAppContainerUsageMap.entrySet()){
                String appId = entry.getKey();
                List<ContainerUsage> usageList = entry.getValue();
                //max를 찾는다.
                ContainerUsage maxUsage = null;
                for(ContainerUsage usage : usageList) {
                    if(maxUsage == null) {
                        maxUsage = usage;
                    } else {
                        if(maxUsage.getLoadAverage() < usage.getLoadAverage()) {
                            maxUsage = usage;
                        }
                    }
                }
                appContainerUsageMap.put(appId, maxUsage);
            }

//
            for (Map.Entry<String, ContainerUsage> entry : appContainerUsageMap.entrySet()) {
                String appId = entry.getKey();
                ContainerUsage usage = entry.getValue();
                AutoScaleRule autoScaleConfig = autoScaleConfigMap.get(appId);
                if(autoScaleConfig == null) {
                    continue;
                }

                //1. scale out 먼저 체크.
                double loadAverage = usage.getLoadAverage();
                if (loadAverage >= autoScaleConfig.getScaleOutLoadAverage()) {

                    if(appScaleOutLastTimestamp == 0) {
                        appScaleOutLastTimestamp = Calendar.getInstance().getTimeInMillis();
                    } else {
                        //차이 확인.
                        long diff = Calendar.getInstance().getTimeInMillis() - appScaleOutLastTimestamp;
                        if(diff / 60000L >= autoScaleConfig.getScaleOutDuringInMin()) {
                            // 스케일을 늘린다. 하지만, 아직 스케일 도중이거나, 바로 효과가 나타나지 않을 경우에는 계속해서 스케일증가 요청이 중복되게 된다.
                            // 그러므로, 스케일도중일때에는 clusterService에서 무시하도록 만든다.

                            //TODO
                            logger.info("#[{}/{}] Requested auto scale-out 1 instance.", clusterService.getClusterId(), appId, usage.getLoadAverage());

                            int scale = 1;
                            scale++;

                            //String appId, String imageName, List<Integer> usedPorts, Float cpus, Float memory, Integer scale, Map<String, String> env
                            clusterService.getMarathonAPI().updateDockerApp(appId, null, null, null, null, scale, null);
                            //다시 초기화한다.
                            appScaleOutLastTimestamp = 0;
                        }
                    }
                } else if (loadAverage < autoScaleConfig.getScaleInLoadAverage()) {
                    if(appScaleInLastTimestamp == 0) {
                        appScaleInLastTimestamp = Calendar.getInstance().getTimeInMillis();
                    } else {
                        //차이 확인.
                        long diff = Calendar.getInstance().getTimeInMillis() - appScaleInLastTimestamp;
                        if(diff / 60000L >= autoScaleConfig.getScaleInDuringInMin()) {
                            // 스케일을 줄인다. 하지만 최저 1개 이상은 돌아가야 하므로, 1이 될때는 clusterService에서 알아서 무시하게 만든다.

                            //TODO
                            logger.info("#[{}/{}] Requested auto scale-in 1 instance.", clusterService.getClusterId(), appId);

//                            MarathonAPI marathonAPI = clusterService.getMarathonAPI();
//                            Response response = marathonAPI.requestGetAPI("/apps/" + appId);
//                            String appString = response.readEntity(String.class);
//                            JsonUtil.toJsonNode(appString).get("");

                            int scale = 2;
                            scale--;

                            //String appId, String imageName, List<Integer> usedPorts, Float cpus, Float memory, Integer scale, Map<String, String> env
                            clusterService.getMarathonAPI().updateDockerApp(appId, null, null, null, null, scale, null);

                            //다시 초기화한다.
                            appScaleInLastTimestamp = 0;
                        }
                    }
                } else {
                    //어느 조건에도 부합하지 않으면 초기화.
                    appScaleOutLastTimestamp = 0;
                    appScaleInLastTimestamp = 0;
                }
            }
        }
    }
}
