
package org.opencloudengine.garuda.mesos.marathon.model;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"apps",
	"dependencies",
	"groups",
	"id",
	"version"
})
public class Group {

	@JsonProperty("apps")
	private List<App> apps;
	@JsonProperty("dependencies")
	private List<Object> dependencies;
	@JsonProperty("groups")
	private List<Object> groups;
	@JsonProperty("id")
	private String id;
	@JsonProperty("version")
	private String version;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("apps")
	public List<App> getApps() {
		return apps;
	}

	@JsonProperty("apps")
	public void setApps(List<App> apps) {
		this.apps = apps;
	}

	@JsonProperty("dependencies")
	public List<Object> getDependencies() {
		return dependencies;
	}

	@JsonProperty("dependencies")
	public void setDependencies(List<Object> dependencies) {
		this.dependencies = dependencies;
	}

	@JsonProperty("groups")
	public List<Object> getGroups() {
		return groups;
	}

	@JsonProperty("groups")
	public void setGroups(List<Object> groups) {
		this.groups = groups;
	}

	@JsonProperty("id")
	public String getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(String id) {
		this.id = id;
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

		Group group = (Group) o;

		if (apps != null ? !apps.equals(group.apps) : group.apps != null) return false;
		if (dependencies != null ? !dependencies.equals(group.dependencies) : group.dependencies != null) return false;
		if (groups != null ? !groups.equals(group.groups) : group.groups != null) return false;
		if (id != null ? !id.equals(group.id) : group.id != null) return false;
		if (version != null ? !version.equals(group.version) : group.version != null) return false;
		if (additionalProperties != null ? !additionalProperties.equals(group.additionalProperties) : group.additionalProperties != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = apps != null ? apps.hashCode() : 0;
		result = 31 * result + (dependencies != null ? dependencies.hashCode() : 0);
		result = 31 * result + (groups != null ? groups.hashCode() : 0);
		result = 31 * result + (id != null ? id.hashCode() : 0);
		result = 31 * result + (version != null ? version.hashCode() : 0);
		result = 31 * result + (additionalProperties != null ? additionalProperties.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Group{" +
			"apps=" + apps +
			", dependencies=" + dependencies +
			", groups=" + groups +
			", id='" + id + '\'' +
			", version='" + version + '\'' +
			", additionalProperties=" + additionalProperties +
			'}';
	}
}
