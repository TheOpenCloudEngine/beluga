package org.opencloudengine.cloud.builder.templete.ucloud;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "deployvirtualmachineresponse")
public class DeployVirtualMachineResponse {

	String id;
	String jobId;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@XmlElement(name = "jobid")  
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	@Override
	public String toString() {
		return "DeployVirtualMachineResponse [id=" + id + ", jobId=" + jobId
				+ "]";
	}
}