package org.openflexo.explorer.model;

import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.openflexo.explorer.util.JavaUtils;

public class JavaFile implements Iterable<JavaType> {
	private String name;
	private JavaPackagePart pakc;
	private Path path;
	private Set<JavaType> javaTypes = new HashSet<>();

	public JavaFile(JavaPackagePart pakc, Path path) {
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

	public String getPackageName() {
		return this.pakc.getName();
	}

	@Override
	public Iterator<JavaType> iterator() {
		return javaTypes.iterator();
	}

	public boolean isAt(Path location) {
		return this.path.equals(location);
	}

	public void add(JavaType t) {
		this.javaTypes.add(t);
	}

	public Path getPath() {
		return this.path;
	}

	public Path getShortPath() {
		return this.pakc.getShortPath().resolve(this.name);
	}
}
