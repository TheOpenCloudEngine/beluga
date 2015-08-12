package org.opencloudengine.garuda.cloud;

import org.opencloudengine.garuda.env.Environment;
import org.opencloudengine.garuda.exception.GarudaException;
import org.opencloudengine.garuda.mesos.MesosService;
import org.opencloudengine.garuda.service.common.ClusterServiceManager;
import org.opencloudengine.garuda.settings.ClusterDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by swsong on 2015. 8. 12..
 */
public class Cluster {
    private final Logger logger = LoggerFactory.getLogger(Cluster.class);
    private String id;
    private Environment environment;

    private ClusterServiceManager serviceManager;
    public long startTime;
    public long stopTime;
    private boolean isRunning;

    public Cluster(String id, Environment environment) {
        this.id = id;
        this.environment = environment;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public ClusterServiceManager services() {
        return serviceManager;
    }

    public void start() {
        this.serviceManager = new ClusterServiceManager(environment);

        serviceManager.registerService("rest", MesosService.class);
        serviceManager.registerService("controller", ClusterService.class);
		/*
		* Start Services
		* */
        serviceManager.startServices();
        startTime = System.currentTimeMillis();
        isRunning = true;
    }

    public void restart() {
        logger.info("Restart Cluster : {}", id);

        stop();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignore) {
            // Thread가 인터럽트 걸리므로 한번더 시도.
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore2) {
            }
        }
        start();

    }

    public void stop() {
		/*
		* Stop services
		* */
        logger.info("Stop Cluster : {}", id);
        serviceManager.stopServices();
        stopTime = System.currentTimeMillis();
        isRunning = false;
    }

    public void close() {

		/*
		* Close services
		* */
        logger.info("Close Cluster : {}", id);
        stop();
        serviceManager.closeServices();
    }

    public static Cluster createCluster(IaasProvider iaasProvider, ClusterDefinition clusterDefinition) {







        //TODO





        return null;
    }
}
