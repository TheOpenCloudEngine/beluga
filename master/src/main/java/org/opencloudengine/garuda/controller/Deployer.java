package org.opencloudengine.garuda.controller;

import org.opencloudengine.garuda.utils.FileTransferUtil;
import org.opencloudengine.garuda.utils.SshUtil;

import java.util.Iterator;
import java.util.List;

/**
 * Created by soo on 2015. 6. 5..
 */
public class Deployer {

    public static final String INIT_PHP_PATH = "scripts/deploy/init_php_apache.sh";
    public static final String DEPLOY_PHP_PATH = "scripts/deploy/deploy_php_apache.sh";


    public void deployPHP(List<String> slaveIpList, String id, String passwd, String pemPath, String registryAddress) throws Exception {
        SshUtil sshUtil = new SshUtil();
        String command = null;

        Iterator iterator = slaveIpList.iterator();
        while (iterator.hasNext()) {
            String ip = (String) iterator.next();
            FileTransferUtil.send("docker/php5_apache2", id, passwd, ip, "/tmp", "Dockerfile", pemPath);
            FileTransferUtil.send(INIT_PHP_PATH, id, passwd, ip, "/tmp", "init_php_apache.sh", pemPath);
            FileTransferUtil.send(DEPLOY_PHP_PATH, id, passwd, ip, "/tmp", "deploy_php_apache.sh", pemPath);

            sshUtil.sessionLogin(ip, id, passwd, pemPath);

            command = "chmod 755 /tmp/init_php_apache.sh";
            sshUtil.runCommand(command);

            command = "chmod 755 /tmp/deploy_php_apache.sh";
            sshUtil.runCommand(command);

            command = String.format("sh /tmp/init_php_apache.sh %s %s", registryAddress, "test-php-apache");
            sshUtil.runCommand(command);

            command = String.format("sh /tmp/deploy_php_apache.sh %s %s %s %s %s", "/home", "test.zip", "final-php-apache", registryAddress, "test-php-apache");
            sshUtil.runCommand(command);

        }

        runPHP();
    }

    public void runPHP(){
        System.out.println("run PHP");
    }
}
