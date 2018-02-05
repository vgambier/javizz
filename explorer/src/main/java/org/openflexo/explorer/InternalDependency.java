package org.openflexo.explorer;

import org.openflexo.explorer.model.GradleDir;

/**
 * @author Fabien Dagnat
 */
public class InternalDependency extends GradleDependency {
	private GradleDir project;

	public InternalDependency(GradleDir project, Kind kind) {
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
