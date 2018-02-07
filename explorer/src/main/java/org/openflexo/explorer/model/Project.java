package org.openflexo.explorer.model;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.openflexo.explorer.gradle.FindDependenciesVisitor;
import org.openflexo.explorer.gradle.GradleDependency;
import org.openflexo.explorer.util.JavaUtils;

/**
 * @author Fabien Dagnat
 */
public class Project extends GradleDir {
	public enum Kind {
		API, Test, RC, Code, Other
	}

	final private Kind kind;
	final private boolean isBuilt;
	private List<GradleDependency> dependencies;
	private List<JavaPackagePart> parts = new ArrayList<>();
	private Repository repo;

	public List<GradleDependency> getDependencies() {
		return dependencies;
	}

	public List<JavaPackagePart> getPackages() {
		return parts;
	}

	private Project(Repository repo, Path path, Kind k, boolean built) {
		super(path);
		this.repo = repo;
		this.kind = k;
		this.isBuilt = built;
		Path codeDir = this.getPath().resolve("src/main/java");
		registerPackageParts(codeDir, Paths.get(""));
	}

	private void registerPackageParts(Path dir, Path container) {
		Path path = dir.resolve(container);
		if (Files.isDirectory(path)) {
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
				for (Path p : stream) {
					if (Files.isDirectory(p)) {
						Path pack = container.resolve(p.getFileName());
						if (containsJavaFile(p)) {
							String packageName = pack.toString().replace("/", ".");
							JavaPackage pa = getRoot().registerPackage(packageName);
							this.parts.add(new JavaPackagePart(this, p, pa));
						}

						registerPackageParts(dir, pack);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private Root getRoot() {
		return repo.getRoot();
	}

	private static boolean containsJavaFile(Path path) {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
			for (Path p : stream) {
				if (JavaUtils.isJavaFile(p.getFileName()))
					return true;
			}
		} catch (IOException e) {
		}
		return false;
	}

	public void parseBuild(Root root) {
		if (this.kind != Kind.Other) {
			Path buildFile = this.getPath().resolve("build.gradle");
			if (Files.exists(buildFile)) {
				// System.out.println(buildFile);
				try {
					String buildContent = new String(Files.readAllBytes(buildFile));
					if (!buildContent.equals("")) {
						List<ASTNode> nodes = new AstBuilder().buildFromString(buildContent);
						FindDependenciesVisitor visitor = new FindDependenciesVisitor(root.getProjects());
						for (ASTNode node : nodes) {
							node.visit(visitor);
						}
						dependencies = visitor.getDependencies();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static Project create(Repository repo, Path path, String name, boolean built) {
		Path thePath = path.resolve(name.replaceAll(":", "/"));
		return new Project(repo, thePath, getKind(thePath), built);
	}

	public static Project create(Repository repo, Path path, boolean built) {
		return new Project(repo, path, getKind(path), built);
	}

	private static Kind getKind(Path path) {
		String filename = path.getFileName().toString();
		if (filename.endsWith("-rc"))
			return Kind.RC;
		else if (filename.endsWith("-test"))
			return Kind.Test;
		else if (filename.endsWith("-api"))
			return Kind.API;
		else if (Files.exists(path.resolve("build.gradle")))
			return Kind.Code;
		return Kind.Other;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (");
		result.append(this.kind);
		if (!this.isBuilt)
			result.append(", not built");
		result.append(")");
		if (this.kind != Kind.Other) {
			result.append(" requires ");
			result.append(this.dependencies);
		}
		return result.toString();
	}

	public Path getShortPath() {
		return this.repo.getShortPath().resolve(this.getName());
	}
}
