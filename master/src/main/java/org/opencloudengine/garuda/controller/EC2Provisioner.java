//package org.opencloudengine.garuda.controller;
//
//import com.amazonaws.AmazonServiceException;
//import com.amazonaws.auth.AWSCredentials;
//import com.amazonaws.auth.PropertiesCredentials;
//import com.amazonaws.services.ec2.AmazonEC2Client;
//import com.amazonaws.services.ec2.model.*;
//import org.apache.commons.io.FileUtils;
//import org.apache.log4j.spi.LoggerFactory;
//import org.opencloudengine.garuda.env.Environment;
//import org.opencloudengine.garuda.env.SettingManager;
//import org.slf4j.Logger;
//
//import java.io.*;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author Sang Wook, Song
// */
//public class EC2Provisioner {
//
//    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(EC2Provisioner.class);
//    private AmazonEC2Client amazonEC2Client;
//    private String keyName;
//
//    public EC2Provisioner(File credentialsFile, String keyName, String region) throws IOException {
//        AWSCredentials credentials = new PropertiesCredentials(credentialsFile);
//        amazonEC2Client = new AmazonEC2Client(credentials);
//        amazonEC2Client.setEndpoint("ec2." + region + ".amazonaws.com");
//        prepareKeyPair(keyName);
//    }
//
//    /*
//    * keyPair가 존재하지 않으면 생성한다.
//    */
//    private void prepareKeyPair(String keyName) {
//        if(!isKeyPairExists(keyName)) {
//            createKeyPair(keyName);
//        }
//        this.keyName = keyName;
//    }
//
//    /*
//    * KepPair가 존재하는지 확인한다.
//    * */
//    protected boolean isKeyPairExists(String keyName) {
//        List<String> keyNames = new ArrayList<String>();
//        keyNames.add(keyName);
//        DescribeKeyPairsRequest describeKeyPairsRequest = new DescribeKeyPairsRequest();
//        describeKeyPairsRequest.setKeyNames(keyNames);
//        try {
//            DescribeKeyPairsResult describeKeyPairsResult = amazonEC2Client.describeKeyPairs(describeKeyPairsRequest);
//            return true;
//        }catch(AmazonServiceException e) {
//            if("InvalidKeyPair.NotFound".equalsIgnoreCase(e.getErrorCode())) {
//                return false;
//            }
//            throw e;
//        }
//    }
//
//    /*
//    * KepPair를 생성한다.
//    * */
//    private void createKeyPair(String keyName) {
//        CreateKeyPairRequest createKeyPairRequest = new CreateKeyPairRequest();
//        createKeyPairRequest.withKeyName(keyName);
//        CreateKeyPairResult createKeyPairResult = amazonEC2Client.createKeyPair(createKeyPairRequest);
//
//        KeyPair keyPair = createKeyPairResult.getKeyPair();
//
//        logger.debug("create keyPair > {}", keyPair.getKeyMaterial());
//
//        Environment env = SettingManager.getInstance().getEnvironment();
//
//        //garuda home / conf 에 저장한다.
//        File f = new File(new File(env.homeFile(), "conf"), keyName + ".pem");
//        if(f.exists()) {
//            FileUtils.deleteQuietly(f);
//        }
//
//        Writer writer = null;
//        try {
//            writer = new OutputStreamWriter(new FileOutputStream(f));
//            writer.write(keyPair.getKeyMaterial());
//            logger.info("PEM file stored at {}", f.getAbsolutePath());
//        } catch (IOException e) {
//            logger.error("", e);
//        } finally {
//            try {
//                if(writer != null) {
//                    writer.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public void launceInstances(String imageId, InstanceType instanceType, int count, String[] securityGroups) {
//        RunInstancesRequest runInstancesRequest = new RunInstancesRequest();
//
//        runInstancesRequest.withImageId(imageId)
//                .withInstanceType(instanceType)
//                .withMinCount(count)
//                .withMaxCount(count)
//                .withKeyName(keyName)
//                .withSecurityGroups(securityGroups);
//
//        RunInstancesResult runInstancesResult = amazonEC2Client.runInstances(runInstancesRequest);
//        Reservation reservation = runInstancesResult.getReservation();
//        List<Instance> instanceList = reservation.getInstances();
//        //TODO instanceState를 주기적으로 받아온다.
////        for(int i =0 ;i < 10000 ; i++) {
////            for (Instance instance : instanceList) {
////
////                InstanceState state = instance.getState();
////                int stateCode = state.getCode();
////                logger.debug("stateCode : {}", stateCode);
////
////            }
////            try {
////                Thread.sleep(1000);
////            } catch (InterruptedException e) {
////            }
////        }
//        logger.debug("runInstancesResult > {}", runInstancesResult);
//
//    }
//}
