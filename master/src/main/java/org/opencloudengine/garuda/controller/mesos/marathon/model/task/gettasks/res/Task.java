
package org.opencloudengine.garuda.controller.mesos.marathon.model.task.gettasks.res;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
	"appId",
	"healthCheckResults",
	"host",
	"id",
	"ports",
	"servicePorts",
	"stagedAt",
	"startedAt",
	"version"
})
public class Task {

	@JsonProperty("appId")
	private String appId;
	@JsonProperty("healthCheckResults")
	private List<HealthCheckResult> healthCheckResults;
	@JsonProperty("host")
	private String host;
	@JsonProperty("id")
	private String id;
	@JsonProperty("ports")
	private List<Integer> ports;
	@JsonProperty("servicePorts")
	private List<Integer> servicePorts;
	@JsonProperty("stagedAt")
	private String stagedAt;
	@JsonProperty("startedAt")
	private String startedAt;
	@JsonProperty("version")
	private String version;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("appId")
	public String getAppId() {
		return appId;
	}

	@JsonProperty("appId")
	public void setAppId(String appId) {
		this.appId = appId;
	}

	@JsonProperty("healthCheckResults")
	public List<HealthCheckResult> getHealthCheckResults() {
		return healthCheckResults;
	}

	@JsonProperty("healthCheckResults")
	public void setHealthCheckResults(List<HealthCheckResult> healthCheckResults) {
		this.healthCheckResults = healthCheckResults;
	}

	@JsonProperty("host")
	public String getHost() {
		return host;
	}

	@JsonProperty("host")
	public void setHost(String host) {
		this.host = host;
	}

	@JsonProperty("id")
	public String getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(String id) {
		this.id = id;
	}

	@JsonProperty("ports")
	public List<Integer> getPorts() {
		return ports;
	}

	@JsonProperty("ports")
	public void setPorts(List<Integer> ports) {
		this.ports = ports;
	}

	@JsonProperty("servicePorts")
	public List<Integer> getServicePorts() {
		return servicePorts;
	}

	@JsonProperty("servicePorts")
	public void setServicePorts(List<Integer> servicePorts) {
		this.servicePorts = servicePorts;
	}

	@JsonProperty("stagedAt")
	public String getStagedAt() {
		return stagedAt;
	}

	@JsonProperty("stagedAt")
	public void setStagedAt(String stagedAt) {
		this.stagedAt = stagedAt;
	}

	@JsonProperty("startedAt")
	public String getStartedAt() {
		return startedAt;
	}

	@JsonProperty("startedAt")
	public void setStartedAt(String startedAt) {
		this.startedAt = startedAt;
	}

	@JsonProperty("version")
	public String getVersion() {
		return version;
	}

	@JsonProperty("version")
	public void setVersion(String version) {
		this.version = version;
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

		Task task = (Task) o;

		if (appId != null ? !appId.equals(task.appId) : task.appId != null) return false;
		if (healthCheckResults != null ? !healthCheckResults.equals(task.healthCheckResults) : task.healthCheckResults != null)
			return false;
		if (host != null ? !host.equals(task.host) : task.host != null) return false;
		if (id != null ? !id.equals(task.id) : task.id != null) return false;
		if (ports != null ? !ports.equals(task.ports) : task.ports != null) return false;
		if (servicePorts != null ? !servicePorts.equals(task.servicePorts) : task.servicePorts != null) return false;
		if (stagedAt != null ? !stagedAt.equals(task.stagedAt) : task.stagedAt != null) return false;
		if (startedAt != null ? !startedAt.equals(task.startedAt) : task.startedAt != null) return false;
		if (version != null ? !version.equals(task.version) : task.version != null) return false;
		if (additionalProperties != null ? !additionalProperties.equals(task.additionalProperties) : task.additionalProperties != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = appId != null ? appId.hashCode() : 0;
		result = 31 * result + (healthCheckResults != null ? healthCheckResults.hashCode() : 0);
		result = 31 * result + (host != null ? host.hashCode() : 0);
		result = 31 * result + (id != null ? id.hashCode() : 0);
		result = 31 * result + (ports != null ? ports.hashCode() : 0);
		result = 31 * result + (servicePorts != null ? servicePorts.hashCode() : 0);
		result = 31 * result + (stagedAt != null ? stagedAt.hashCode() : 0);
		result = 31 * result + (startedAt != null ? startedAt.hashCode() : 0);
		result = 31 * result + (version != null ? version.hashCode() : 0);
		result = 31 * result + (additionalProperties != null ? additionalProperties.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Task{" +
			"appId='" + appId + '\'' +
			", healthCheckResults=" + healthCheckResults +
			", host='" + host + '\'' +
			", id='" + id + '\'' +
			", ports=" + ports +
			", servicePorts=" + servicePorts +
			", stagedAt='" + stagedAt + '\'' +
			", startedAt='" + startedAt + '\'' +
			", version='" + version + '\'' +
			", additionalProperties=" + additionalProperties +
			'}';
	}
}
