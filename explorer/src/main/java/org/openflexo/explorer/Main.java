package org.openflexo.explorer;

import org.openflexo.explorer.model.Root;

/**
 * @author Fabien Dagnat
 */
public class Main {

	public static void main(String[] args) {
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
		System.out.println(root);
	}
}
