package org.opencloudengine.garuda.controller;

import com.amazonaws.services.ec2.model.InstanceType;
import org.junit.Before;
import org.junit.Test;
import org.opencloudengine.garuda.env.Environment;

import java.io.File;
import java.io.IOException;

/**
 * @author Sang Wook, Song
 */
public class EC2ProvisionerTest {

    File credentialsFile = new File("/Users/swsong/Dropbox/System/auth/AwsCredentials.properties");
    String keyName = "aws-garuda";
    String region = "ap-northeast-1";

    @Before
    public void init(){
        String serverHome = "/Users/swsong/TEST_HOME/garuda";
        Environment environment = new Environment(serverHome).init();
    }

    @Test
    public void testRun() throws IOException {
        EC2Provisioner ec2Provisioner =  new EC2Provisioner(credentialsFile, keyName, region);
        String imageId = "ami-936d9d93";  //ubuntu 14.04 HVM
        InstanceType instanceType = InstanceType.T2Small;
        ec2Provisioner.launceInstances(imageId, instanceType, 1, new String[]{"default"});
    }

    @Test
    public void testKeyExists() throws IOException {
        String keyName = "test";
        EC2Provisioner ec2Provisioner =  new EC2Provisioner(credentialsFile, keyName, region);
        boolean isExists = ec2Provisioner.isKeyPairExists(keyName);
        System.out.println("keyName = " + keyName);
        System.out.println("isExists = " + isExists);
    }
}
