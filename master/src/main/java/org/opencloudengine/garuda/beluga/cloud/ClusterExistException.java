package org.opencloudengine.garuda.beluga.cloud;

/**
 * Created by swsong on 2015. 8. 5..
 */
public class ClusterExistException extends Exception {
    public ClusterExistException() {
    }

    public ClusterExistException(String message) {
        super(message);
    }

    public ClusterExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClusterExistException(Throwable cause) {
        super(cause);
    }
}
