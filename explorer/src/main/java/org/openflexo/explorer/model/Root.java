package org.openflexo.explorer.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.openflexo.explorer.gradle.FindIncludeBuild;

import edu.uci.ics.jung.graph.Graph;

/**
 * @author Fabien Dagnat
 */
public class Root extends GradleComposite implements Iterable<Repository> {
	private List<Repository> content;
	private Map<String, JavaPackage> packages = new HashMap<>();

	public JavaPackage registerPackage(String name) {
		JavaPackage result = packages.get(name);
		if (result == null) {
			result = new JavaPackage(name);
			packages.put(name, result);
		}
		return result;
	}

	public Root(String path) {
		super(path);
		Path settingsFile = this.getPath().resolve("settings.gradle");
		try {
			List<ASTNode> nodes = new AstBuilder().buildFromString(new String(Files.readAllBytes(settingsFile)));
			FindIncludeBuild visitor = new FindIncludeBuild();
			for (ASTNode node : nodes) {
				node.visit(visitor);
			}
			content = visitor.getResult().stream().map(p -> new Repository(this, this.getPath().resolve(p))).collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void parseBuilds() {
		for (Repository r : content)
			r.parseBuilds(this);
	}

	public List<JavaFile> getJavaFiles() {
		List<JavaFile> result = new ArrayList<>();
		for (Repository r : this)
			for (Project p : r)
				for (JavaPackagePart pa : p.getPackages())
					for (JavaFile f : pa)
						result.add(f);
		return result;
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

	@Override
	public void addToGraph(Graph<GradleDir, String> graph) {
		super.addToGraph(graph);
		for (Repository r : content) {
			r.addToGraph(graph);
			graph.addEdge("edgeTo" + r.getName(), this, r);
		}
	}

	@Override
	public Iterator<Repository> iterator() {
		return this.content.iterator();
	}

	public Map<String, JavaPackage> getPackages() {
		return this.packages;
	}
}
