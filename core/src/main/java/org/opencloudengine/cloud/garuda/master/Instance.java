package org.opencloudengine.cloud.garuda.master;

import java.io.Serializable;
import java.util.Date;

public class Instance implements Serializable
{

    public Instance()
    {
    }

    public String getInstanceId()
    {
        return instanceId;
    }

    public void setInstanceId(String instanceId)
    {
        this.instanceId = instanceId;
    }

    public Instance withInstanceId(String instanceId)
    {
        this.instanceId = instanceId;
        return this;
    }

    public String getImageId()
    {
        return imageId;
    }

    public void setImageId(String imageId)
    {
        this.imageId = imageId;
    }

    public Instance withImageId(String imageId)
    {
        this.imageId = imageId;
        return this;
    }

    public String getPrivateDnsName()
    {
        return privateDnsName;
    }

    public void setPrivateDnsName(String privateDnsName)
    {
        this.privateDnsName = privateDnsName;
    }

    public Instance withPrivateDnsName(String privateDnsName)
    {
        this.privateDnsName = privateDnsName;
        return this;
    }

    public String getPublicDnsName()
    {
        return publicDnsName;
    }

    public void setPublicDnsName(String publicDnsName)
    {
        this.publicDnsName = publicDnsName;
    }

    public Instance withPublicDnsName(String publicDnsName)
    {
        this.publicDnsName = publicDnsName;
        return this;
    }

    public String getStateTransitionReason()
    {
        return stateTransitionReason;
    }

    public void setStateTransitionReason(String stateTransitionReason)
    {
        this.stateTransitionReason = stateTransitionReason;
    }

    public Instance withStateTransitionReason(String stateTransitionReason)
    {
        this.stateTransitionReason = stateTransitionReason;
        return this;
    }

    public String getKeyName()
    {
        return keyName;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getState()
    {
        return state;
    }

    public void setKeyName(String keyName)
    {
        this.keyName = keyName;
    }

    public Instance withKeyName(String keyName)
    {
        this.keyName = keyName;
        return this;
    }

    public Integer getAmiLaunchIndex()
    {
        return amiLaunchIndex;
    }

    public void setAmiLaunchIndex(Integer amiLaunchIndex)
    {
        this.amiLaunchIndex = amiLaunchIndex;
    }

    public Instance withAmiLaunchIndex(Integer amiLaunchIndex)
    {
        this.amiLaunchIndex = amiLaunchIndex;
        return this;
    }

    public String getInstanceType()
    {
        return instanceType;
    }

    public void setInstanceType(String instanceType)
    {
        this.instanceType = instanceType;
    }

    public Instance withInstanceType(String instanceType)
    {
        this.instanceType = instanceType;
        return this;
    }

    public Date getLaunchTime()
    {
        return launchTime;
    }

    public void setLaunchTime(Date launchTime)
    {
        this.launchTime = launchTime;
    }

    public Instance withLaunchTime(Date launchTime)
    {
        this.launchTime = launchTime;
        return this;
    }

    public String getKernelId()
    {
        return kernelId;
    }

    public void setKernelId(String kernelId)
    {
        this.kernelId = kernelId;
    }

    public Instance withKernelId(String kernelId)
    {
        this.kernelId = kernelId;
        return this;
    }

    public String getRamdiskId()
    {
        return ramdiskId;
    }

    public void setRamdiskId(String ramdiskId)
    {
        this.ramdiskId = ramdiskId;
    }

    public Instance withRamdiskId(String ramdiskId)
    {
        this.ramdiskId = ramdiskId;
        return this;
    }

    public String getPlatform()
    {
        return platform;
    }

    public void setPlatform(String platform)
    {
        this.platform = platform;
    }

    public Instance withPlatform(String platform)
    {
        this.platform = platform;
        return this;
    }

    public String getSubnetId()
    {
        return subnetId;
    }

    public void setSubnetId(String subnetId)
    {
        this.subnetId = subnetId;
    }

    public Instance withSubnetId(String subnetId)
    {
        this.subnetId = subnetId;
        return this;
    }

    public String getVpcId()
    {
        return vpcId;
    }

    public void setVpcId(String vpcId)
    {
        this.vpcId = vpcId;
    }

    public Instance withVpcId(String vpcId)
    {
        this.vpcId = vpcId;
        return this;
    }

    public String getPrivateIpAddress()
    {
        return privateIpAddress;
    }

    public void setPrivateIpAddress(String privateIpAddress)
    {
        this.privateIpAddress = privateIpAddress;
    }

    public Instance withPrivateIpAddress(String privateIpAddress)
    {
        this.privateIpAddress = privateIpAddress;
        return this;
    }

    public String getPublicIpAddress()
    {
        return publicIpAddress;
    }

    public void setPublicIpAddress(String publicIpAddress)
    {
        this.publicIpAddress = publicIpAddress;
    }

    public Instance withPublicIpAddress(String publicIpAddress)
    {
        this.publicIpAddress = publicIpAddress;
        return this;
    }

    public String getArchitecture()
    {
        return architecture;
    }

    public void setArchitecture(String architecture)
    {
        this.architecture = architecture;
    }

    public Instance withArchitecture(String architecture)
    {
        this.architecture = architecture;
        return this;
    }

    public String getVirtualizationType()
    {
        return virtualizationType;
    }

    public void setVirtualizationType(String virtualizationType)
    {
        this.virtualizationType = virtualizationType;
    }

    public Instance withVirtualizationType(String virtualizationType)
    {
        this.virtualizationType = virtualizationType;
        return this;
    }

    public String getSpotInstanceRequestId()
    {
        return spotInstanceRequestId;
    }

    public void setSpotInstanceRequestId(String spotInstanceRequestId)
    {
        this.spotInstanceRequestId = spotInstanceRequestId;
    }

    public Instance withSpotInstanceRequestId(String spotInstanceRequestId)
    {
        this.spotInstanceRequestId = spotInstanceRequestId;
        return this;
    }

    public String getClientToken()
    {
        return clientToken;
    }

    public void setClientToken(String clientToken)
    {
        this.clientToken = clientToken;
    }

    public Instance withClientToken(String clientToken)
    {
        this.clientToken = clientToken;
        return this;
    }

    public Boolean isSourceDestCheck()
    {
        return sourceDestCheck;
    }

    public void setSourceDestCheck(Boolean sourceDestCheck)
    {
        this.sourceDestCheck = sourceDestCheck;
    }

    public Instance withSourceDestCheck(Boolean sourceDestCheck)
    {
        this.sourceDestCheck = sourceDestCheck;
        return this;
    }

    public Boolean getSourceDestCheck()
    {
        return sourceDestCheck;
    }

    public String getHypervisor()
    {
        return hypervisor;
    }

    public void setHypervisor(String hypervisor)
    {
        this.hypervisor = hypervisor;
    }

    public Instance withHypervisor(String hypervisor)
    {
        this.hypervisor = hypervisor;
        return this;
    }

    public Boolean isEbsOptimized()
    {
        return ebsOptimized;
    }

    public void setEbsOptimized(Boolean ebsOptimized)
    {
        this.ebsOptimized = ebsOptimized;
    }

    public Instance withEbsOptimized(Boolean ebsOptimized)
    {
        this.ebsOptimized = ebsOptimized;
        return this;
    }

    public Boolean getEbsOptimized()
    {
        return ebsOptimized;
    }

    public String getSriovNetSupport()
    {
        return sriovNetSupport;
    }

    public void setSriovNetSupport(String sriovNetSupport)
    {
        this.sriovNetSupport = sriovNetSupport;
    }

    public Instance withSriovNetSupport(String sriovNetSupport)
    {
        this.sriovNetSupport = sriovNetSupport;
        return this;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if(getInstanceId() != null)
            sb.append((new StringBuilder("InstanceId: ")).append(getInstanceId()).append(",").toString());
        if(getImageId() != null)
            sb.append((new StringBuilder("ImageId: ")).append(getImageId()).append(",").toString());
        if(getPrivateDnsName() != null)
            sb.append((new StringBuilder("PrivateDnsName: ")).append(getPrivateDnsName()).append(",").toString());
        if(getPublicDnsName() != null)
            sb.append((new StringBuilder("PublicDnsName: ")).append(getPublicDnsName()).append(",").toString());
        if(getStateTransitionReason() != null)
            sb.append((new StringBuilder("StateTransitionReason: ")).append(getStateTransitionReason()).append(",").toString());
        if(getKeyName() != null)
            sb.append((new StringBuilder("KeyName: ")).append(getKeyName()).append(",").toString());
        if(getAmiLaunchIndex() != null)
            sb.append((new StringBuilder("AmiLaunchIndex: ")).append(getAmiLaunchIndex()).append(",").toString());
        if(getInstanceType() != null)
            sb.append((new StringBuilder("InstanceType: ")).append(getInstanceType()).append(",").toString());
        if(getLaunchTime() != null)
            sb.append((new StringBuilder("LaunchTime: ")).append(getLaunchTime()).append(",").toString());
        if(getKernelId() != null)
            sb.append((new StringBuilder("KernelId: ")).append(getKernelId()).append(",").toString());
        if(getRamdiskId() != null)
            sb.append((new StringBuilder("RamdiskId: ")).append(getRamdiskId()).append(",").toString());
        if(getPlatform() != null)
            sb.append((new StringBuilder("Platform: ")).append(getPlatform()).append(",").toString());
        if(getSubnetId() != null)
            sb.append((new StringBuilder("SubnetId: ")).append(getSubnetId()).append(",").toString());
        if(getVpcId() != null)
            sb.append((new StringBuilder("VpcId: ")).append(getVpcId()).append(",").toString());
        if(getPrivateIpAddress() != null)
            sb.append((new StringBuilder("PrivateIpAddress: ")).append(getPrivateIpAddress()).append(",").toString());
        if(getPublicIpAddress() != null)
            sb.append((new StringBuilder("PublicIpAddress: ")).append(getPublicIpAddress()).append(",").toString());
        if(getArchitecture() != null)
            sb.append((new StringBuilder("Architecture: ")).append(getArchitecture()).append(",").toString());
        if(getVirtualizationType() != null)
            sb.append((new StringBuilder("VirtualizationType: ")).append(getVirtualizationType()).append(",").toString());
        if(getSpotInstanceRequestId() != null)
            sb.append((new StringBuilder("SpotInstanceRequestId: ")).append(getSpotInstanceRequestId()).append(",").toString());
        if(getClientToken() != null)
            sb.append((new StringBuilder("ClientToken: ")).append(getClientToken()).append(",").toString());
        if(isSourceDestCheck() != null)
            sb.append((new StringBuilder("SourceDestCheck: ")).append(isSourceDestCheck()).append(",").toString());
        if(getHypervisor() != null)
            sb.append((new StringBuilder("Hypervisor: ")).append(getHypervisor()).append(",").toString());
        if(isEbsOptimized() != null)
            sb.append((new StringBuilder("EbsOptimized: ")).append(isEbsOptimized()).append(",").toString());
        if(getSriovNetSupport() != null)
            sb.append((new StringBuilder("SriovNetSupport: ")).append(getSriovNetSupport()).toString());
        sb.append("}");
        return sb.toString();
    }

    public int hashCode()
    {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (getInstanceId() != null ? getInstanceId().hashCode() : 0);
        hashCode = 31 * hashCode + (getImageId() != null ? getImageId().hashCode() : 0);
        hashCode = 31 * hashCode + (getPrivateDnsName() != null ? getPrivateDnsName().hashCode() : 0);
        hashCode = 31 * hashCode + (getPublicDnsName() != null ? getPublicDnsName().hashCode() : 0);
        hashCode = 31 * hashCode + (getStateTransitionReason() != null ? getStateTransitionReason().hashCode() : 0);
        hashCode = 31 * hashCode + (getKeyName() != null ? getKeyName().hashCode() : 0);
        hashCode = 31 * hashCode + (getAmiLaunchIndex() != null ? getAmiLaunchIndex().hashCode() : 0);
        hashCode = 31 * hashCode + (getInstanceType() != null ? getInstanceType().hashCode() : 0);
        hashCode = 31 * hashCode + (getLaunchTime() != null ? getLaunchTime().hashCode() : 0);
        hashCode = 31 * hashCode + (getKernelId() != null ? getKernelId().hashCode() : 0);
        hashCode = 31 * hashCode + (getRamdiskId() != null ? getRamdiskId().hashCode() : 0);
        hashCode = 31 * hashCode + (getPlatform() != null ? getPlatform().hashCode() : 0);
        hashCode = 31 * hashCode + (getSubnetId() != null ? getSubnetId().hashCode() : 0);
        hashCode = 31 * hashCode + (getVpcId() != null ? getVpcId().hashCode() : 0);
        hashCode = 31 * hashCode + (getPrivateIpAddress() != null ? getPrivateIpAddress().hashCode() : 0);
        hashCode = 31 * hashCode + (getPublicIpAddress() != null ? getPublicIpAddress().hashCode() : 0);
        hashCode = 31 * hashCode + (getArchitecture() != null ? getArchitecture().hashCode() : 0);
        hashCode = 31 * hashCode + (getVirtualizationType() != null ? getVirtualizationType().hashCode() : 0);
        hashCode = 31 * hashCode + (getSpotInstanceRequestId() != null ? getSpotInstanceRequestId().hashCode() : 0);
        hashCode = 31 * hashCode + (getClientToken() != null ? getClientToken().hashCode() : 0);
        hashCode = 31 * hashCode + (isSourceDestCheck() != null ? isSourceDestCheck().hashCode() : 0);
        hashCode = 31 * hashCode + (getHypervisor() != null ? getHypervisor().hashCode() : 0);
        hashCode = 31 * hashCode + (isEbsOptimized() != null ? isEbsOptimized().hashCode() : 0);
        hashCode = 31 * hashCode + (getSriovNetSupport() != null ? getSriovNetSupport().hashCode() : 0);
        return hashCode;
    }

    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(!(obj instanceof Instance))
            return false;
        Instance other = (Instance)obj;
        if((other.getInstanceId() == null) ^ (getInstanceId() == null))
            return false;
        if(other.getInstanceId() != null && !other.getInstanceId().equals(getInstanceId()))
            return false;
        if((other.getImageId() == null) ^ (getImageId() == null))
            return false;
        if(other.getImageId() != null && !other.getImageId().equals(getImageId()))
            return false;
        if((other.getPrivateDnsName() == null) ^ (getPrivateDnsName() == null))
            return false;
        if(other.getPrivateDnsName() != null && !other.getPrivateDnsName().equals(getPrivateDnsName()))
            return false;
        if((other.getPublicDnsName() == null) ^ (getPublicDnsName() == null))
            return false;
        if(other.getPublicDnsName() != null && !other.getPublicDnsName().equals(getPublicDnsName()))
            return false;
        if((other.getStateTransitionReason() == null) ^ (getStateTransitionReason() == null))
            return false;
        if(other.getStateTransitionReason() != null && !other.getStateTransitionReason().equals(getStateTransitionReason()))
            return false;
        if((other.getKeyName() == null) ^ (getKeyName() == null))
            return false;
        if(other.getKeyName() != null && !other.getKeyName().equals(getKeyName()))
            return false;
        if((other.getAmiLaunchIndex() == null) ^ (getAmiLaunchIndex() == null))
            return false;
        if(other.getAmiLaunchIndex() != null && !other.getAmiLaunchIndex().equals(getAmiLaunchIndex()))
            return false;
        if((other.getInstanceType() == null) ^ (getInstanceType() == null))
            return false;
        if(other.getInstanceType() != null && !other.getInstanceType().equals(getInstanceType()))
            return false;
        if((other.getLaunchTime() == null) ^ (getLaunchTime() == null))
            return false;
        if(other.getLaunchTime() != null && !other.getLaunchTime().equals(getLaunchTime()))
            return false;
        if((other.getKernelId() == null) ^ (getKernelId() == null))
            return false;
        if(other.getKernelId() != null && !other.getKernelId().equals(getKernelId()))
            return false;
        if((other.getRamdiskId() == null) ^ (getRamdiskId() == null))
            return false;
        if(other.getRamdiskId() != null && !other.getRamdiskId().equals(getRamdiskId()))
            return false;
        if((other.getPlatform() == null) ^ (getPlatform() == null))
            return false;
        if(other.getPlatform() != null && !other.getPlatform().equals(getPlatform()))
            return false;
        if((other.getSubnetId() == null) ^ (getSubnetId() == null))
            return false;
        if(other.getSubnetId() != null && !other.getSubnetId().equals(getSubnetId()))
            return false;
        if((other.getVpcId() == null) ^ (getVpcId() == null))
            return false;
        if(other.getVpcId() != null && !other.getVpcId().equals(getVpcId()))
            return false;
        if((other.getPrivateIpAddress() == null) ^ (getPrivateIpAddress() == null))
            return false;
        if(other.getPrivateIpAddress() != null && !other.getPrivateIpAddress().equals(getPrivateIpAddress()))
            return false;
        if((other.getPublicIpAddress() == null) ^ (getPublicIpAddress() == null))
            return false;
        if(other.getPublicIpAddress() != null && !other.getPublicIpAddress().equals(getPublicIpAddress()))
            return false;
        if((other.getArchitecture() == null) ^ (getArchitecture() == null))
            return false;
        if(other.getArchitecture() != null && !other.getArchitecture().equals(getArchitecture()))
            return false;
        if((other.getVirtualizationType() == null) ^ (getVirtualizationType() == null))
            return false;
        if(other.getVirtualizationType() != null && !other.getVirtualizationType().equals(getVirtualizationType()))
            return false;
        if((other.getSpotInstanceRequestId() == null) ^ (getSpotInstanceRequestId() == null))
            return false;
        if(other.getSpotInstanceRequestId() != null && !other.getSpotInstanceRequestId().equals(getSpotInstanceRequestId()))
            return false;
        if((other.getClientToken() == null) ^ (getClientToken() == null))
            return false;
        if(other.getClientToken() != null && !other.getClientToken().equals(getClientToken()))
            return false;
        if((other.isSourceDestCheck() == null) ^ (isSourceDestCheck() == null))
            return false;
        if(other.isSourceDestCheck() != null && !other.isSourceDestCheck().equals(isSourceDestCheck()))
            return false;
        if((other.getHypervisor() == null) ^ (getHypervisor() == null))
            return false;
        if(other.getHypervisor() != null && !other.getHypervisor().equals(getHypervisor()))
            return false;
        if((other.isEbsOptimized() == null) ^ (isEbsOptimized() == null))
            return false;
        if(other.isEbsOptimized() != null && !other.isEbsOptimized().equals(isEbsOptimized()))
            return false;
        if((other.getSriovNetSupport() == null) ^ (getSriovNetSupport() == null))
            return false;
        return other.getSriovNetSupport() == null || other.getSriovNetSupport().equals(getSriovNetSupport());
    }

    private String instanceId;
    private String imageId;
    private String state;
    private String privateDnsName;
    private String publicDnsName;
    private String stateTransitionReason;
    private String keyName;
    private Integer amiLaunchIndex;
    private String instanceType;
    private Date launchTime;
    private String kernelId;
    private String ramdiskId;
    private String platform;
    private String subnetId;
    private String vpcId;
    private String privateIpAddress;
    private String publicIpAddress;
    private String architecture;
    private String rootDeviceType;
    private String rootDeviceName;
    private String virtualizationType;
    private String instanceLifecycle;
    private String spotInstanceRequestId;
    private String clientToken;
    private Boolean sourceDestCheck;
    private String hypervisor;
    private Boolean ebsOptimized;
    private String sriovNetSupport;
}