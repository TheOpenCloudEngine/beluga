
package org.opencloudengine.garuda.mesos.bak.marathon.model.apps.createapp.res;

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
	"disk",
	"env",
	"executor",
	"healthChecks",
	"id",
	"instances",
	"mem",
	"ports",
	"requirePorts",
	"storeUrls",
	"upgradeStrategy",
	"uris",
	"user",
	"version"
})
public class CreateAppRes {

	@JsonProperty("args")
	private Object args;
	@JsonProperty("backoffFactor")
	private Double backoffFactor;
	@JsonProperty("backoffSeconds")
	private Integer backoffSeconds;
	@JsonProperty("maxLaunchDelaySeconds")
	private Integer maxLaunchDelaySeconds;
	@JsonProperty("cmd")
	private String cmd;
	@JsonProperty("constraints")
	private List<List<String>> constraints;
	@JsonProperty("container")
	private Container container;
	@JsonProperty("cpus")
	private Double cpus;
	@JsonProperty("dependencies")
	private List<Object> dependencies;
	@JsonProperty("disk")
	private Double disk;
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
	@JsonProperty("mem")
	private Double mem;
	@JsonProperty("ports")
	private List<Integer> ports;
	@JsonProperty("requirePorts")
	private Boolean requirePorts;
	@JsonProperty("storeUrls")
	private List<Object> storeUrls;
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
	public Double getBackoffFactor() {
		return backoffFactor;
	}

	@JsonProperty("backoffFactor")
	public void setBackoffFactor(Double backoffFactor) {
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
	public List<List<String>> getConstraints() {
		return constraints;
	}

	@JsonProperty("constraints")
	public void setConstraints(List<List<String>> constraints) {
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
	public Double getCpus() {
		return cpus;
	}

	@JsonProperty("cpus")
	public void setCpus(Double cpus) {
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

	@JsonProperty("disk")
	public Double getDisk() {
		return disk;
	}

	@JsonProperty("disk")
	public void setDisk(Double disk) {
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

	@JsonProperty("mem")
	public Double getMem() {
		return mem;
	}

	@JsonProperty("mem")
	public void setMem(Double mem) {
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

		CreateAppRes that = (CreateAppRes) o;

		if (args != null ? !args.equals(that.args) : that.args != null) return false;
		if (backoffFactor != null ? !backoffFactor.equals(that.backoffFactor) : that.backoffFactor != null)
			return false;
		if (backoffSeconds != null ? !backoffSeconds.equals(that.backoffSeconds) : that.backoffSeconds != null)
			return false;
		if (maxLaunchDelaySeconds != null ? !maxLaunchDelaySeconds.equals(that.maxLaunchDelaySeconds) : that.maxLaunchDelaySeconds != null)
			return false;
		if (cmd != null ? !cmd.equals(that.cmd) : that.cmd != null) return false;
		if (constraints != null ? !constraints.equals(that.constraints) : that.constraints != null) return false;
		if (container != null ? !container.equals(that.container) : that.container != null) return false;
		if (cpus != null ? !cpus.equals(that.cpus) : that.cpus != null) return false;
		if (dependencies != null ? !dependencies.equals(that.dependencies) : that.dependencies != null) return false;
		if (disk != null ? !disk.equals(that.disk) : that.disk != null) return false;
		if (env != null ? !env.equals(that.env) : that.env != null) return false;
		if (executor != null ? !executor.equals(that.executor) : that.executor != null) return false;
		if (healthChecks != null ? !healthChecks.equals(that.healthChecks) : that.healthChecks != null) return false;
		if (id != null ? !id.equals(that.id) : that.id != null) return false;
		if (instances != null ? !instances.equals(that.instances) : that.instances != null) return false;
		if (mem != null ? !mem.equals(that.mem) : that.mem != null) return false;
		if (ports != null ? !ports.equals(that.ports) : that.ports != null) return false;
		if (requirePorts != null ? !requirePorts.equals(that.requirePorts) : that.requirePorts != null) return false;
		if (storeUrls != null ? !storeUrls.equals(that.storeUrls) : that.storeUrls != null) return false;
		if (upgradeStrategy != null ? !upgradeStrategy.equals(that.upgradeStrategy) : that.upgradeStrategy != null)
			return false;
		if (uris != null ? !uris.equals(that.uris) : that.uris != null) return false;
		if (user != null ? !user.equals(that.user) : that.user != null) return false;
		if (version != null ? !version.equals(that.version) : that.version != null) return false;
		if (additionalProperties != null ? !additionalProperties.equals(that.additionalProperties) : that.additionalProperties != null)
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
		result = 31 * result + (disk != null ? disk.hashCode() : 0);
		result = 31 * result + (env != null ? env.hashCode() : 0);
		result = 31 * result + (executor != null ? executor.hashCode() : 0);
		result = 31 * result + (healthChecks != null ? healthChecks.hashCode() : 0);
		result = 31 * result + (id != null ? id.hashCode() : 0);
		result = 31 * result + (instances != null ? instances.hashCode() : 0);
		result = 31 * result + (mem != null ? mem.hashCode() : 0);
		result = 31 * result + (ports != null ? ports.hashCode() : 0);
		result = 31 * result + (requirePorts != null ? requirePorts.hashCode() : 0);
		result = 31 * result + (storeUrls != null ? storeUrls.hashCode() : 0);
		result = 31 * result + (upgradeStrategy != null ? upgradeStrategy.hashCode() : 0);
		result = 31 * result + (uris != null ? uris.hashCode() : 0);
		result = 31 * result + (user != null ? user.hashCode() : 0);
		result = 31 * result + (version != null ? version.hashCode() : 0);
		result = 31 * result + (additionalProperties != null ? additionalProperties.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "CreateAppRes{" +
			"args=" + args +
			", backoffFactor=" + backoffFactor +
			", backoffSeconds=" + backoffSeconds +
			", maxLaunchDelaySeconds=" + maxLaunchDelaySeconds +
			", cmd='" + cmd + '\'' +
			", constraints=" + constraints +
			", container=" + container +
			", cpus=" + cpus +
			", dependencies=" + dependencies +
			", disk=" + disk +
			", env=" + env +
			", executor='" + executor + '\'' +
			", healthChecks=" + healthChecks +
			", id='" + id + '\'' +
			", instances=" + instances +
			", mem=" + mem +
			", ports=" + ports +
			", requirePorts=" + requirePorts +
			", storeUrls=" + storeUrls +
			", upgradeStrategy=" + upgradeStrategy +
			", uris=" + uris +
			", user=" + user +
			", version='" + version + '\'' +
			", additionalProperties=" + additionalProperties +
			'}';
	}
}
