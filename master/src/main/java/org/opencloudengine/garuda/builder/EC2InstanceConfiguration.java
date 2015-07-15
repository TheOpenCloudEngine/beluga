package org.opencloudengine.garuda.builder;

/**
 * Created by soo on 2015. 6. 4..
 */
public class EC2InstanceConfiguration {

    private String ec2InstanceType;
    private String ec2ImageId;
    private String ec2KeyPair;
    private String securityGroup;

    public EC2InstanceConfiguration() {

    }

    public EC2InstanceConfiguration(String ec2InstanceType, String ec2ImageId,
                                    String ec2KeyPair, String securityGroup) {
        this.ec2InstanceType = ec2InstanceType;
        this.ec2ImageId = ec2ImageId;
        this.ec2KeyPair = ec2KeyPair;
        this.securityGroup = securityGroup;
    }

    public String getEc2InstanceType() {
        return ec2InstanceType;
    }

    public void setEc2InstanceType(String ec2InstanceType) {
        this.ec2InstanceType = ec2InstanceType;
    }

    public String getEc2ImageId() {
        return ec2ImageId;
    }

    public void setEc2ImageId(String ec2ImageId) {
        this.ec2ImageId = ec2ImageId;
    }

    public String getEc2KeyPairName() {
        return ec2KeyPair;
    }

    public void setEc2KeyPair(String ec2KeyPair) {
        this.ec2KeyPair = ec2KeyPair;
    }

    public boolean isValid() {
        if (ec2InstanceType == null) {
            return false;
        }
        if (ec2ImageId == null) {
            return false;
        }
        if (ec2KeyPair == null) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String r = "";
        r += "ec2 image id: " + ec2ImageId + "\n";
        r += "ec2 instance type: " + ec2InstanceType + "\n";
        r += "ec2 key pair: " + ec2KeyPair;
        return r;
    }

    public void setSecurityGroup(String securityGroup) {
        this.securityGroup = securityGroup;
    }

    public String getSecurityGroup() {
        return securityGroup;
    }
}
