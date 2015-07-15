package org.opencloudengine.cloud.builder.templete.ucloud;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD) 
@XmlRootElement(name="listvirtualmachinesresponse")
public class ListVirtualMachinesResponse {
	@XmlElement(name="virtualmachine")
	List<VirtualMachine> virtualMachine;
	int count;
	
	public List<VirtualMachine> getVirtualMachine() {
		return virtualMachine;
	}
	public void setVirtualMachine(List<VirtualMachine> virtualMachine) {
		this.virtualMachine = virtualMachine;
	}
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	@Override
	public String toString() {
		return "ListVirtualMachinesResponse [virtualMachine=" + virtualMachine
				+ ", count=" + count + "]";
	}
}