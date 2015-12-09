package org.opencloudengine.garuda.beluga.cloud.watcher;

/**
 * Created by swsong on 2015. 11. 27..
 */
public class AutoScaleRule {
    private Integer scaleOutLoadAverage;
    private Integer scaleOutDuringInMin;
    private Integer scaleInLoadAverage;
    private Integer scaleInDuringInMin;

    public AutoScaleRule(Integer scaleOutLoadAverage, Integer scaleOutDuringInMin, Integer scaleInLoadAverage, Integer scaleInDuringInMin) {
        this.scaleOutLoadAverage = scaleOutLoadAverage;
        this.scaleOutDuringInMin = scaleOutDuringInMin;
        this.scaleInLoadAverage = scaleInLoadAverage;
        this.scaleInDuringInMin = scaleInDuringInMin;
    }

    public Integer getScaleOutLoadAverage() {
        return scaleOutLoadAverage;
    }

    public void setScaleOutLoadAverage(Integer scaleOutLoadAverage) {
        this.scaleOutLoadAverage = scaleOutLoadAverage;
    }

    public Integer getScaleOutDuringInMin() {
        return scaleOutDuringInMin;
    }

    public void setScaleOutDuringInMin(Integer scaleOutDuringInMin) {
        this.scaleOutDuringInMin = scaleOutDuringInMin;
    }

    public Integer getScaleInLoadAverage() {
        return scaleInLoadAverage;
    }

    public void setScaleInLoadAverage(Integer scaleInLoadAverage) {
        this.scaleInLoadAverage = scaleInLoadAverage;
    }

    public Integer getScaleInDuringInMin() {
        return scaleInDuringInMin;
    }

    public void setScaleInDuringInMin(Integer scaleInDuringInMin) {
        this.scaleInDuringInMin = scaleInDuringInMin;
    }
}
