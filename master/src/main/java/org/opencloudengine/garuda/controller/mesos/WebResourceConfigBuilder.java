package org.opencloudengine.garuda.controller.mesos;

import com.github.dockerjava.core.LocalDirectorySSLConfig;
import com.github.dockerjava.core.SSLConfig;

import java.net.URI;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by soul on 15. 4. 14.
 *
 * @author Seong Jong, Jeon
 * @since 1.0
 */
public class WebResourceConfigBuilder {
	private URI uri;
	private SSLConfig sslConfig;
	private Integer readTimeout, maxTotalConnections, maxPerRouteConnections;
	private boolean loggingFilterEnabled, followRedirectsFilterEnabled;

	public final WebResourceConfigBuilder withUri(String uri) {
		checkNotNull(uri, "uri was not specified");
		this.uri = URI.create(uri);
		return this;
	}

	public WebResourceConfigBuilder withCertPath(String certPath) {
		this.sslConfig = new LocalDirectorySSLConfig(certPath);
		return this;
	}

	public WebResourceConfigBuilder withSSLConfig(SSLConfig config) {
		this.sslConfig = config;
		return this;
	}

	public WebResourceConfigBuilder withReadTimeout(Integer readTimeout) {
		this.readTimeout = readTimeout;
		return this;
	}

	public WebResourceConfigBuilder withMaxTotalConnections(Integer maxTotalConnections) {
		this.maxTotalConnections = maxTotalConnections;
		return this;
	}

	public WebResourceConfigBuilder withMaxPerRouteConnections(Integer maxPerRouteConnections) {
		this.maxPerRouteConnections = maxPerRouteConnections;
		return this;
	}

	public WebResourceConfigBuilder withLoggingFilter(boolean loggingFilterEnabled) {
		this.loggingFilterEnabled = loggingFilterEnabled;
		return this;
	}

	public WebResourceConfigBuilder withFollowRedirectsFilter(boolean followRedirectsFilterEnabled) {
		this.followRedirectsFilterEnabled = followRedirectsFilterEnabled;
		return this;
	}

	public WebResourceConfig build() {
		return new WebResourceConfig(
			uri
			, sslConfig
			, readTimeout
			, maxTotalConnections
			, maxPerRouteConnections
			, loggingFilterEnabled
			, followRedirectsFilterEnabled
		);
	}
}
