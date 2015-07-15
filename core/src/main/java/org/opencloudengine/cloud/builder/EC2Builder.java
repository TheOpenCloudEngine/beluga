package org.opencloudengine.cloud.builder;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.ec2.compute.options.EC2TemplateOptions;
import org.opencloudengine.cloud.CloudInstance;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;
import static org.jclouds.compute.predicates.NodePredicates.*;

public class EC2Builder extends JCloudsBuilder {

	private final static String PROVIDER = "ec2";
	
	public final static String PUBLIC_DNS_NAME = "ec2-%s.%s.%s";	// public address, locationId, subfix 
	public final static String PUBLIC_DNS_NAME_SUBFIX = "compute.amazonaws.com";
	
	protected String getProvider(){
		return PROVIDER;
	}
	
	@Override
	protected void setLaunchOption(TemplateOptions options){
		super.setLaunchOption(options);
		
		options.as(EC2TemplateOptions.class).keyPair(this.keypair);
	}

	@Override
	protected Iterable<? extends NodeMetadata> listNodes() {
		return filter(this.getCompute().listNodesDetailsMatching(all()), and(not(TERMINATED), parentLocationId(this.locationId)));
	}

	@Override
	protected Iterable<? extends NodeMetadata> listNodesForGroup(String groupId) {
		List<NodeMetadata> nodes = new ArrayList<NodeMetadata>();
		
		for(NodeMetadata node : this.listNodes())
			if(groupId.equals(extractGropuId(node.getName())))
				nodes.add(node);
		
		return nodes;
	}
	
	@Override
	protected CloudInstance convertInstance(NodeMetadata nodeMetadata){

		String publicDnsName = convertPublicDnsName(firstOfArray(nodeMetadata.getPublicAddresses()), nodeMetadata.getLocation().getParent().getId());
		String privateAddress = firstOfArray(nodeMetadata.getPrivateAddresses());

		CloudInstance instance = new CloudInstance();
		instance.setId(nodeMetadata.getProviderId());
		instance.setGroupId(nodeMetadata.getGroup());
		instance.setLocation(nodeMetadata.getLocation().getParent().getId());
		instance.setState(nodeMetadata.getStatus().toString());
		instance.setPublicDnsName(publicDnsName);
		instance.setPrivateIpAddress(privateAddress);

		return instance;
	}
	
	private String convertPublicDnsName(String publicAddress, String locationId){
		return String.format(PUBLIC_DNS_NAME, publicAddress.replaceAll("[.]", "-"), locationId, PUBLIC_DNS_NAME_SUBFIX);
	}

}