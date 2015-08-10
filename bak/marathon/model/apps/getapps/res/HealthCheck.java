
package org.opencloudengine.garuda.mesos.bak.marathon.model.apps.getapps.res;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
	"command",
	"gracePeriodSeconds",
	"intervalSeconds",
	"maxConsecutiveFailures",
	"path",
	"portIndex",
	"protocol",
	"timeoutSeconds"
})
public class HealthCheck {

	@JsonProperty("command")
	private Object command;
	@JsonProperty("gracePeriodSeconds")
	private Integer gracePeriodSeconds;
	@JsonProperty("intervalSeconds")
	private Integer intervalSeconds;
	@JsonProperty("maxConsecutiveFailures")
	private Integer maxConsecutiveFailures;
	@JsonProperty("path")
	private String path;
	@JsonProperty("portIndex")
	private Integer portIndex;
	@JsonProperty("protocol")
	private String protocol;
	@JsonProperty("timeoutSeconds")
	private Integer timeoutSeconds;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("command")
	public Object getCommand() {
		return command;
	}

	@JsonProperty("command")
	public void setCommand(Object command) {
		this.command = command;
	}

	@JsonProperty("gracePeriodSeconds")
	public Integer getGracePeriodSeconds() {
		return gracePeriodSeconds;
	}

	@JsonProperty("gracePeriodSeconds")
	public void setGracePeriodSeconds(Integer gracePeriodSeconds) {
		this.gracePeriodSeconds = gracePeriodSeconds;
	}

	@JsonProperty("intervalSeconds")
	public Integer getIntervalSeconds() {
		return intervalSeconds;
	}

	@JsonProperty("intervalSeconds")
	public void setIntervalSeconds(Integer intervalSeconds) {
		this.intervalSeconds = intervalSeconds;
	}

	@JsonProperty("maxConsecutiveFailures")
	public Integer getMaxConsecutiveFailures() {
		return maxConsecutiveFailures;
	}

	@JsonProperty("maxConsecutiveFailures")
	public void setMaxConsecutiveFailures(Integer maxConsecutiveFailures) {
		this.maxConsecutiveFailures = maxConsecutiveFailures;
	}

	@JsonProperty("path")
	public String getPath() {
		return path;
	}

	@JsonProperty("path")
	public void setPath(String path) {
		this.path = path;
	}

	@JsonProperty("portIndex")
	public Integer getPortIndex() {
		return portIndex;
	}

	@JsonProperty("portIndex")
	public void setPortIndex(Integer portIndex) {
		this.portIndex = portIndex;
	}

	@JsonProperty("protocol")
	public String getProtocol() {
		return protocol;
	}

	@JsonProperty("protocol")
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	@JsonProperty("timeoutSeconds")
	public Integer getTimeoutSeconds() {
		return timeoutSeconds;
	}

	@JsonProperty("timeoutSeconds")
	public void setTimeoutSeconds(Integer timeoutSeconds) {
		this.timeoutSeconds = timeoutSeconds;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		HealthCheck that = (HealthCheck) o;

		if (command != null ? !command.equals(that.command) : that.command != null) return false;
		if (gracePeriodSeconds != null ? !gracePeriodSeconds.equals(that.gracePeriodSeconds) : that.gracePeriodSeconds != null)
			return false;
		if (intervalSeconds != null ? !intervalSeconds.equals(that.intervalSeconds) : that.intervalSeconds != null)
			return false;
		if (maxConsecutiveFailures != null ? !maxConsecutiveFailures.equals(that.maxConsecutiveFailures) : that.maxConsecutiveFailures != null)
			return false;
		if (path != null ? !path.equals(that.path) : that.path != null) return false;
		if (portIndex != null ? !portIndex.equals(that.portIndex) : that.portIndex != null) return false;
		if (protocol != null ? !protocol.equals(that.protocol) : that.protocol != null) return false;
		if (timeoutSeconds != null ? !timeoutSeconds.equals(that.timeoutSeconds) : that.timeoutSeconds != null)
			return false;
		if (additionalProperties != null ? !additionalProperties.equals(that.additionalProperties) : that.additionalProperties != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = command != null ? command.hashCode() : 0;
		result = 31 * result + (gracePeriodSeconds != null ? gracePeriodSeconds.hashCode() : 0);
		result = 31 * result + (intervalSeconds != null ? intervalSeconds.hashCode() : 0);
		result = 31 * result + (maxConsecutiveFailures != null ? maxConsecutiveFailures.hashCode() : 0);
		result = 31 * result + (path != null ? path.hashCode() : 0);
		result = 31 * result + (portIndex != null ? portIndex.hashCode() : 0);
		result = 31 * result + (protocol != null ? protocol.hashCode() : 0);
		result = 31 * result + (timeoutSeconds != null ? timeoutSeconds.hashCode() : 0);
		result = 31 * result + (additionalProperties != null ? additionalProperties.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "HealthCheck{" +
			"command=" + command +
			", gracePeriodSeconds=" + gracePeriodSeconds +
			", intervalSeconds=" + intervalSeconds +
			", maxConsecutiveFailures=" + maxConsecutiveFailures +
			", path='" + path + '\'' +
			", portIndex=" + portIndex +
			", protocol='" + protocol + '\'' +
			", timeoutSeconds=" + timeoutSeconds +
			", additionalProperties=" + additionalProperties +
			'}';
	}
}
