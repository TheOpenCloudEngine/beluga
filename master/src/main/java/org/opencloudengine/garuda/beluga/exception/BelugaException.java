package org.opencloudengine.garuda.beluga.exception;

/**
 * Beluga Exception
 *
 * @author Sang Wook, Song
 *
 */
public class BelugaException extends Exception {

    public BelugaException() {
    }

    public BelugaException(String message) {
        super(message);
    }

    public BelugaException(String message, Throwable cause) {
        super(message, cause);
    }

    public BelugaException(Throwable cause) {
        super(cause);
    }
}
