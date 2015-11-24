
package org.opencloudengine.garuda.beluga.mesos.marathon.model;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
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

}
