package org.opencloudengine.garuda.action;

import org.opencloudengine.garuda.utils.TimeUtils;

import java.util.concurrent.CountDownLatch;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class ActionStatus {

    public static final String STATE_NONE = "none";
    public static final String STATE_QUEUE = "in-queue";
    public static final String STATE_PROGRESS = "in-progress";
    public static final String STATE_COMPLETE = "completed";
    public static final String STATE_ERROR = "error";

    private final String id;
    private final String actionName;
    private String startTime = "";
    private String completeTime = "";
    private String error = "";
    private String state = STATE_NONE;
    private Object result;
    private ActionStep step;

    private CountDownLatch latch;

    @Override
    public String toString() {
        return String.format("[%s] id[%s] state[%s] actionName[%s] startTime[%s] completeTime[%s] error[%s] result[%s] step[%s]"
                , getClass().getSimpleName(), id, state, actionName, startTime, completeTime, error
                , result, step);
    }

    public ActionStatus(String id, String actionName){
        this.id = id;
        this.actionName = actionName;
        step = new ActionStep();
        latch = new CountDownLatch(1);
    }

    public String getId() {
        return id;
    }

    public String getState() {
        return state;
    }

    protected void setState(String state) {
        this.state = state;
    }

    public String getActionName() {
        return actionName;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getCompleteTime() {
        return completeTime;
    }

    public String getError() {
        return error;
    }

    public Object getResult() {
        return result;
    }

    public boolean checkDone() {
        return state == STATE_COMPLETE || state == STATE_ERROR;
    }

    //
    // Step 진행상황.
    //
    public void registerStep(String message) {
        step.getStepMessages().add(message);
    }



    public void setStart() {
        state = STATE_PROGRESS;
        startTime = TimeUtils.getCurrentLocalTimeString();
        step.setCurrentStep(0);
    }

    public void setInQueue() {
        state = STATE_QUEUE;
    }

    public void setError(String error) {
        setError(error, null);
    }
    public void setError(Throwable t) {
        setError(null, t);
    }
    public void setError(String error, Throwable t) {
        state = STATE_ERROR;
        this.error = error != null ? error : t.getMessage();
        result = t;
        completeTime = TimeUtils.getCurrentLocalTimeString();
        latch.countDown();
    }

    public void setComplete() {
        if(state == STATE_ERROR) {
            //에러라면 보존한다.
            return;
        }
        state = STATE_COMPLETE;
        completeTime = TimeUtils.getCurrentLocalTimeString();
        latch.countDown();
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public void walkStep() {
        step.walkStep();
    }

    public ActionStep getStep() {
        return step;
    }

    public void waitForDone() throws InterruptedException {
        latch.await();
    }
}
