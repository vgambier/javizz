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

public class JavaPackagePart implements Iterable<JavaFile> {
	private Path path;
	private Project container;
	private Set<JavaFile> javaFiles = new HashSet<>();
	private JavaPackage pack;

	public JavaPackagePart(Project container, Path path, JavaPackage pa) {
		this.container = container;
		this.path = path;
		this.pack = pa;
		pa.registerPart(this);
		registerJavaFiles(path);
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
		return this.pack.getName();
	}

	@Override
	public Iterator<JavaFile> iterator() {
		return javaFiles.iterator();
	}

	void registerType(com.thoughtworks.qdox.model.JavaClass t) {
		try {
			Path location = Paths.get(t.getSource().getURL().toURI());
			if (this.contains(location)) {
				JavaFile file = this.getFile(location);
				if (t.isInterface())
					file.add(new JavaInterface(file, t));
				else if (t.isEnum())
					file.add(new JavaEnum(file, t));
				else if (t.isAnnotation())
					file.add(new JavaAnnotation(file, t));
				else
					file.add(new JavaClass(file, t));
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
		return this.path.equals(location.getParent());
	}

	public String getName() {
		return pack.getName();
	}

	public Path getShortPath() {
		return this.container.getShortPath().resolve(pack.getName());
	}
}
