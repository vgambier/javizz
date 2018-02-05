package org.openflexo.explorer;

/**
 * @author Fabien Dagnat
 */
public class ExternalDependency extends GradleDependency {

	private String group;
	private String name;
	private String version;

	public ExternalDependency(String group, String name, String version, Kind kind) {
		super(kind);
		this.group = group;
		this.name = name;
		this.version = version;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer(super.toString());
		result.append(":");
		result.append(this.name);
		result.append(":");
		result.append(this.version);
		return result.toString();
	}
}
