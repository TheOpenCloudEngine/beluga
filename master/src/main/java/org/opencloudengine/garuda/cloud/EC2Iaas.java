package org.opencloudengine.garuda.cloud;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by swsong on 2015. 8. 3..
 */
public class EC2IaaS implements IaaS {

    private static final Logger logger = LoggerFactory.getLogger(EC2IaaS.class);

    private final AmazonEC2Client client;
    private final static String DEFAULT_ENDPOINT = "ec2.ap-northeast-1.amazonaws.com";
    private final static String DEVICE_NAME = "/dev/sda1";

    public EC2IaaS(String endPoint, String accessKey, String secretKey, Properties overrides) {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        client = new AmazonEC2Client(credentials);
        if(endPoint == null) {
            endPoint = DEFAULT_ENDPOINT;
        }
        client.setEndpoint(endPoint);
    }

    public void waitUntilInstancesReady(List<CommonInstance> instanceList) {
        int size = instanceList.size();
        BitSet status = new BitSet(size);

        while(status.cardinality() < size) {
            for (int i = 0; i < instanceList.size(); i++) {
                CommonInstance instance = instanceList.get(i);

                //완료되지 않았고, 실행중으로 변했다면.
                if (!status.get(i) && instance.as(Instance.class).getState().getCode() == 32) {
                    status.set(i);
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {
            }
        }

    }
    @Override
    public List<CommonInstance> launchInstance(InstanceRequest request, String tag, int scale) {
        List<CommonInstance> newInstances = new ArrayList<CommonInstance>();

        RunInstancesResult runInstancesResult = null;
        if (scale > 0) {
            RunInstancesRequest runRequest = new RunInstancesRequest();
            runRequest.setImageId(request.getImageId());
            runRequest.setInstanceType(request.getInstanceType());
            runRequest.setKeyName(request.getKeyPair());
            runRequest.setMaxCount(scale);
            runRequest.setMinCount(scale);
            runRequest.setSecurityGroups(request.getGroups());
            EbsBlockDevice ebs = new EbsBlockDevice().withVolumeSize(request.getVolumeSize()).withVolumeType(VolumeType.Gp2);
            BlockDeviceMapping m = new BlockDeviceMapping().withDeviceName(DEVICE_NAME).withEbs(ebs);
            List<BlockDeviceMapping> blockDeviceMappingList = new ArrayList<>();
            blockDeviceMappingList.add(m);
            runRequest.setBlockDeviceMappings(blockDeviceMappingList);
            runInstancesResult = client.runInstances(runRequest);
        }

        if (runInstancesResult != null) {


            if(tag != null) {
                //tag request전에 1초정도 대기.
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignore) {
                }

                int idx = 0;
                for (Instance instance : runInstancesResult.getReservation().getInstances()) {
                    CreateTagsRequest createTagsRequest = new CreateTagsRequest();
                    String name = tag;
                    if (idx > 0) {
                        name = String.format("%s-%d", tag, idx);
                    }

                    createTagsRequest.withResources(instance.getInstanceId()).withTags(new Tag("Name", name));
                    client.createTags(createTagsRequest);
                    newInstances.add(new CommonInstance(instance));
                    idx++;
                }
            }
        }

        return newInstances;
    }

    public void terminateInstances(Collection<CommonInstance> list) {
        List<String> instanceIdList = new ArrayList<String>();
        for(CommonInstance instance : list) {
            instanceIdList.add(instance.getInstanceId());
        }
        terminateInstanceList(instanceIdList);
    }
    @Override
    public void terminateInstance(String id) {
        List<String> instanceIdList = new ArrayList<String>();
        instanceIdList.add(id);
        terminateInstanceList(instanceIdList);
    }

    @Override
    public String provider() {
        return EC2_PROVIDER;
    }

    public void terminateInstanceList(Collection<String> instanceIdList) {
        TerminateInstancesRequest terminateInstancesRequest = new TerminateInstancesRequest();
        terminateInstancesRequest.setInstanceIds(instanceIdList);
        client.terminateInstances(terminateInstancesRequest);
    }


    public void createSecurityGroup(String groupName, String description)
            throws Exception {

        CreateSecurityGroupRequest group = new CreateSecurityGroupRequest();
        group.setGroupName(groupName);
        group.setDescription(description);

        client.createSecurityGroup(group);
    }

    public void deleteSecurityGroup(String groupName) {

        DeleteSecurityGroupRequest group = new DeleteSecurityGroupRequest();
        group.setGroupName(groupName);
        try {
            client.deleteSecurityGroup(group);
        } catch (Exception e) {
        }
    }
}
