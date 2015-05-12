
package org.opencloudengine.garuda.controller.mesos.master.model.state;

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
    "executor_id",
    "framework_id",
    "id",
    "labels",
    "name",
    "resources",
    "slave_id",
    "state",
    "statuses"
})
public class CompletedTask {

    @JsonProperty("executor_id")
    private String executorId;
    @JsonProperty("framework_id")
    private String frameworkId;
    @JsonProperty("id")
    private String id;
    @JsonProperty("labels")
    private List<Object> labels = new ArrayList<Object>();
    @JsonProperty("name")
    private String name;
    @JsonProperty("resources")
    private Resources resources;
    @JsonProperty("slave_id")
    private String slaveId;
    @JsonProperty("state")
    private String state;
    @JsonProperty("statuses")
    private List<Status> statuses = new ArrayList<Status>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("executor_id")
    public String getExecutorId() {
        return executorId;
    }

    @JsonProperty("executor_id")
    public void setExecutorId(String executorId) {
        this.executorId = executorId;
    }

    @JsonProperty("framework_id")
    public String getFrameworkId() {
        return frameworkId;
    }

    @JsonProperty("framework_id")
    public void setFrameworkId(String frameworkId) {
        this.frameworkId = frameworkId;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("labels")
    public List<Object> getLabels() {
        return labels;
    }

    @JsonProperty("labels")
    public void setLabels(List<Object> labels) {
        this.labels = labels;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("resources")
    public Resources getResources() {
        return resources;
    }

    @JsonProperty("resources")
    public void setResources(Resources resources) {
        this.resources = resources;
    }

    @JsonProperty("slave_id")
    public String getSlaveId() {
        return slaveId;
    }

    @JsonProperty("slave_id")
    public void setSlaveId(String slaveId) {
        this.slaveId = slaveId;
    }

    @JsonProperty("state")
    public String getState() {
        return state;
    }

    @JsonProperty("state")
    public void setState(String state) {
        this.state = state;
    }

    @JsonProperty("statuses")
    public List<Status> getStatuses() {
        return statuses;
    }

    @JsonProperty("statuses")
    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
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
        return new HashCodeBuilder().append(executorId).append(frameworkId).append(id).append(labels).append(name).append(resources).append(slaveId).append(state).append(statuses).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof CompletedTask) == false) {
            return false;
        }
        CompletedTask rhs = ((CompletedTask) other);
        return new EqualsBuilder().append(executorId, rhs.executorId).append(frameworkId, rhs.frameworkId).append(id, rhs.id).append(labels, rhs.labels).append(name, rhs.name).append(resources, rhs.resources).append(slaveId, rhs.slaveId).append(state, rhs.state).append(statuses, rhs.statuses).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
