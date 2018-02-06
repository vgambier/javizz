package org.openflexo.explorer.model;

import org.openflexo.explorer.util.JavaUtils.Visibility;

public class JavaEnum extends JavaType {

	public JavaEnum(JavaFile file, String name, boolean isStatic, Visibility visibility) {
		super(file, name, isStatic, visibility);
	}

	@Override
	public String getKind() {
		return "E";
	}
}
