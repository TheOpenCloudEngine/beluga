package org.opencloudengine.garuda.service;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.opencloudengine.garuda.env.Environment;
import org.opencloudengine.garuda.env.Settings;
import org.opencloudengine.garuda.exception.GarudaException;
import org.opencloudengine.garuda.rest.HumanAPI;
import org.opencloudengine.garuda.rest.SampleAPI;
import org.opencloudengine.garuda.service.common.ServiceManager;

/**
 * RESTful API를 제공하는 서비스.
 *
 * @author Sang Wook, Song
 *
 */
public class RESTService extends AbstractService {

	private Server jettyServer;

	public RESTService(Environment environment, Settings settings, ServiceManager serviceManager) {
		super(environment, settings, serviceManager);

	}

	@Override
	protected boolean doStart() throws GarudaException {
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");

		int servicePort = environment.settingManager().getSystemSettings().getInt("service.port");
		jettyServer = new Server(servicePort);
		jettyServer.setHandler(context);

		ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
		jerseyServlet.setInitOrder(0);

		// Tells the Jersey Servlet which REST service/class to load.
		jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", HumanAPI.class.getCanonicalName());
		jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", SampleAPI.class.getCanonicalName());

		try {
			jettyServer.start();
			return true;
		} catch (Exception e) {
			throw new GarudaException(e);
		}
	}

	@Override
	protected boolean doStop() throws GarudaException {
		try {
			jettyServer.stop();
			return true;
		} catch (Exception e) {
			throw new GarudaException(e);
		}
	}

	@Override
	protected boolean doClose() throws GarudaException {
		try {
			jettyServer.destroy();
			jettyServer = null;
			return true;
		} catch (Exception e) {
			throw new GarudaException(e);
		}
	}
}
