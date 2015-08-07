package org.opencloudengine.garuda.exception;

/**
 * Garuda Exception
 *
 * @author Sang Wook, Song
 *
 */
public class GarudaException extends Exception {

    public GarudaException() {
    }

    public GarudaException(String message) {
        super(message);
    }

    public GarudaException(String message, Throwable cause) {
        super(message, cause);
    }

    public GarudaException(Throwable cause) {
        super(cause);
    }
}
