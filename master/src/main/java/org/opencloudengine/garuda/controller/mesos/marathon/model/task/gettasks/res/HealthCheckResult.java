
package org.opencloudengine.garuda.controller.mesos.marathon.model.task.gettasks.res;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
	"alive",
	"consecutiveFailures",
	"firstSuccess",
	"lastFailure",
	"lastSuccess",
	"taskId"
})
public class HealthCheckResult {

	@JsonProperty("alive")
	private Boolean alive;
	@JsonProperty("consecutiveFailures")
	private Integer consecutiveFailures;
	@JsonProperty("firstSuccess")
	private String firstSuccess;
	@JsonProperty("lastFailure")
	private Object lastFailure;
	@JsonProperty("lastSuccess")
	private String lastSuccess;
	@JsonProperty("taskId")
	private String taskId;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("alive")
	public Boolean getAlive() {
		return alive;
	}

	@JsonProperty("alive")
	public void setAlive(Boolean alive) {
		this.alive = alive;
	}

	@JsonProperty("consecutiveFailures")
	public Integer getConsecutiveFailures() {
		return consecutiveFailures;
	}

	@JsonProperty("consecutiveFailures")
	public void setConsecutiveFailures(Integer consecutiveFailures) {
		this.consecutiveFailures = consecutiveFailures;
	}

	@JsonProperty("firstSuccess")
	public String getFirstSuccess() {
		return firstSuccess;
	}

	@JsonProperty("firstSuccess")
	public void setFirstSuccess(String firstSuccess) {
		this.firstSuccess = firstSuccess;
	}

	@JsonProperty("lastFailure")
	public Object getLastFailure() {
		return lastFailure;
	}

	@JsonProperty("lastFailure")
	public void setLastFailure(Object lastFailure) {
		this.lastFailure = lastFailure;
	}

	@JsonProperty("lastSuccess")
	public String getLastSuccess() {
		return lastSuccess;
	}

	@JsonProperty("lastSuccess")
	public void setLastSuccess(String lastSuccess) {
		this.lastSuccess = lastSuccess;
	}

	@JsonProperty("taskId")
	public String getTaskId() {
		return taskId;
	}

	@JsonProperty("taskId")
	public void setTaskId(String taskId) {
		this.taskId = taskId;
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

		HealthCheckResult that = (HealthCheckResult) o;

		if (alive != null ? !alive.equals(that.alive) : that.alive != null) return false;
		if (consecutiveFailures != null ? !consecutiveFailures.equals(that.consecutiveFailures) : that.consecutiveFailures != null)
			return false;
		if (firstSuccess != null ? !firstSuccess.equals(that.firstSuccess) : that.firstSuccess != null) return false;
		if (lastFailure != null ? !lastFailure.equals(that.lastFailure) : that.lastFailure != null) return false;
		if (lastSuccess != null ? !lastSuccess.equals(that.lastSuccess) : that.lastSuccess != null) return false;
		if (taskId != null ? !taskId.equals(that.taskId) : that.taskId != null) return false;
		if (additionalProperties != null ? !additionalProperties.equals(that.additionalProperties) : that.additionalProperties != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = alive != null ? alive.hashCode() : 0;
		result = 31 * result + (consecutiveFailures != null ? consecutiveFailures.hashCode() : 0);
		result = 31 * result + (firstSuccess != null ? firstSuccess.hashCode() : 0);
		result = 31 * result + (lastFailure != null ? lastFailure.hashCode() : 0);
		result = 31 * result + (lastSuccess != null ? lastSuccess.hashCode() : 0);
		result = 31 * result + (taskId != null ? taskId.hashCode() : 0);
		result = 31 * result + (additionalProperties != null ? additionalProperties.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "HealthCheckResult{" +
			"alive=" + alive +
			", consecutiveFailures=" + consecutiveFailures +
			", firstSuccess='" + firstSuccess + '\'' +
			", lastFailure=" + lastFailure +
			", lastSuccess='" + lastSuccess + '\'' +
			", taskId='" + taskId + '\'' +
			", additionalProperties=" + additionalProperties +
			'}';
	}
}
