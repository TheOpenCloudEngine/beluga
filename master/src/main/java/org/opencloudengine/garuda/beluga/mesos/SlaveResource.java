package org.opencloudengine.garuda.beluga.mesos;

/**
 * Created by swsong on 2015. 12. 10..
 */
public class SlaveResource {
    private int mem;
    private double cpus;
    private int usedMem;
    private double usedCpus;

    public SlaveResource(int mem, double cpus, int usedMem, double usedCpus) {
        this.mem = mem;
        this.cpus = cpus;
        this.usedMem = usedMem;
        this.usedCpus = usedCpus;
    }

    public int getMem() {
        return mem;
    }

    public void setMem(int mem) {
        this.mem = mem;
    }

    public double getCpus() {
        return cpus;
    }

    public void setCpus(double cpus) {
        this.cpus = cpus;
    }

    public int getUsedMem() {
        return usedMem;
    }

    public void setUsedMem(int usedMem) {
        this.usedMem = usedMem;
    }

    public double getUsedCpus() {
        return usedCpus;
    }

    public void setUsedCpus(double usedCpus) {
        this.usedCpus = usedCpus;
    }

    @Override
    public String toString() {
        return new StringBuffer().append("SlaveResource mem[").append(mem).append("] usedMem[").append(usedMem)
                .append("] cpus[").append(cpus).append("] usedCpus[").append(usedCpus).append("]").toString();
    }
}
