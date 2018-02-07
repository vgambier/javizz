package org.openflexo.explorer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openflexo.explorer.model.JavaFile;
import org.openflexo.explorer.model.JavaPackage;
import org.openflexo.explorer.model.JavaPackagePart;
import org.openflexo.explorer.model.JavaType;
import org.openflexo.explorer.model.Project;
import org.openflexo.explorer.model.Repository;
import org.openflexo.explorer.model.Root;

import com.thoughtworks.qdox.JavaProjectBuilder;

/**
 * @author Fabien Dagnat
 */
public class Main {

	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			System.out.println("You must specify a starting path!");
			return;
		}
		// Creating the root
		Root root = new Root(args[0]);
		root.parseBuilds();
		// System.out.println(root);

		// Populate builder (each Java file is added to the builder)
		JavaProjectBuilder builder = new JavaProjectBuilder();
		for (JavaFile f : root.getJavaFiles())
			if (!f.getName().equals("Platform")) // qdox does not support this file (don't know why precisely)
				builder.addSource(f.getFile());

		// Modify model using builder (populate types)
		for (JavaPackage p : root.getPackages().values()) {
			p.setQdoxPackage(builder);
		}

		// print(root);

		// findDuplicateClasses(root);

		findPackageSplit(root);
	}

	private static void findPackageSplit(Root root) {
		for (JavaPackage p : root.getPackages().values()) {
			if (p.isSplit()) {
				System.out.println(p.getName());
				for (JavaPackagePart pp : p)
					System.out.println("  " + pp.getShortPath());
			}
		}
	}

	@SuppressWarnings("unused")
	private static void print(Root root) {
		// Print content
		for (Repository r : root) {
			System.out.println(r.getURL() + ":");
			for (Project p : r) {
				System.out.println("  " + p.getName());
				for (JavaPackagePart pa : p.getPackages()) {
					System.out.println("    " + pa);
					for (JavaFile f : pa) {
						System.out.println("      " + f.getName());
						for (JavaType t : f) {
							System.out.println("        " + t.getInfo());
						}
					}
				}
			}
		}

	}

	@SuppressWarnings("unused")
	private static void findDuplicateClasses(Root root) {
		Map<String, List<JavaType>> allClasses = new HashMap<>();
		for (Repository r : root)
			for (Project p : r)
				for (JavaPackagePart pa : p.getPackages())
					for (JavaFile f : pa)
						for (JavaType t : f) {
							List<JavaType> types = allClasses.get(t.getName());
							if (types == null) {
								types = new ArrayList<>();
								allClasses.put(t.getName(), types);
							}
							types.add(t);
						}
		for (Map.Entry<String, List<JavaType>> e : allClasses.entrySet())
			if (e.getValue().size() > 1) {
				StringBuffer result = new StringBuffer(e.getKey());
				for (JavaType t : e.getValue()) {
					result.append("\n  ");
					result.append(t.getShortPath());
				}
				System.out.println(result);
			}
	}
}
