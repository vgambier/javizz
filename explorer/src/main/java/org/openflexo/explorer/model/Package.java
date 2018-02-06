package org.openflexo.explorer.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.openflexo.explorer.util.JavaUtils;

public class Package implements Iterable<JavaType> {
	private Path location;
	private Path name;
	private Project container;
	private Set<JavaType> javaTypes = new HashSet<>();

	public Package(Project container, Path location, Path name) {
		this.container = container;
		this.location = location;
		this.name = name;
		registerJavaTypes(location.resolve(name));
	}

	public File getFolder() {
		return location.resolve(name).toFile();
	}

	private void registerJavaTypes(Path path) {
		if (Files.isDirectory(path)) {
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
				for (Path p : stream) {
					if (JavaUtils.isJavaFile(p))
						this.javaTypes.add(new JavaType(this, p));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public String toString() {
		return this.getName();
	}

	public static void main(String[] args) {
		System.out.println(new Package(null, Paths.get("src/main/java"), Paths.get("org/openflexo/essai")).toString());
	}

	@Override
	public Iterator<JavaType> iterator() {
		return javaTypes.iterator();
	}

	public String getName() {
		return name.toString().replace("/", ".");
	}
}
