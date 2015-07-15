package org.opencloudengine.cloud.builder.templete.ucloud;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Nic {

	String id;
	@XmlElement(name="networkid")
	String networkId;
	String netmask;
	String gateway;
	@XmlElement(name="ipaddress")
	String ipAddress;
	@XmlElement(name="traffictype")
	String trafficType;
	String type;
	@XmlElement(name="isdefault")
	boolean isDefault;
	@XmlElement(name="macaddress")
	String macAddress;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNetworkId() {
		return networkId;
	}
	public void setNetworkId(String networkId) {
		this.networkId = networkId;
	}
	public String getNetmask() {
		return netmask;
	}
	public void setNetmask(String netmask) {
		this.netmask = netmask;
	}
	public String getGateway() {
		return gateway;
	}
	public void setGateway(String gateway) {
		this.gateway = gateway;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getTrafficType() {
		return trafficType;
	}
	public void setTrafficType(String trafficType) {
		this.trafficType = trafficType;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isDefault() {
		return isDefault;
	}
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	
	@Override
	public String toString() {
		return "Nic [id=" + id + ", networkId=" + networkId + ", netmask="
				+ netmask + ", gateway=" + gateway + ", ipAddress=" + ipAddress
				+ ", trafficType=" + trafficType + ", type=" + type
				+ ", isDefault=" + isDefault + ", macAddress=" + macAddress
				+ "]";
	}
}
