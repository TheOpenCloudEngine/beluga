package org.opencloudengine.garuda.beluga.action.terminal;

import org.opencloudengine.garuda.beluga.action.RunnableAction;
import org.opencloudengine.garuda.beluga.cloud.ClusterService;
import org.opencloudengine.garuda.beluga.cloud.ClusterTopology;
import org.opencloudengine.garuda.beluga.cloud.ClustersService;
import org.opencloudengine.garuda.beluga.exception.BelugaException;
import org.opencloudengine.garuda.beluga.mesos.docker.DockerAPI;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by swsong on 2015. 8. 6..
 */
public class TerminalCommitAction extends RunnableAction<TerminalCommitActionRequest> {

    public TerminalCommitAction(TerminalCommitActionRequest actionRequest) {
        super(actionRequest);
    }

    @Override
    protected void doAction() throws Exception {
        TerminalCommitActionRequest request = getActionRequest();
        String clusterId = request.getClusterId();
        String image = request.getImage();
        String container = request.getContainer();
        String cmd = request.getCmd();
        int port = request.getPort();


        // clusterId를 통해 인스턴스 주소를 받아온다.
        ClusterService clusterService = serviceManager.getService(ClustersService.class).getClusterService(clusterId);
        ClusterTopology topology = clusterService.getClusterTopology();
        if (topology == null) {
            // 그런 클러스터가 없다.
            throw new BelugaException("No such cluster: " + clusterId);
        }

        String registryAddress = topology.getRegistryAddressPort();
        if (registryAddress == null) {
            throw new BelugaException("No registry instance in " + clusterId);
        }

        image = registryAddress + "/" + image;

        DockerAPI dockerAPI = clusterService.getDockerAPI();

        /*
         * 1 commit terminal
         * */
        int exitValue = dockerAPI.terminalCommit(container, image, cmd, port);
        if (exitValue != 0) {
            throw new BelugaException(String.format("Error while commit docker terminal : %s", image));
        }

        /*
         * 2 push image to registry
         * */
        exitValue = dockerAPI.pushDockerImageToRegistry(image);
        if (exitValue != 0) {
            throw new BelugaException(String.format("Error while push docker image : %s", image));
        }

        Map result = new HashMap();
        result.put("image", image);
        setResult(result);
    }
}
