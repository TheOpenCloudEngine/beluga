
package org.opencloudengine.garuda.controller.mesos.marathon.model.apps.createapp.res;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
	"minimumHealthCapacity",
	"maximumOverCapacity"
})
public class UpgradeStrategy {

	@JsonProperty("minimumHealthCapacity")
	private Double minimumHealthCapacity;
	@JsonProperty("maximumOverCapacity")
	private Double maximumOverCapacity;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("minimumHealthCapacity")
	public Double getMinimumHealthCapacity() {
		return minimumHealthCapacity;
	}

	@JsonProperty("minimumHealthCapacity")
	public void setMinimumHealthCapacity(Double minimumHealthCapacity) {
		this.minimumHealthCapacity = minimumHealthCapacity;
	}

	@JsonProperty("maximumOverCapacity")
	public Double getMaximumOverCapacity() {
		return maximumOverCapacity;
	}

	@JsonProperty("maximumOverCapacity")
	public void setMaximumOverCapacity(Double maximumOverCapacity) {
		this.maximumOverCapacity = maximumOverCapacity;
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

		UpgradeStrategy that = (UpgradeStrategy) o;

		if (minimumHealthCapacity != null ? !minimumHealthCapacity.equals(that.minimumHealthCapacity) : that.minimumHealthCapacity != null)
			return false;
		if (maximumOverCapacity != null ? !maximumOverCapacity.equals(that.maximumOverCapacity) : that.maximumOverCapacity != null)
			return false;
		if (additionalProperties != null ? !additionalProperties.equals(that.additionalProperties) : that.additionalProperties != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = minimumHealthCapacity != null ? minimumHealthCapacity.hashCode() : 0;
		result = 31 * result + (maximumOverCapacity != null ? maximumOverCapacity.hashCode() : 0);
		result = 31 * result + (additionalProperties != null ? additionalProperties.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "UpgradeStrategy{" +
			"minimumHealthCapacity=" + minimumHealthCapacity +
			", maximumOverCapacity=" + maximumOverCapacity +
			", additionalProperties=" + additionalProperties +
			'}';
	}
}
