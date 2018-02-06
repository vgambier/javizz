package org.openflexo.explorer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openflexo.explorer.model.JavaFile;
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
		// Populate builder (each Java file is added to the builder)
		for (Repository r : root)
			for (Project p : r)
				for (org.openflexo.explorer.model.Package pa : p.getPackages())
					for (JavaFile f : pa)
						if (!f.getName().equals("Platform"))
							builder.addSource(f.getFile());
		// System.out.println(builder.getPackages());
		// Modify model using builder (populate types)
		for (Repository r : root) {
			for (Project p : r) {
				for (org.openflexo.explorer.model.Package pa : p.getPackages())
					pa.setQdoxPackage(builder);
			}
		}
		// print(root);
		findDuplicateClasses(root);
	}

	private static void print(Root root) {
		// Print content
		for (Repository r : root) {
			System.out.println(r.getURL() + ":");
			for (Project p : r) {
				System.out.println("  " + p.getName());
				for (org.openflexo.explorer.model.Package pa : p.getPackages()) {
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

	private static void findDuplicateClasses(Root root) {
		Map<String, List<JavaType>> allClasses = new HashMap<>();
		for (Repository r : root)
			for (Project p : r)
				for (org.openflexo.explorer.model.Package pa : p.getPackages())
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
