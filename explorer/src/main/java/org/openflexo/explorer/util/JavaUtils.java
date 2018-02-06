package org.openflexo.explorer.util;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

public class JavaUtils {

	public enum Visibility {
		Private {
			@Override
			public String toString() {
				return "-";
			}
		},
		Public {
			@Override
			public String toString() {
				return "+";
			}
		},
		Protected {
			@Override
			public String toString() {
				return "#";
			}
		},
		None {
			@Override
			public String toString() {
				return "_";
			}
		}
	}

	public static Visibility getVisibility(com.thoughtworks.qdox.model.JavaClass c) {
		if (c.isPrivate())
			return Visibility.Private;
		if (c.isProtected())
			return Visibility.Protected;
		if (c.isPublic())
			return Visibility.Public;
		return Visibility.None;
	}

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
