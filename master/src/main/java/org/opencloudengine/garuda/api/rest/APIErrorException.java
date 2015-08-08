package org.opencloudengine.garuda.api.rest;

/**
 * API 에러 발생시 전달.
 * Created by swsong on 2015. 8. 8..
 */
public class APIErrorException extends Exception {
    public APIErrorException() {
    }

    public APIErrorException(String message) {
        super(message);
    }

    public APIErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public APIErrorException(Throwable cause) {
        super(cause);
    }
}
