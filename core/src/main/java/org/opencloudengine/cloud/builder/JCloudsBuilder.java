package org.opencloudengine.cloud.builder;

import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import org.opencloudengine.cloud.CloudInstance;

import java.util.*;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;
import static org.jclouds.compute.predicates.NodePredicates.*;

public abstract class JCloudsBuilder extends CloudBuilder {

	ComputeServiceContext context = null;
	ComputeService compute = null;

	protected ComputeService getCompute(){
		if(this.compute == null)
			this.compute = createCompute();

		return this.compute;
	}

	protected ComputeService createCompute() {
		return this.getContext().getComputeService();
	}
	
	protected ComputeServiceContext getContext(){
		if(this.context == null)
			this.context = createContext();

		return this.context;
	}

	protected ComputeServiceContext createContext() {

		try {
			ContextBuilder builder = createBuilder();

			builder.credentials(this.accessKey, this.secretKey);
			
			Properties overrides = createOverrides();
			if(overrides != null)
				builder.overrides(overrides);
			if(this.endpoint != null)
				builder.endpoint(this.endpoint);

			
			return builder.buildView(ComputeServiceContext.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	};

	protected ContextBuilder createBuilder(){
		try {
			return ContextBuilder.newBuilder(this.getProvider());
		} catch (NoSuchElementException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	protected Properties createOverrides() {
		return null;
	}
	
	abstract protected String getProvider();

	@Override
	public List<CloudInstance> launchInstances(int N, String group) {
		List<CloudInstance> instances = new ArrayList<CloudInstance>();

		Template template = buildTemplate();

		setLaunchOption(template.getOptions());

		try {
			Set<? extends NodeMetadata> launchNodeMetadatas = compute.createNodesInGroup(group, N, template);

			for (NodeMetadata nodeMetadata : launchNodeMetadatas)
				instances.add(this.convertInstance(nodeMetadata));

		} catch (Throwable e) {
			e.printStackTrace();
		}

		return instances;
	}

	protected void setLaunchOption(TemplateOptions options){
		int[] inboundPorts = {22, 5555, 8000};		
		options.as(TemplateOptions.class).inboundPorts(inboundPorts);
	}

	protected Template buildTemplate() {
		// 이미지로 인스턴스를 생성하기 위해서는 locationId + "/" + imageId 로 지정
		Template template = this.getCompute().templateBuilder()
				.locationId(this.locationId)
				.imageId(this.imageId)
				.hardwareId(this.hardwareId)
				.build();

		return template;
	}
	
	@Override
	public void teminateInstance(CloudInstance instance) {
		this.teminateInstance(instance.getLocation() + "/" + instance.getId());
	}

	@Override
	public void teminateInstance(String instanceId) {

		System.out.println("instanceId: " + instanceId);
		try {
			this.getCompute().destroyNode(instanceId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected CloudInstance convertInstance(NodeMetadata nodeMetadata){

		String publicDnsName = firstOfArray(nodeMetadata.getPublicAddresses());
		String privateAddress = firstOfArray(nodeMetadata.getPrivateAddresses());

		CloudInstance instance = new CloudInstance();
		instance.setId(nodeMetadata.getProviderId());
		instance.setGroupId(nodeMetadata.getGroup());
		instance.setLocation(nodeMetadata.getLocation().getId());
		if(nodeMetadata.getLocation().getParent() != null)
			instance.setLocation(nodeMetadata.getLocation().getParent().getId());
		
		instance.setState(nodeMetadata.getStatus().toString());
		instance.setPrivateIpAddress(privateAddress);
		instance.setPublicDnsName(publicDnsName);
		if(instance.getPublicDnsName().length() == 0)
			instance.setPublicDnsName(instance.getPrivateIpAddress());

		return instance;
	}

	@Override
	public CloudInstance getRunningInstance(String instanceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CloudInstance> getRunningInstances() {
		return getRunningInstances(listNodes());
	}

	@Override
	public List<CloudInstance> getRunningInstances(String groupId) {
		return getRunningInstances(listNodesForGroup(groupId));
	}

	private List<CloudInstance> getRunningInstances(Iterable<? extends NodeMetadata> listNodes) {
		List<CloudInstance> instances = new ArrayList<CloudInstance>();

		for(NodeMetadata node : listNodes)
			instances.add(convertInstance(node));

		return instances;
	}

	protected Iterable<? extends NodeMetadata> listNodes() {
		return this.getCompute().listNodesDetailsMatching(all());
	}

	protected Iterable<? extends NodeMetadata> listNodesForGroup(String groupId) {
		return filter(this.getCompute().listNodesDetailsMatching(all()), and(not(TERMINATED), inGroup(groupId)));
	}

	protected String firstOfArray(Set<String> set){
		Iterator<String> iterator = set.iterator();
		if(iterator.hasNext())
			return iterator.next();

		return "";
	}
}
