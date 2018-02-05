package org.openflexo.explorer.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.openflexo.explorer.FindIncludeBuild;

/**
 * @author Fabien Dagnat
 */
public class Root extends GradleComposite {
	private List<Repository> content;

	public Root(String path) {
		super(path);
		Path settingsFile = this.getPath().resolve("settings.gradle");
		try {
			List<ASTNode> nodes = new AstBuilder().buildFromString(new String(Files.readAllBytes(settingsFile)));
			FindIncludeBuild visitor = new FindIncludeBuild();
			for (ASTNode node : nodes) {
				node.visit(visitor);
			}
			content = visitor.getResult().stream().map(p -> new Repository(this.getPath().resolve(p))).collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void parseBuilds() {
		for (Repository r : content)
			r.parseBuilds(this);
	}

	@Override
	public String toString() {
		return super.toString() + "\n" + this.content.stream().map(Repository::toString).collect(Collectors.joining("\n  ", "  ", ""));
	}

	public List<Project> getProjects() {
		List<Project> result = new ArrayList<>();
		for (Repository r : content)
			result.addAll(r.getProjects());
		return result;
	}
}
