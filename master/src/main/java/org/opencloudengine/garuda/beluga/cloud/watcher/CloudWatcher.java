package org.opencloudengine.garuda.beluga.cloud.watcher;

import org.opencloudengine.garuda.beluga.docker.CAdvisor1_3_API;
import org.opencloudengine.garuda.beluga.cloud.ClusterService;
import org.opencloudengine.garuda.beluga.cloud.CommonInstance;
import org.opencloudengine.garuda.beluga.docker.DockerRemoteApi;
import org.opencloudengine.garuda.beluga.service.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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

    private AutoScaleConfig autoScaleInConfig;
    private AutoScaleConfig autoScaleOutConfig;

    private Map<String, AtomicInteger> appScaleOutCheck;
    private Map<String, AtomicInteger> appScaleInCheck;

    //TODO scale-in/out 룰을 받아야한다.
    public CloudWatcher(ClusterService clusterService) {
        this.clusterService = clusterService;
        cAdvisorAPI = new CAdvisor1_3_API();
        dockerRemoteApi = new DockerRemoteApi();
    }

    public boolean start() throws ServiceException {
        appScaleOutCheck = new HashMap<>();
        appScaleInCheck = new HashMap<>();
        timer = new Timer();
        cloudResourceWatcher = new CloudResourceWatcher();
        //30초후에 10초간격.
        timer.schedule(cloudResourceWatcher, 2 * 1000, 5 * 1000);
        AutoScaleRule autoScaleRule = clusterService.getAutoScaleRule();
        return true;
    }

    public boolean stop() throws ServiceException {
        cloudResourceWatcher.cancel();
        return true;
    }

    class CloudResourceWatcher extends TimerTask {

        @Override
        public void run() {
            try {
            /*
            * 모든 slave노드에서 앱 사용률을 가져온다.
            * webapp과 resouceApp이 섞여있다.
            * */
                Map<String, List<ContainerUsage>> AllAppContainerUsageMap = new HashMap<>();
                for (CommonInstance slaveInstance : clusterService.getClusterTopology().getMesosSlaveList()) {
//                String host = slaveInstance.getPrivateIpAddress();
                    String host = slaveInstance.getPublicIpAddress();

                    Map<String, List<String>> appIdContainerIdMap = dockerRemoteApi.getAppIdWithContainerIdMap(host);
                    Map<String, List<ContainerUsage>> appContainerUsageMap = cAdvisorAPI.getAppIdWithDockerContainerUsages(host, appIdContainerIdMap);
                    for (Map.Entry<String, List<ContainerUsage>> entry : appContainerUsageMap.entrySet()) {
                        String appId = entry.getKey();
                        List<ContainerUsage> usageList = entry.getValue();
                        List<ContainerUsage> allUsageList = AllAppContainerUsageMap.get(appId);
                        if (allUsageList == null) {
                            AllAppContainerUsageMap.put(appId, usageList);
                        } else {
                            allUsageList.addAll(usageList);
                        }
                    }
                }

            }catch(Throwable t) {
                logger.error("error while resource watching..", t);
            }
//            Set<String> runningAppIdSet = clusterService.getRunningAppIdSet();
//            /*
//            * 앱별로 사용률을 모은다. "앱의 스케일 갯수" == "list길이" 이다.
//            * */
//            for(ContainerUsage usage : containerUsageList) {
//                String appId = usage.getAppId();
//                if(!runningAppIdSet.contains(appId)) {
//                    //등록되어 있는 app이 아니면 스킵.
//                    //resouceApp이 해당된다.
//                    continue;
//                }
//
//                List<ContainerUsage> usageList = containerUsagePerApp.get(appId);
//
//                if (usageList == null) {
//                    usageList = new ArrayList<>();
//                    containerUsagePerApp.put(appId, usageList);
//                }
//                usageList.add(usage);
//            }
//
//
//            //Load 를 확인하고 containerLoadCheck 누적갯수를 올려주면서 룰에 부합하는지 확인.
//            for(String appId : runningAppIdSet) {
//                List<ContainerUsage> usageList = containerUsagePerApp.get(appId);
//                if(usageList == null) {
//                    //없울수 없을텐데...
//                } else {
//
//                    for(ContainerUsage usage : usageList) {
//                        //1. scale out 먼저 체크.
//                        if(usage.getLoadAverage() >= 0.7) {
//                            //연속 C회 부합하는지.
//                            appScaleInCheck.get(appId).set(0);
//                            if(appScaleOutCheck.get(appId).incrementAndGet() >= 5) {
//                                //TODO
//                                // N개를 늘려준다.
//
//
//                                //다시 초기화.
//                                appScaleOutCheck.get(appId).set(0);
//                            }
//                        } else if(usage.getLoadAverage() <= 0.5) {
//                            appScaleOutCheck.get(appId).set(0);
//                            if(appScaleInCheck.get(appId).incrementAndGet() >= 5) {
//                                //TODO
//
//
//                                //다시 초기화.
//                                appScaleInCheck.get(appId).set(0);
//                            }
//                        } else {
//                            //초기화.
//                            appScaleOutCheck.get(appId).set(0);
//                            appScaleInCheck.get(appId).set(0);
//                        }
//                    }
//                }
//
//            }

        }
    }
}
