package org.opencloudengine.garuda.service;

import org.opencloudengine.garuda.env.Environment;
import org.opencloudengine.garuda.env.Settings;
import org.opencloudengine.garuda.service.common.Lifecycle;
import org.opencloudengine.garuda.service.common.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract 서비스.
 *
 * @author Sang Wook, Song
 *
 */
public abstract class AbstractService {
	protected static Logger logger = LoggerFactory.getLogger(AbstractService.class);
	
	protected Lifecycle lifecycle;
	protected Environment environment;
	protected Settings settings;
	protected ServiceManager serviceManager;
	
	public AbstractService(Environment environment, Settings settings, ServiceManager serviceManager){
		logger.debug("Service [{}]", getClass().getName());//, settings.properties());
		this.environment = environment;
		this.settings = settings;
		this.serviceManager = serviceManager;
		lifecycle = new Lifecycle();
	}
	
	public boolean isRunning() {
		return lifecycle.started();
	}
	
	public Settings settings(){
		return settings;
	}
	
	public boolean start() throws ServiceException {

		//엔진구동시 시작되는 서비스가 아니라면, 현 상태가 구동시점인지 stop상태인지 확인해봐야한다.
		if(!settings.getBoolean("start_on_load", true)){
			if(lifecycle.initialized()){
				logger.info(getClass().getSimpleName()+"는 구동시 시작하지 않습니다.");
				//STOP 상태로 변경한다.
				lifecycle.moveToStarted();
				lifecycle.moveToStopped();
				//구동시점에 stop으로 변경하므로 차후 start가 가능하다.
				return false;
			}
		}
		
		if(lifecycle.canMoveToStarted()){
			if(doStart()){
				logger.info(getClass().getSimpleName()+" 시작!");
				lifecycle.moveToStarted();
				return true;
			}else{
				return false;
			}
		}
		return false;
	}
	
	protected abstract boolean doStart() throws ServiceException;
		
	public boolean stop() throws ServiceException {
		
		if(lifecycle.canMoveToStopped()){
			if(doStop()){
				logger.info(getClass().getSimpleName()+" 정지!");
				lifecycle.moveToStopped();
				return true;
			}else{
				return false;
			}
		}
		return false;
	}
	
	protected abstract boolean doStop() throws ServiceException;
	
	public boolean restart() throws ServiceException {
		logger.info(this.getClass().getName()+" restart..");
		if(stop()){
		//start는 성공해야 하므로 해당값을 리턴해준다.
			return start();
		}
		return false;
	}
	
	public boolean close() throws ServiceException {
		if(lifecycle.canMoveToClosed()){
			if(doClose()){
				logger.info(getClass().getSimpleName()+" 정지!");
				lifecycle.moveToClosed();
				return true;
			}else{
				return false;
			}
		}
		return false;
	}
	
	protected abstract boolean doClose() throws ServiceException;
	
}
