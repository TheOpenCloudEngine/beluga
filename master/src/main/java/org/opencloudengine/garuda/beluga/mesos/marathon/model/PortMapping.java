
package org.opencloudengine.garuda.beluga.mesos.marathon.model;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "containerPort",
    "hostPort",
    "servicePort",
    "protocol"
})
public class PortMapping {

    @JsonProperty("containerPort")
    private Integer containerPort;
    @JsonProperty("hostPort")
    private Integer hostPort = 0;
    @JsonProperty("servicePort")
    private Integer servicePort = 0;
    @JsonProperty("protocol")
    private String protocol = "tcp";

    /**
     * 
     * @return
     *     The containerPort
     */
    @JsonProperty("containerPort")
    public Integer getContainerPort() {
        return containerPort;
    }

    /**
     * 
     * @param containerPort
     *     The containerPort
     */
    @JsonProperty("containerPort")
    public void setContainerPort(Integer containerPort) {
        this.containerPort = containerPort;
    }

    /**
     * 
     * @return
     *     The hostPort
     */
    @JsonProperty("hostPort")
    public Integer getHostPort() {
        return hostPort;
    }

    /**
     * 
     * @param hostPort
     *     The hostPort
     */
    @JsonProperty("hostPort")
    public void setHostPort(Integer hostPort) {
        this.hostPort = hostPort;
    }

    /**
     * 
     * @return
     *     The servicePort
     */
    @JsonProperty("servicePort")
    public Integer getServicePort() {
        return servicePort;
    }

    /**
     * 
     * @param servicePort
     *     The servicePort
     */
    @JsonProperty("servicePort")
    public void setServicePort(Integer servicePort) {
        this.servicePort = servicePort;
    }

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


}
