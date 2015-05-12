
package org.opencloudengine.garuda.controller.mesos.marathon.model.apps.createapp.req;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "protocol",
    "path",
    "gracePeriodSeconds",
    "intervalSeconds",
    "portIndex",
    "timeoutSeconds",
    "maxConsecutiveFailures",
    "command"
})
public class HealthCheck {

    @JsonProperty("protocol")
    private String protocol;
    @JsonProperty("path")
    private String path;
    @JsonProperty("gracePeriodSeconds")
    private Integer gracePeriodSeconds;
    @JsonProperty("intervalSeconds")
    private Integer intervalSeconds;
    @JsonProperty("portIndex")
    private Integer portIndex;
    @JsonProperty("timeoutSeconds")
    private Integer timeoutSeconds;
    @JsonProperty("maxConsecutiveFailures")
    private Integer maxConsecutiveFailures;
    @JsonProperty("command")
    private Command command;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The protocol
     */
    @JsonProperty("protocol")
    public String getProtocol() {
        return protocol;
    }

    /**
     * 
     * @param protocol
     *     The protocol
     */
    @JsonProperty("protocol")
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * 
     * @return
     *     The path
     */
    @JsonProperty("path")
    public String getPath() {
        return path;
    }

    /**
     * 
     * @param path
     *     The path
     */
    @JsonProperty("path")
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 
     * @return
     *     The gracePeriodSeconds
     */
    @JsonProperty("gracePeriodSeconds")
    public Integer getGracePeriodSeconds() {
        return gracePeriodSeconds;
    }

    /**
     * 
     * @param gracePeriodSeconds
     *     The gracePeriodSeconds
     */
    @JsonProperty("gracePeriodSeconds")
    public void setGracePeriodSeconds(Integer gracePeriodSeconds) {
        this.gracePeriodSeconds = gracePeriodSeconds;
    }

    /**
     * 
     * @return
     *     The intervalSeconds
     */
    @JsonProperty("intervalSeconds")
    public Integer getIntervalSeconds() {
        return intervalSeconds;
    }

    /**
     * 
     * @param intervalSeconds
     *     The intervalSeconds
     */
    @JsonProperty("intervalSeconds")
    public void setIntervalSeconds(Integer intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
    }

    /**
     * 
     * @return
     *     The portIndex
     */
    @JsonProperty("portIndex")
    public Integer getPortIndex() {
        return portIndex;
    }

    /**
     * 
     * @param portIndex
     *     The portIndex
     */
    @JsonProperty("portIndex")
    public void setPortIndex(Integer portIndex) {
        this.portIndex = portIndex;
    }

    /**
     * 
     * @return
     *     The timeoutSeconds
     */
    @JsonProperty("timeoutSeconds")
    public Integer getTimeoutSeconds() {
        return timeoutSeconds;
    }

    /**
     * 
     * @param timeoutSeconds
     *     The timeoutSeconds
     */
    @JsonProperty("timeoutSeconds")
    public void setTimeoutSeconds(Integer timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    /**
     * 
     * @return
     *     The maxConsecutiveFailures
     */
    @JsonProperty("maxConsecutiveFailures")
    public Integer getMaxConsecutiveFailures() {
        return maxConsecutiveFailures;
    }

    /**
     * 
     * @param maxConsecutiveFailures
     *     The maxConsecutiveFailures
     */
    @JsonProperty("maxConsecutiveFailures")
    public void setMaxConsecutiveFailures(Integer maxConsecutiveFailures) {
        this.maxConsecutiveFailures = maxConsecutiveFailures;
    }

    /**
     * 
     * @return
     *     The command
     */
    @JsonProperty("command")
    public Command getCommand() {
        return command;
    }

    /**
     * 
     * @param command
     *     The command
     */
    @JsonProperty("command")
    public void setCommand(Command command) {
        this.command = command;
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
