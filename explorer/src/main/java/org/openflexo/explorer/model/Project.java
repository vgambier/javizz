package org.openflexo.explorer.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.openflexo.explorer.FindDependenciesVisitor;
import org.openflexo.explorer.GradleDependency;

public class Project extends GradleDir {
	public enum Kind {
		API, Test, RC, Code, Other
	}

	final private Kind kind;
	final private boolean isBuilt;
	private List<GradleDependency> dependencies;

	private Project(Path path, Kind k, boolean built) {
		super(path);
		this.kind = k;
		this.isBuilt = built;
		if (k != Kind.Other) {
			Path buildFile = this.getPath().resolve("build.gradle");
			if (Files.exists(buildFile)) {
				System.out.println(buildFile);
				try {
					String buildContent = new String(Files.readAllBytes(buildFile));
					if (!buildContent.equals("")) {
						List<ASTNode> nodes = new AstBuilder().buildFromString(buildContent);
						FindDependenciesVisitor visitor = new FindDependenciesVisitor();
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

	public static Project create(Path path, String name, boolean built) {
		Path thePath = path.resolve(name.replaceAll(":", "/"));
		return new Project(thePath, getKind(thePath), built);
	}

	public static Project create(Path path, boolean built) {
		return new Project(path, getKind(path), built);
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
}
