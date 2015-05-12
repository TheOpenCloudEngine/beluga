package org.opencloudengine.garuda.service.common;

import org.opencloudengine.garuda.env.Environment;
import org.opencloudengine.garuda.env.Settings;
import org.opencloudengine.garuda.exception.GarudaException;
import org.opencloudengine.garuda.service.AbstractService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 내부 서비스들을 시작하고 관리를 담당한다.
 *
 * @author Sang Wook, Song
 *
 */
public class ServiceManager {
	private final Logger logger = LoggerFactory.getLogger(ServiceManager.class);
	
	private static ServiceManager instance;
	private Environment environment;
	private Map<Class<?>, AbstractService> serviceMap;
	
	public static ServiceManager getInstance(){
		return instance;
	}
	
	public void asSingleton(){
		instance = this;
	}
	
	public ServiceManager(Environment environment){
		this.environment = environment;
		serviceMap = new ConcurrentHashMap<Class<?>, AbstractService>();
	}
	
	public <T extends AbstractService> T createService(String settingName, Class<T> serviceClass){
		try {
			Constructor<T> construct = serviceClass.getConstructor(Environment.class, Settings.class, ServiceManager.class);
			T t = construct.newInstance(environment, environment.settingManager().getSystemSettings().getSubSettings(settingName), this);
			serviceMap.put(serviceClass, t);
			return t;
		} catch (Exception e) {
			logger.error("can not make instance of class <"+serviceClass.getName()+">, {}", e);
		}
		return null;
	}
	
	
	public <T extends AbstractService> T getService(Class<T> serviceClass) {
		return (T) serviceMap.get(serviceClass);
	}
	
	public <T extends AbstractService> boolean stopService(Class<T> serviceClass) throws GarudaException {
		T service = (T) serviceMap.get(serviceClass);
		if(service != null){
			return service.stop();
		}else{
			return false;
		}
	}
	
	public <T extends AbstractService> boolean closeService(Class<T> serviceClass) throws GarudaException {
		T service = (T) serviceMap.get(serviceClass);
		if(service != null){
			return service.close();
		}else{
			return false;
		}
	}

}
