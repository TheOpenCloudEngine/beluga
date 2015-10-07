/*
 * Copyright 2013 Websquared, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencloudengine.garuda.beluga.utils.classloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DynamicClassLoader {
	private static Logger logger = LoggerFactory.getLogger(DynamicClassLoader.class);
	private static final ReadWriteLock lock = new ReentrantReadWriteLock();
	
	private static Map<String, URLClassLoader> classLoaderList = new HashMap<String, URLClassLoader>();
	
	public static boolean remove(String tag) {
		try{
			lock.writeLock().lock();
			classLoaderList.remove(tag);
		}finally{
			lock.writeLock().unlock();
		}
		return true;
	}
	public static boolean removeAll() {
		try{
			lock.writeLock().lock();
			classLoaderList.clear();
		}finally{
			lock.writeLock().unlock();
		}
		return true;
	}
	
	public static boolean add(String tag, List<File> jarFiles) {
		//URL[] jarUrls = new URL[jarFiles.size()];
		return add(tag, jarFiles.toArray(new File[0]));
	}
	public static boolean add(String tag, File[] jarFiles) {
		URL[] jarUrls = new URL[jarFiles.length];
		StringBuilder sb = new StringBuilder(); 
		for (int i = 0; i < jarFiles.length; i++) {
			try {
				jarUrls[i] = jarFiles[i].toURI().toURL();
				sb.append(jarUrls[i]).append(", ");
			} catch (MalformedURLException e) {
			}
		}
		
		URLClassLoader l = new URLClassLoader(jarUrls, Thread.currentThread().getContextClassLoader());
		try{
			lock.writeLock().lock();
			logger.trace("Add Classpath {}:{}", tag, sb.toString());
			classLoaderList.put(tag, l);
		}finally{
			lock.writeLock().unlock();
		}
		return true;
	}
	
	public static boolean add(String tag, String[] jarFilePath) {
		URL[] jarUrls = new URL[jarFilePath.length];
		for (int i = 0; i < jarFilePath.length; i++) {
			File f = new File(jarFilePath[i]);
			try {
				jarUrls[i] = f.toURI().toURL();
			} catch (MalformedURLException e) {
			}
		}
		
		URLClassLoader l = new URLClassLoader(jarUrls);
		try{
			lock.writeLock().lock();
			classLoaderList.put(tag, l);
		}finally{
			lock.writeLock().unlock();
		}
		return true;
	}
	public static Object loadObject(String className){
		try {
			Class<?> clazz = loadClass(className);
			if(clazz != null){
				return clazz.newInstance();
			}
		} catch (Exception e){
			logger.debug("loadObject error > {}", e.getMessage());
		}
		
		return null;
	}
	@SuppressWarnings("unchecked")
	public static <T> T loadObject(String className, Class<T> type){
		try {
			Class<?> clazz = loadClass(className);
			if(clazz != null){
				return (T) clazz.newInstance();
			}
		} catch (Exception e){
			logger.error("loadObject error > {}", e);
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T loadObject(String className, Class<T> type, Class<?>[] paramTypes ,Object[] initargs) throws Exception {
        Class<?> clazz = loadClass(className);
        if(clazz != null){
            try{
                Constructor<?> constructor = clazz.getConstructor(paramTypes);
                return (T) constructor.newInstance(initargs);
            } catch(NoSuchMethodException e) {
                logger.trace("해당 생성자가 없습니다. {} >> {} {} {} {} {} {} {}", className, paramTypes);
            } catch(InvocationTargetException e) {
                throw new Exception(e.getCause());
            }
        }

		return null;
	}
	
	public static Class<?> loadClass(String className) {
		
		Class<?> clazz = null;
		try {
			clazz = Class.forName(className);
			if(clazz != null){
				return clazz;
			}
			
		}catch(NoSuchMethodError ignore){
		}catch(NoSuchFieldError ignore){
		}catch(ClassNotFoundException ignore){
		}catch(NoClassDefFoundError ignore){
		}
		try {
			clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
			if(clazz != null){
				return clazz;
			}
		} catch (ClassNotFoundException ignore) {
		}
		logger.trace("basic cl {}, {}", clazz, className);
		try{
			lock.readLock().lock();
			Iterator<URLClassLoader> iter = classLoaderList.values().iterator();
			while(iter.hasNext()){
				URLClassLoader l = (URLClassLoader)iter.next();
				try {
					clazz = Class.forName(className, true, l);
					logger.trace("{} cl {} : {}", l, clazz, className);
				} catch (ClassNotFoundException e) {
					logger.trace("{} cl {} : {}", l, clazz, className);
					continue;
				} catch (NoClassDefFoundError e) {
					logger.trace("{} cl {} : {}", l, clazz, className);
					continue;
				}
				
				if(clazz != null){
					return clazz;
				}
			}
		}finally{
			lock.readLock().unlock();
		}
		
		//logger.debug("Classloader cannot find {}", className);
		return null;
	}
	
	public static Enumeration<URL> getResources(String name){
		CompoundEnumeration<URL> compoundEnumeration = new CompoundEnumeration<URL>();
		try{
			Enumeration<URL> e = Thread.currentThread().getContextClassLoader().getResources(name);
//			logger.debug("getSystemResources >> {}, {}", e, e.hasMoreElements());
			if(e != null && e.hasMoreElements()){
				compoundEnumeration.add(e);
			}
			lock.readLock().lock();
			Iterator<URLClassLoader> iter = classLoaderList.values().iterator();
			while(iter.hasNext()){
				URLClassLoader l = (URLClassLoader)iter.next();
				e = l.getResources(name);
				
				logger.debug("getResources {} >> {}, {}", l, e, e.hasMoreElements());
				if(e != null && e.hasMoreElements()){
					compoundEnumeration.add(e);
				}
			}
			
		} catch (IOException e) {
			logger.error("", e);
		}finally{
			lock.readLock().unlock();
		}
		return compoundEnumeration;
	}
	
	public static Set<String> getPackageList() {
		Set<String> ret = new HashSet<String>();
		new ClassLoader() {
			public void init(Set<String> set) {
				Package[] packages = super.getPackages();
				for(Package pkg : packages) {
					String pkgName = pkg.getName();
					if(!set.contains(pkgName)) {
						set.add(pkgName);
					}
				}
			}
		}.init(ret);
		return ret;
	}
	
	//특정패키지에서 특정클래스의 하위클래스 얻어오기
	public static List<Class<?>> findChildrenClass(String packageName, final Class<?> parentCls) {
		ClassScanner<Class<?>> scanner = new ClassScanner<Class<?>>() {
			@Override
			public Class<?> done(String className, String pkg, Object param) {
				logger.trace("testing class:{}", className);
				try {
					Class<?> cls = DynamicClassLoader.loadClass(className);
					if(cls!=null && parentCls.isAssignableFrom(cls)) {
						if(!parentCls.equals(cls)){
							//자기자신은 포함하지 않는다.
							return cls;
						}
					}
				} catch (NoClassDefFoundError e) {
					logger.debug("class not found : {}", className);
				}
				return null;
			}
		};
		
		List<Class<?>> ret = new ArrayList<Class<?>>();
		String[] packageNameArray = packageName.split(",");
		for(String packageNameStr : packageNameArray) {
			try {
				ret.addAll(scanner.scanClass(packageNameStr, null));
			} catch (Throwable t) {
				logger.error("", t);
			}
		}
		return ret;
	}

	//특정패키지에서 특정 어노테이션 클래스 얻어오기
	public static List<Class<?>> findClassByAnnotation(String packageName, final Class<? extends Annotation> anonClass) {
		ClassScanner<Class<?>> scanner = new ClassScanner<Class<?>>() {
			@Override
			public Class<?> done(String className, String pkg, Object param) {
				try {
					Class<?> cls = DynamicClassLoader.loadClass(className);
					if(cls!=null) {
						Annotation annotation = cls.getAnnotation(anonClass);
						if(annotation != null){
							return cls;
						}
					}
				} catch (NoClassDefFoundError e) {
					logger.debug("class not found : {}", className);
				}
				return null;
			}
		};
		List<Class<?>> ret = new ArrayList<Class<?>>();
		String[] packageNameArray = packageName.split(",");
		for(String packageNameStr : packageNameArray) {
			try {
				ret.addAll(scanner.scanClass(packageNameStr, null));
			} catch (Throwable t) {
				logger.error("", t);
			}
		}
		return ret;
	}
}
