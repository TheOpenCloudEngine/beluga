package org.opencloudengine.garuda.beluga.action.terminal;

import org.opencloudengine.garuda.beluga.action.ActionException;
import org.opencloudengine.garuda.beluga.action.RunnableAction;
import org.opencloudengine.garuda.beluga.action.webapp.DeployWebAppActionRequest;
import org.opencloudengine.garuda.beluga.action.webapp.WebAppContextFile;
import org.opencloudengine.garuda.beluga.cloud.ClusterService;
import org.opencloudengine.garuda.beluga.cloud.ClusterTopology;
import org.opencloudengine.garuda.beluga.cloud.ClustersService;
import org.opencloudengine.garuda.beluga.env.DockerWebAppPorts;
import org.opencloudengine.garuda.beluga.exception.BelugaException;
import org.opencloudengine.garuda.beluga.mesos.docker.DockerAPI;
import org.opencloudengine.garuda.beluga.mesos.marathon.MarathonAPI;
import org.springframework.util.StringUtils;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by swsong on 2015. 8. 6..
 */
public class TerminalOpenAction extends RunnableAction<TerminalOpenActionRequest> {

    public TerminalOpenAction(TerminalOpenActionRequest actionRequest) {
        super(actionRequest);
    }

    @Override
    protected void doAction() throws Exception {
        TerminalOpenActionRequest request = getActionRequest();
        String clusterId = request.getClusterId();
        String container = request.getContainer();
        String image = request.getImage();
        String host = request.getHost();

        //TODO 포트 바인딩 클래스 프로바이더 제작
        int bindPort = 9999;
        int containerPort = 9999;

        //도커 머신으로 부팅 한 경우 도커 호스트 변경
//        String docker_host = System.getenv("DOCKER_HOST");
//        if (!StringUtils.isEmpty(docker_host)) {
//            docker_host = docker_host.replace("tcp://", "");
//            String[] split = docker_host.split(":");
//            host = split[0];
//        }

        // clusterId를 통해 인스턴스 주소를 받아온다.
        ClusterService clusterService = serviceManager.getService(ClustersService.class).getClusterService(clusterId);
        ClusterTopology topology = clusterService.getClusterTopology();
        if (topology == null) {
            // 그런 클러스터가 없다.
            throw new BelugaException("No such cluster: " + clusterId);
        }

        DockerAPI dockerAPI = clusterService.getDockerAPI();

        /*
         * 1 get host if mac os x
         * */
        String osName = System.getProperty("os.name").toLowerCase();
        boolean isMacOs = osName.startsWith("mac os x");
        if (isMacOs)
        {
            host = dockerAPI.getMacHost();
        }

        /*
         * 2 open terminal
         * */
        int exitValue = dockerAPI.terminalOpen(container, image, bindPort, containerPort);
        if (exitValue != 0) {
            throw new BelugaException(String.format("Error while open docker terminal : %s", image));
        }

        Map result = new HashMap();
        result.put("host", host);
        result.put("port", bindPort);
        result.put("container", container);
        result.put("image", image);

        setResult(result);
    }
}
