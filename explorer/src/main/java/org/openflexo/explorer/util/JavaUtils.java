package org.openflexo.explorer.util;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

public class JavaUtils {

	private static PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.java");

	public static boolean isJavaFile(Path path) {
		return matcher.matches(path.getFileName());
	}

	public static String getName(Path path) {
		String filename = path.getFileName().toString();
		if (filename.indexOf(".") > 0) {
			filename = filename.substring(0, filename.lastIndexOf("."));
		}
		return filename;
	}
}
