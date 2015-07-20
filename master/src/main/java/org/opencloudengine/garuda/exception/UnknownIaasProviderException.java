package org.opencloudengine.garuda.exception;

/**
 * Created by swsong on 2015. 7. 20..
 */
public class UnknownIaasProviderException extends Exception {

    public UnknownIaasProviderException(Exception e) {
        super(e);
    }

    public UnknownIaasProviderException(String msg) {
        super(msg);
    }
}
