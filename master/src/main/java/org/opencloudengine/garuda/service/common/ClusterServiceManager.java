package org.opencloudengine.garuda.service.common;

import org.opencloudengine.garuda.env.Environment;
import org.opencloudengine.garuda.env.Settings;
import org.opencloudengine.garuda.exception.GarudaException;
import org.opencloudengine.garuda.service.AbstractClusterService;
import org.opencloudengine.garuda.service.AbstractService;
import org.opencloudengine.garuda.service.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cluster별 내부 서비스들을 시작하고 관리를 담당한다.
 *
 * @author Sang Wook, Song
 *
 */
public class ClusterServiceManager {
	private final Logger logger = LoggerFactory.getLogger(ClusterServiceManager.class);

	private Environment environment;
	private Map<Class<?>, AbstractClusterService> serviceMap;
    private Map<Class<? extends AbstractClusterService>, String> serviceIdMap;

	public ClusterServiceManager(Environment environment){
		this.environment = environment;
		serviceMap = new ConcurrentHashMap<>();
        serviceIdMap = new ConcurrentHashMap<>();
	}

    public Class<? extends AbstractClusterService> registerService(String serviceId, Class<? extends AbstractClusterService> serviceClass){
        serviceIdMap.put(serviceClass, serviceId);
        return serviceClass;
    }

	private <T extends AbstractClusterService> T createService(String serviceId, Class<T> serviceClass) throws ServiceException {
		try {
			Constructor<T> construct = serviceClass.getConstructor(String.class, Environment.class, Settings.class, ClusterServiceManager.class);
			T t = construct.newInstance(environment, environment.settingManager().getSystemSettings().getSubSettings(serviceId), this);
			serviceMap.put(serviceClass, t);
			return t;
		} catch (Exception e) {
			throw new ServiceException("Can not make instance of class <"+serviceClass.getName()+">, {}", e);
		}
	}

	public <T extends AbstractClusterService> T getService(Class<T> serviceClass) {

		T t = (T) serviceMap.get(serviceClass);
        if(t == null) {
            String serviceId = serviceIdMap.get(serviceClass);
            if(serviceId == null) {
                throw new RuntimeException("Not registered service : " + serviceId + " : " + serviceClass.getName());
            }
            t = createService(serviceId, serviceClass);
            if(!t.isRunning()) {
                t.start();
            }
            serviceMap.put(serviceClass, t);
        }
        return t;
	}
	
	public <T extends AbstractClusterService> boolean stopService(Class<T> serviceClass) {
		T service = (T) serviceMap.get(serviceClass);
		if(service != null){
			return service.stop();
		}else{
			return false;
		}
	}
	
	public <T extends AbstractClusterService> boolean closeService(Class<T> serviceClass) {
		T service = (T) serviceMap.get(serviceClass);
		if(service != null){
			return service.close();
		}else{
			return false;
		}
	}

    public void startServices() {
        if(serviceMap != null) {

            for(Class serviceClass : serviceIdMap.keySet()) {
                getService(serviceClass);
            }
        }
    }

    public void stopServices() {
        if(serviceMap != null) {

            for(Class serviceClass : serviceIdMap.keySet()) {
                stopService(serviceClass);
            }
        }
    }

    public void closeServices() {
        if(serviceMap != null) {

            for(Class serviceClass : serviceIdMap.keySet()) {
                closeService(serviceClass);
            }
        }
    }

}
