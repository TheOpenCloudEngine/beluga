package org.opencloudengine.garuda.action;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class ActionStatus {

    public static final String STATE_NONE = "none";
    public static final String STATE_QUEUE = "in-queue";
    public static final String STATE_PROGRESS = "in-progress";
    public static final String STATE_COMPLETE = "completed";
    public static final String STATE_ERROR = "error";

    private String actionName;
    private long startTime;
    private long completeTime;
    private String error;

    private String state = STATE_NONE;
    private List<String> stepMessageList;
    private int step;
    private Object result;

    public ActionStatus(String actionName){
        this.actionName = actionName;
        stepMessageList = new ArrayList<>();
    }

    public String getState() {
        return state;
    }

    protected void setState(String state) {
        this.state = state;
    }

    public boolean isDone() {
        return state == STATE_COMPLETE || state == STATE_ERROR;
    }

    //
    // Step 진행상황.
    //
    public void registerStep(String message) {
        stepMessageList.add(message);
    }

    public List<String> getStepMessageList() {
        return stepMessageList;
    }

    public void setStart() {
        state = STATE_PROGRESS;
        startTime = System.currentTimeMillis();
        step = 0;
    }

    public void setInQueue() {
        state = STATE_QUEUE;
    }

    public void setError(Throwable t) {
        setError(null, t);
    }
    public void setError(String error, Throwable t) {
        state = STATE_ERROR;
        this.error = error != null ? error : t.getMessage();
        result = t;
        completeTime = System.currentTimeMillis();
        step = 0;
    }

    public void setComplete() {
        if(state == STATE_ERROR) {
            //에러라면 보존한다.
            return;
        }
        state = STATE_COMPLETE;
        completeTime = System.currentTimeMillis();
    }

    public void walkStep() {
        step++;
    }

    public int getStep() {
        return step;
    }

    public int getTotalStep() {
        return stepMessageList.size();
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
