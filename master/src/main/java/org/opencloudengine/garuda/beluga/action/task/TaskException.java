package org.opencloudengine.garuda.beluga.action.task;

/**
 * Created by swsong on 2015. 8. 5..
 */
public class TaskException extends Exception {
    public TaskException() {
    }

    public TaskException(String message) {
        super(message);
    }

    public TaskException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskException(Throwable cause) {
        super(cause);
    }
}
