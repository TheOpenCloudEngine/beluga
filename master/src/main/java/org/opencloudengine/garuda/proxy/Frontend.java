package org.opencloudengine.garuda.proxy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by swsong on 2015. 8. 10..
 */
public class Frontend {
    private String name;
    private String ip;
    private int port;
    private String mode;
    private List<ACL> aclList;
    private String defaultBackend;

    public Frontend(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public Frontend withIp(String ip) {
        this.ip = ip;
        return this;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public Frontend withPort(int port) {
        this.port = port;
        return this;
    }
    public void setPort(int port) {
        this.port = port;
    }

    public String getMode() {
        return mode;
    }

    public Frontend withMode(String mode) {
        this.mode = mode;
        return this;
    }
    public void setMode(String mode) {
        this.mode = mode;
    }

    public List<ACL> getAclList() {
        return aclList;
    }

    public Frontend withAcl(ACL acl) {
        if(aclList == null) {
            aclList = new ArrayList<>();
        }
        aclList.add(acl);
        return this;
    }

    public void setAclList(List<ACL> aclList) {
        this.aclList = aclList;
    }

    public String getDefaultBackend() {
        return defaultBackend;
    }

    public Frontend withDefaultBackend(String defaultBackend) {
        this.defaultBackend = defaultBackend;
        return this;
    }

    public void setDefaultBackend(String defaultBackend) {
        this.defaultBackend = defaultBackend;
    }

    public static class ACL {
        private String name;
        private String criterion;
        private String value;
        private String backendName;

        public ACL(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCriterion() {
            return criterion;
        }

        public ACL withCriterion(String criterion) {
            this.criterion = criterion;
            return this;
        }

        public void setCriterion(String criterion) {
            this.criterion = criterion;
        }

        public String getValue() {
            return value;
        }

        public ACL withValue(String value) {
            this.value = value;
            return this;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getBackendName() {
            return backendName;
        }

        public ACL withBackendName(String backendName) {
            this.backendName = backendName;
            return this;
        }

        public void setBackendName(String backendName) {
            this.backendName = backendName;
        }
    }
}
