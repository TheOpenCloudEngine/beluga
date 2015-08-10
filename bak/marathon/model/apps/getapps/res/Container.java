
package org.opencloudengine.garuda.mesos.bak.marathon.model.apps.getapps.res;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
	"docker",
	"type",
	"volumes"
})
public class Container {

	@JsonProperty("docker")
	private Docker docker;
	@JsonProperty("type")
	private String type;
	@JsonProperty("volumes")
	private List<Volume> volumes;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("docker")
	public Docker getDocker() {
		return docker;
	}

	@JsonProperty("docker")
	public void setDocker(Docker docker) {
		this.docker = docker;
	}

	@JsonProperty("type")
	public String getType() {
		return type;
	}

	@JsonProperty("type")
	public void setType(String type) {
		this.type = type;
	}

	@JsonProperty("volumes")
	public List<Volume> getVolumes() {
		return volumes;
	}

	@JsonProperty("volumes")
	public void setVolumes(List<Volume> volumes) {
		this.volumes = volumes;
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

		Container container = (Container) o;

		if (docker != null ? !docker.equals(container.docker) : container.docker != null) return false;
		if (type != null ? !type.equals(container.type) : container.type != null) return false;
		if (volumes != null ? !volumes.equals(container.volumes) : container.volumes != null) return false;
		if (additionalProperties != null ? !additionalProperties.equals(container.additionalProperties) : container.additionalProperties != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = docker != null ? docker.hashCode() : 0;
		result = 31 * result + (type != null ? type.hashCode() : 0);
		result = 31 * result + (volumes != null ? volumes.hashCode() : 0);
		result = 31 * result + (additionalProperties != null ? additionalProperties.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Container{" +
			"docker=" + docker +
			", type='" + type + '\'' +
			", volumes=" + volumes +
			", additionalProperties=" + additionalProperties +
			'}';
	}
}
