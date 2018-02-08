package org.openflexo.explorer.model;

import java.util.Map;

public class JavaEnum extends JavaType {

	public JavaEnum(JavaFile file, com.thoughtworks.qdox.model.JavaClass c) {
		super(file, c);
	}

	@Override
	public String getKind() {
		return "E";
	}

	@Override
	public void updateInfo(Map<String, JavaType> allClasses) {
		// TODO Auto-generated method stub

	}
}
