package org.opencloudengine.cloud.builder;

import org.jclouds.Constants;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.openstack.keystone.v2_0.config.CredentialTypes;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties;
import org.jclouds.openstack.nova.v2_0.compute.options.NovaTemplateOptions;

import java.util.Properties;

public class OpenStackBuilder extends JCloudsBuilder {

	private final static String PROVIDER = "openstack-nova";
	
	protected String getProvider(){
		return PROVIDER;
	}
	
	@Override
	protected void setLaunchOption(TemplateOptions options){
		super.setLaunchOption(options);
		
		options.as(NovaTemplateOptions.class).keyPairName(this.keypair);
	}

	@Override
	protected Properties createOverrides(){
		Properties overrides = new Properties();
		overrides.setProperty(KeystoneProperties.CREDENTIAL_TYPE, CredentialTypes.PASSWORD_CREDENTIALS);
		overrides.setProperty(Constants.PROPERTY_API_VERSION, "2");

		return overrides;
	}
}
