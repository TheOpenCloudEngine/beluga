
package org.opencloudengine.garuda.mesos.marathon.model;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "minimumHealthCapacity",
    "maximumOverCapacity"
})
public class UpgradeStrategy {

    @JsonProperty("minimumHealthCapacity")
    private Float minimumHealthCapacity;
    @JsonProperty("maximumOverCapacity")
    private Float maximumOverCapacity;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The minimumHealthCapacity
     */
    @JsonProperty("minimumHealthCapacity")
    public Float getMinimumHealthCapacity() {
        return minimumHealthCapacity;
    }

    /**
     * 
     * @param minimumHealthCapacity
     *     The minimumHealthCapacity
     */
    @JsonProperty("minimumHealthCapacity")
    public void setMinimumHealthCapacity(Float minimumHealthCapacity) {
        this.minimumHealthCapacity = minimumHealthCapacity;
    }

    /**
     * 
     * @return
     *     The maximumOverCapacity
     */
    @JsonProperty("maximumOverCapacity")
    public Float getMaximumOverCapacity() {
        return maximumOverCapacity;
    }

    /**
     * 
     * @param maximumOverCapacity
     *     The maximumOverCapacity
     */
    @JsonProperty("maximumOverCapacity")
    public void setMaximumOverCapacity(Float maximumOverCapacity) {
        this.maximumOverCapacity = maximumOverCapacity;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
