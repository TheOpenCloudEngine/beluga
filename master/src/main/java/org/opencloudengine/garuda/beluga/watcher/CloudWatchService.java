package org.opencloudengine.garuda.beluga.watcher;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.opencloudengine.garuda.beluga.cadvisor.CAdvisor1_3_API;
import org.opencloudengine.garuda.beluga.cloud.ClusterService;
import org.opencloudengine.garuda.beluga.cloud.CommonInstance;
import org.opencloudengine.garuda.beluga.env.Environment;
import org.opencloudengine.garuda.beluga.env.Settings;
import org.opencloudengine.garuda.beluga.service.AbstractService;
import org.opencloudengine.garuda.beluga.service.ServiceException;
import org.opencloudengine.garuda.beluga.service.common.ServiceManager;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by swsong on 2015. 7. 15..
 */
public class CloudWatchService extends AbstractService {

    private ClusterService clusterService;
    private String clusterId;

    private CAdvisor1_3_API cAdvisorAPI;

    private TimerTask cloudResourceWatcher;

    private Timer timer;

    private AutoScaleConfig autoScaleInConfig;
    private AutoScaleConfig autoScaleOutConfig;

    private Map<String, AtomicInteger> appScaleOutCheck;
    private Map<String, AtomicInteger> appScaleInCheck;

    //TODO scale-in/out 룰을 받아야한다.
    public CloudWatchService(Environment environment, Settings settings, ServiceManager serviceManager) {
        super(environment, settings, serviceManager);
        cAdvisorAPI = new CAdvisor1_3_API();
    }

    @Override
    protected boolean doStart() throws ServiceException {
        appScaleOutCheck = new HashMap<>();
        appScaleInCheck = new HashMap<>();
        timer = new Timer();
        cloudResourceWatcher = new CloudResourceWatcher();
        //30초후에 60초간격.
        timer.scheduleAtFixedRate(cloudResourceWatcher, 30 * 1000, 60 * 1000);

        AutoScaleRule autoScaleRule = clusterService.getAutoScaleRule();
        return true;
    }

    @Override
    protected boolean doStop() throws ServiceException {
        cloudResourceWatcher.cancel();
        return true;
    }

    @Override
    protected boolean doClose() throws ServiceException {
        doStop();
        return true;
    }

    class CloudResourceWatcher extends TimerTask {

        @Override
        public void run() {
            Map<String, List<ContainerUsage>> containerUsagePerApp = new HashMap<>();
            List<ContainerUsage> containerUsageList = new ArrayList<>();

            /*
            * 모든 slave노드에서 앱 사용률을 가져온다.
            * webapp과 resouceApp이 섞여있다.
            * */
            for(CommonInstance slaveInstance : clusterService.getClusterTopology().getMesosSlaveList()) {
                String host = slaveInstance.getPrivateIpAddress();
                containerUsageList.addAll(cAdvisorAPI.getSlaveContainerUsages(host));
            }

            Set<String> runningAppIdSet = clusterService.getRunningAppIdSet();
            /*
            * 앱별로 사용률을 모은다. "앱의 스케일 갯수" == "list길이" 이다.
            * */
            for(ContainerUsage usage : containerUsageList) {
                String appId = usage.getAppId();
                if(!runningAppIdSet.contains(appId)) {
                    //등록되어 있는 app이 아니면 스킵.
                    //resouceApp이 해당된다.
                    continue;
                }

                List<ContainerUsage> usageList = containerUsagePerApp.get(appId);

                if (usageList == null) {
                    usageList = new ArrayList<>();
                    containerUsagePerApp.put(appId, usageList);
                }
                usageList.add(usage);
            }


            //Load 를 확인하고 containerLoadCheck 누적갯수를 올려주면서 룰에 부합하는지 확인.
            for(String appId : runningAppIdSet) {
                List<ContainerUsage> usageList = containerUsagePerApp.get(appId);
                if(usageList == null) {
                    //없울수 없을텐데...
                } else {

                    for(ContainerUsage usage : usageList) {
                        //1. scale out 먼저 체크.
                        if(usage.getLoadAverage() >= 0.7) {
                            //연속 C회 부합하는지.
                            appScaleInCheck.get(appId).set(0);
                            if(appScaleOutCheck.get(appId).incrementAndGet() >= 5) {
                                //TODO
                                // N개를 늘려준다.


                                //다시 초기화.
                                appScaleOutCheck.get(appId).set(0);
                            }
                        } else if(usage.getLoadAverage() <= 0.5) {
                            appScaleOutCheck.get(appId).set(0);
                            if(appScaleInCheck.get(appId).incrementAndGet() >= 5) {
                                //TODO


                                //다시 초기화.
                                appScaleInCheck.get(appId).set(0);
                            }
                        } else {
                            //초기화.
                            appScaleOutCheck.get(appId).set(0);
                            appScaleInCheck.get(appId).set(0);
                        }
                    }
                }

            }

        }
    }
}
