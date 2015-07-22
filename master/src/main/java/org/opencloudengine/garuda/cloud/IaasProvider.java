package org.opencloudengine.garuda.cloud;

import org.opencloudengine.garuda.exception.UnknownIaasProviderException;

import java.util.Properties;

/**
 * Created by swsong on 2015. 7. 15..
 */
public class IaasProvider {
    private String id;
    private String name;
    private String identity;
    private String credential;
    private Properties overrides;

    public IaasProvider(String type, String name, String identity, String credential) {
        this(type, name, identity, credential, new Properties());
    }
    public IaasProvider(String type, String name, String identity, String credential, Properties overrides) {
        this.id = type;
        this.name = name;
        this.identity = identity;
        this.credential = credential;
        this.overrides = overrides;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Properties getOverrides() {
        return overrides;
    }

    public void setOverrides(Properties overrides) {
        this.overrides = overrides;
    }

    public Iaas getIaas() throws UnknownIaasProviderException {
        if(id.equalsIgnoreCase("ec2")) {
            return new EC2Iaas(id, identity, credential, overrides);
        } else {
            throw new UnknownIaasProviderException("iaas provider id : " + id);
        }
    }

    @Override
    public String toString() {
        return String.format("IaasProvider id[%s] name[%s] identity[%s] credential[%s]", id, name, identity, credential);
    }

}
