package org.openflexo.explorer.gradle;

import org.openflexo.explorer.model.Dir;

/**
 * @author Fabien Dagnat
 */
public class InternalDependency extends GradleDependency {
	private Dir project;

	public InternalDependency(Dir project, Kind kind) {
		super(kind);
		this.project = project;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer(super.toString());
		result.append(":");
		result.append(this.project.getName());
		return result.toString();
	}
}
