
package org.opencloudengine.garuda.mesos.bak.marathon.model.apps.createapp.res;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
	"image"
})
public class Docker {

	@JsonProperty("image")
	private String image;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("image")
	public String getImage() {
		return image;
	}

	@JsonProperty("image")
	public void setImage(String image) {
		this.image = image;
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

		Docker docker = (Docker) o;

		if (image != null ? !image.equals(docker.image) : docker.image != null) return false;
		if (additionalProperties != null ? !additionalProperties.equals(docker.additionalProperties) : docker.additionalProperties != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = image != null ? image.hashCode() : 0;
		result = 31 * result + (additionalProperties != null ? additionalProperties.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Docker{" +
			"image='" + image + '\'' +
			", additionalProperties=" + additionalProperties +
			'}';
	}
}
