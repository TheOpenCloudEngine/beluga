package org.opencloudengine.garuda.builder;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

/**
* Created by soo on 2015. 6. 4..
*/
public class EC2Interface {

    private final int maxInstances;
    private AmazonEC2 ec2Client = null;

    private Properties options = null;

    /**
     * @param AWSAccessKey
     *            AWS Access Key
     * @param AWSSecretKey
     *            AWS Secret Key
     * @param ec2EndPoint
     *            EC2 End Point
     * @param maxInstances
     *            Maximum amount of instances allowed to run
     */
    public EC2Interface(String AWSAccessKey, String AWSSecretKey,
                        String ec2EndPoint, int maxInstances) {

        this.maxInstances = maxInstances;
    }

    public EC2Interface(Properties options, int maxInstances) {
        this.options = options;
        this.maxInstances = maxInstances;
    }

    public AmazonEC2 getAmazonEC2Client() {
        return ec2Client;
    }

    /**
     *
     * @param port
     *            Port to open.
     * @param ip
     *            IP address from which to allow connections. Allow from all
     *            connections with null.
     * @param securityGroupName
     *            The name of the security group in which to open the port.
     */
    public void openPort(int port, String ip, String securityGroupName)
            throws Exception {

        AuthorizeSecurityGroupIngressRequest authRequest = new AuthorizeSecurityGroupIngressRequest();

        authRequest.setIpProtocol("tcp");
        authRequest.setGroupName(securityGroupName);
        authRequest.setFromPort(port);
        authRequest.setToPort(port);

        String cidr;
        if (ip == null
                || !ip.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
            cidr = "0.0.0.0/0";
        } else {
            cidr = ip + "/32";
        }
        authRequest.setCidrIp(cidr);

        ec2Client.authorizeSecurityGroupIngress(authRequest);
        System.out.println("Security group " + securityGroupName
                + " now accepting connections from " + cidr + " on port "
                + port);

    }

    public void createSecurityGroup(String securityGroupName, String description)
            throws Exception {

        CreateSecurityGroupRequest group = new CreateSecurityGroupRequest();
        group.setGroupName(securityGroupName);
        group.setDescription(description);

        ec2Client.createSecurityGroup(group);
        System.out.println("Created security group: " + securityGroupName);

    }

    public void deleteSecurityGroup(String securityGroupName) {

        DeleteSecurityGroupRequest group = new DeleteSecurityGroupRequest();
        group.setGroupName(securityGroupName);
        try {
            ec2Client.deleteSecurityGroup(group);
            System.out.println("Removed security group: " + securityGroupName);
        } catch (Exception e) {
        }
    }

    public void revokeRulesOnPort(String securityGroupName, int port) {

        // Get groups
        DescribeSecurityGroupsRequest group = new DescribeSecurityGroupsRequest();
        Vector<String> groups = new Vector<String>();
        groups.add(securityGroupName);
        group.setGroupNames(groups);
        DescribeSecurityGroupsResult result = ec2Client
                .describeSecurityGroups(group);
        for (SecurityGroup securityGroup : result.getSecurityGroups()) {
            for (IpPermission ipPermission : securityGroup.getIpPermissions()) {
                if (ipPermission.getToPort() == port) {
                    RevokeSecurityGroupIngressRequest request = new RevokeSecurityGroupIngressRequest();
                    // request.setIpPermissions(securityGroup.getIpPermissions());
                    request.setCidrIp(ipPermission.getIpRanges().get(0));
                    request.setFromPort(port);
                    request.setToPort(port);
                    request.setIpProtocol(ipPermission.getIpProtocol());
                    request.setGroupName(securityGroupName);
                    ec2Client.revokeSecurityGroupIngress(request);
                }
            }
        }
    }

    /**
     * Initializes the Amazon EC2 Client with the given AWS Access Key and AWS
     * Secret Key.
     *
     * @throws Exception
     *             if Amazon EC2 Client could not be initialized due to invalid
     *             credentials or other error.
     */
    public void initializeEC2Client() throws Exception {

        String credentialsString;
        credentialsString = "accessKey = "
                + options.getProperty("awsAccessKey") + "\n";
        credentialsString += "secretKey = "
                + options.getProperty("awsSecretKey") + "\n";
        InputStream is;

        is = new ByteArrayInputStream(credentialsString.getBytes());

        AWSCredentials credentials = new PropertiesCredentials(is);

        ec2Client = new AmazonEC2Client(credentials);
        ec2Client.setEndpoint(options.getProperty("ec2EndPoint"));
    }

    public Vector<Instance> launchEC2Instances(int N,
                                               EC2InstanceConfiguration ec2InstanceConfiguration) {
        Vector<String> securityGroups = new Vector<String>();
        securityGroups.add(ec2InstanceConfiguration.getSecurityGroup());
        return launchEC2Instances(N, ec2InstanceConfiguration.getEc2ImageId(),
                ec2InstanceConfiguration.getEc2InstanceType(),
                ec2InstanceConfiguration.getEc2KeyPairName(), securityGroups);
    }

    /**
     * Launches EC2 instances using the previously specified credentials.
     *
     * @param N
     *            The number of EC2 instances to launch
     */
    public Vector<Instance> launchEC2Instances(int N, String ec2ImageId,
                                               String ec2InstanceType, String ec2KeyPairName,
                                               Collection<String> securityGroups) {

        Vector<Instance> newInstances = new Vector<Instance>();

        // Check the upper limit of concurrent running instances
        Vector<Instance> runningInstances = getRunningInstances();
        if (maxInstances > 0 && N + runningInstances.size() > maxInstances) {
            N = maxInstances - runningInstances.size();
        }

        // Launch ec2 instances if needed
        RunInstancesResult runInstancesResult = null;
        if (N > 0) {
            RunInstancesRequest runRequest = new RunInstancesRequest();
            runRequest.setImageId(ec2ImageId);
            runRequest.setInstanceType(ec2InstanceType);
            runRequest.setKeyName(ec2KeyPairName);
            runRequest.setMaxCount(N);
            runRequest.setMinCount(N);
            runRequest.setSecurityGroups(securityGroups);

            runInstancesResult = ec2Client.runInstances(runRequest);
        }

        // Make new instances of InstanceObject and add those to the list of
        // instances
        if (runInstancesResult != null) {
            for (Instance i : runInstancesResult.getReservation()
                    .getInstances()) {
                newInstances.add(i);
            }
        }

        // Return the launched instances
        return newInstances;
    }

    /**
     * Terminates a previously started EC2 instance.
     *
     * @param instance
     *            The InstanceObject connected to the EC2 instance that will be
     *            stopped.
     */
    public void stopEC2Instance(Instance instance) {

        ArrayList<String> instanceIds = new ArrayList<String>();
        /*
         * for (InstanceObject i : instances) {
         * instanceIds.add(i.getInstanceId()); }
         */

        instanceIds.add(instance.getInstanceId());

        TerminateInstancesRequest terminateInstancesRequest = new TerminateInstancesRequest();
        terminateInstancesRequest.setInstanceIds(instanceIds);
        ec2Client.terminateInstances(terminateInstancesRequest);
    }

    /**
     * Creates a new keypair and generates a new ssh authentication key for that
     * keypair.
     *
     * @return The generated ssh authentication key.
     * @throws Exception
     *             if EC2 client was not initialized.
     */
    public String generateNewKey(String keyPairName) throws Exception {

        String sshAuthenticationKey;

        // ec2KeyPairName = "astoria-keypair";

        if (ec2Client == null) {
            throw new Exception("EC2 Client has not been initialized");
        }

        DescribeKeyPairsResult keyPairs = ec2Client.describeKeyPairs();

        for (KeyPairInfo keyPairInfo : keyPairs.getKeyPairs()) {
            if (keyPairInfo.getKeyName().equals(keyPairName)) {

                DeleteKeyPairRequest deleteKeyPairRequest = new DeleteKeyPairRequest();
                deleteKeyPairRequest.setKeyName(keyPairName);
                ec2Client.deleteKeyPair(deleteKeyPairRequest);
            }
        }

        CreateKeyPairRequest createKeyPairRequest = new CreateKeyPairRequest();
        createKeyPairRequest.setKeyName(keyPairName);

        CreateKeyPairResult createKeyPairResult = ec2Client
                .createKeyPair(createKeyPairRequest);
        KeyPair keyPair = createKeyPairResult.getKeyPair();

        sshAuthenticationKey = "KEYPAIR " + keyPairName + " "
                + keyPair.getKeyFingerprint() + "\n";
        sshAuthenticationKey += keyPair.getKeyMaterial();

        // logger.info("Successfully generated ssh authentication key");

        return sshAuthenticationKey;
    }

    public Instance getRunningInstance(String instanceId) {

        DescribeInstancesResult describeInstancesResult = ec2Client.describeInstances();

        for (Reservation reservation : describeInstancesResult.getReservations()) {
            for (Instance instance : reservation.getInstances()) {

                if (instance.getInstanceId().equals(instanceId)) {
                    return instance;
                }
            }
        }
        return null;
    }

    public Vector<Instance> getRunningInstances() {

        List<Reservation> reservations = getReservations();
        Vector<Instance> instances = new Vector<Instance>();
        for (Reservation reservation : reservations) {
            for (Instance instance : reservation.getInstances()) {

                String instanceState = instance.getState().getName();
                if (instanceState.equals("running") || instanceState.equals("pending")) {
                    instances.add(instance);
                }
            }
        }
        return instances;
    }

    public Vector<Instance> getRunningInstances(String securityGroupName) {
        Vector<Instance> instances = new Vector<Instance>();

        Vector<Instance> runningInstances = getRunningInstances();
        for (Instance runningInstance : runningInstances) {
            List<GroupIdentifier>securityGroups = runningInstance.getSecurityGroups();

            if(EC2Interface.existSecurityGroup(securityGroups, securityGroupName))
                instances.add(runningInstance);
        }
        return instances;
    }

    public static boolean existSecurityGroup(List<GroupIdentifier>securityGroups, String securityGroupName){
        for (GroupIdentifier securityGroup : securityGroups) {
            if(securityGroup.getGroupName().equals(securityGroupName))
                return true;
        }

        return false;
    }

    public List<InstanceStatus> getInstanceStatus(){
        List<InstanceStatus> describeInstancesStatus = ec2Client.describeInstanceStatus().getInstanceStatuses();

        return describeInstancesStatus;
    }

    public String getInstanceStatus(String instanceId) {

        List<InstanceStatus> describeInstancesStatus = ec2Client.describeInstanceStatus().getInstanceStatuses();

        for (InstanceStatus instanceStatus : describeInstancesStatus) {
            if(instanceStatus.getInstanceId().equals(instanceId)){
                InstanceStatusSummary instanceStatusSummary = instanceStatus.getInstanceStatus();
                InstanceStatusSummary systemStatusSummary = instanceStatus.getSystemStatus();

                return instanceStatusSummary.getStatus() + systemStatusSummary.getStatus();
            }
        }

        return null;
    }

    public String getInstancesStatus(Instance instance) {
        return getInstanceStatus(instance.getInstanceId());
    }

    public HashMap<String, InstanceState> getRunningInstanceStates() {

        HashMap<String, InstanceState> instanceStates = new HashMap<String, InstanceState>();

        DescribeInstancesResult describeInstancesResult = ec2Client.describeInstances();

        for (Reservation reservation : describeInstancesResult.getReservations()) {
            for (Instance instance : reservation.getInstances()) {
                instanceStates.put(instance.getInstanceId(), instance.getState());
            }
        }

        return instanceStates;
    }

    public List<Reservation> getReservations() {
        return ec2Client.describeInstances().getReservations();
    }


}
