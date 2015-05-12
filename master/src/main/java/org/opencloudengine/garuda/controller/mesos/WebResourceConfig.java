package org.opencloudengine.garuda.controller.mesos;

import com.github.dockerjava.core.SSLConfig;

import java.net.URI;

/**
 * Created by soul on 15. 4. 14.
 *
 * @author Seong Jong, Jeon
 * @since 1.0
 */
public class WebResourceConfig {
	private URI uri;
	private SSLConfig sslConfig;
	private Integer readTimeout, maxTotalConnections, maxPerRouteConnections;
	private boolean loggingFilterEnabled, followRedirectsFilterEnabled;

	public WebResourceConfig(URI uri, SSLConfig sslConfig,
	                         Integer readTimeout, Integer maxTotalConnections, Integer maxPerRouteConnections,
	                         boolean loggingFilterEnabled, boolean followRedirectsFilterEnabled) {
		this.uri = uri;
		this.sslConfig = sslConfig;
		this.readTimeout = readTimeout;
		this.maxTotalConnections = maxTotalConnections;
		this.maxPerRouteConnections = maxPerRouteConnections;
		this.loggingFilterEnabled = loggingFilterEnabled;
		this.followRedirectsFilterEnabled = followRedirectsFilterEnabled;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public SSLConfig getSslConfig() {
		return sslConfig;
	}

	public void setSslConfig(SSLConfig sslConfig) {
		this.sslConfig = sslConfig;
	}

	public Integer getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(Integer readTimeout) {
		this.readTimeout = readTimeout;
	}

	public Integer getMaxTotalConnections() {
		return maxTotalConnections;
	}

	public void setMaxTotalConnections(Integer maxTotalConnections) {
		this.maxTotalConnections = maxTotalConnections;
	}

	public Integer getMaxPerRouteConnections() {
		return maxPerRouteConnections;
	}

	public void setMaxPerRouteConnections(Integer maxPerRouteConnections) {
		this.maxPerRouteConnections = maxPerRouteConnections;
	}

	public boolean isLoggingFilterEnabled() {
		return loggingFilterEnabled;
	}

	public void setLoggingFilterEnabled(boolean loggingFilterEnabled) {
		this.loggingFilterEnabled = loggingFilterEnabled;
	}

	public boolean isFollowRedirectsFilterEnabled() {
		return followRedirectsFilterEnabled;
	}

	public void setFollowRedirectsFilterEnabled(boolean followRedirectsFilterEnabled) {
		this.followRedirectsFilterEnabled = followRedirectsFilterEnabled;
	}
}
