package org.opencloudengine.cloud.builder.templete.ucloud;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class VirtualMachine {

	String id;
	String name;
	@XmlElement(name="displayname")
	String displayName;
	@XmlElement(name="domainid")
	String domainId;
	String domain;
	String state;
	@XmlElement(name="zoneid")
	String zoneId;
	@XmlElement(name="zonename")
	String zoneName;
	Nic nic;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getDomainId() {
		return domainId;
	}
	public void setDomainId(String domainId) {
		this.domainId = domainId;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZoneId() {
		return zoneId;
	}
	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}
	public String getZoneName() {
		return zoneName;
	}
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}
	public Nic getNic() {
		return nic;
	}
	public void setNic(Nic nic) {
		this.nic = nic;
	}
	
	@Override
	public String toString() {
		return "VirtualMachine [id=" + id + ", name=" + name + ", displayName="
				+ displayName + ", domainId=" + domainId + ", domain=" + domain
				+ ", state=" + state + ", zoneId=" + zoneId + ", zoneName="
				+ zoneName + ", nic=" + nic + "]";
	}
}
