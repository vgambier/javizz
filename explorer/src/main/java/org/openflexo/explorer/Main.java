package org.openflexo.explorer;

import java.io.IOException;

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
		// Print content
		for (Repository r : root) {
			if (r.getName().equals("connie")) {
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
	}
}
