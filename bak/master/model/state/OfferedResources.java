
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
    "cpus",
    "disk",
    "mem"
})
public class OfferedResources {

    @JsonProperty("cpus")
    private Integer cpus;
    @JsonProperty("disk")
    private Integer disk;
    @JsonProperty("mem")
    private Integer mem;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("cpus")
    public Integer getCpus() {
        return cpus;
    }

    @JsonProperty("cpus")
    public void setCpus(Integer cpus) {
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
        return new HashCodeBuilder().append(cpus).append(disk).append(mem).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof OfferedResources) == false) {
            return false;
        }
        OfferedResources rhs = ((OfferedResources) other);
        return new EqualsBuilder().append(cpus, rhs.cpus).append(disk, rhs.disk).append(mem, rhs.mem).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
