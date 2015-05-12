
package org.opencloudengine.garuda.controller.mesos.marathon.model.apps.getapps.res;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
	"containerPort",
	"hostPort",
	"servicePort",
	"protocol"
})
public class PortMapping {

	@JsonProperty("containerPort")
	private Integer containerPort;
	@JsonProperty("hostPort")
	private Integer hostPort;
	@JsonProperty("servicePort")
	private Integer servicePort;
	@JsonProperty("protocol")
	private String protocol;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("containerPort")
	public Integer getContainerPort() {
		return containerPort;
	}

	@JsonProperty("containerPort")
	public void setContainerPort(Integer containerPort) {
		this.containerPort = containerPort;
	}

	@JsonProperty("hostPort")
	public Integer getHostPort() {
		return hostPort;
	}

	@JsonProperty("hostPort")
	public void setHostPort(Integer hostPort) {
		this.hostPort = hostPort;
	}

	@JsonProperty("servicePort")
	public Integer getServicePort() {
		return servicePort;
	}

	@JsonProperty("servicePort")
	public void setServicePort(Integer servicePort) {
		this.servicePort = servicePort;
	}

	@JsonProperty("protocol")
	public String getProtocol() {
		return protocol;
	}

	@JsonProperty("protocol")
	public void setProtocol(String protocol) {
		this.protocol = protocol;
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

		PortMapping that = (PortMapping) o;

		if (containerPort != null ? !containerPort.equals(that.containerPort) : that.containerPort != null)
			return false;
		if (hostPort != null ? !hostPort.equals(that.hostPort) : that.hostPort != null) return false;
		if (servicePort != null ? !servicePort.equals(that.servicePort) : that.servicePort != null) return false;
		if (protocol != null ? !protocol.equals(that.protocol) : that.protocol != null) return false;
		if (additionalProperties != null ? !additionalProperties.equals(that.additionalProperties) : that.additionalProperties != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = containerPort != null ? containerPort.hashCode() : 0;
		result = 31 * result + (hostPort != null ? hostPort.hashCode() : 0);
		result = 31 * result + (servicePort != null ? servicePort.hashCode() : 0);
		result = 31 * result + (protocol != null ? protocol.hashCode() : 0);
		result = 31 * result + (additionalProperties != null ? additionalProperties.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "PortMapping{" +
			"containerPort=" + containerPort +
			", hostPort=" + hostPort +
			", servicePort=" + servicePort +
			", protocol='" + protocol + '\'' +
			", additionalProperties=" + additionalProperties +
			'}';
	}
}
