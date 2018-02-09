package org.openflexo.explorer.model;

import java.nio.file.Path;
import java.nio.file.Paths;

import edu.uci.ics.jung.graph.Graph;

/**
 * @author Fabien Dagnat
 */
public class Dir {
	private Path path;

	public Path getPath() {
		return path;
	}

	protected Dir(String path) {
		this.path = Paths.get(path).toAbsolutePath().normalize();
	}

	protected Dir(Path path) {
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
		Dir other = (Dir) obj;
		if (path == null) {
			return other.path != null;
		}
		return path.equals(other.path);
	}

	public String getName() {
		return this.path.getFileName().toString();
	}

	public void addToGraph(Graph<Dir, String> graph) {
		graph.addVertex(this);
	}

	protected boolean contains(Path location) {
		return this.path.equals(location.getParent());
	}
}
