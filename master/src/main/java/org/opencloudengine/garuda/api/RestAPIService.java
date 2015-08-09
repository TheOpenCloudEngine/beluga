package org.opencloudengine.garuda.api;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.opencloudengine.garuda.env.Environment;
import org.opencloudengine.garuda.env.Settings;
import org.opencloudengine.garuda.service.AbstractService;
import org.opencloudengine.garuda.service.ServiceException;
import org.opencloudengine.garuda.service.common.ServiceManager;

/**
 * RESTful API를 제공하는 서비스.
 *
 * @author Sang Wook, Song
 *
 */
public class RestAPIService extends AbstractService {

	private Server jettyServer;

	private static final String jersey_provider_class = "jersey.config.server.provider.classnames";
	private static final String garuda_rest_api_package = "org.opencloudengine.garuda.api.rest";

	public RestAPIService(Environment environment, Settings settings, ServiceManager serviceManager) {
		super(environment, settings, serviceManager);

	}

	@Override
	protected boolean doStart() throws ServiceException {


		int servicePort = environment.settingManager().getSystemSettings().getInt("service.port");
		jettyServer = new Server(servicePort);

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
//		jettyServer.setHandler(context);

//		ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
//		jerseyServlet.setInitOrder(0);

		ResourceConfig resourceConfig = new ResourceConfig();
		resourceConfig.packages(true, garuda_rest_api_package);
		resourceConfig.registerClasses(JacksonFeature.class, MultiPartFeature.class);
		ServletContainer servletContainer = new ServletContainer(resourceConfig);
		ServletHolder sh = new ServletHolder(servletContainer);

		context.addServlet(sh, "/*");
		jettyServer.setHandler(context);


		// Tells the Jersey Servlet which REST service/class to load.
//		jerseyServlet.setInitParameter(jersey_provider_class, HumanAPI.class.getCanonicalName());
//		jerseyServlet.setInitParameter(jersey_provider_class, SampleAPI.class.getCanonicalName());

		try {
			jettyServer.start();
            logger.info("REST Service is listening on port {}", servicePort);
			return true;
		} catch (Exception e) {
			throw new ServiceException(e);
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
