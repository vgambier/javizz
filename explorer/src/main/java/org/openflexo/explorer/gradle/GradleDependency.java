package org.openflexo.explorer.gradle;

/**
 * @author Fabien Dagnat
 */
public class GradleDependency {

	public enum Kind {
		Compile, TestCompile, Runtime, TestRuntime
	}

	private Kind kind;

	public GradleDependency(Kind kind) {
		this.kind = kind;
	}

	@Override
	public String toString() {
		return this.kind == null ? "null" : this.kind.toString();
	}
}
