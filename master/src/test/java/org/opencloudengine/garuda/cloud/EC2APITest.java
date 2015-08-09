package org.opencloudengine.garuda.cloud;

import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by swsong on 2015. 8. 9..
 */
public class EC2APITest {

    private AmazonEC2Client ec2Client;

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
        List<String> groups = new ArrayList<String>();
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

    public static boolean existSecurityGroup(List<GroupIdentifier> securityGroups, String securityGroupName){
        for (GroupIdentifier securityGroup : securityGroups) {
            if(securityGroup.getGroupName().equals(securityGroupName))
                return true;
        }

        return false;
    }


}
