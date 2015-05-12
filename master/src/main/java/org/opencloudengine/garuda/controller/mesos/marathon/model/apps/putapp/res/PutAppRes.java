package org.opencloudengine.garuda.controller.mesos.marathon.model.apps.putapp.res;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by soul on 15. 4. 14.
 *
 * @author Seong Jong, Jeon
 * @since 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
	"deploymentId",
	"version"
})
public class PutAppRes {
	@JsonProperty("deploymentId")
	private String deploymentId;
	@JsonProperty("version")
	private String version;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("deploymentId")
	public String getDeploymentId() {
		return deploymentId;
	}

	@JsonProperty("deploymentId")
	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
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

		PutAppRes putAppRes = (PutAppRes) o;

		if (deploymentId != null ? !deploymentId.equals(putAppRes.deploymentId) : putAppRes.deploymentId != null)
			return false;
		if (version != null ? !version.equals(putAppRes.version) : putAppRes.version != null) return false;
		if (additionalProperties != null ? !additionalProperties.equals(putAppRes.additionalProperties) : putAppRes.additionalProperties != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = deploymentId != null ? deploymentId.hashCode() : 0;
		result = 31 * result + (version != null ? version.hashCode() : 0);
		result = 31 * result + (additionalProperties != null ? additionalProperties.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "PutAppRes{" +
			"deploymentId='" + deploymentId + '\'' +
			", version='" + version + '\'' +
			", additionalProperties=" + additionalProperties +
			'}';
	}
}
