
package org.opencloudengine.garuda.mesos.bak.master.model.state;

import com.fasterxml.jackson.annotation.*;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
	"active",
	"attributes",
	"hostname",
	"id",
	"pid",
	"registered_time",
	"resources"
})
public class Slafe {

	@JsonProperty("active")
	private Boolean active;
	@JsonProperty("attributes")
	private Attributes attributes;
	@JsonProperty("hostname")
	private String hostname;
	@JsonProperty("id")
	private String id;
	@JsonProperty("pid")
	private String pid;
	@JsonProperty("registered_time")
	private Float registeredTime;
	@JsonProperty("resources")
	private Resources resources;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("active")
	public Boolean getActive() {
		return active;
	}

	@JsonProperty("active")
	public void setActive(Boolean active) {
		this.active = active;
	}

	@JsonProperty("attributes")
	public Attributes getAttributes() {
		return attributes;
	}

	@JsonProperty("attributes")
	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}

	@JsonProperty("hostname")
	public String getHostname() {
		return hostname;
	}

	@JsonProperty("hostname")
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	@JsonProperty("id")
	public String getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(String id) {
		this.id = id;
	}

	@JsonProperty("pid")
	public String getPid() {
		return pid;
	}

	@JsonProperty("pid")
	public void setPid(String pid) {
		this.pid = pid;
	}

	@JsonProperty("registered_time")
	public Float getRegisteredTime() {
		return registeredTime;
	}

	@JsonProperty("registered_time")
	public void setRegisteredTime(Float registeredTime) {
		this.registeredTime = registeredTime;
	}

	@JsonProperty("resources")
	public Resources getResources() {
		return resources;
	}

	@JsonProperty("resources")
	public void setResources(Resources resources) {
		this.resources = resources;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
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
	public int hashCode() {
		return new HashCodeBuilder().append(active).append(attributes).append(hostname).append(id).append(pid).append(registeredTime).append(resources).append(additionalProperties).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof Slafe) == false) {
			return false;
		}
		Slafe rhs = ((Slafe) other);
		return new EqualsBuilder().append(active, rhs.active).append(attributes, rhs.attributes).append(hostname, rhs.hostname).append(id, rhs.id).append(pid, rhs.pid).append(registeredTime, rhs.registeredTime).append(resources, rhs.resources).append(additionalProperties, rhs.additionalProperties).isEquals();
	}

}
