package org.opencloudengine.garuda.cloud;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.Template;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by swsong on 2015. 7. 15..
 */
public class IaasProvider {
    private String type;
    private String name;
    private String image;
    private String identity;
    private String credential;

    private transient ComputeService computeService;
    private transient Template template;

    public IaasProvider(String type, String name, String image, String identity, String credential) {
        this.type = type;
        this.name = name;
        this.image = image;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public ComputeService getComputeService() {
        return computeService;
    }


    public Template getTemplate() {
        return template;
    }

}
