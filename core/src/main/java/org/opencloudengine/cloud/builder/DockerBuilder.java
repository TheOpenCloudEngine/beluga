package org.opencloudengine.cloud.builder;

import org.opencloudengine.cloud.CloudInstance;

public class DockerBuilder extends JCloudsBuilder {
	
	private final static String PROVIDER = "docker";
	
	protected String getProvider(){
		return PROVIDER;
	}

	@Override
	public void teminateInstance(CloudInstance instance) {
		this.teminateInstance(instance.getId());
	}
}