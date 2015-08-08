
package org.opencloudengine.garuda.mesos.bak.marathon.model.apps.getapps.res;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "minimumHealthCapacity"
})
public class UpgradeStrategy {

    @JsonProperty("minimumHealthCapacity")
    private Float minimumHealthCapacity;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("minimumHealthCapacity")
    public Float getMinimumHealthCapacity() {
        return minimumHealthCapacity;
    }

    @JsonProperty("minimumHealthCapacity")
    public void setMinimumHealthCapacity(Float minimumHealthCapacity) {
        this.minimumHealthCapacity = minimumHealthCapacity;
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

        UpgradeStrategy that = (UpgradeStrategy) o;

        if (minimumHealthCapacity != null ? !minimumHealthCapacity.equals(that.minimumHealthCapacity) : that.minimumHealthCapacity != null)
            return false;
        if (additionalProperties != null ? !additionalProperties.equals(that.additionalProperties) : that.additionalProperties != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = minimumHealthCapacity != null ? minimumHealthCapacity.hashCode() : 0;
        result = 31 * result + (additionalProperties != null ? additionalProperties.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UpgradeStrategy{" +
            "minimumHealthCapacity=" + minimumHealthCapacity +
            ", additionalProperties=" + additionalProperties +
            '}';
    }
}
