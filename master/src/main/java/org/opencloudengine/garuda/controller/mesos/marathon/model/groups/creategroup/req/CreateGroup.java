
package org.opencloudengine.garuda.controller.mesos.marathon.model.groups.creategroup.req;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.opencloudengine.garuda.controller.mesos.marathon.model.apps.createapp.req.CreateApp;

import javax.annotation.Generated;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
	"id",
	"apps"
})
public class CreateGroup {

	@JsonProperty("id")
	private String id;
	@JsonProperty("apps")
	private List<CreateApp> apps;

	/**
	 * @return The id
	 */
	@JsonProperty("id")
	public String getId() {
		return id;
	}

	/**
	 * @param id The id
	 */
	@JsonProperty("id")
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return The apps
	 */
	@JsonProperty("apps")
	public List<CreateApp> getApps() {
		return apps;
	}

	/**
	 * @param apps The apps
	 */
	@JsonProperty("apps")
	public void setApps(List<CreateApp> apps) {
		this.apps = apps;
	}
}
