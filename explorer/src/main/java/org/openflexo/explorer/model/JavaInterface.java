package org.openflexo.explorer.model;

import java.util.Map;

public class JavaInterface extends JavaType {

	public JavaInterface(JavaFile file, com.thoughtworks.qdox.model.JavaClass c) {
		super(file, c);
	}

	@Override
	public String getKind() {
		return "I";
	}

	@Override
	public void updateInfo(Map<String, JavaType> allClasses) {
		// TODO Auto-generated method stub

	}
}
