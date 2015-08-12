package org.opencloudengine.garuda.service;

import org.opencloudengine.garuda.env.Environment;
import org.opencloudengine.garuda.env.Settings;
import org.opencloudengine.garuda.service.common.ClusterServiceManager;
import org.opencloudengine.garuda.service.common.Lifecycle;
import org.opencloudengine.garuda.service.common.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract Cluster 별 서비스.
 * 각 클러스터별로 유지될 서비스는 이 클래스를 상속받아서 구현한다.
 * 하나의 서비스에서 여러 클러스터를 관리시 데이터가 서로 중복될 우려가 있고, 클러스터끼리의 장애가 전파되므로,
 * 별도의 서비스 인스턴스로 관리하도록 한다.
 *
 * @author Sang Wook, Song
 *
 */
public abstract class AbstractClusterService {
	protected static Logger logger = LoggerFactory.getLogger(AbstractClusterService.class);

    protected String clusterId;
	protected Lifecycle lifecycle;
	protected Environment environment;
	protected Settings settings;

	public AbstractClusterService(String clusterId, Environment environment, Settings settings){
//		logger.debug("[{}] Service [{}]", clusterId, getClass().getName());//, settings.properties());
        this.clusterId = clusterId;
		this.environment = environment;
		this.settings = settings;
		lifecycle = new Lifecycle();
	}
	
	public boolean isRunning() {
		return lifecycle.started();
	}
	
	public Settings settings(){
		return settings;
	}
	
	public boolean start() throws ServiceException {

		if(lifecycle.canMoveToStarted()){
			if(doStart()){
				logger.info("["+clusterId+"] "+getClass().getSimpleName()+" started!");
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
				logger.info("["+clusterId+"] "+getClass().getSimpleName()+" stopped!");
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
		logger.info("["+clusterId+"] "+this.getClass().getName()+" restarted..");
		if(stop()){
		//start는 성공해야 하므로 해당값을 리턴해준다.
			return start();
		}
		return false;
	}
	
	public boolean close() throws ServiceException {
		if(lifecycle.canMoveToClosed()){
			if(doClose()){
				logger.info("["+clusterId+"] "+getClass().getSimpleName()+" closed!");
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
