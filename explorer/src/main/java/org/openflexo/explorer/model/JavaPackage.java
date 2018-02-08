package org.openflexo.explorer.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.thoughtworks.qdox.JavaProjectBuilder;

public class JavaPackage implements Iterable<JavaPackagePart> {
	private List<JavaPackagePart> parts = new ArrayList<>();
	private com.thoughtworks.qdox.model.JavaPackage qdoxPackage;
	private String name;

	public JavaPackage(String name) {
		this.name = name;
	}

	public void setQdoxPackage(JavaProjectBuilder builder) {
		this.qdoxPackage = builder.getPackageByName(this.name);
		if (this.qdoxPackage != null)
			for (com.thoughtworks.qdox.model.JavaClass t : new ArrayList<>(this.qdoxPackage.getClasses())) {
				// System.out.println("===" + t);
				// System.out.println("===" + t.getNestedClasses());
				for (JavaPackagePart pp : parts)
					pp.registerType(t);
				// for (com.thoughtworks.qdox.model.JavaClass ic : t.getNestedClasses())
				// registerType(ic);
			}
	}

	public String getName() {
		return this.name;
	}

	public void registerPart(JavaPackagePart packagePart) {
		this.parts.add(packagePart);
	}

	public boolean isSplit() {
		return this.parts.size() > 1;
	}

	@Override
	public Iterator<JavaPackagePart> iterator() {
		return this.parts.iterator();
	}
}
