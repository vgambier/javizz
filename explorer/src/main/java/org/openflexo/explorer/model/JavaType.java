package org.openflexo.explorer.model;

import org.openflexo.explorer.util.JavaUtils.Visibility;

public abstract class JavaType {
	private String name;
	private JavaFile file;
	private boolean isStatic;
	private Visibility visibility;

	public JavaType(JavaFile file, String name, boolean isStatic, Visibility visibility) {
		this.name = name;
		this.file = file;
		this.isStatic = isStatic;
		this.visibility = visibility;
	}

	@Override
	public String toString() {
		return name;
	}

	public String getName() {
		return this.name;
	}

	protected String getKind() {
		return "U";
	}

	public String getInfo() {
		StringBuffer result = new StringBuffer(this.name);
		result.append("(");
		result.append(this.getKind());
		if (this.isStatic)
			result.append(", S");
		result.append(", ");
		result.append(this.visibility);
		result.append(")");
		return result.toString();
	}
}
