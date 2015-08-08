
package org.opencloudengine.garuda.mesos.marathon.model;

import com.fasterxml.jackson.annotation.*;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"id",
	"cmd",
	"args",
	"container",
	"cpus",
	"mem",
	"deployments",
	"env",
	"executor",
	"constraints",
	"healthChecks",
	"instances",
	"ports",
	"backoffSeconds",
	"backoffFactor",
	"maxLaunchDelaySeconds",
	"tasksRunning",
	"tasksStaged",
	"uris",
	"dependencies",
	"upgradeStrategy",
	"version"
})
public class App {
	private String id;
	private String cmd;
	private List<String> args;
	private Container container;
	private Float cpus;
	private Float mem;
	private List<Deployment> deployments;
	private Env env;
	private String executor;
	private List<List<String>> constraints;
	private List<HealthCheck> healthChecks;
	private Integer instances;
	private List<Integer> ports;
	private Integer backoffSeconds;
	private Float backoffFactor;
	private Integer maxLaunchDelaySeconds;
	private Integer tasksRunning;
	private Integer tasksStaged;
	private List<String> uris;
	private List<String> dependencies;
	private UpgradeStrategy upgradeStrategy;
	private String version;
	@JsonIgnore
	private Map<String, Object> additionalProperties;

	public App() {}

	public App(String id) {
		this.id = id;
	}
	/**
	 * @return The id
	 */
	@JsonProperty("id")
	public String getId() {
		return id;
	}

	/**
	 * @param id The id
	 */
	@JsonProperty("id")
	public void setId(String id) {
		this.id = id;
	}

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
	 * @return The args
	 */
	@JsonProperty("args")
	public List<String> getArgs() {
		return args;
	}

	/**
	 * @param args The args
	 */
	@JsonProperty("args")
	public void setArgs(List<String> args) {
		this.args = args;
	}

	/**
	 * @return The container
	 */
	@JsonProperty("container")
	public Container getContainer() {
		return container;
	}

	/**
	 * @param container The container
	 */
	@JsonProperty("container")
	public void setContainer(Container container) {
		this.container = container;
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
	 * @return The deployments
	 */
	@JsonProperty("deployments")
	public List<Deployment> getDeployments() {
		return deployments;
	}

	/**
	 * @param deployments The deployments
	 */
	@JsonProperty("deployments")
	public void setDeployments(List<Deployment> deployments) {
		this.deployments = deployments;
	}

	/**
	 * @return The env
	 */
	@JsonProperty("env")
	public Env getEnv() {
		return env;
	}

	/**
	 * @param env The env
	 */
	@JsonProperty("env")
	public void setEnv(Env env) {
		this.env = env;
	}

	/**
	 * @return The executor
	 */
	@JsonProperty("executor")
	public String getExecutor() {
		return executor;
	}

	/**
	 * @param executor The executor
	 */
	@JsonProperty("executor")
	public void setExecutor(String executor) {
		this.executor = executor;
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
	 * @return The healthChecks
	 */
	@JsonProperty("healthChecks")
	public List<HealthCheck> getHealthChecks() {
		return healthChecks;
	}

	/**
	 * @param healthChecks The healthChecks
	 */
	@JsonProperty("healthChecks")
	public void setHealthChecks(List<HealthCheck> healthChecks) {
		this.healthChecks = healthChecks;
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

	/**
	 * @return The backoffSeconds
	 */
	@JsonProperty("backoffSeconds")
	public Integer getBackoffSeconds() {
		return backoffSeconds;
	}

	/**
	 * @param backoffSeconds The backoffSeconds
	 */
	@JsonProperty("backoffSeconds")
	public void setBackoffSeconds(Integer backoffSeconds) {
		this.backoffSeconds = backoffSeconds;
	}

	/**
	 * @return The backoffFactor
	 */
	@JsonProperty("backoffFactor")
	public Float getBackoffFactor() {
		return backoffFactor;
	}

	/**
	 * @param backoffFactor The backoffFactor
	 */
	@JsonProperty("backoffFactor")
	public void setBackoffFactor(Float backoffFactor) {
		this.backoffFactor = backoffFactor;
	}

	/**
	 * @return The maxLaunchDelaySeconds
	 */
	@JsonProperty("maxLaunchDelaySeconds")
	public Integer getMaxLaunchDelaySeconds() {
		return maxLaunchDelaySeconds;
	}

	/**
	 * @param maxLaunchDelaySeconds The maxLaunchDelaySeconds
	 */
	@JsonProperty("maxLaunchDelaySeconds")
	public void setMaxLaunchDelaySeconds(Integer maxLaunchDelaySeconds) {
		this.maxLaunchDelaySeconds = maxLaunchDelaySeconds;
	}

	/**
	 * @return The tasksRunning
	 */
	@JsonProperty("tasksRunning")
	public Integer getTasksRunning() {
		return tasksRunning;
	}

	/**
	 * @param tasksRunning The tasksRunning
	 */
	@JsonProperty("tasksRunning")
	public void setTasksRunning(Integer tasksRunning) {
		this.tasksRunning = tasksRunning;
	}

	/**
	 * @return The tasksStaged
	 */
	@JsonProperty("tasksStaged")
	public Integer getTasksStaged() {
		return tasksStaged;
	}

	/**
	 * @param tasksStaged The tasksStaged
	 */
	@JsonProperty("tasksStaged")
	public void setTasksStaged(Integer tasksStaged) {
		this.tasksStaged = tasksStaged;
	}

	/**
	 * @return The uris
	 */
	@JsonProperty("uris")
	public List<String> getUris() {
		return uris;
	}

	/**
	 * @param uris The uris
	 */
	@JsonProperty("uris")
	public void setUris(List<String> uris) {
		this.uris = uris;
	}

	/**
	 * @return The dependencies
	 */
	@JsonProperty("dependencies")
	public List<String> getDependencies() {
		return dependencies;
	}

	/**
	 * @param dependencies The dependencies
	 */
	@JsonProperty("dependencies")
	public void setDependencies(List<String> dependencies) {
		this.dependencies = dependencies;
	}

	/**
	 * @return The upgradeStrategy
	 */
	@JsonProperty("upgradeStrategy")
	public UpgradeStrategy getUpgradeStrategy() {
		return upgradeStrategy;
	}

	/**
	 * @param upgradeStrategy The upgradeStrategy
	 */
	@JsonProperty("upgradeStrategy")
	public void setUpgradeStrategy(UpgradeStrategy upgradeStrategy) {
		this.upgradeStrategy = upgradeStrategy;
	}

	/**
	 * @return The version
	 */
	@JsonProperty("version")
	public String getVersion() {
		return version;
	}

	/**
	 * @param version The version
	 */
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

}
