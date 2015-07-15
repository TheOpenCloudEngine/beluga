package org.opencloudengine.cloud.builder;

import net.iharder.base64.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.opencloudengine.cloud.CloudInstance;
import org.opencloudengine.cloud.builder.templete.ucloud.DeployVirtualMachineResponse;
import org.opencloudengine.cloud.builder.templete.ucloud.ListVirtualMachinesResponse;
import org.opencloudengine.cloud.builder.templete.ucloud.VirtualMachine;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

public class UCloudBuilder extends CloudBuilder {

	@Override
	public List<CloudInstance> getRunningInstances() {
		StringBuffer sb = new StringBuffer();
		sb.append("listVirtualMachines");
		sb.append("&zoneid=" + locationId);
		
		return listNodes(sb.toString());
	}

	@Override
	public List<CloudInstance> getRunningInstances(String groupId) {
		List<CloudInstance> instances = getRunningInstances();
		
		for(int i=instances.size()-1; i>=0; i--){
			CloudInstance instance = instances.get(i);
			
			if(!groupId.equals(instance.getGroupId()))
				instances.remove(i);
		}
	
		return instances;
	}

	public List<CloudInstance> listNodes(String command) {
		String result = requestCommand(command);
		List<CloudInstance> instances = new ArrayList<CloudInstance>(); 
		try {
			ListVirtualMachinesResponse lvmr = (ListVirtualMachinesResponse)convertXmlToObject(result.getBytes(), ListVirtualMachinesResponse.class);
			if(lvmr.getCount() > 0){
				for(VirtualMachine vm : lvmr.getVirtualMachine())
					instances.add(convertVirtualMachineToCloudInstance(vm));
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		return instances;
	}
	
	@Override
	public CloudInstance getRunningInstance(String instanceId) {

		StringBuffer sb = new StringBuffer();
		sb.append("listVirtualMachines");
		sb.append("&id=" + instanceId);
		
		String result = requestCommand(sb.toString());
		
		try {
			ListVirtualMachinesResponse lvmr = (ListVirtualMachinesResponse)convertXmlToObject(result.getBytes(), ListVirtualMachinesResponse.class);
			if(lvmr.getCount() > 0)
				return convertVirtualMachineToCloudInstance(lvmr.getVirtualMachine().get(0));
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		return new CloudInstance();
	}

	@Override
	public List<CloudInstance> launchInstances(int N, String group) {
		List<CloudInstance> list = new ArrayList<CloudInstance>();

		for(int i=0; i<N; i++){
			CloudInstance instance = launchInstance(group);
			
			if(instance.getId() != null)
				list.add(instance);
		}
		
		return list;
	}

	@Override
	public void teminateInstance(CloudInstance instance) {
		if(!CloudInstance.INSTANCE_STOPPED.equals(instance.getState()))
			stopInstance(instance.getId());
		
		this.teminateInstance(instance.getId());
	}

	@Override
	public void teminateInstance(String instanceId) {
		StringBuffer sb = new StringBuffer();
		sb.append("destroyVirtualMachine");
		sb.append(String.format("&%s=%s", "id", instanceId));
		
		requestCommand(sb.toString());
	}

	protected void stopInstance(String instanceId){
		StringBuffer sb = new StringBuffer();
		sb.append("stopVirtualMachine");
		sb.append(String.format("&%s=%s", "id", instanceId));
		
		requestCommand(sb.toString());
		
		waitUpdateInstance(CloudInstance.INSTANCE_STOPPED, instanceId, 60);
	}
	
	protected CloudInstance launchInstance(String group){
		StringBuffer sb = new StringBuffer();
		sb.append("deployVirtualMachine");
		sb.append(String.format("&%s=%s", "serviceofferingid", "c504e367-20d6-47c6-a82c-183b12d357f2"));
		sb.append(String.format("&%s=%s", "templateid", imageId));
		sb.append(String.format("&%s=%s", "diskofferingid", "87c0a6f6-c684-4fbe-a393-d8412bcf788d"));
		sb.append(String.format("&%s=%s", "zoneid", locationId));
		sb.append(String.format("&%s=%s", "displayname", String.format("%s-%s", group, System.currentTimeMillis())));

		String result = requestCommand(sb.toString());

		CloudInstance instance = new CloudInstance();
		
		try{
			DeployVirtualMachineResponse dvmr = (DeployVirtualMachineResponse) convertXmlToObject(result.getBytes(), DeployVirtualMachineResponse.class);

			instance.setId(dvmr.getId());
		}catch(JAXBException e){
			e.printStackTrace();
		}
		
		CloudInstance updateInstance = waitUpdateInstance(CloudInstance.INSTANCE_RUNNING, instance.getId(), 120);
		if(updateInstance != null)
			instance = updateInstance;
		
		return instance;
	}
	
	public String requestCommand(String command){
		String result = null;
		try {
			String apiUrl = "command=" + command;

			// Step 1: Make sure your APIKey is URL encoded
			String encodedApiKey = urlencoding(accessKey, "UTF-8");

			// Step 2: URL encode each parameter value, then sort the parameters
			// and
			// apiKey in
			// alphabetical order, and then toLowerCase all the parameters,
			// parameter values and apiKey.
			// Please note that if any parameters with a '&' as a value will
			// cause
			// this test client to fail since we are using
			// '&' to delimit
			// the string
			List<String> sortedParams = new ArrayList<String>();
			sortedParams.add("apikey=" + encodedApiKey.toLowerCase());
			StringTokenizer st = new StringTokenizer(apiUrl, "&");
			String url = null;
			boolean first = true;
			while (st.hasMoreTokens()) {
				String paramValue = st.nextToken();
				String param = paramValue.substring(0, paramValue.indexOf("="));
				String value = "";
				if ("userdata".equals(param)) {
					value = (paramValue.substring(paramValue.indexOf("=") + 1,
							paramValue.length()));
				} else {
					value = urlencoding(paramValue.substring(
							paramValue.indexOf("=") + 1, paramValue.length()),
							"UTF-8");
				}

				if (first) {
					url = param + "=" + value;
					first = false;
				} else {
					url = url + "&" + param + "=" + value;
				}
				sortedParams.add(param.toLowerCase() + "="
						+ value.toLowerCase());
			}
			Collections.sort(sortedParams);

			// System.out.println("Sorted Parameters: " + sortedParams);

			// Step 3: Construct the sorted URL and sign and URL encode the
			// sorted
			// URL with your secret key
			String sortedUrl = null;
			first = true;
			for (String param : sortedParams) {
				if (first) {
					sortedUrl = param;
					first = false;
				} else {
					sortedUrl = sortedUrl + "&" + param;
				}
			}
			// System.out.println("sorted URL : " + sortedUrl);
			String encodedSignature = signRequest(sortedUrl, secretKey);

			// Step 4: Construct the final URL we want to send to the CloudStack
			// Management Server
			// Final result should look like:
			// http(s)://://client/api?&apiKey=&signature=
			String finalUrl = this.endpoint + "?" + url + "&apiKey=" + accessKey
					+ "&signature=" + encodedSignature;
			// System.out.println(finalUrl);
			// System.out.println("final URL : " + finalUrl);

			// System.out.println(encodedSignature);

			if (finalUrl.indexOf("https") != -1) {
				ProtocolSocketFactory socketFactory = new EasySSLProtocolSocketFactory();
				Protocol https = new Protocol("https", socketFactory, 443);
				Protocol.registerProtocol("https", https);
			}
			HttpClient client = new HttpClient();
			HttpMethod method = new GetMethod(finalUrl);
			int responseCode = client.executeMethod(method);
			if (responseCode == 200) {	
				// SUCCESS!
				//				System.out.println("Successfully executed command");
				//				System.out.println("Success Result : "
				//						+ method.getResponseBodyAsString());
				result = method.getResponseBodyAsString();
			} else {
				// FAILED!
				System.out.println("Unable to execute command with response code: " + responseCode);
				System.out.println("Fail Result : " + method.getResponseBodyAsString());
				result = method.getResponseBodyAsString();
			}
		} catch (Throwable t) {
			System.out.println(t);
		}

		return result;
	}

	public String urlencoding(String str, String charset) {
		String retStr = str;
		try {
			retStr = URLEncoder.encode(str, charset)
						.replace("+", "%20")
						.replace("*", "%2A")
						.replace("%7E", "~");
		} catch (Exception e) {
		}
		return retStr;
	}

	public String signRequest(String commandString, String signature) {

		byte[] encryptedBytes;
		try {
			Mac mac = Mac.getInstance ( "HmacSHA1" );
			SecretKeySpec keySpec = new SecretKeySpec(signature.getBytes(), "HmacSHA1");
			mac.init( keySpec );
			mac.update ( commandString.getBytes() );
			encryptedBytes = mac.doFinal();

			return urlencoding(Base64.encodeBytes(encryptedBytes), "UTF-8");
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private CloudInstance convertVirtualMachineToCloudInstance(VirtualMachine vm){
		CloudInstance instance = new CloudInstance();
		instance.setId(vm.getId());
		instance.setState(vm.getState().toUpperCase());
		instance.setLocation(vm.getZoneId());
		instance.setPrivateIpAddress(vm.getNic().getIpAddress());
		instance.setPublicDnsName(instance.getPrivateIpAddress());		
		instance.setGroupId(extractGropuId(vm.getDisplayName()));
		
		return instance;
	}
		
	private Object convertXmlToObject(byte[] result, Class<?> clazz) throws JAXBException {
		ByteArrayInputStream bais = new ByteArrayInputStream(result);

		JAXBContext jaxbContext = null;

		jaxbContext = JAXBContext.newInstance(clazz);
		return jaxbContext.createUnmarshaller().unmarshal(bais);
	}
	
	private CloudInstance waitUpdateInstance(String state, String instanceId, int trySecond){
		CloudInstance instance = null;
		int count = 0;
		do {
			try {
				Thread.sleep(1000);
				count++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(count >= trySecond)
				break;
			
			instance = getRunningInstance(instanceId);
		} while(!state.equals(instance.getState()));
		
		return instance;
	}
}