package org.openflexo.explorer;

import java.io.IOException;

import org.openflexo.explorer.model.JavaType;
import org.openflexo.explorer.model.Project;
import org.openflexo.explorer.model.Repository;
import org.openflexo.explorer.model.Root;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaPackage;

/**
 * @author Fabien Dagnat
 */
public class Main {

	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			System.out.println("You must specify a starting path!");
			return;
		}
		// try (DirectoryStream<Path> stream = Files.newDirectoryStream(root)) {
		// for (Path p : stream) {
		// System.out.println(p);
		// }
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		Root root = new Root(args[0]);
		root.parseBuilds();
		// System.out.println(root);
		JavaProjectBuilder builder = new JavaProjectBuilder();
		// Populate builder
		for (Repository r : root)
			for (Project p : r)
				for (org.openflexo.explorer.model.Package pa : p.getPackages())
					for (JavaType t : pa)
						if (!t.getName().equals("Platform"))
							builder.addSource(t.getFile());
		// System.out.println(builder.getPackages());
		// Print content
		for (Repository r : root) {
			if (r.getName().equals("connie")) {
				System.out.println(r.getURL() + ":");
				for (Project p : r) {
					System.out.println("  " + p.getName());
					for (org.openflexo.explorer.model.Package pa : p.getPackages()) {
						System.out.println("    " + pa);
						JavaPackage jp = builder.getPackageByName(pa.getName());
						if (jp != null)
							for (com.thoughtworks.qdox.model.JavaClass t : jp.getClasses()) {
								for (com.thoughtworks.qdox.model.JavaClass ic : t.getNestedClasses()) {
									System.out.println("      " + ic.getName() + "(" + getKind(ic) + ")");
								}
							}
					}
				}
			}
		}
	}

	public static String getKind(JavaClass ic) {
		if (ic.isInterface())
			return "I";
		if (ic.isEnum())
			return "E";
		return "C";
	}
}
