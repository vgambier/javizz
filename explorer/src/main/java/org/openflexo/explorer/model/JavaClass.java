package org.openflexo.explorer.model;

import java.util.Map;

public class JavaClass extends JavaType {
	private String parent = "?";
	private JavaClass parentClass;
	// private com.thoughtworks.qdox.model.JavaClass qdoxClass;

	public JavaClass(JavaFile file, com.thoughtworks.qdox.model.JavaClass c) {
		super(file, c);
	}

	@Override
	public String getKind() {
		return "C";
	}

	@Override
	public String getInfo() {
		StringBuffer result = new StringBuffer(super.getInfo());
		if (this.parentClass != null) {
			result.append(" <: ");
			result.append(this.parentClass.getName());
		}
		else if (!this.parent.equals("java.lang.Object")) {
			result.append(" <: [");
			result.append(this.parent);
			result.append("]");
		}
		return result.toString();
	}

	@Override
	public void updateInfo(Map<String, JavaType> allClasses) {
		com.thoughtworks.qdox.model.JavaClass parent = qdoxClass.getSuperJavaClass();
		if (parent == null) {
			this.parent = "Object";
			return;
		}
		this.parent = parent.getBinaryName();
		this.parentClass = (JavaClass) allClasses.get(this.parent);
		/*
		  if (this.parent.contains(".")) {
			// The complete name of the class is given
			this.parentClass = (JavaClass) allClasses.get(this.parent);
			return;
		}
		if (this.qdoxClass.isInner()) {
			// The class may be a sibling inner class
			com.thoughtworks.qdox.model.JavaClass declaringClass = this.qdoxClass.getDeclaringClass();
			for (com.thoughtworks.qdox.model.JavaClass sibling : declaringClass.getNestedClasses())
				if (sibling.getName().equals(this.parent)) {
					this.parent = declaringClass.getFullyQualifiedName() + "$" + this.parent;
					this.parentClass = (JavaClass) allClasses.get(this.parent);
					return;
				}
		}
		// The class may be in the current package
		String possibleParent = this.file.getPackageName() + "." + this.parent;
		this.parentClass = (JavaClass) allClasses.get(possibleParent);
		if (this.parentClass == null) {
			for (String i : this.qdoxClass.getSource().getImports()) {
				possibleParent = i + "$" + this.parent;
				System.out.println(possibleParent);
				this.parentClass = (JavaClass) allClasses.get(possibleParent);
				if (this.parentClass != null)
					return;
			}
			System.out.println("     ??????????????");
		}
		else {
			this.parent = possibleParent;
			return;
		}
		*/
	}
}
