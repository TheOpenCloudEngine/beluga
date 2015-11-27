package org.opencloudengine.garuda.beluga.watcher;

/**
 * Created by swsong on 2015. 11. 27..
 */
public class AutoScaleConfig {
    private Integer loadAverage;
    private Integer duringInMin;
    private Integer scale;

    public AutoScaleConfig(Integer loadAverage, Integer duringInMin) {
        this.loadAverage = loadAverage;
        this.duringInMin = duringInMin;
    }

    public AutoScaleConfig(Integer loadAverage, Integer duringInMin, Integer scale) {
        this.loadAverage = loadAverage;
        this.duringInMin = duringInMin;
        this.scale = scale;
    }

    public Integer getLoadAverage() {
        return loadAverage;
    }

    public void setLoadAverage(Integer loadAverage) {
        this.loadAverage = loadAverage;
    }

    public Integer getDuringInMin() {
        return duringInMin;
    }

    public void setDuringInMin(Integer duringInMin) {
        this.duringInMin = duringInMin;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }
}
