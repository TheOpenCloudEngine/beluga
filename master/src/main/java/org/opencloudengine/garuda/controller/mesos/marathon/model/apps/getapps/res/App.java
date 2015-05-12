
package org.opencloudengine.garuda.controller.mesos.marathon.model.apps.getapps.res;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
	"args",
	"backoffFactor",
	"backoffSeconds",
	"maxLaunchDelaySeconds",
	"cmd",
	"constraints",
	"container",
	"cpus",
	"dependencies",
	"deployments",
	"disk",
	"env",
	"executor",
	"healthChecks",
	"id",
	"instances",
	"lastTaskFailure",
	"mem",
	"ports",
	"requirePorts",
	"storeUrls",
	"tasks",
	"tasksRunning",
	"tasksStaged",
	"upgradeStrategy",
	"uris",
	"user",
	"version"
})
public class App {

	@JsonProperty("args")
	private Object args;
	@JsonProperty("backoffFactor")
	private Float backoffFactor;
	@JsonProperty("backoffSeconds")
	private Integer backoffSeconds;
	@JsonProperty("maxLaunchDelaySeconds")
	private Integer maxLaunchDelaySeconds;
	@JsonProperty("cmd")
	private String cmd;
	@JsonProperty("constraints")
	private List<Object> constraints;
	@JsonProperty("container")
	private Container container;
	@JsonProperty("cpus")
	private Float cpus;
	@JsonProperty("dependencies")
	private List<Object> dependencies;
	@JsonProperty("deployments")
	private List<Deployment> deployments;
	@JsonProperty("disk")
	private Float disk;
	@JsonProperty("env")
	private Env env;
	@JsonProperty("executor")
	private String executor;
	@JsonProperty("healthChecks")
	private List<HealthCheck> healthChecks;
	@JsonProperty("id")
	private String id;
	@JsonProperty("instances")
	private Integer instances;
	@JsonProperty("lastTaskFailure")
	private LastTaskFailure lastTaskFailure;
	@JsonProperty("mem")
	private Float mem;
	@JsonProperty("ports")
	private List<Integer> ports;
	@JsonProperty("requirePorts")
	private Boolean requirePorts;
	@JsonProperty("storeUrls")
	private List<Object> storeUrls;
	@JsonProperty("tasks")
	private List<Task> tasks;
	@JsonProperty("tasksRunning")
	private Integer tasksRunning;
	@JsonProperty("tasksStaged")
	private Integer tasksStaged;
	@JsonProperty("upgradeStrategy")
	private UpgradeStrategy upgradeStrategy;
	@JsonProperty("uris")
	private List<Object> uris;
	@JsonProperty("user")
	private Object user;
	@JsonProperty("version")
	private String version;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("args")
	public Object getArgs() {
		return args;
	}

	@JsonProperty("args")
	public void setArgs(Object args) {
		this.args = args;
	}

	@JsonProperty("backoffFactor")
	public Float getBackoffFactor() {
		return backoffFactor;
	}

	@JsonProperty("backoffFactor")
	public void setBackoffFactor(Float backoffFactor) {
		this.backoffFactor = backoffFactor;
	}

	@JsonProperty("backoffSeconds")
	public Integer getBackoffSeconds() {
		return backoffSeconds;
	}

	@JsonProperty("backoffSeconds")
	public void setBackoffSeconds(Integer backoffSeconds) {
		this.backoffSeconds = backoffSeconds;
	}

	@JsonProperty("maxLaunchDelaySeconds")
	public Integer getMaxLaunchDelaySeconds() {
		return maxLaunchDelaySeconds;
	}

	@JsonProperty("maxLaunchDelaySeconds")
	public void setMaxLaunchDelaySeconds(Integer maxLaunchDelaySeconds) {
		this.maxLaunchDelaySeconds = maxLaunchDelaySeconds;
	}

	@JsonProperty("cmd")
	public String getCmd() {
		return cmd;
	}

	@JsonProperty("cmd")
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	@JsonProperty("constraints")
	public List<Object> getConstraints() {
		return constraints;
	}

	@JsonProperty("constraints")
	public void setConstraints(List<Object> constraints) {
		this.constraints = constraints;
	}

	@JsonProperty("container")
	public Container getContainer() {
		return container;
	}

	@JsonProperty("container")
	public void setContainer(Container container) {
		this.container = container;
	}

	@JsonProperty("cpus")
	public Float getCpus() {
		return cpus;
	}

	@JsonProperty("cpus")
	public void setCpus(Float cpus) {
		this.cpus = cpus;
	}

	@JsonProperty("dependencies")
	public List<Object> getDependencies() {
		return dependencies;
	}

	@JsonProperty("dependencies")
	public void setDependencies(List<Object> dependencies) {
		this.dependencies = dependencies;
	}

	@JsonProperty("deployments")
	public List<Deployment> getDeployments() {
		return deployments;
	}

	@JsonProperty("deployments")
	public void setDeployments(List<Deployment> deployments) {
		this.deployments = deployments;
	}

	@JsonProperty("disk")
	public Float getDisk() {
		return disk;
	}

	@JsonProperty("disk")
	public void setDisk(Float disk) {
		this.disk = disk;
	}

	@JsonProperty("env")
	public Env getEnv() {
		return env;
	}

	@JsonProperty("env")
	public void setEnv(Env env) {
		this.env = env;
	}

	@JsonProperty("executor")
	public String getExecutor() {
		return executor;
	}

	@JsonProperty("executor")
	public void setExecutor(String executor) {
		this.executor = executor;
	}

	@JsonProperty("healthChecks")
	public List<HealthCheck> getHealthChecks() {
		return healthChecks;
	}

	@JsonProperty("healthChecks")
	public void setHealthChecks(List<HealthCheck> healthChecks) {
		this.healthChecks = healthChecks;
	}

	@JsonProperty("id")
	public String getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(String id) {
		this.id = id;
	}

	@JsonProperty("instances")
	public Integer getInstances() {
		return instances;
	}

	@JsonProperty("instances")
	public void setInstances(Integer instances) {
		this.instances = instances;
	}

	@JsonProperty("lastTaskFailure")
	public LastTaskFailure getLastTaskFailure() {
		return lastTaskFailure;
	}

	@JsonProperty("lastTaskFailure")
	public void setLastTaskFailure(LastTaskFailure lastTaskFailure) {
		this.lastTaskFailure = lastTaskFailure;
	}

	@JsonProperty("mem")
	public Float getMem() {
		return mem;
	}

	@JsonProperty("mem")
	public void setMem(Float mem) {
		this.mem = mem;
	}

	@JsonProperty("ports")
	public List<Integer> getPorts() {
		return ports;
	}

	@JsonProperty("ports")
	public void setPorts(List<Integer> ports) {
		this.ports = ports;
	}

	@JsonProperty("requirePorts")
	public Boolean getRequirePorts() {
		return requirePorts;
	}

	@JsonProperty("requirePorts")
	public void setRequirePorts(Boolean requirePorts) {
		this.requirePorts = requirePorts;
	}

	@JsonProperty("storeUrls")
	public List<Object> getStoreUrls() {
		return storeUrls;
	}

	@JsonProperty("storeUrls")
	public void setStoreUrls(List<Object> storeUrls) {
		this.storeUrls = storeUrls;
	}

	@JsonProperty("tasks")
	public List<Task> getTasks() {
		return tasks;
	}

	@JsonProperty("tasks")
	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	@JsonProperty("tasksRunning")
	public Integer getTasksRunning() {
		return tasksRunning;
	}

	@JsonProperty("tasksRunning")
	public void setTasksRunning(Integer tasksRunning) {
		this.tasksRunning = tasksRunning;
	}

	@JsonProperty("tasksStaged")
	public Integer getTasksStaged() {
		return tasksStaged;
	}

	@JsonProperty("tasksStaged")
	public void setTasksStaged(Integer tasksStaged) {
		this.tasksStaged = tasksStaged;
	}

	@JsonProperty("upgradeStrategy")
	public UpgradeStrategy getUpgradeStrategy() {
		return upgradeStrategy;
	}

	@JsonProperty("upgradeStrategy")
	public void setUpgradeStrategy(UpgradeStrategy upgradeStrategy) {
		this.upgradeStrategy = upgradeStrategy;
	}

	@JsonProperty("uris")
	public List<Object> getUris() {
		return uris;
	}

	@JsonProperty("uris")
	public void setUris(List<Object> uris) {
		this.uris = uris;
	}

	@JsonProperty("user")
	public Object getUser() {
		return user;
	}

	@JsonProperty("user")
	public void setUser(Object user) {
		this.user = user;
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

		App app = (App) o;

		if (args != null ? !args.equals(app.args) : app.args != null) return false;
		if (backoffFactor != null ? !backoffFactor.equals(app.backoffFactor) : app.backoffFactor != null) return false;
		if (backoffSeconds != null ? !backoffSeconds.equals(app.backoffSeconds) : app.backoffSeconds != null)
			return false;
		if (maxLaunchDelaySeconds != null ? !maxLaunchDelaySeconds.equals(app.maxLaunchDelaySeconds) : app.maxLaunchDelaySeconds != null)
			return false;
		if (cmd != null ? !cmd.equals(app.cmd) : app.cmd != null) return false;
		if (constraints != null ? !constraints.equals(app.constraints) : app.constraints != null) return false;
		if (container != null ? !container.equals(app.container) : app.container != null) return false;
		if (cpus != null ? !cpus.equals(app.cpus) : app.cpus != null) return false;
		if (dependencies != null ? !dependencies.equals(app.dependencies) : app.dependencies != null) return false;
		if (deployments != null ? !deployments.equals(app.deployments) : app.deployments != null) return false;
		if (disk != null ? !disk.equals(app.disk) : app.disk != null) return false;
		if (env != null ? !env.equals(app.env) : app.env != null) return false;
		if (executor != null ? !executor.equals(app.executor) : app.executor != null) return false;
		if (healthChecks != null ? !healthChecks.equals(app.healthChecks) : app.healthChecks != null) return false;
		if (id != null ? !id.equals(app.id) : app.id != null) return false;
		if (instances != null ? !instances.equals(app.instances) : app.instances != null) return false;
		if (lastTaskFailure != null ? !lastTaskFailure.equals(app.lastTaskFailure) : app.lastTaskFailure != null)
			return false;
		if (mem != null ? !mem.equals(app.mem) : app.mem != null) return false;
		if (ports != null ? !ports.equals(app.ports) : app.ports != null) return false;
		if (requirePorts != null ? !requirePorts.equals(app.requirePorts) : app.requirePorts != null) return false;
		if (storeUrls != null ? !storeUrls.equals(app.storeUrls) : app.storeUrls != null) return false;
		if (tasks != null ? !tasks.equals(app.tasks) : app.tasks != null) return false;
		if (tasksRunning != null ? !tasksRunning.equals(app.tasksRunning) : app.tasksRunning != null) return false;
		if (tasksStaged != null ? !tasksStaged.equals(app.tasksStaged) : app.tasksStaged != null) return false;
		if (upgradeStrategy != null ? !upgradeStrategy.equals(app.upgradeStrategy) : app.upgradeStrategy != null)
			return false;
		if (uris != null ? !uris.equals(app.uris) : app.uris != null) return false;
		if (user != null ? !user.equals(app.user) : app.user != null) return false;
		if (version != null ? !version.equals(app.version) : app.version != null) return false;
		if (additionalProperties != null ? !additionalProperties.equals(app.additionalProperties) : app.additionalProperties != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = args != null ? args.hashCode() : 0;
		result = 31 * result + (backoffFactor != null ? backoffFactor.hashCode() : 0);
		result = 31 * result + (backoffSeconds != null ? backoffSeconds.hashCode() : 0);
		result = 31 * result + (maxLaunchDelaySeconds != null ? maxLaunchDelaySeconds.hashCode() : 0);
		result = 31 * result + (cmd != null ? cmd.hashCode() : 0);
		result = 31 * result + (constraints != null ? constraints.hashCode() : 0);
		result = 31 * result + (container != null ? container.hashCode() : 0);
		result = 31 * result + (cpus != null ? cpus.hashCode() : 0);
		result = 31 * result + (dependencies != null ? dependencies.hashCode() : 0);
		result = 31 * result + (deployments != null ? deployments.hashCode() : 0);
		result = 31 * result + (disk != null ? disk.hashCode() : 0);
		result = 31 * result + (env != null ? env.hashCode() : 0);
		result = 31 * result + (executor != null ? executor.hashCode() : 0);
		result = 31 * result + (healthChecks != null ? healthChecks.hashCode() : 0);
		result = 31 * result + (id != null ? id.hashCode() : 0);
		result = 31 * result + (instances != null ? instances.hashCode() : 0);
		result = 31 * result + (lastTaskFailure != null ? lastTaskFailure.hashCode() : 0);
		result = 31 * result + (mem != null ? mem.hashCode() : 0);
		result = 31 * result + (ports != null ? ports.hashCode() : 0);
		result = 31 * result + (requirePorts != null ? requirePorts.hashCode() : 0);
		result = 31 * result + (storeUrls != null ? storeUrls.hashCode() : 0);
		result = 31 * result + (tasks != null ? tasks.hashCode() : 0);
		result = 31 * result + (tasksRunning != null ? tasksRunning.hashCode() : 0);
		result = 31 * result + (tasksStaged != null ? tasksStaged.hashCode() : 0);
		result = 31 * result + (upgradeStrategy != null ? upgradeStrategy.hashCode() : 0);
		result = 31 * result + (uris != null ? uris.hashCode() : 0);
		result = 31 * result + (user != null ? user.hashCode() : 0);
		result = 31 * result + (version != null ? version.hashCode() : 0);
		result = 31 * result + (additionalProperties != null ? additionalProperties.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "App{" +
			"args=" + args +
			", backoffFactor=" + backoffFactor +
			", backoffSeconds=" + backoffSeconds +
			", maxLaunchDelaySeconds=" + maxLaunchDelaySeconds +
			", cmd='" + cmd + '\'' +
			", constraints=" + constraints +
			", container=" + container +
			", cpus=" + cpus +
			", dependencies=" + dependencies +
			", deployments=" + deployments +
			", disk=" + disk +
			", env=" + env +
			", executor='" + executor + '\'' +
			", healthChecks=" + healthChecks +
			", id='" + id + '\'' +
			", instances=" + instances +
			", lastTaskFailure=" + lastTaskFailure +
			", mem=" + mem +
			", ports=" + ports +
			", requirePorts=" + requirePorts +
			", storeUrls=" + storeUrls +
			", tasks=" + tasks +
			", tasksRunning=" + tasksRunning +
			", tasksStaged=" + tasksStaged +
			", upgradeStrategy=" + upgradeStrategy +
			", uris=" + uris +
			", user=" + user +
			", version='" + version + '\'' +
			", additionalProperties=" + additionalProperties +
			'}';
	}
}
