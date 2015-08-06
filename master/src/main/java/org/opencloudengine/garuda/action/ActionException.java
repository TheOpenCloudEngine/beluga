package org.opencloudengine.garuda.action;

/**
 * Created by swsong on 2015. 8. 6..
 */
public class ActionException extends Exception {
    public ActionException() {
    }

    public ActionException(String message) {
        super(message);
    }

    public ActionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActionException(Throwable cause) {
        super(cause);
    }
}
