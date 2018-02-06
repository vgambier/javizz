package org.openflexo.explorer.model;

import java.io.File;
import java.nio.file.Path;

import org.openflexo.explorer.util.JavaUtils;

public class JavaType {
	private String name;
	private Package pakc;
	private Path path;

	public JavaType(Package pakc, Path path) {
		this.name = JavaUtils.getName(path);
		this.pakc = pakc;
		this.path = path;
	}

	@Override
	public String toString() {
		return name;
	}

	public File getFile() {
		return this.path.toFile();
	}

	public String getName() {
		return this.name;
	}
}
