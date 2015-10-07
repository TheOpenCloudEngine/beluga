package org.opencloudengine.garuda.beluga.utils.classloader;

import java.io.IOException;
import java.util.List;


public class ClassScannerTool {
	public static void main(String[] args) throws IOException {
		if(args.length == 0){
			
			System.out.println("java ClassScannerTool [package]");
			System.exit(1);
		}
		ClassScannerTool tester = new ClassScannerTool();
		List<String> list = tester.scan(args[0]);
		
		int i = 0;
		for(String c : list){
			System.out.println(++i + " : " + c);
		}
		
	}
	
	public List<String> scan(String clazz) throws IOException{
		ClassScanner<String> scanner = new ClassScanner<String>() {
			@Override
			public String done(String ename, String pkg, Object param) {
				return ename + " / " + pkg + " / " + param;
			}
		};
		
		
		return scanner.scanClass(clazz, null);
	}
}
