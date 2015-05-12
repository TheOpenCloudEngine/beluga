package org.opencloudengine.garuda.controller.mesos;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.github.dockerjava.api.DockerClientException;
import com.github.dockerjava.core.util.FollowRedirectsFilter;
import com.github.dockerjava.core.util.JsonClientFilter;
import com.github.dockerjava.core.util.SelectiveLoggingFilter;
import com.github.dockerjava.jaxrs.UnixConnectionSocketFactory;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.apache.connector.ApacheClientProperties;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.opencloudengine.garuda.controller.mesos.marathon.ResponseStatusExceptionFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.net.URI;

/**
 * Created by soul on 15. 4. 14.
 *
 * @author Seong Jong, Jeon
 * @since 1.0
 */
public class WebResourceClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(WebResourceClient.class);

	private static Registry<ConnectionSocketFactory> getSchemeRegistry(final URI originalUri, SSLContext sslContext) {
		RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.create();
		registryBuilder.register("http", PlainConnectionSocketFactory.getSocketFactory());
		if (sslContext != null) {
			registryBuilder.register("https", new SSLConnectionSocketFactory(sslContext));
		}
		registryBuilder.register("unix", new UnixConnectionSocketFactory(originalUri));
		return registryBuilder.build();
	}

	private static ClientConfig getConfig(WebResourceConfig webResourceConfig) {
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.connectorProvider(new ApacheConnectorProvider());
		clientConfig.property(CommonProperties.FEATURE_AUTO_DISCOVERY_DISABLE, true);
		clientConfig.register(ResponseStatusExceptionFilter.class);
		clientConfig.register(JsonClientFilter.class);
		clientConfig.register(JacksonJsonProvider.class);

		if (webResourceConfig.isFollowRedirectsFilterEnabled()) {
			clientConfig.register(FollowRedirectsFilter.class);
		}

		if (webResourceConfig.isLoggingFilterEnabled()) {
			clientConfig.register(new SelectiveLoggingFilter(LOGGER, true));
		}

		if (webResourceConfig.getReadTimeout() != null) {
			clientConfig.property(ClientProperties.READ_TIMEOUT, webResourceConfig.getReadTimeout());
		}

		return clientConfig;
	}

	public static Client getWebClient(WebResourceConfig webResourceConfig) {
		SSLContext sslContext = null;
		if (webResourceConfig.getSslConfig() != null) {
			try {
				sslContext = webResourceConfig.getSslConfig().getSSLContext();
			} catch (Exception ex) {
				throw new DockerClientException("Error in SSL Configuration", ex);
			}
		}

		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(
			getSchemeRegistry(webResourceConfig.getUri(), sslContext));

		if (webResourceConfig.getMaxTotalConnections() != null) {
			connManager.setMaxTotal(webResourceConfig.getMaxTotalConnections());
		}

		if (webResourceConfig.getMaxPerRouteConnections() != null) {
			connManager.setDefaultMaxPerRoute(webResourceConfig.getMaxPerRouteConnections());
		}

		ClientConfig clientConfig = getConfig(webResourceConfig);
		clientConfig.property(ApacheClientProperties.CONNECTION_MANAGER, connManager);
		ClientBuilder clientBuilder = ClientBuilder.newBuilder().withConfig(clientConfig);

		if (sslContext != null) {
			clientBuilder.sslContext(sslContext);
		}

		if ("unix".equals(webResourceConfig.getUri().getScheme())) {
			webResourceConfig.setUri(UnixConnectionSocketFactory.sanitizeUri(webResourceConfig.getUri()));
		}

		return clientBuilder.build();
	}
}
