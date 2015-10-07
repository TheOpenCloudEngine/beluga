package org.opencloudengine.garuda.beluga.utils.classloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract class ClassScanner<E> {
	
	private static final Logger logger = LoggerFactory.getLogger(ClassScanner.class);
	
	public List<E> scanClass(String packageName, Object param) throws IOException {
		List<E> ret = new ArrayList<E>();
		
		if(packageName==null || "".equals(packageName)) {
			packageName="";
		}
		
		String pathStr = "";
		pathStr = packageName.replace(".", "/");
		if (!pathStr.endsWith("/")) {
			pathStr = pathStr + "/";
		}
		
		String[] pathArray = pathStr.split(",");
		
		for(String path : pathArray) {
			Enumeration<URL> classEnumeration = null;
			logger.trace("find class from {}", path);
			classEnumeration = DynamicClassLoader.getResources(path);
			while(classEnumeration.hasMoreElements()) {
				String urlString = classEnumeration.nextElement().toString();
				logger.trace("find class url > {}", urlString);
				if(urlString.startsWith("jar:file:")) {
					String jpath = urlString.substring(9);
					int st = jpath.indexOf("!/");
					String jarPath = jpath.substring(0, st);
					String entryPath = jpath.substring(st + 2);
					
					logger.trace("jarPath > {}, {}", jarPath, entryPath);
					JarFile jarFile = new JarFile(jarPath);
					try {
						Enumeration<JarEntry>jee = jarFile.entries();
						while(jee.hasMoreElements()) {
							JarEntry jarEntry = jee.nextElement();
							String className = jarEntry.getName();
							logger.trace("jar entry > {}, {}", className, entryPath);
							
							if (className.startsWith(entryPath)) {
								if(className.endsWith(".class")) {
									className = className.substring(0,className.length() - 6);
									className = className.replaceAll("/", ".");
									//in-a class 는 인정하지 않는다.
									if(!className.contains("$")) {
										E args = done(className, packageName, param);
										if(args!=null && !ret.contains(args)) { ret.add(args); }
									}
								}
							}
						}
					} finally{
						jarFile.close();
					}
				} else  if(urlString.startsWith("file:")) {
					String rootPath = urlString.substring(5);
					int prefixLength = rootPath.indexOf(path);
					File baseFile = new File(rootPath);
					
					Set<File> fileSet = new HashSet<File>();
					if (baseFile.isDirectory()) {
						addDirectory(fileSet, baseFile);
					} else {
						fileSet.add(baseFile);
					}
					for (File file : fileSet) {
						String classPath = file.toURI().toURL().toString()
							.substring(5).substring(prefixLength);
						if(classPath.endsWith(".class")) {
							classPath = classPath.substring(0,classPath.length() - 6);
							classPath = classPath.replaceAll("/", ".");
							//in-a class 는 인정하지 않는다.
							if(!classPath.contains("$")) {
								E args = done(classPath, packageName, param);
								if(args!=null && !ret.contains(args)) { ret.add(args); }
							}
						}
					}
				}
			}
		}
		return ret;
	}
	
	private void addDirectory(Set<File> set, File d){
		File[] files = d.listFiles();
		for (int i = 0; i < files.length; i++) {
			if(files[i].isDirectory()){
				addDirectory(set, files[i]);
			}else{
				set.add(files[i]);
			}
		}
	}
	protected abstract E done(String className, String packageName, Object param);
}
