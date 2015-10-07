package org.opencloudengine.garuda.beluga.api;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.opencloudengine.garuda.beluga.env.Environment;
import org.opencloudengine.garuda.beluga.env.Settings;
import org.opencloudengine.garuda.beluga.service.AbstractService;
import org.opencloudengine.garuda.beluga.service.ServiceException;
import org.opencloudengine.garuda.beluga.service.common.ServiceManager;

/**
 * RESTful API를 제공하는 서비스.
 *
 * @author Sang Wook, Song
 *
 */
public class RestAPIService extends AbstractService {

	private Server jettyServer;
	private int servicePort;

	private static final String jersey_provider_class = "jersey.config.server.provider.classnames";
	private static final String beluga_rest_api_package = "org.opencloudengine.garuda.beluga.api.rest";

	public RestAPIService(Environment environment, Settings settings, ServiceManager serviceManager) {
		super(environment, settings, serviceManager);

	}

	public int getServicePort() {
		return servicePort;
	}

	@Override
	protected boolean doStart() throws ServiceException {

		servicePort = settings.getInt("port");
		jettyServer = new Server(servicePort);

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");

		ResourceConfig resourceConfig = new ResourceConfig();
		resourceConfig.packages(true, beluga_rest_api_package);
		resourceConfig.registerClasses(JacksonFeature.class, MultiPartFeature.class);
		ServletContainer servletContainer = new ServletContainer(resourceConfig);
		ServletHolder sh = new ServletHolder(servletContainer);

		context.addServlet(sh, "/*");
		jettyServer.setHandler(context);

		try {
			jettyServer.setStopAtShutdown(true);
			jettyServer.start();
            logger.info("REST Service is listening on port {}", servicePort);
			return true;
		} catch (Exception e) {
			throw new ServiceException("Error while starting REST service on port " + servicePort, e);
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
