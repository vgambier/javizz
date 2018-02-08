package org.openflexo.explorer.model;

import java.util.Map;

public class JavaAnnotation extends JavaType {

	public JavaAnnotation(JavaFile file, com.thoughtworks.qdox.model.JavaClass c) {
		super(file, c);
	}

	@Override
	public String getKind() {
		return "A";
	}

	@Override
	public void updateInfo(Map<String, JavaType> allClasses) {
		// TODO Auto-generated method stub

	}

}
