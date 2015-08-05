package org.opencloudengine.garuda.cloud;

import org.opencloudengine.garuda.exception.UnknownIaasProviderException;

import java.util.Properties;

/**
 * Created by swsong on 2015. 7. 15..
 */
public class IaasProvider {
    public static final String EC2_TYPE = "EC2";
    public static final String OPENSTACK_TYPE = "OPENSTACK";

    private String type;
    private String name;
    private String identity;
    private String credential;
    private String endPoint;
    private Properties overrides;

    public IaasProvider(String type, String name, String identity, String credential, String endPoint) {
        this(type, name, identity, credential, endPoint, new Properties());
    }
    public IaasProvider(String type, String name, String identity, String credential, String endPoint, Properties overrides) {
        this.type = type;
        this.name = name;
        this.identity = identity;
        this.credential = credential;
        this.endPoint = endPoint;
        this.overrides = overrides;
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

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public Properties getOverrides() {
        return overrides;
    }

    public void setOverrides(Properties overrides) {
        this.overrides = overrides;
    }

    public IaaS getIaas() throws UnknownIaasProviderException {
        if(type.equalsIgnoreCase(EC2_TYPE)) {
            return new EC2IaaS(endPoint, identity, credential, overrides);
        } else if(type.equalsIgnoreCase(OPENSTACK_TYPE)) {
                return new OpenstackIaaS(endPoint, identity, credential, overrides);
        } else {
            throw new UnknownIaasProviderException("iaas provider type : " + type);
        }
    }

    @Override
    public String toString() {
        return String.format("IaasProvider type[%s] name[%s] endPoint[%s]", type, name, endPoint);
    }

}
