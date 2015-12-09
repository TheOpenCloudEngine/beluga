package org.opencloudengine.garuda.beluga.cloud.watcher;

/**
 * Created by swsong on 2015. 11. 27..
 */
public class ContainerUsage {

    private String appId;
    private String containerId;
    private double loadAverage;
    private double cpuPercent;
    private double memoryPercent;
    private long maxMemory;
    private long usedMemory;

    public ContainerUsage(String appId, String containerId, double loadAverage, double cpuPercent, double memoryPercent, long maxMemory, long usedMemory) {
        this.appId = appId;
        this.containerId = containerId;
        this.loadAverage = loadAverage;
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

    public double getLoadAverage() {
        return loadAverage;
    }

    public void setLoadAverage(double loadAverage) {
        this.loadAverage = loadAverage;
    }

    public double getCpuPercent() {
        return cpuPercent;
    }

    public void setCpuPercent(double cpuPercent) {
        this.cpuPercent = cpuPercent;
    }

    public double getMemoryPercent() {
        return memoryPercent;
    }

    public void setMemoryPercent(double memoryPercent) {
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
                .append(loadAverage).append("] cpu[").append(cpuPercent).append("%] memory[").append(memoryPercent).append("%] memUsed[")
                .append(usedMemory).append("] memMax[").append(maxMemory).append("]").toString();
    }
}
