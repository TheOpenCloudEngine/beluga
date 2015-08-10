
package org.opencloudengine.garuda.mesos.bak.marathon.model.apps.createapp.req;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
	"image",
	"network",
	"portMappings",
	"privileged",
	"parameters"
})
public class Docker {

	@JsonProperty("image")
	private String image;
	@JsonProperty("network")
	private String network;
	@JsonProperty("portMappings")
	private List<PortMapping> portMappings;
	@JsonProperty("privileged")
	private Boolean privileged;
	@JsonIgnoreProperties(ignoreUnknown = true)
	@JsonProperty("parameters")
	private List<Parameter> parameters;
	@JsonIgnore
	private Map<String, Object> additionalProperties;

	/**
	 * @return The image
	 */
	@JsonProperty("image")
	public String getImage() {
		return image;
	}

	/**
	 * @param image The image
	 */
	@JsonProperty("image")
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * @return The network
	 */
	@JsonProperty("network")
	public String getNetwork() {
		return network;
	}

	/**
	 * @param network The network
	 */
	@JsonProperty("network")
	public void setNetwork(String network) {
		this.network = network;
	}

	/**
	 * @return The portMappings
	 */
	@JsonProperty("portMappings")
	public List<PortMapping> getPortMappings() {
		return portMappings;
	}

	/**
	 * @param portMappings The portMappings
	 */
	@JsonProperty("portMappings")
	public void setPortMappings(List<PortMapping> portMappings) {
		this.portMappings = portMappings;
	}

	/**
	 * @return The privileged
	 */
	@JsonProperty("privileged")
	public Boolean getPrivileged() {
		return privileged;
	}

	/**
	 * @param privileged The privileged
	 */
	@JsonProperty("privileged")
	public void setPrivileged(Boolean privileged) {
		this.privileged = privileged;
	}

	/**
	 * @return The parameters
	 */
	@JsonProperty("parameters")
	public List<Parameter> getParameters() {
		return parameters;
	}

	/**
	 * @param parameters The parameters
	 */
	@JsonProperty("parameters")
	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

}
