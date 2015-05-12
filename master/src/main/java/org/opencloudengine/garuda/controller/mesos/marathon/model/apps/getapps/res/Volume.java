
package org.opencloudengine.garuda.controller.mesos.marathon.model.apps.getapps.res;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
	"containerPath",
	"hostPath",
	"mode"
})
public class Volume {

	@JsonProperty("containerPath")
	private String containerPath;
	@JsonProperty("hostPath")
	private String hostPath;
	@JsonProperty("mode")
	private String mode;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("containerPath")
	public String getContainerPath() {
		return containerPath;
	}

	@JsonProperty("containerPath")
	public void setContainerPath(String containerPath) {
		this.containerPath = containerPath;
	}

	@JsonProperty("hostPath")
	public String getHostPath() {
		return hostPath;
	}

	@JsonProperty("hostPath")
	public void setHostPath(String hostPath) {
		this.hostPath = hostPath;
	}

	@JsonProperty("mode")
	public String getMode() {
		return mode;
	}

	@JsonProperty("mode")
	public void setMode(String mode) {
		this.mode = mode;
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

		Volume volume = (Volume) o;

		if (containerPath != null ? !containerPath.equals(volume.containerPath) : volume.containerPath != null)
			return false;
		if (hostPath != null ? !hostPath.equals(volume.hostPath) : volume.hostPath != null) return false;
		if (mode != null ? !mode.equals(volume.mode) : volume.mode != null) return false;
		if (additionalProperties != null ? !additionalProperties.equals(volume.additionalProperties) : volume.additionalProperties != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = containerPath != null ? containerPath.hashCode() : 0;
		result = 31 * result + (hostPath != null ? hostPath.hashCode() : 0);
		result = 31 * result + (mode != null ? mode.hashCode() : 0);
		result = 31 * result + (additionalProperties != null ? additionalProperties.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Volume{" +
			"containerPath='" + containerPath + '\'' +
			", hostPath='" + hostPath + '\'' +
			", mode='" + mode + '\'' +
			", additionalProperties=" + additionalProperties +
			'}';
	}
}
