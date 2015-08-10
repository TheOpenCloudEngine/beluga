
package org.opencloudengine.garuda.mesos.bak.master.model.state;

import com.fasterxml.jackson.annotation.*;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "active",
    "checkpoint",
    "completed_tasks",
    "failover_timeout",
    "hostname",
    "id",
    "name",
    "offered_resources",
    "offers",
    "registered_time",
    "reregistered_time",
    "resources",
    "role",
    "tasks",
    "unregistered_time",
    "used_resources",
    "user",
    "webui_url"
})
public class Framework {

    @JsonProperty("active")
    private Boolean active;
    @JsonProperty("checkpoint")
    private Boolean checkpoint;
    @JsonProperty("completed_tasks")
    private List<CompletedTask> completedTasks = new ArrayList<CompletedTask>();
    @JsonProperty("failover_timeout")
    private Integer failoverTimeout;
    @JsonProperty("hostname")
    private String hostname;
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("offered_resources")
    private OfferedResources offeredResources;
    @JsonProperty("offers")
    private List<Object> offers = new ArrayList<Object>();
    @JsonProperty("registered_time")
    private Float registeredTime;
    @JsonProperty("reregistered_time")
    private Float reregisteredTime;
    @JsonProperty("resources")
    private Resources resources;
    @JsonProperty("role")
    private String role;
    @JsonProperty("tasks")
    private List<Task> tasks = new ArrayList<Task>();
    @JsonProperty("unregistered_time")
    private Integer unregisteredTime;
    @JsonProperty("used_resources")
    private UsedResources usedResources;
    @JsonProperty("user")
    private String user;
    @JsonProperty("webui_url")
    private String webuiUrl;
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

    @JsonProperty("checkpoint")
    public Boolean getCheckpoint() {
        return checkpoint;
    }

    @JsonProperty("checkpoint")
    public void setCheckpoint(Boolean checkpoint) {
        this.checkpoint = checkpoint;
    }

    @JsonProperty("completed_tasks")
    public List<CompletedTask> getCompletedTasks() {
        return completedTasks;
    }

    @JsonProperty("completed_tasks")
    public void setCompletedTasks(List<CompletedTask> completedTasks) {
        this.completedTasks = completedTasks;
    }

    @JsonProperty("failover_timeout")
    public Integer getFailoverTimeout() {
        return failoverTimeout;
    }

    @JsonProperty("failover_timeout")
    public void setFailoverTimeout(Integer failoverTimeout) {
        this.failoverTimeout = failoverTimeout;
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

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("offered_resources")
    public OfferedResources getOfferedResources() {
        return offeredResources;
    }

    @JsonProperty("offered_resources")
    public void setOfferedResources(OfferedResources offeredResources) {
        this.offeredResources = offeredResources;
    }

    @JsonProperty("offers")
    public List<Object> getOffers() {
        return offers;
    }

    @JsonProperty("offers")
    public void setOffers(List<Object> offers) {
        this.offers = offers;
    }

    @JsonProperty("registered_time")
    public Float getRegisteredTime() {
        return registeredTime;
    }

    @JsonProperty("registered_time")
    public void setRegisteredTime(Float registeredTime) {
        this.registeredTime = registeredTime;
    }

    @JsonProperty("reregistered_time")
    public Float getReregisteredTime() {
        return reregisteredTime;
    }

    @JsonProperty("reregistered_time")
    public void setReregisteredTime(Float reregisteredTime) {
        this.reregisteredTime = reregisteredTime;
    }

    @JsonProperty("resources")
    public Resources getResources() {
        return resources;
    }

    @JsonProperty("resources")
    public void setResources(Resources resources) {
        this.resources = resources;
    }

    @JsonProperty("role")
    public String getRole() {
        return role;
    }

    @JsonProperty("role")
    public void setRole(String role) {
        this.role = role;
    }

    @JsonProperty("tasks")
    public List<Task> getTasks() {
        return tasks;
    }

    @JsonProperty("tasks")
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    @JsonProperty("unregistered_time")
    public Integer getUnregisteredTime() {
        return unregisteredTime;
    }

    @JsonProperty("unregistered_time")
    public void setUnregisteredTime(Integer unregisteredTime) {
        this.unregisteredTime = unregisteredTime;
    }

    @JsonProperty("used_resources")
    public UsedResources getUsedResources() {
        return usedResources;
    }

    @JsonProperty("used_resources")
    public void setUsedResources(UsedResources usedResources) {
        this.usedResources = usedResources;
    }

    @JsonProperty("user")
    public String getUser() {
        return user;
    }

    @JsonProperty("user")
    public void setUser(String user) {
        this.user = user;
    }

    @JsonProperty("webui_url")
    public String getWebuiUrl() {
        return webuiUrl;
    }

    @JsonProperty("webui_url")
    public void setWebuiUrl(String webuiUrl) {
        this.webuiUrl = webuiUrl;
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
        return new HashCodeBuilder().append(active).append(checkpoint).append(completedTasks).append(failoverTimeout).append(hostname).append(id).append(name).append(offeredResources).append(offers).append(registeredTime).append(reregisteredTime).append(resources).append(role).append(tasks).append(unregisteredTime).append(usedResources).append(user).append(webuiUrl).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Framework) == false) {
            return false;
        }
        Framework rhs = ((Framework) other);
        return new EqualsBuilder().append(active, rhs.active).append(checkpoint, rhs.checkpoint).append(completedTasks, rhs.completedTasks).append(failoverTimeout, rhs.failoverTimeout).append(hostname, rhs.hostname).append(id, rhs.id).append(name, rhs.name).append(offeredResources, rhs.offeredResources).append(offers, rhs.offers).append(registeredTime, rhs.registeredTime).append(reregisteredTime, rhs.reregisteredTime).append(resources, rhs.resources).append(role, rhs.role).append(tasks, rhs.tasks).append(unregisteredTime, rhs.unregisteredTime).append(usedResources, rhs.usedResources).append(user, rhs.user).append(webuiUrl, rhs.webuiUrl).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
