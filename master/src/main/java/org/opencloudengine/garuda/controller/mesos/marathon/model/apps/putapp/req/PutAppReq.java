package org.opencloudengine.garuda.controller.mesos.marathon.model.apps.putapp.req;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.List;
import java.util.Map;

/**
 * Created by soul on 15. 4. 14.
 *
 * @author Seong Jong, Jeon
 * @since 1.0
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
	"cmd",
	"constraints",
	"cpus",
	"instances",
	"mem",
	"ports"
})
public class PutAppReq {

	@JsonProperty("cmd")
	private String cmd;
	@JsonProperty("constraints")
	private List<List<String>> constraints;
	@JsonProperty("cpus")
	private Float cpus;
	@JsonProperty("instances")
	private Integer instances;
	@JsonProperty("mem")
	private Float mem;
	@JsonProperty("ports")
	private List<Integer> ports;
	@JsonIgnore
	private Map<String, Object> additionalProperties;

	/**
	 * @return The cmd
	 */
	@JsonProperty("cmd")
	public String getCmd() {
		return cmd;
	}

	/**
	 * @param cmd The cmd
	 */
	@JsonProperty("cmd")
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	/**
	 * @return The constraints
	 */
	@JsonProperty("constraints")
	public List<List<String>> getConstraints() {
		return constraints;
	}

	/**
	 * @param constraints The constraints
	 */
	@JsonProperty("constraints")
	public void setConstraints(List<List<String>> constraints) {
		this.constraints = constraints;
	}

	/**
	 * @return The cpus
	 */
	@JsonProperty("cpus")
	public Float getCpus() {
		return cpus;
	}

	/**
	 * @param cpus The cpus
	 */
	@JsonProperty("cpus")
	public void setCpus(Float cpus) {
		this.cpus = cpus;
	}

	/**
	 * @return The instances
	 */
	@JsonProperty("instances")
	public Integer getInstances() {
		return instances;
	}

	/**
	 * @param instances The instances
	 */
	@JsonProperty("instances")
	public void setInstances(Integer instances) {
		this.instances = instances;
	}

	/**
	 * @return The mem
	 */
	@JsonProperty("mem")
	public Float getMem() {
		return mem;
	}

	/**
	 * @param mem The mem
	 */
	@JsonProperty("mem")
	public void setMem(Float mem) {
		this.mem = mem;
	}

	/**
	 * @return The ports
	 */
	@JsonProperty("ports")
	public List<Integer> getPorts() {
		return ports;
	}

	/**
	 * @param ports The ports
	 */
	@JsonProperty("ports")
	public void setPorts(List<Integer> ports) {
		this.ports = ports;
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
