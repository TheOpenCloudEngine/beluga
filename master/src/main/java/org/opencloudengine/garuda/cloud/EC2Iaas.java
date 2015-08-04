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
    private final static String DEVICE_NAME = "/dev/sda1";
    private final static int RUNNING_STATE = 16;

    public EC2IaaS(String endPoint, String accessKey, String secretKey, Properties overrides) {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        client = new AmazonEC2Client(credentials);
        client.setEndpoint(endPoint);
    }

    @Override
    public List<CommonInstance> launchInstance(InstanceRequest request, String name, int scale) {
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
            if(name != null) {
                //tag request전에 1초정도 대기.
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignore) {
                }

                int idx = 1;
                for (Instance instance : runInstancesResult.getReservation().getInstances()) {
                    CreateTagsRequest createTagsRequest = new CreateTagsRequest();
                    String tagName = name;
                    if (scale > 1) {
                        tagName = String.format("%s-%d", name, idx);
                    }

                    createTagsRequest.withResources(instance.getInstanceId()).withTags(new Tag("Name", tagName));
                    client.createTags(createTagsRequest);
                    newInstances.add(new CommonInstance(instance));
                    idx++;
                }
            }
        }

        return newInstances;
    }

    @Override
    public List<CommonInstance> getRunningInstances(Collection<String> instanceList) {
        List<CommonInstance> list = new ArrayList<>();
        DescribeInstancesRequest request = new DescribeInstancesRequest().withInstanceIds(instanceList);
        DescribeInstancesResult result = client.describeInstances(request);
        List<Reservation> reservationList = result.getReservations();
       for(Reservation r : reservationList) {
           for(Instance i : r.getInstances()) {
               list.add(new CommonInstance(i));
           }
       }

        return list;
    }


    @Override
    public void waitUntilInstancesReady(Collection<CommonInstance> instanceList) {
        int size = instanceList.size();
        BitSet statusSet = new BitSet(size);

        List<String> idList = new ArrayList<>();
        for(CommonInstance i : instanceList) {
            idList.add(i.getInstanceId());
        }

        while(statusSet.cardinality() < size) {
            DescribeInstanceStatusRequest request = new DescribeInstanceStatusRequest().withInstanceIds(idList);
            DescribeInstanceStatusResult result = client.describeInstanceStatus(request);
            List<InstanceStatus> list = result.getInstanceStatuses();

            for (int i = 0; i < list.size(); i++) {
                InstanceState state = list.get(i).getInstanceState();
                //완료되지 않았고, 실행중으로 변했다면.
                if (!statusSet.get(i) && state.getCode() == RUNNING_STATE) {
                    statusSet.set(i);
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {
            }
        }
    }

    @Override
    public void terminateInstance(String id) {
        List<String> instanceIdList = new ArrayList<String>();
        instanceIdList.add(id);
        terminateInstanceList(instanceIdList);
    }

    @Override
    public void terminateInstanceList(Collection<String> instanceIdList) {
        TerminateInstancesRequest terminateInstancesRequest = new TerminateInstancesRequest();
        terminateInstancesRequest.setInstanceIds(instanceIdList);
        client.terminateInstances(terminateInstancesRequest);
    }

    @Override
    public void terminateInstances(Collection<CommonInstance> list) {
        List<String> instanceIdList = new ArrayList<String>();
        for(CommonInstance instance : list) {
            instanceIdList.add(instance.getInstanceId());
        }
        terminateInstanceList(instanceIdList);
    }

    @Override
    public String provider() {
        return IaasProvider.EC2_TYPE;
    }

    @Override
    public void close() {
        if(client != null) {

        }
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
