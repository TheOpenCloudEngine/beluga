package org.opencloudengine.garuda.action;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class ActionResult {
    private boolean isSuccess;
    private Object result;
    private String errorMessage;
    private Throwable exception;

    public ActionResult() {
    }

    public ActionResult withError(String errorMessage){
        isSuccess = false;
        this.errorMessage = errorMessage;
        return this;
    }
    public ActionResult withError(String format, String... args){
        isSuccess = false;
        this.errorMessage = String.format(format, args);
        return this;
    }
    public ActionResult withError(Throwable exception){
        isSuccess = false;
        this.exception = exception;
        return this;
    }
    public ActionResult withError(Throwable exception, String errorMessage){
        isSuccess = false;
        this.exception = exception;
        this.errorMessage = errorMessage;
        return this;
    }
    public ActionResult withError(Throwable exception, String format, String... args){
        isSuccess = false;
        this.exception = exception;
        this.errorMessage = String.format(format, args);
        return this;
    }
    public ActionResult withResult(Object result) {
        isSuccess = false;
        this.result = result;
        return this;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public Object getResult() {
        return result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Throwable getException() {
        return exception;
    }
}
