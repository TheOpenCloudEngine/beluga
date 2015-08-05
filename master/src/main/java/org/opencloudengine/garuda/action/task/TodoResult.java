package org.opencloudengine.garuda.action.task;

/**
 * Created by swsong on 2015. 8. 5..
 */
public class TodoResult {
    private Object result;
    private Throwable exception;

    public void setResult(Object result) {
        this.result = result;
    }

    public Object getResult() {
        return result;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public Throwable getException() {
        return exception;
    }
}
