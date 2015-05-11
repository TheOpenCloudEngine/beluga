package org.opencloudengine.garuda.common.util;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.*;
import java.util.Map;

public class TempleteUtils {
	/**
	 *
	 * @param viewFile Velocity 원본 파일 경로
	 * @param generateFilePath 파라미터를 매핑한 파일을 생성할 경로
	 * @param params
	 */
	public static void generateTemplete(String viewFile, String generateFilePath, Map<String, Object> params) {
		VelocityContext vc = new VelocityContext();
		for (String key : params.keySet()) {
			vc.put(key, params.get(key));
		}

		writeTemplete(viewFile, generateFilePath, vc);
	}

	private static void writeTemplete(String viewFile, String generateFilePath, VelocityContext context) {
		// Start the VelocityEngine
		VelocityEngine ve = new VelocityEngine();
		ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		ve.init();

		// Open the template
		Template templete = ve.getTemplate(viewFile, "UTF-8");

		// Create a new string from the template
		StringWriter stringWriter = new StringWriter();
		templete.merge(context, stringWriter);
		String configurationString = stringWriter.toString();

		// Write configuration to file
		try {
			String tempDir = generateFilePath.substring(0, generateFilePath.lastIndexOf("/"));
			(new File(tempDir)).mkdirs();

			BufferedWriter writer = new BufferedWriter(new FileWriter(generateFilePath));
			writer.write(configurationString);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
