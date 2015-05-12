
package org.opencloudengine.garuda.controller.mesos.master.model.state;

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
    "cpus",
    "disk",
    "mem",
    "ports"
})
public class UsedResources {

    @JsonProperty("cpus")
    private Float cpus;
    @JsonProperty("disk")
    private Integer disk;
    @JsonProperty("mem")
    private Integer mem;
    @JsonProperty("ports")
    private String ports;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("cpus")
    public Float getCpus() {
        return cpus;
    }

    @JsonProperty("cpus")
    public void setCpus(Float cpus) {
        this.cpus = cpus;
    }

    @JsonProperty("disk")
    public Integer getDisk() {
        return disk;
    }

    @JsonProperty("disk")
    public void setDisk(Integer disk) {
        this.disk = disk;
    }

    @JsonProperty("mem")
    public Integer getMem() {
        return mem;
    }

    @JsonProperty("mem")
    public void setMem(Integer mem) {
        this.mem = mem;
    }

    @JsonProperty("ports")
    public String getPorts() {
        return ports;
    }

    @JsonProperty("ports")
    public void setPorts(String ports) {
        this.ports = ports;
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
        return new HashCodeBuilder().append(cpus).append(disk).append(mem).append(ports).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof UsedResources) == false) {
            return false;
        }
        UsedResources rhs = ((UsedResources) other);
        return new EqualsBuilder().append(cpus, rhs.cpus).append(disk, rhs.disk).append(mem, rhs.mem).append(ports, rhs.ports).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
