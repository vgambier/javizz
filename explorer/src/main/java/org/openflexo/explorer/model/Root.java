package org.openflexo.explorer.model;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;

import edu.uci.ics.jung.graph.Graph;

/**
 * @author Fabien Dagnat
 */
public class Root extends GradleDir implements Iterable<Repository> {
	private List<Repository> content;
	private Map<String, JavaPackage> packages = new HashMap<>();
	private Map<String, JavaType> allClasses = new HashMap<>();

	public Map<String, JavaType> getAllClasses() {
		return allClasses;
	}

	public void registerClasses() {
		for (Repository r : this)
			for (Project p : r)
				for (JavaPackagePart pa : p.getPackages())
					for (JavaFile f : pa)
						for (JavaType t : f)
							this.allClasses.put(t.getQdoxClass().getBinaryName(), t);
	}

	public void updateTypeInfo() {
		for (Repository r : this)
			for (Project p : r)
				for (JavaPackagePart pa : p.getPackages())
					for (JavaFile f : pa)
						for (JavaType t : f)
							if (t instanceof JavaClass)
								t.updateInfo(this.allClasses);
	}

	public JavaPackage registerPackage(String name) {
		JavaPackage result = packages.get(name);
		if (result == null) {
			result = new JavaPackage(name);
			packages.put(name, result);
		}
		return result;
	}

	public Root(String path) throws IOException {
		super(path);
		this.visitSettings(new CodeVisitorSupport() {
			@Override
			public void visitMethodCallExpression(MethodCallExpression call) {
				if (call.getMethodAsString().equals("includeBuild")) {
					String includedFile = ((TupleExpression) call.getArguments()).getExpression(0).getText();
					try {
						new Repository(Root.this, Root.this.getPath().resolve(Paths.get(includedFile)));
					} catch (IOException e) {}
				}
				super.visitMethodCallExpression(call);
			}
		});
	}

	public void parseBuilds() throws IOException {
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
	public void addToGraph(Graph<Dir, String> graph) {
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
