
package org.opencloudengine.garuda.mesos.bak.marathon.model.apps.createapp.req;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "containerPath",
    "hostPath",
    "mode"
})
public class Volume {

    @JsonProperty("containerPath")
    private String containerPath;
    @JsonProperty("hostPath")
    private String hostPath;
    @JsonProperty("mode")
    private String mode;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The containerPath
     */
    @JsonProperty("containerPath")
    public String getContainerPath() {
        return containerPath;
    }

    /**
     * 
     * @param containerPath
     *     The containerPath
     */
    @JsonProperty("containerPath")
    public void setContainerPath(String containerPath) {
        this.containerPath = containerPath;
    }

    /**
     * 
     * @return
     *     The hostPath
     */
    @JsonProperty("hostPath")
    public String getHostPath() {
        return hostPath;
    }

    /**
     * 
     * @param hostPath
     *     The hostPath
     */
    @JsonProperty("hostPath")
    public void setHostPath(String hostPath) {
        this.hostPath = hostPath;
    }

    /**
     * 
     * @return
     *     The mode
     */
    @JsonProperty("mode")
    public String getMode() {
        return mode;
    }

    /**
     * 
     * @param mode
     *     The mode
     */
    @JsonProperty("mode")
    public void setMode(String mode) {
        this.mode = mode;
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
