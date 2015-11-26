package org.opencloudengine.garuda.beluga.cloud;

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
public class EC2Iaas implements Iaas {

    private static final Logger logger = LoggerFactory.getLogger(EC2Iaas.class);

    private final AmazonEC2Client client;
    private final static String DEVICE_NAME = "/dev/sda1";

    private final static int PENDING_STATE = 0;
    private final static int RUNNING_STATE = 16;
    private final static int SHUTTING_DOWN_STATE = 32;
    private final static int TERMINATED_STATE = 48;
    private final static int STOPPING_STATE = 64;
    private final static int STOPPED_STATE = 80;

    public EC2Iaas(String endPoint, String accessKey, String secretKey, Properties overrides) {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        client = new AmazonEC2Client(credentials);
        client.setEndpoint(endPoint);
    }

    @Override
    public List<CommonInstance> launchInstance(InstanceRequest request, String name, int scale, int startIndex) {
        List<CommonInstance> newInstances = new ArrayList<CommonInstance>();

        String clusterId = request.getClusterId();
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

                for (Instance instance : runInstancesResult.getReservation().getInstances()) {
                    CreateTagsRequest createTagsRequest = new CreateTagsRequest();
                    String tagName = null;
                    if (startIndex > 1) {
                        tagName = String.format("%s/%s-%d", clusterId, name, startIndex);
                    } else {
                        tagName = String.format("%s/%s", clusterId, name);
                    }

                    createTagsRequest.withResources(instance.getInstanceId()).withTags(new Tag("Name", tagName));
                    client.createTags(createTagsRequest);
                    newInstances.add(new CommonInstance(instance));
                    startIndex++;
                }
            }
        }

        return newInstances;
    }

    @Override
    public void updateInstancesInfo(List<CommonInstance> instanceList) {
        List<String> idList = IaasUtils.getIdList(instanceList);
        DescribeInstancesRequest request = new DescribeInstancesRequest().withInstanceIds(idList);
        DescribeInstancesResult result = client.describeInstances(request);
        List<Reservation> reservationList = result.getReservations();
        for(Reservation r : reservationList) {
            for(Instance i : r.getInstances()) {
                for(CommonInstance ci : instanceList){
                    if(i.getInstanceId().equals(ci.getInstanceId())) {
                        //update
                        ci.update(i);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public List<CommonInstance> getInstances(Collection<String> instanceList) {

        Map<String, Instance> map = new HashMap<>();
        DescribeInstancesRequest request = new DescribeInstancesRequest().withInstanceIds(instanceList);
        DescribeInstancesResult result = client.describeInstances(request);
        List<Reservation> reservationList = result.getReservations();
        for (Reservation r : reservationList) {
            for (Instance i : r.getInstances()) {
                map.put(i.getInstanceId(), i);
            }
        }

        List<CommonInstance> list = new ArrayList<>();
        for(String id : instanceList) {
            Instance instance = map.get(id);
            if(instance != null) {
                list.add(new CommonInstance(instance));
            } else {
                list.add(new CommonInstance(id));
            }
        }
        return list;
    }

    @Override
    public void waitUntilInstancesRunning(Collection<CommonInstance> instanceList) {
        waitUntilInstancesState(instanceList, RUNNING_STATE);
    }

    @Override
    public void waitUntilInstancesStopped(Collection<CommonInstance> instanceList) {
        waitUntilInstancesState(instanceList, STOPPED_STATE);
    }

    private void waitUntilInstancesState(Collection<CommonInstance> instanceList, int stateCode) {
        int size = instanceList.size();
        BitSet statusSet = new BitSet(size);

        List<String> idList = new ArrayList<>();
        for(CommonInstance i : instanceList) {
            idList.add(i.getInstanceId());
        }

        while(true) {
            DescribeInstancesRequest request = new DescribeInstancesRequest().withInstanceIds(idList);
            DescribeInstancesResult result = client.describeInstances(request);
            List<Reservation> reservations = result.getReservations();
            int i = 0;
            for(Reservation reservation : reservations) {
                List<Instance> instances = reservation.getInstances();
                for(Instance instance : instances) {
                    InstanceState state = instance.getState();
                    logger.debug("Instance status [{}] {}/{}/{}", instance.getInstanceId(), state.getName(), state.getCode(), instance.getStateReason());
                    //완료되지 않았고, 실행중으로 변했다면.
                    if (!statusSet.get(i) && state.getCode() == stateCode) {
                        statusSet.set(i);
                    }
                    i++;
                }
            }

            if(statusSet.cardinality() < size) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignore) {
                }
            } else {
                break;
            }
        }
    }

    @Override
    public void terminateInstance(String id) {
        List<String> instanceIdList = new ArrayList<String>(1);
        instanceIdList.add(id);
        terminateInstances(instanceIdList);
    }

    @Override
    public void terminateInstances(Collection<String> instanceIdList) {
        TerminateInstancesRequest terminateInstancesRequest = new TerminateInstancesRequest();
        terminateInstancesRequest.setInstanceIds(instanceIdList);
        client.terminateInstances(terminateInstancesRequest);
    }

    @Override
    public void stopInstances(Collection<String> instanceIdList) {
        StopInstancesRequest stopInstancesRequest = new StopInstancesRequest();
        stopInstancesRequest.setInstanceIds(instanceIdList);
        client.stopInstances(stopInstancesRequest);
    }

    @Override
    public void startInstances(Collection<String> instanceIdList) {
        StartInstancesRequest startInstancesRequest = new StartInstancesRequest();
        startInstancesRequest.setInstanceIds(instanceIdList);
        client.startInstances(startInstancesRequest);
    }

    @Override
    public void rebootInstances(Collection<String> instanceIdList) {
        RebootInstancesRequest rebootInstancesRequest = new RebootInstancesRequest();
        rebootInstancesRequest.setInstanceIds(instanceIdList);
        client.rebootInstances(rebootInstancesRequest);
    }

    @Override
    public String provider() {
        return IaasProvider.EC2_TYPE;
    }

    @Override
    public void close() {
        //client 에서 close를 제공하지 않음.
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
