package org.openflexo.explorer.model;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.eclipse.jgit.api.Git;
import org.openflexo.explorer.FindInclude;

public class Repository extends GradleComposite {
	private org.eclipse.jgit.lib.Repository repo;
	private Set<Project> projects;

	public Repository(Path path) {
		super(path);
		try {
			this.repo = Git.open(path.resolve(".git").toFile()).getRepository();
			Path settingsFile = this.getPath().resolve("settings.gradle");
			try {
				List<ASTNode> nodes = new AstBuilder().buildFromString(new String(Files.readAllBytes(settingsFile)));
				FindInclude visitor = new FindInclude();
				for (ASTNode node : nodes) {
					node.visit(visitor);
				}
				projects = visitor.getResult().stream().map(p -> Project.create(this.getPath(), p, true)).collect(Collectors.toSet());
			} catch (IOException e) {
				e.printStackTrace();
			}
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.getPath())) {
				for (Path p : stream) {
					if (Files.isDirectory(p) && !projects.contains(new GradleDir(p)))
						registerProject(p);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void registerProject(Path dir) {
		if (Arrays.asList(".git", ".gradle", "gradle", ".settings", "bin", "infer-out").contains(dir.getFileName().toString()))
			return;
		if (projects.contains(new GradleDir(dir)))
			return;
		List<Path> subdirs = new ArrayList<>();
		boolean hasSubProjects = false;
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			for (Path p : stream) {
				if (Files.isDirectory(p)) {
					if (projects.contains(new GradleDir(p)))
						hasSubProjects = true;
					else
						subdirs.add(p);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (hasSubProjects) {
			for (Path p : subdirs)
				registerProject(p);
			return;
		}
		projects.add(Project.create(dir, false));
	}

	public String getURL() {
		return this.repo.getConfig().getString("remote", "origin", "url");
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append(this.getURL());
		result.append(":\n    ");
		result.append(this.projects.stream().map(Project::toString).sorted().collect(Collectors.joining("\n    ", "", "")));
		return result.toString();
	}
}
