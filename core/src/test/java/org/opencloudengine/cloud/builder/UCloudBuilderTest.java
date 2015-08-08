package org.opencloudengine.cloud.builder;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.opencloudengine.cloud.CloudInstance;
import org.opencloudengine.cloud.builder.UCloudBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class UCloudBuilderTest {

	private static UCloudBuilder builder = null;
	
	@BeforeClass
	public static void setUpOnce(){
		System.out.println("setUpOnce");
		Properties options = new Properties();
		try {
			File file = new File("/Users/somehow/Documents/workspace/garuda-ws/garuda-master1/src/main/resources/conf/arvue.conf");
			if(file.exists())
				options.load(new FileInputStream(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		builder = new UCloudBuilder();
		builder.config(options);
	}
	
	@AfterClass
	public static void tearDownOnce(){
		System.out.println("tearDownOnce");
	}
	
	@Test
	@Ignore
	public void testLaunchInstance() throws Exception {
		System.out.println("testLaunchInstance");
		CloudInstance instance = builder.launchInstance("arvue");
		assertNotNull(instance);
		assertTrue(instance.getId().length() > 0);
	}

	@Test
	@Ignore
	public void testGetRunningInstance() throws Exception {
		System.out.println("testGetRunningInstance");
		List<CloudInstance> instances = builder.getRunningInstances("arvue");
		
		System.out.println(instances);
		
		assertTrue(instances.size() > 0);
	}
	
	@Test
	@Ignore
	public void testTeminateInstance() throws Exception { 
		System.out.println("testTeminateInstance");
		List<CloudInstance> instances = builder.getRunningInstances("arvue");
		for(CloudInstance instance : instances)
			builder.teminateInstance(instance);
		
		assertTrue(builder.getRunningInstances("arvue").size() == 0);
	}
}