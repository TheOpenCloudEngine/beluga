package org.opencloudengine.garuda.beluga.cloud.watcher;

import org.opencloudengine.garuda.beluga.cloud.ClusterService;
import org.opencloudengine.garuda.beluga.cloud.CommonInstance;
import org.opencloudengine.garuda.beluga.docker.CAdvisor1_3_API;
import org.opencloudengine.garuda.beluga.docker.DockerRemoteApi;
import org.opencloudengine.garuda.beluga.env.SettingManager;
import org.opencloudengine.garuda.beluga.mesos.marathon.MarathonAPI;
import org.opencloudengine.garuda.beluga.service.ServiceException;
import org.opencloudengine.garuda.beluga.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.IOException;
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

    private String clusterId;
    private ClusterService clusterService;

    private CAdvisor1_3_API cAdvisorAPI;
    private DockerRemoteApi dockerRemoteApi;

    private TimerTask cloudResourceWatcher;

    private Timer timer;

    private Map<String, AutoScaleRule> autoScaleRuleMap;

    private Map<String, List<ContainerUsage>> allAppContainerUsageMap;
    private Map<String, ContainerUsage> appContainerUsageMap;

    public CloudWatcher(ClusterService clusterService) {
        this.clusterService = clusterService;
        this.clusterId = clusterService.getClusterId();
        cAdvisorAPI = new CAdvisor1_3_API();
        dockerRemoteApi = new DockerRemoteApi();
    }

    public boolean start() throws ServiceException {
        allAppContainerUsageMap = new HashMap<>();
        appContainerUsageMap = new HashMap<>();
        //오토 스케일-인/아웃.
        autoScaleRuleMap = SettingManager.getInstance().getAutoScaleRule(clusterId);
        logger.debug("[{}] Autoscale Rule > {}", clusterId, autoScaleRuleMap);
        timer = new Timer();
        cloudResourceWatcher = new CloudResourceWatcher();
        //2초후에 5초간격.
        timer.schedule(cloudResourceWatcher, 2 * 1000, 5 * 1000);
        return true;
    }

    public boolean stop() throws ServiceException {
        timer.cancel();
        autoScaleRuleMap.clear();
        return true;
    }

    public synchronized void updateAutoScaleRule(String appId, AutoScaleRule autoScaleRule) {
        if(autoScaleRule != null) {
            autoScaleRuleMap.put(appId, autoScaleRule);
        } else {
            autoScaleRuleMap.remove(appId);
        }
        //오토스케일 룰 저장.
        SettingManager.getInstance().storeAutoScaleRule(clusterId, autoScaleRuleMap);
        logger.debug("[{}] Autoscale Rule Updated > {}", clusterId, autoScaleRuleMap);
    }

    class CloudResourceWatcher extends TimerTask {

        @Override
        public void run() {
            try {
            /*
            * 모든 slave노드에서 앱 사용률을 가져온다.
            * webapp과 resouceApp이 섞여있다.
            * */
                Map<String, List<ContainerUsage>> currentAllAppContainerUsageMap = new HashMap<>();
                for (CommonInstance slaveInstance : clusterService.getClusterTopology().getMesosSlaveList()) {
//                String host = slaveInstance.getPrivateIpAddress();
                    String host = slaveInstance.getPublicIpAddress();

                    Map<String, List<String>> appIdContainerIdMap = dockerRemoteApi.getAppIdWithContainerIdMap(host);
                    Map<String, List<ContainerUsage>> appContainerUsageMap = cAdvisorAPI.getAppIdWithDockerContainerUsages(host, appIdContainerIdMap);

                    for (Map.Entry<String, List<ContainerUsage>> entry : appContainerUsageMap.entrySet()) {
                        String appId = entry.getKey();
                        List<ContainerUsage> usageList = entry.getValue();
                        List<ContainerUsage> allUsageList = currentAllAppContainerUsageMap.get(appId);
                        if (allUsageList == null) {
                            currentAllAppContainerUsageMap.put(appId, usageList);
                        } else {
                            allUsageList.addAll(usageList);
                        }
                    }
                }
                allAppContainerUsageMap = currentAllAppContainerUsageMap;

                Map<String, ContainerUsage> currentAppContainerUsageMap = new HashMap<>();
                for (Map.Entry<String, List<ContainerUsage>> entry : allAppContainerUsageMap.entrySet()) {
                    String appId = entry.getKey();
                    List<ContainerUsage> usageList = entry.getValue();
                    //max를 찾는다.
                    ContainerUsage maxUsage = null;
                    for (ContainerUsage usage : usageList) {
                        if (maxUsage == null) {
                            maxUsage = usage;
                        } else {
                            if (maxUsage.getWorkLoadPercent() < usage.getWorkLoadPercent()) {
                                maxUsage = usage;
                            }
                        }
                    }
                    currentAppContainerUsageMap.put(appId, maxUsage);
                }
                appContainerUsageMap = currentAppContainerUsageMap;

                for (Map.Entry<String, ContainerUsage> entry : appContainerUsageMap.entrySet()) {
                    String appId = entry.getKey();
                    ContainerUsage usage = entry.getValue();
                    AutoScaleRule autoScaleRule = autoScaleRuleMap.get(appId);
                    if (autoScaleRule == null) {
                        continue;
                    }

                    //1. scale out 먼저 체크.
                    int workLoadPercent = usage.getWorkLoadPercent();
                    if (workLoadPercent >= autoScaleRule.getScaleOutWorkLoad()) {

                        if (autoScaleRule.getAppScaleOutLastTimestamp() == 0) {
                            autoScaleRule.setAppScaleOutLastTimestamp(Calendar.getInstance().getTimeInMillis());
                        } else {
                            //차이 확인.
                            long diff = Calendar.getInstance().getTimeInMillis() - autoScaleRule.getAppScaleOutLastTimestamp();
                            if (diff / 60000L >= autoScaleRule.getScaleOutTimeInMin()) {
                                // 스케일을 늘린다. 하지만, 아직 스케일 도중이거나, 바로 효과가 나타나지 않을 경우에는 계속해서 스케일증가 요청이 중복되게 된다.
                                // 그러므로, 스케일도중일때에는 clusterService에서 무시하도록 만든다.

                                int scale = getScale(appId);
                                logger.info("#[{}/{}] Requested auto scale-out from {} to {} instances. workLoad[{}] time[{}Min]", clusterId, appId, scale, scale + 1, usage.getWorkLoadPercent(), diff / 60000L);
                                scale++;

                                //String appId, String imageName, List<Integer> usedPorts, Float cpus, Float memory, Integer scale, Map<String, String> env
                                clusterService.getMarathonAPI().updateDockerApp(appId, null, null, null, null, scale, null);
                                //다시 초기화한다.
                                autoScaleRule.setAppScaleOutLastTimestamp(0);
                            }
                        }
                    } else if (workLoadPercent < autoScaleRule.getScaleInWorkLoad()) {
                        List<ContainerUsage> usageList = allAppContainerUsageMap.get(appId);
                        if (usageList == null || usageList.size() <= 1) {
                            //없거나 하나이면 스케일을 줄이지 않으므로, 검사도하지 않는다.
                            continue;
                        }
                        if (autoScaleRule.getAppScaleInLastTimestamp() == 0) {
                            autoScaleRule.setAppScaleInLastTimestamp(Calendar.getInstance().getTimeInMillis());
                        } else {
                            //차이 확인.
                            long diff = Calendar.getInstance().getTimeInMillis() - autoScaleRule.getAppScaleInLastTimestamp();
                            if (diff / 60000L >= autoScaleRule.getScaleInTimeInMin()) {
                                // 스케일을 줄인다. 하지만 최저 1개 이상은 돌아가야 하므로, 1이 될때는 clusterService에서 알아서 무시하게 만든다.

                                int scale = getScale(appId);
                                logger.info("#[{}/{}] Requested auto scale-in from {} to {} instances. workLoad[{}] time[{}Min]", clusterId, appId, scale, scale - 1, usage.getWorkLoadPercent(), diff / 60000L);
                                scale--;

                                //String appId, String imageName, List<Integer> usedPorts, Float cpus, Float memory, Integer scale, Map<String, String> env
                                clusterService.getMarathonAPI().updateDockerApp(appId, null, null, null, null, scale, null);

                                //다시 초기화한다.
                                autoScaleRule.setAppScaleInLastTimestamp(0);
                            }
                        }
                    } else {
                        //어느 조건에도 부합하지 않으면 초기화.
                        autoScaleRule.setAppScaleOutLastTimestamp(0);
                        autoScaleRule.setAppScaleInLastTimestamp(0);
                    }
                }
            }catch(Throwable t) {
                logger.error("error while cloud watching..", t);
            }
        }

        private int getScale(String appId) {
            MarathonAPI marathonAPI = clusterService.getMarathonAPI();
            Response response = marathonAPI.requestGetAPI("/apps/" + appId);
            String appString = response.readEntity(String.class);
            try {
                int scale = JsonUtil.toJsonNode(appString).get("app").get("instances").asInt();
                return scale;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return -1;
        }
    }
}
