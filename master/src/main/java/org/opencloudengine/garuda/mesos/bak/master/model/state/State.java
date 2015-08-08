
package org.opencloudengine.garuda.mesos.bak.master.model.state;

import com.fasterxml.jackson.annotation.*;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "activated_slaves",
    "build_date",
    "build_time",
    "build_user",
    "cluster",
    "completed_frameworks",
    "deactivated_slaves",
    "elected_time",
    "flags",
    "frameworks",
    "git_branch",
    "git_sha",
    "hostname",
    "id",
    "leader",
    "log_dir",
    "orphan_tasks",
    "pid",
    "slaves",
    "start_time",
    "unregistered_frameworks",
    "version"
})
public class State {

    @JsonProperty("activated_slaves")
    private Integer activatedSlaves;
    @JsonProperty("build_date")
    private String buildDate;
    @JsonProperty("build_time")
    private Integer buildTime;
    @JsonProperty("build_user")
    private String buildUser;
    @JsonProperty("cluster")
    private String cluster;
    @JsonProperty("completed_frameworks")
    private List<Object> completedFrameworks = new ArrayList<Object>();
    @JsonProperty("deactivated_slaves")
    private Integer deactivatedSlaves;
    @JsonProperty("elected_time")
    private Float electedTime;
    @JsonProperty("flags")
    private Flags flags;
    @JsonProperty("frameworks")
    private List<Framework> frameworks = new ArrayList<Framework>();
    @JsonProperty("git_branch")
    private String gitBranch;
    @JsonProperty("git_sha")
    private String gitSha;
    @JsonProperty("hostname")
    private String hostname;
    @JsonProperty("id")
    private String id;
    @JsonProperty("leader")
    private String leader;
    @JsonProperty("log_dir")
    private String logDir;
    @JsonProperty("orphan_tasks")
    private List<Object> orphanTasks = new ArrayList<Object>();
    @JsonProperty("pid")
    private String pid;
    @JsonProperty("slaves")
    private List<Slafe> slaves = new ArrayList<Slafe>();
    @JsonProperty("start_time")
    private Float startTime;
    @JsonProperty("unregistered_frameworks")
    private List<Object> unregisteredFrameworks = new ArrayList<Object>();
    @JsonProperty("version")
    private String version;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("activated_slaves")
    public Integer getActivatedSlaves() {
        return activatedSlaves;
    }

    @JsonProperty("activated_slaves")
    public void setActivatedSlaves(Integer activatedSlaves) {
        this.activatedSlaves = activatedSlaves;
    }

    @JsonProperty("build_date")
    public String getBuildDate() {
        return buildDate;
    }

    @JsonProperty("build_date")
    public void setBuildDate(String buildDate) {
        this.buildDate = buildDate;
    }

    @JsonProperty("build_time")
    public Integer getBuildTime() {
        return buildTime;
    }

    @JsonProperty("build_time")
    public void setBuildTime(Integer buildTime) {
        this.buildTime = buildTime;
    }

    @JsonProperty("build_user")
    public String getBuildUser() {
        return buildUser;
    }

    @JsonProperty("build_user")
    public void setBuildUser(String buildUser) {
        this.buildUser = buildUser;
    }

    @JsonProperty("cluster")
    public String getCluster() {
        return cluster;
    }

    @JsonProperty("cluster")
    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    @JsonProperty("completed_frameworks")
    public List<Object> getCompletedFrameworks() {
        return completedFrameworks;
    }

    @JsonProperty("completed_frameworks")
    public void setCompletedFrameworks(List<Object> completedFrameworks) {
        this.completedFrameworks = completedFrameworks;
    }

    @JsonProperty("deactivated_slaves")
    public Integer getDeactivatedSlaves() {
        return deactivatedSlaves;
    }

    @JsonProperty("deactivated_slaves")
    public void setDeactivatedSlaves(Integer deactivatedSlaves) {
        this.deactivatedSlaves = deactivatedSlaves;
    }

    @JsonProperty("elected_time")
    public Float getElectedTime() {
        return electedTime;
    }

    @JsonProperty("elected_time")
    public void setElectedTime(Float electedTime) {
        this.electedTime = electedTime;
    }

    @JsonProperty("flags")
    public Flags getFlags() {
        return flags;
    }

    @JsonProperty("flags")
    public void setFlags(Flags flags) {
        this.flags = flags;
    }

    @JsonProperty("frameworks")
    public List<Framework> getFrameworks() {
        return frameworks;
    }

    @JsonProperty("frameworks")
    public void setFrameworks(List<Framework> frameworks) {
        this.frameworks = frameworks;
    }

    @JsonProperty("git_branch")
    public String getGitBranch() {
        return gitBranch;
    }

    @JsonProperty("git_branch")
    public void setGitBranch(String gitBranch) {
        this.gitBranch = gitBranch;
    }

    @JsonProperty("git_sha")
    public String getGitSha() {
        return gitSha;
    }

    @JsonProperty("git_sha")
    public void setGitSha(String gitSha) {
        this.gitSha = gitSha;
    }

    @JsonProperty("hostname")
    public String getHostname() {
        return hostname;
    }

    @JsonProperty("hostname")
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("leader")
    public String getLeader() {
        return leader;
    }

    @JsonProperty("leader")
    public void setLeader(String leader) {
        this.leader = leader;
    }

    @JsonProperty("log_dir")
    public String getLogDir() {
        return logDir;
    }

    @JsonProperty("log_dir")
    public void setLogDir(String logDir) {
        this.logDir = logDir;
    }

    @JsonProperty("orphan_tasks")
    public List<Object> getOrphanTasks() {
        return orphanTasks;
    }

    @JsonProperty("orphan_tasks")
    public void setOrphanTasks(List<Object> orphanTasks) {
        this.orphanTasks = orphanTasks;
    }

    @JsonProperty("pid")
    public String getPid() {
        return pid;
    }

    @JsonProperty("pid")
    public void setPid(String pid) {
        this.pid = pid;
    }

    @JsonProperty("slaves")
    public List<Slafe> getSlaves() {
        return slaves;
    }

    @JsonProperty("slaves")
    public void setSlaves(List<Slafe> slaves) {
        this.slaves = slaves;
    }

    @JsonProperty("start_time")
    public Float getStartTime() {
        return startTime;
    }

    @JsonProperty("start_time")
    public void setStartTime(Float startTime) {
        this.startTime = startTime;
    }

    @JsonProperty("unregistered_frameworks")
    public List<Object> getUnregisteredFrameworks() {
        return unregisteredFrameworks;
    }

    @JsonProperty("unregistered_frameworks")
    public void setUnregisteredFrameworks(List<Object> unregisteredFrameworks) {
        this.unregisteredFrameworks = unregisteredFrameworks;
    }

    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
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
        return new HashCodeBuilder().append(activatedSlaves).append(buildDate).append(buildTime).append(buildUser).append(cluster).append(completedFrameworks).append(deactivatedSlaves).append(electedTime).append(flags).append(frameworks).append(gitBranch).append(gitSha).append(hostname).append(id).append(leader).append(logDir).append(orphanTasks).append(pid).append(slaves).append(startTime).append(unregisteredFrameworks).append(version).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof State) == false) {
            return false;
        }
        State rhs = ((State) other);
        return new EqualsBuilder().append(activatedSlaves, rhs.activatedSlaves).append(buildDate, rhs.buildDate).append(buildTime, rhs.buildTime).append(buildUser, rhs.buildUser).append(cluster, rhs.cluster).append(completedFrameworks, rhs.completedFrameworks).append(deactivatedSlaves, rhs.deactivatedSlaves).append(electedTime, rhs.electedTime).append(flags, rhs.flags).append(frameworks, rhs.frameworks).append(gitBranch, rhs.gitBranch).append(gitSha, rhs.gitSha).append(hostname, rhs.hostname).append(id, rhs.id).append(leader, rhs.leader).append(logDir, rhs.logDir).append(orphanTasks, rhs.orphanTasks).append(pid, rhs.pid).append(slaves, rhs.slaves).append(startTime, rhs.startTime).append(unregisteredFrameworks, rhs.unregisteredFrameworks).append(version, rhs.version).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
