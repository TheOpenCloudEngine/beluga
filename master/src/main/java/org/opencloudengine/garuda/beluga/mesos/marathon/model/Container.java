
package org.opencloudengine.garuda.beluga.mesos.marathon.model;

import com.fasterxml.jackson.annotation.*;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"type",
	"docker",
	"volumes"
})
public class Container {

	@JsonProperty("type")
	private String type;
	@JsonProperty("docker")
	private Docker docker;
	@JsonProperty("volumes")
	private List<Volume> volumes;

	/**
	 * @return The type
	 */
	@JsonProperty("type")
	public String getType() {
		return type;
	}

	/**
	 * @param type The type
	 */
	@JsonProperty("type")
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return The docker
	 */
	@JsonProperty("docker")
	public Docker getDocker() {
		return docker;
	}

	/**
	 * @param docker The docker
	 */
	@JsonProperty("docker")
	public void setDocker(Docker docker) {
		this.docker = docker;
	}

	/**
	 * @return The volumes
	 */
	@JsonProperty("volumes")
	public List<Volume> getVolumes() {
		return volumes;
	}

	/**
	 * @param volumes The volumes
	 */
	@JsonProperty("volumes")
	public void setVolumes(List<Volume> volumes) {
		this.volumes = volumes;
	}

}
