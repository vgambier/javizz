package org.openflexo.explorer.model;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.eclipse.jgit.api.Git;

import edu.uci.ics.jung.graph.Graph;

/**
 * @author Fabien Dagnat
 */
public class Repository extends GradleDir implements Iterable<Project> {
	private org.eclipse.jgit.lib.Repository repo;
	private Set<Project> projects;
	private Root root;

	public Repository(Root root, Path path) throws IOException {
		super(path);
		this.root = root;
		this.repo = Git.open(path.resolve(".git").toFile()).getRepository();
		this.visitSettings(new CodeVisitorSupport() {
			@Override
			public void visitMethodCallExpression(MethodCallExpression call) {
				if (call.getMethodAsString().equals("include"))
					for (Expression e : ((TupleExpression) call.getArguments()).getExpressions())
						Project.create(Repository.this, e.getText(), true);
				super.visitMethodCallExpression(call);
			}
		});
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.getPath())) {
			for (Path p : stream) {
				if (Files.isDirectory(p) && !projects.contains(new Dir(p)) && !Files.exists(p.resolve("settings.gradle")))
					registerProject(p);
			}
		}
	}

	private static List<String> ignoredDirs = Arrays.asList(".git", ".gradle", "gradle", ".settings", "bin", "infer-out");

	private void registerProject(Path dir) {
		if (ignoredDirs.contains(dir.getFileName().toString()) || projects.contains(new Dir(dir)))
			return;
		List<Path> subdirs = new ArrayList<>();
		boolean hasSubProjects = false;
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			for (Path p : stream) {
				if (Files.isDirectory(p)) {
					if (projects.contains(new Dir(p)))
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
		projects.add(Project.create(this, dir, false));
	}

	public String getURL() {
		return this.repo.getConfig().getString("remote", "origin", "url");
	}

	public void parseBuilds(Root root) throws IOException {
		for (Project p : projects)
			p.parseBuild(root);
	}

	public Collection<Project> getProjects() {
		return projects;
	}

	@Override
	public void addToGraph(Graph<Dir, String> graph) {
		super.addToGraph(graph);
		for (Project p : projects) {
			p.addToGraph(graph);
			graph.addEdge("edgeFrom" + this.getName() + "To" + p.getName(), this, p);
		}
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append(this.getURL());
		result.append(":\n    ");
		result.append(this.projects.stream().map(Project::toString).sorted().collect(Collectors.joining("\n    ", "", "")));
		return result.toString();
	}

	@Override
	public Iterator<Project> iterator() {
		return this.projects.iterator();
	}

	public Path getShortPath() {
		return this.getPath().getFileName();
	}

	public Root getRoot() {
		return this.root;
	}
}
