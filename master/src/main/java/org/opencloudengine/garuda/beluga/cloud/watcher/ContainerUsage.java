package org.opencloudengine.garuda.beluga.cloud.watcher;

/**
 * Created by swsong on 2015. 11. 27..
 */
public class ContainerUsage {

    private String appId;
    private String containerId;
    private int workLoadPercent;
    private int cpuPercent;
    private int memoryPercent;
    private long maxMemory;
    private long usedMemory;

    public ContainerUsage(String appId, String containerId, int workLoadPercent, int cpuPercent, int memoryPercent, long maxMemory, long usedMemory) {
        this.appId = appId;
        this.containerId = containerId;
        this.workLoadPercent = workLoadPercent;
        this.cpuPercent = cpuPercent;
        this.memoryPercent = memoryPercent;
        this.maxMemory = maxMemory;
        this.usedMemory = usedMemory;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public int getWorkLoadPercent() {
        return workLoadPercent;
    }

    public void setWorkLoadPercent(int workLoadPercent) {
        this.workLoadPercent = workLoadPercent;
    }

    public int getCpuPercent() {
        return cpuPercent;
    }

    public void setCpuPercent(int cpuPercent) {
        this.cpuPercent = cpuPercent;
    }

    public int getMemoryPercent() {
        return memoryPercent;
    }

    public void setMemoryPercent(int memoryPercent) {
        this.memoryPercent = memoryPercent;
    }

    public long getMaxMemory() {
        return maxMemory;
    }

    public void setMaxMemory(long maxMemory) {
        this.maxMemory = maxMemory;
    }

    public long getUsedMemory() {
        return usedMemory;
    }

    public void setUsedMemory(long usedMemory) {
        this.usedMemory = usedMemory;
    }

    @Override
    public String toString() {
        return new StringBuffer().append("ResourceUsage: app[").append(appId).append("] container[").append(containerId).append("] load[")
                .append(workLoadPercent).append("%] cpu[").append(cpuPercent).append("%] memory[").append(memoryPercent).append("%] memUsed[")
                .append(usedMemory).append("] memMax[").append(maxMemory).append("]").toString();
    }
}
