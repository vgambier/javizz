package org.openflexo.explorer.model;

import java.nio.file.Path;
import java.nio.file.Paths;

import edu.uci.ics.jung.graph.Graph;

/**
 * @author Fabien Dagnat
 */
public class GradleDir {
	private Path path;

	public Path getPath() {
		return path;
	}

	protected GradleDir(String path) {
		this.path = Paths.get(path).toAbsolutePath().normalize();
	}

	protected GradleDir(Path path) {
		this.path = path.toAbsolutePath().normalize();
	}

	@Override
	public String toString() {
		return this.path.getFileName().toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		GradleDir other = (GradleDir) obj;
		if (path == null) {
			return other.path != null;
		}
		return path.equals(other.path);
	}

	public String getName() {
		return this.path.getFileName().toString();
	}

	public void addToGraph(Graph<GradleDir, String> graph) {
		graph.addVertex(this);
	}
}
