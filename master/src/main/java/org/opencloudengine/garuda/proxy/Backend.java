package org.opencloudengine.garuda.proxy;

import java.util.List;

/**
 * Created by swsong on 2015. 8. 10..
 */
public class Backend {
    private String name;
    private String mode;
    private String balance;
    private List<Server> serverList;

    public Backend(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMode() {
        return mode;
    }

    public Backend withMode(String mode) {
        this.mode = mode;
        return this;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getBalance() {
        return balance;
    }

    public Backend withBalance(String balance) {
        this.balance = balance;
        return this;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public List<Server> getServerList() {
        return serverList;
    }

    public Backend withServerList(List<Server> serverList) {
        this.serverList = serverList;
        return this;
    }

    public void setServerList(List<Server> serverList) {
        this.serverList = serverList;
    }

    public static class Server {
        private String name;
        private String ip;
        private int port;

        public Server(String name) {
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

        public Server withIp(String ip) {
            this.ip = ip;
            return this;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public Server withPort(int port) {
            this.port = port;
            return this;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }


}
