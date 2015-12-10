package org.opencloudengine.garuda.beluga.cloud.watcher;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by swsong on 2015. 11. 27..
 */
public class AutoScaleRule {
    private Boolean inUse;
    private Integer scaleOutWorkLoad;
    private Integer scaleOutTimeInMin;
    private Integer scaleInWorkLoad;
    private Integer scaleInTimeInMin;

    // 조건에 부합하던 마지막 시간. 0이면 부합하지 않았던 것을 의미한다.
    // runtime 변수로 활용하며 저장하지는 않는다.
    @JsonIgnore
    private long appScaleOutLastTimestamp;
    @JsonIgnore
    private long appScaleInLastTimestamp;

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

    public long getAppScaleOutLastTimestamp() {
        return appScaleOutLastTimestamp;
    }

    public void setAppScaleOutLastTimestamp(long appScaleOutLastTimestamp) {
        this.appScaleOutLastTimestamp = appScaleOutLastTimestamp;
    }

    public long getAppScaleInLastTimestamp() {
        return appScaleInLastTimestamp;
    }

    public void setAppScaleInLastTimestamp(long appScaleInLastTimestamp) {
        this.appScaleInLastTimestamp = appScaleInLastTimestamp;
    }
}
