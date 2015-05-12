
package org.opencloudengine.garuda.controller.mesos.marathon.model.task.gettasks.res;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
	"tasks"
})
public class GetTasks {

	@JsonProperty("tasks")
	private List<Task> tasks;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("tasks")
	public List<Task> getTasks() {
		return tasks;
	}

	@JsonProperty("tasks")
	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
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

		GetTasks getTasks = (GetTasks) o;

		if (tasks != null ? !tasks.equals(getTasks.tasks) : getTasks.tasks != null) return false;
		if (additionalProperties != null ? !additionalProperties.equals(getTasks.additionalProperties) : getTasks.additionalProperties != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = tasks != null ? tasks.hashCode() : 0;
		result = 31 * result + (additionalProperties != null ? additionalProperties.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "GetTasks{" +
			"tasks=" + tasks +
			", additionalProperties=" + additionalProperties +
			'}';
	}
}
