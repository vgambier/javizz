package org.openflexo.explorer.model;

import java.nio.file.Path;
import java.util.Map;

import org.openflexo.explorer.util.JavaUtils;
import org.openflexo.explorer.util.JavaUtils.Visibility;

import com.thoughtworks.qdox.model.JavaClass;

public abstract class JavaType {
	private String name;
	protected JavaFile file;
	private Visibility visibility;
	protected com.thoughtworks.qdox.model.JavaClass qdoxClass;

	protected JavaType(JavaFile file, com.thoughtworks.qdox.model.JavaClass c) {
		this.name = c.getName();
		this.file = file;
		this.visibility = JavaUtils.getVisibility(c);
		this.qdoxClass = c;
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
		if (this.qdoxClass.isStatic())
			result.append(", S");
		result.append(", ");
		result.append(this.visibility);
		result.append(")");
		return result.toString();
	}

	public Path getShortPath() {
		return file.getShortPath();
	}

	public String getCompleteName() {
		StringBuffer sb = new StringBuffer(this.file.getPackageName());
		sb.append(".");
		if (this.qdoxClass.isInner()) {
			sb.append(this.file.getName());
			sb.append("$");
		}
		sb.append(this.name);
		return sb.toString();
	}

	public abstract void updateInfo(Map<String, JavaType> allClasses);

	public JavaClass getQdoxClass() {
		return this.qdoxClass;
	}
}
