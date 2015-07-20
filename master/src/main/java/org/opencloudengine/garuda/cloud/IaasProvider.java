package org.opencloudengine.garuda.cloud;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.Template;
import org.opencloudengine.garuda.exception.UnknownIaasProviderException;
import org.opencloudengine.garuda.utils.classloader.DynamicClassLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by swsong on 2015. 7. 15..
 */
public class IaasProvider {
    private String type;
    private String name;
    private String identity;
    private String credential;

    public IaasProvider(String type, String name, String identity, String credential) {
        this.type = type;
        this.name = name;
        this.identity = identity;
        this.credential = credential;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }

    public Iaas getIaas() throws UnknownIaasProviderException {
        if(type.equalsIgnoreCase("AWS-EC2")) {
            return new EC2Iaas(type, identity, credential);
        } else {
            throw new UnknownIaasProviderException("iaas provider type : " + type);
        }
    }

}
