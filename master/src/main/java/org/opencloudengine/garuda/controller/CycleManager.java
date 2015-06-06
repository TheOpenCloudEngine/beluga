package org.opencloudengine.garuda.controller;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

/**
 * Created by soo on 2015. 6. 5..
 */
public class CycleManager {

    public static final String PEM_PATH = "/Users/soo/soo.pem";
    public static final String ID = "ubuntu";
    public static final String PASSWD = "";
    public static final String REGISTRY_ADDRESS = "52.69.67.161:5000";

    Provisioner provisioner;

    Deployer deployer;

    List<String> slaveIpList;

    public CycleManager(){
        provisioner = new Provisioner();
        deployer = new Deployer();
        slaveIpList = new ArrayList<>();
        slaveIpList.add("52.69.67.167");
    }
    public void deployPHP(List<String> slaveIpList, String id, String passwd, String pemPath, String registryAddress) throws Exception {
        deployer.deployPHP(slaveIpList, id, passwd, pemPath, registryAddress);
    }

    public void provisionSlave(){
        provisioner.provisionSlave();
    }

    public static void main(String agrs[]) throws Exception {
        CycleManager cycleManager = new CycleManager();

//        cycleManager.provisionSlave();

        cycleManager.deployPHP(cycleManager.slaveIpList, ID, PASSWD, PEM_PATH, REGISTRY_ADDRESS);

    }
}
