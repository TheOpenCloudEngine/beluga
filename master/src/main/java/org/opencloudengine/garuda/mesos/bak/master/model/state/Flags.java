
package org.opencloudengine.garuda.mesos.bak.master.model.state;

import com.fasterxml.jackson.annotation.*;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "allocation_interval",
    "authenticate",
    "authenticate_slaves",
    "authenticators",
    "cluster",
    "framework_sorter",
    "help",
    "initialize_driver_logging",
    "ip",
    "log_auto_initialize",
    "log_dir",
    "logbufsecs",
    "logging_level",
    "port",
    "quiet",
    "quorum",
    "recovery_slave_removal_limit",
    "registry",
    "registry_fetch_timeout",
    "registry_store_timeout",
    "registry_strict",
    "root_submissions",
    "slave_reregister_timeout",
    "user_sorter",
    "version",
    "webui_dir",
    "work_dir",
    "zk",
    "zk_session_timeout"
})
public class Flags {

    @JsonProperty("allocation_interval")
    private String allocationInterval;
    @JsonProperty("authenticate")
    private String authenticate;
    @JsonProperty("authenticate_slaves")
    private String authenticateSlaves;
    @JsonProperty("authenticators")
    private String authenticators;
    @JsonProperty("cluster")
    private String cluster;
    @JsonProperty("framework_sorter")
    private String frameworkSorter;
    @JsonProperty("help")
    private String help;
    @JsonProperty("initialize_driver_logging")
    private String initializeDriverLogging;
    @JsonProperty("ip")
    private String ip;
    @JsonProperty("log_auto_initialize")
    private String logAutoInitialize;
    @JsonProperty("log_dir")
    private String logDir;
    @JsonProperty("logbufsecs")
    private String logbufsecs;
    @JsonProperty("logging_level")
    private String loggingLevel;
    @JsonProperty("port")
    private String port;
    @JsonProperty("quiet")
    private String quiet;
    @JsonProperty("quorum")
    private String quorum;
    @JsonProperty("recovery_slave_removal_limit")
    private String recoverySlaveRemovalLimit;
    @JsonProperty("registry")
    private String registry;
    @JsonProperty("registry_fetch_timeout")
    private String registryFetchTimeout;
    @JsonProperty("registry_store_timeout")
    private String registryStoreTimeout;
    @JsonProperty("registry_strict")
    private String registryStrict;
    @JsonProperty("root_submissions")
    private String rootSubmissions;
    @JsonProperty("slave_reregister_timeout")
    private String slaveReregisterTimeout;
    @JsonProperty("user_sorter")
    private String userSorter;
    @JsonProperty("version")
    private String version;
    @JsonProperty("webui_dir")
    private String webuiDir;
    @JsonProperty("work_dir")
    private String workDir;
    @JsonProperty("zk")
    private String zk;
    @JsonProperty("zk_session_timeout")
    private String zkSessionTimeout;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("allocation_interval")
    public String getAllocationInterval() {
        return allocationInterval;
    }

    @JsonProperty("allocation_interval")
    public void setAllocationInterval(String allocationInterval) {
        this.allocationInterval = allocationInterval;
    }

    @JsonProperty("authenticate")
    public String getAuthenticate() {
        return authenticate;
    }

    @JsonProperty("authenticate")
    public void setAuthenticate(String authenticate) {
        this.authenticate = authenticate;
    }

    @JsonProperty("authenticate_slaves")
    public String getAuthenticateSlaves() {
        return authenticateSlaves;
    }

    @JsonProperty("authenticate_slaves")
    public void setAuthenticateSlaves(String authenticateSlaves) {
        this.authenticateSlaves = authenticateSlaves;
    }

    @JsonProperty("authenticators")
    public String getAuthenticators() {
        return authenticators;
    }

    @JsonProperty("authenticators")
    public void setAuthenticators(String authenticators) {
        this.authenticators = authenticators;
    }

    @JsonProperty("cluster")
    public String getCluster() {
        return cluster;
    }

    @JsonProperty("cluster")
    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    @JsonProperty("framework_sorter")
    public String getFrameworkSorter() {
        return frameworkSorter;
    }

    @JsonProperty("framework_sorter")
    public void setFrameworkSorter(String frameworkSorter) {
        this.frameworkSorter = frameworkSorter;
    }

    @JsonProperty("help")
    public String getHelp() {
        return help;
    }

    @JsonProperty("help")
    public void setHelp(String help) {
        this.help = help;
    }

    @JsonProperty("initialize_driver_logging")
    public String getInitializeDriverLogging() {
        return initializeDriverLogging;
    }

    @JsonProperty("initialize_driver_logging")
    public void setInitializeDriverLogging(String initializeDriverLogging) {
        this.initializeDriverLogging = initializeDriverLogging;
    }

    @JsonProperty("ip")
    public String getIp() {
        return ip;
    }

    @JsonProperty("ip")
    public void setIp(String ip) {
        this.ip = ip;
    }

    @JsonProperty("log_auto_initialize")
    public String getLogAutoInitialize() {
        return logAutoInitialize;
    }

    @JsonProperty("log_auto_initialize")
    public void setLogAutoInitialize(String logAutoInitialize) {
        this.logAutoInitialize = logAutoInitialize;
    }

    @JsonProperty("log_dir")
    public String getLogDir() {
        return logDir;
    }

    @JsonProperty("log_dir")
    public void setLogDir(String logDir) {
        this.logDir = logDir;
    }

    @JsonProperty("logbufsecs")
    public String getLogbufsecs() {
        return logbufsecs;
    }

    @JsonProperty("logbufsecs")
    public void setLogbufsecs(String logbufsecs) {
        this.logbufsecs = logbufsecs;
    }

    @JsonProperty("logging_level")
    public String getLoggingLevel() {
        return loggingLevel;
    }

    @JsonProperty("logging_level")
    public void setLoggingLevel(String loggingLevel) {
        this.loggingLevel = loggingLevel;
    }

    @JsonProperty("port")
    public String getPort() {
        return port;
    }

    @JsonProperty("port")
    public void setPort(String port) {
        this.port = port;
    }

    @JsonProperty("quiet")
    public String getQuiet() {
        return quiet;
    }

    @JsonProperty("quiet")
    public void setQuiet(String quiet) {
        this.quiet = quiet;
    }

    @JsonProperty("quorum")
    public String getQuorum() {
        return quorum;
    }

    @JsonProperty("quorum")
    public void setQuorum(String quorum) {
        this.quorum = quorum;
    }

    @JsonProperty("recovery_slave_removal_limit")
    public String getRecoverySlaveRemovalLimit() {
        return recoverySlaveRemovalLimit;
    }

    @JsonProperty("recovery_slave_removal_limit")
    public void setRecoverySlaveRemovalLimit(String recoverySlaveRemovalLimit) {
        this.recoverySlaveRemovalLimit = recoverySlaveRemovalLimit;
    }

    @JsonProperty("registry")
    public String getRegistry() {
        return registry;
    }

    @JsonProperty("registry")
    public void setRegistry(String registry) {
        this.registry = registry;
    }

    @JsonProperty("registry_fetch_timeout")
    public String getRegistryFetchTimeout() {
        return registryFetchTimeout;
    }

    @JsonProperty("registry_fetch_timeout")
    public void setRegistryFetchTimeout(String registryFetchTimeout) {
        this.registryFetchTimeout = registryFetchTimeout;
    }

    @JsonProperty("registry_store_timeout")
    public String getRegistryStoreTimeout() {
        return registryStoreTimeout;
    }

    @JsonProperty("registry_store_timeout")
    public void setRegistryStoreTimeout(String registryStoreTimeout) {
        this.registryStoreTimeout = registryStoreTimeout;
    }

    @JsonProperty("registry_strict")
    public String getRegistryStrict() {
        return registryStrict;
    }

    @JsonProperty("registry_strict")
    public void setRegistryStrict(String registryStrict) {
        this.registryStrict = registryStrict;
    }

    @JsonProperty("root_submissions")
    public String getRootSubmissions() {
        return rootSubmissions;
    }

    @JsonProperty("root_submissions")
    public void setRootSubmissions(String rootSubmissions) {
        this.rootSubmissions = rootSubmissions;
    }

    @JsonProperty("slave_reregister_timeout")
    public String getSlaveReregisterTimeout() {
        return slaveReregisterTimeout;
    }

    @JsonProperty("slave_reregister_timeout")
    public void setSlaveReregisterTimeout(String slaveReregisterTimeout) {
        this.slaveReregisterTimeout = slaveReregisterTimeout;
    }

    @JsonProperty("user_sorter")
    public String getUserSorter() {
        return userSorter;
    }

    @JsonProperty("user_sorter")
    public void setUserSorter(String userSorter) {
        this.userSorter = userSorter;
    }

    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
    }

    @JsonProperty("webui_dir")
    public String getWebuiDir() {
        return webuiDir;
    }

    @JsonProperty("webui_dir")
    public void setWebuiDir(String webuiDir) {
        this.webuiDir = webuiDir;
    }

    @JsonProperty("work_dir")
    public String getWorkDir() {
        return workDir;
    }

    @JsonProperty("work_dir")
    public void setWorkDir(String workDir) {
        this.workDir = workDir;
    }

    @JsonProperty("zk")
    public String getZk() {
        return zk;
    }

    @JsonProperty("zk")
    public void setZk(String zk) {
        this.zk = zk;
    }

    @JsonProperty("zk_session_timeout")
    public String getZkSessionTimeout() {
        return zkSessionTimeout;
    }

    @JsonProperty("zk_session_timeout")
    public void setZkSessionTimeout(String zkSessionTimeout) {
        this.zkSessionTimeout = zkSessionTimeout;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
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
    public int hashCode() {
        return new HashCodeBuilder().append(allocationInterval).append(authenticate).append(authenticateSlaves).append(authenticators).append(cluster).append(frameworkSorter).append(help).append(initializeDriverLogging).append(ip).append(logAutoInitialize).append(logDir).append(logbufsecs).append(loggingLevel).append(port).append(quiet).append(quorum).append(recoverySlaveRemovalLimit).append(registry).append(registryFetchTimeout).append(registryStoreTimeout).append(registryStrict).append(rootSubmissions).append(slaveReregisterTimeout).append(userSorter).append(version).append(webuiDir).append(workDir).append(zk).append(zkSessionTimeout).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Flags) == false) {
            return false;
        }
        Flags rhs = ((Flags) other);
        return new EqualsBuilder().append(allocationInterval, rhs.allocationInterval).append(authenticate, rhs.authenticate).append(authenticateSlaves, rhs.authenticateSlaves).append(authenticators, rhs.authenticators).append(cluster, rhs.cluster).append(frameworkSorter, rhs.frameworkSorter).append(help, rhs.help).append(initializeDriverLogging, rhs.initializeDriverLogging).append(ip, rhs.ip).append(logAutoInitialize, rhs.logAutoInitialize).append(logDir, rhs.logDir).append(logbufsecs, rhs.logbufsecs).append(loggingLevel, rhs.loggingLevel).append(port, rhs.port).append(quiet, rhs.quiet).append(quorum, rhs.quorum).append(recoverySlaveRemovalLimit, rhs.recoverySlaveRemovalLimit).append(registry, rhs.registry).append(registryFetchTimeout, rhs.registryFetchTimeout).append(registryStoreTimeout, rhs.registryStoreTimeout).append(registryStrict, rhs.registryStrict).append(rootSubmissions, rhs.rootSubmissions).append(slaveReregisterTimeout, rhs.slaveReregisterTimeout).append(userSorter, rhs.userSorter).append(version, rhs.version).append(webuiDir, rhs.webuiDir).append(workDir, rhs.workDir).append(zk, rhs.zk).append(zkSessionTimeout, rhs.zkSessionTimeout).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
