package org.opencloudengine.garuda.beluga.cloud;

import org.junit.Before;
import org.junit.Test;
import org.opencloudengine.garuda.beluga.env.Environment;
import org.opencloudengine.garuda.beluga.exception.BelugaException;
import org.opencloudengine.garuda.beluga.exception.UnknownIaasProviderException;
import org.opencloudengine.garuda.beluga.service.common.ServiceManager;

/**
 * Created by swsong on 2015. 7. 21..
 */
public class ClusterServiceTest {

    Environment environment;
    ServiceManager serviceManager;
    ClusterService clusterService;

    String clusterId = "test-cluster";
    String domainName = "fastcatsearch.com";

    @Before
    public void init() throws BelugaException {
        String home = "production";
        environment = new Environment(home);
        environment.init();
        serviceManager = new ServiceManager(environment);
        serviceManager.asSingleton();

        serviceManager.registerService("cluster", ClustersService.class);
        clusterService = serviceManager.getService(ClustersService.class).getClusterService(clusterId);
        System.out.println("Before finished.");
    }

    @Test
    public void testLaunch() throws BelugaException, UnknownIaasProviderException, ClusterExistException {
        String definitionId = "ec2-real";
        clusterService.createCluster(definitionId, domainName, true);
    }

    @Test
    public void testLoad() {
        ClusterTopology clusterTopology = clusterService.getClusterTopology();
        if(clusterTopology == null) {
            System.out.println(String.format("cluster %s is null", clusterId));
        } else {
            System.out.println(clusterId);
            System.out.println(clusterTopology.toString());
        }
    }

    @Test
    public void testDestroy() throws UnknownIaasProviderException {
        clusterService.destroyCluster();
    }
}
