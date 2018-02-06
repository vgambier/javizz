package org.openflexo.explorer.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.openflexo.explorer.util.JavaUtils;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaPackage;

public class Package implements Iterable<JavaFile> {
	private Path location;
	private Path name;
	private Project container;
	private Set<JavaFile> javaFiles = new HashSet<>();
	private JavaPackage qdoxPackage;

	public Package(Project container, Path location, Path name) {
		this.container = container;
		this.location = location;
		this.name = name;
		registerJavaFiles(location.resolve(name));
	}

	private void registerJavaFiles(Path path) {
		if (Files.isDirectory(path)) {
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
				for (Path p : stream) {
					if (JavaUtils.isJavaFile(p))
						this.javaFiles.add(new JavaFile(this, p));
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

	@Override
	public Iterator<JavaFile> iterator() {
		return javaFiles.iterator();
	}

	public String getName() {
		return name.toString().replace("/", ".");
	}

	public void setQdoxPackage(JavaProjectBuilder builder) {
		this.qdoxPackage = builder.getPackageByName(this.getName());
		if (this.qdoxPackage != null)
			for (com.thoughtworks.qdox.model.JavaClass t : this.qdoxPackage.getClasses()) {
				// System.out.println("===" + t);
				// System.out.println("===" + t.getNestedClasses());
				registerType(t);
				// for (com.thoughtworks.qdox.model.JavaClass ic : t.getNestedClasses())
				// registerType(ic);
			}
	}

	private void registerType(com.thoughtworks.qdox.model.JavaClass t) {
		try {
			Path location = Paths.get(t.getSource().getURL().toURI());
			if (this.contains(location)) {
				JavaFile file = this.getFile(location);
				if (t.isInterface())
					file.add(new JavaInterface(file, t.getName(), t.isStatic(), JavaUtils.getVisibility(t)));
				else if (t.isEnum())
					file.add(new JavaEnum(file, t.getName(), t.isStatic(), JavaUtils.getVisibility(t)));
				else
					file.add(new JavaClass(file, t.getName(), t.isStatic(), JavaUtils.getVisibility(t)));
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	private JavaFile getFile(Path location) {
		for (JavaFile jf : this)
			if (jf.isAt(location))
				return jf;
		return null;
	}

	private boolean contains(Path location) {
		return this.location.resolve(name).equals(location.getParent());
	}

	public Path getShortPath() {
		return this.container.getShortPath().resolve(name);
	}
}
