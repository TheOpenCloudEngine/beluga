package org.opencloudengine.garuda.env;

import java.io.File;

/**
 * Path
 *
 * @author Sang Wook, Song
 *
 */
public class Path {

	private File root;

	public Path(File root) {
		this.root = root;
	}

	public Path(File root, String sub) {
		this.root = new File(root, sub);
	}

	public Path newPath() {
		return new Path(root);
	}
	
	@Override
	public Path clone() {
		return newPath();

	}
	
	public Path path(String... dirs) {
		return new Path(file(dirs));
	}
	
	public Path configPath() {
		return new Path(root, "conf");
	}
	
	public File file(String... dirs) {
		File file = root;
		for (int i = 0; i < dirs.length; i++) {
			file = new File(file, dirs[i]);
		}
		return file;
	}
	
	
	public Path makePath(String path) {
		if (path == null) {
			return newPath();
		}

		if (Environment.OS_NAME.startsWith("Windows")) {
			// 절대경로 패턴일때..
			if (path.matches("^[a-zA-Z]:\\\\.*")) { // 윈도우즈는 c:\\와 같이 시작하지 않으면 상대경로이다.
				return makeAbsolutePath(path);
			}
			if (path.matches("^[a-zA-Z]://.*")) { // c://와 같이 사용시를 고려.
				return makeAbsolutePath(path);
			}
			if (path.startsWith("/")) {
				return makeAbsolutePath(path);
			}
		}

		if (path.startsWith(Environment.FILE_SEPARATOR)) {
			return makeAbsolutePath(path);
		}

		return makeRelativePath(path);
	}

	// 상대경로.
	public Path makeRelativePath(String path) {
		if (path == null) {
			return newPath();
		}
		return path(path);
	}

	// 절대경로.
	public Path makeAbsolutePath(String path) {
		if (path == null) {
			return newPath();
		}
		return new Path(null, path);
	}

	public File relativise(File file){
		String relativePath = root.toURI().relativize(file.toURI()).getPath();
		return new File(relativePath);
	}
	
	
	public File file() {
		return root;
	}

	@Override
	public String toString() {
		return root.getPath();
	}
	
	public Path getStatisticsRoot() {
		return makeRelativePath("statistics");
	}
	
	public Path getKeywordsRoot() {
		return makeRelativePath("keywords");
	}

}
