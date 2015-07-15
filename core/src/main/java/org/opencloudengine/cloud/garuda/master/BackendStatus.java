package org.opencloudengine.cloud.garuda.master;

public class BackendStatus {

    private float cpuUsage1;
    private float cpuUsage5;
    private float cpuUsage10;
    private long memTotal;
    private long memFree;
    private String appRepoUrl;

    public BackendStatus() {

    }

    public float getCpuUsage1() {
        return cpuUsage1;
    }

    public void setCpuUsage1(float cpuUsage1) {
        this.cpuUsage1 = cpuUsage1;
    }

    public float getCpuUsage5() {
        return cpuUsage5;
    }

    public void setCpuUsage5(float cpuUsage5) {
        this.cpuUsage5 = cpuUsage5;
    }

    public float getCpuUsage10() {
        return cpuUsage10;
    }

    public void setCpuUsage10(float cpuUsage10) {
        this.cpuUsage10 = cpuUsage10;
    }

    public long getMemTotal() {
        return memTotal;
    }

    public void setMemTotal(long memTotal) {
        this.memTotal = memTotal;
    }

    public long getMemFree() {
        return memFree;
    }

    public void setMemFree(long memFree) {
        this.memFree = memFree;
    }

    public float getMemUsage() {
        return (float) (memTotal - memFree) / memTotal;
    }

    @Override
    public String toString() {
        String r = "";
        r += "[" + cpuUsage1 + " " + cpuUsage5 + " " + cpuUsage10 + "],"
                + this.getMemUsage() + " " + appRepoUrl;
        return r;
    }

    public void setAppRepoUrl(String appRepoUrl) {
        this.appRepoUrl = appRepoUrl;
    }

    public String getAppRepoUrl() {
        return appRepoUrl;
    }

}
