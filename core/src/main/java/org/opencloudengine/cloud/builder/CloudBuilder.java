package org.opencloudengine.cloud.builder;

import org.opencloudengine.cloud.CloudInstance;

import java.util.List;
import java.util.Properties;

public abstract class CloudBuilder {
	
	Properties options = null;
	
	String endpoint = null;
	String keypair = null;
	String accessKey = null;
	String secretKey = null;
	String locationId = null;
	String imageId = null;
	String hardwareId = null;

	public void config(Properties options){
		this.options = options;
		
		endpoint = options.getProperty("endpoint");
		keypair = options.getProperty("keypair");
		accessKey = options.getProperty("accessKey");
		secretKey = options.getProperty("secretKey");
		locationId = this.options.getProperty("locationId");
		imageId = this.options.getProperty("imageId");
		hardwareId = this.options.getProperty("hardwareId");
	}
	
	public abstract List<CloudInstance> getRunningInstances();
	public abstract List<CloudInstance> getRunningInstances(String groupId);
	public abstract CloudInstance getRunningInstance(String instanceId);

	public abstract List<CloudInstance> launchInstances(int N, String group);
	
	public abstract void teminateInstance(CloudInstance instance);
	public abstract void teminateInstance(String instanceId);
	
	protected String extractGropuId(String name){
		String groupId = "";
		
		if(name == null)
			return groupId;
		
		int pos = name.lastIndexOf("-");
		if(pos > -1)
			groupId = name.substring(0, pos);
		
		return groupId;
	}
}
