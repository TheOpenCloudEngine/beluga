
package org.opencloudengine.garuda.mesos.bak.marathon.model.apps.getapps.res;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "appId",
    "host",
    "message",
    "state",
    "taskId",
    "timestamp",
    "version"
})
public class LastTaskFailure {

    @JsonProperty("appId")
    private String appId;
    @JsonProperty("host")
    private String host;
    @JsonProperty("message")
    private String message;
    @JsonProperty("state")
    private String state;
    @JsonProperty("taskId")
    private String taskId;
    @JsonProperty("timestamp")
    private String timestamp;
    @JsonProperty("version")
    private String version;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("appId")
    public String getAppId() {
        return appId;
    }

    @JsonProperty("appId")
    public void setAppId(String appId) {
        this.appId = appId;
    }

    @JsonProperty("host")
    public String getHost() {
        return host;
    }

    @JsonProperty("host")
    public void setHost(String host) {
        this.host = host;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("state")
    public String getState() {
        return state;
    }

    @JsonProperty("state")
    public void setState(String state) {
        this.state = state;
    }

    @JsonProperty("taskId")
    public String getTaskId() {
        return taskId;
    }

    @JsonProperty("taskId")
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @JsonProperty("timestamp")
    public String getTimestamp() {
        return timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
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

        LastTaskFailure that = (LastTaskFailure) o;

        if (appId != null ? !appId.equals(that.appId) : that.appId != null) return false;
        if (host != null ? !host.equals(that.host) : that.host != null) return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        if (state != null ? !state.equals(that.state) : that.state != null) return false;
        if (taskId != null ? !taskId.equals(that.taskId) : that.taskId != null) return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;
        if (additionalProperties != null ? !additionalProperties.equals(that.additionalProperties) : that.additionalProperties != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = appId != null ? appId.hashCode() : 0;
        result = 31 * result + (host != null ? host.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (taskId != null ? taskId.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (additionalProperties != null ? additionalProperties.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LastTaskFailure{" +
            "appId='" + appId + '\'' +
            ", host='" + host + '\'' +
            ", message='" + message + '\'' +
            ", state='" + state + '\'' +
            ", taskId='" + taskId + '\'' +
            ", timestamp='" + timestamp + '\'' +
            ", version='" + version + '\'' +
            ", additionalProperties=" + additionalProperties +
            '}';
    }
}
