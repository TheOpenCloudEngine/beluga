package org.opencloudengine.garuda.exception;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class InvalidRoleException extends Exception {
    public InvalidRoleException() {
    }

    public InvalidRoleException(String message) {
        super(message);
    }

    public InvalidRoleException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidRoleException(Throwable cause) {
        super(cause);
    }
}
