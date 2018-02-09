package org.openflexo.explorer.model;

import java.nio.file.Path;
import java.util.Map;

import org.openflexo.explorer.util.JavaUtils;

import com.thoughtworks.qdox.model.JavaClass;

public abstract class JavaType {
	protected JavaFile file;
	protected com.thoughtworks.qdox.model.JavaClass qdoxClass;

	protected JavaType(JavaFile file, com.thoughtworks.qdox.model.JavaClass c) {
		this.file = file;
		this.qdoxClass = c;
	}

	@Override
	public String toString() {
		return this.getName();
	}

	public String getName() {
		return this.qdoxClass.getName();
	}

	protected String getKind() {
		return "U";
	}

	public String getInfo() {
		StringBuffer result = new StringBuffer(this.getName());
		result.append("(");
		result.append(this.getKind());
		if (this.qdoxClass.isStatic())
			result.append(", S");
		result.append(", ");
		result.append(JavaUtils.getVisibility(this.qdoxClass));
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
		sb.append(this.getName());
		return sb.toString();
	}

	public abstract void updateInfo(Map<String, JavaType> allClasses);

	public JavaClass getQdoxClass() {
		return this.qdoxClass;
	}
}
