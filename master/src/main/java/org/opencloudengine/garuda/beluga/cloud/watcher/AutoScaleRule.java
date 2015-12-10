package org.opencloudengine.garuda.beluga.cloud.watcher;

/**
 * Created by swsong on 2015. 11. 27..
 */
public class AutoScaleRule {
    private Boolean inUse;
    private Integer scaleOutWorkLoad;
    private Integer scaleOutTimeInMin;
    private Integer scaleInWorkLoad;
    private Integer scaleInTimeInMin;

    public AutoScaleRule() { }

    public Boolean isInUse() {
        return inUse;
    }

    public void setInUse(Boolean inUse) {
        this.inUse = inUse;
    }

    public Integer getScaleOutWorkLoad() {
        return scaleOutWorkLoad;
    }

    public void setScaleOutWorkLoad(Integer scaleOutWorkLoad) {
        this.scaleOutWorkLoad = scaleOutWorkLoad;
    }

    public Integer getScaleOutTimeInMin() {
        return scaleOutTimeInMin;
    }

    public void setScaleOutTimeInMin(Integer scaleOutTimeInMin) {
        this.scaleOutTimeInMin = scaleOutTimeInMin;
    }

    public Integer getScaleInWorkLoad() {
        return scaleInWorkLoad;
    }

    public void setScaleInWorkLoad(Integer scaleInWorkLoad) {
        this.scaleInWorkLoad = scaleInWorkLoad;
    }

    public Integer getScaleInTimeInMin() {
        return scaleInTimeInMin;
    }

    public void setScaleInTimeInMin(Integer scaleInTimeInMin) {
        this.scaleInTimeInMin = scaleInTimeInMin;
    }

    @Override
    public String toString() {
        return new StringBuffer().append("AutoScaleRule scaleOutWorkLoad[")
                .append(scaleOutWorkLoad).append("%] scaleOutTime[")
                .append(scaleOutTimeInMin).append("Min] scaleInWorkLoad[")
                .append(scaleInWorkLoad).append("%] scaleInTime[")
                .append(scaleInTimeInMin).append("Min]").toString();
    }
}
