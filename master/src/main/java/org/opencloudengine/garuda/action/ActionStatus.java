package org.opencloudengine.garuda.action;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class ActionStatus {

    private boolean isDone;
    private boolean isStarted;

    private List<String> stepMessageList;
    private int step;

    public ActionStatus(){
        stepMessageList = new ArrayList<>();
    }
    public boolean isDone() {
        return isDone;
    }

    public void setDone() {
        isDone = true;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void registerStep(String message) {
        stepMessageList.add(message);
    }

    public List<String> getStepMessageList() {
        return stepMessageList;
    }

    public void startStep() {
        isStarted = true;
    }

    public void walkStep() {
        step++;
        if(step == stepMessageList.size()) {
            isDone = true;
        }
    }

    public int getStep() {
        return step;
    }

    public int getTotalStep() {
        return stepMessageList.size();
    }
}
