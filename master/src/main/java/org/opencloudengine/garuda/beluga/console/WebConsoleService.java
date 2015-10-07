package org.opencloudengine.garuda.beluga.console;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.opencloudengine.garuda.beluga.env.Environment;
import org.opencloudengine.garuda.beluga.env.Settings;
import org.opencloudengine.garuda.beluga.service.AbstractService;
import org.opencloudengine.garuda.beluga.service.ServiceException;
import org.opencloudengine.garuda.beluga.service.common.ServiceManager;

import java.io.File;
import java.io.FilenameFilter;

/**
 * 웹 콘솔을 제공하는 서비스.
 *
 * @author Sang Wook, Song
 *
 */
public class WebConsoleService extends AbstractService {

	private Server jettyServer;


	public WebConsoleService(Environment environment, Settings settings, ServiceManager serviceManager) {
		super(environment, settings, serviceManager);

	}

	@Override
	protected boolean doStart() throws ServiceException {

		int servicePort = settings.getInt("port");

		File webAppDir = environment.filePaths().file("console");
		File[] files = webAppDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".war");
			}
		});

		File warFile = null;
		if(files != null && files.length > 0){
			//하나만 사용한다.
			warFile = files[0];
			logger.info("Garuda console war = {}", warFile.getAbsolutePath());
		}else{
			//war파일을 찾지못했다면 개발환경 경로를 확인한다.
			File devWebApp = new File("../console/src/main/webapp");
			logger.info("Garuda console dir = {}", devWebApp.getAbsolutePath());
			if(devWebApp.exists()){
				warFile = devWebApp;
			}else{
				logger.error("Cannot find webapp");
				return false;
			}
		}

		String warFilePath = warFile.getAbsolutePath();
		jettyServer = new Server(servicePort);
		File tempDir = new File(webAppDir, "temp");
		tempDir.delete();


		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath("/");
		webapp.setWar(warFilePath);
		webapp.setTempDirectory(tempDir);
		webapp.setParentLoaderPriority(true);
//		webapp.setAttribute(AbstractController.ENVIRONMENT, environment);
		jettyServer.setHandler(webapp);

		try {
			jettyServer.setStopAtShutdown(true);
			jettyServer.start();
            logger.info("Console service is listening on port {}", servicePort);
			return true;
		} catch (Exception e) {
			throw new ServiceException("Error while starting Console service on port " + servicePort, e);
		}
	}

	@Override
	protected boolean doStop() throws ServiceException {
		try {
			jettyServer.stop();
			return true;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	protected boolean doClose() throws ServiceException {
		try {
			jettyServer.destroy();
			jettyServer = null;
			return true;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
}
