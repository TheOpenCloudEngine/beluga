package org.opencloudengine.cloud;

public class CloudInstance {

	public final static String INSTANCE_RUNNING  = "RUNNING";
	public final static String INSTANCE_STARTING = "STARTING";
	public final static String INSTANCE_STOPPED  = "STOPPED";
	
	String id;
	String state;
	String groupId;
	
	String location;
	String publicDnsName;
	String privateIpAddress;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getPublicDnsName() {
		return publicDnsName;
	}
	public void setPublicDnsName(String publicDnsName) {
		this.publicDnsName = publicDnsName;
	}
	
	public String getPrivateIpAddress() {
		return privateIpAddress;
	}
	public void setPrivateIpAddress(String privateIpAddress) {
		this.privateIpAddress = privateIpAddress;
	}
	
	public boolean isRunning(){
		return INSTANCE_RUNNING.equals(this.getState()); 
	}
	
	@Override
	public String toString() {
		return "CloudInstance [id=" + id + ", state=" + state + ", groupId="
				+ groupId + ", publicDnsName=" + publicDnsName
				+ ", privateIpAddress=" + privateIpAddress + "]";
	}
}