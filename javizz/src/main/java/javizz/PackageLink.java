package javizz;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.openflexo.pamela.ModelContextLibrary;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.pamela.factory.ModelFactory;

public class PackageLink {

	private PackageModel packageModel;
	private String path; // the path where the package is located

	public PackageLink(ProjectModel projectModel, String path) throws ModelDefinitionException, FileNotFoundException {

		// Instantiating attributes

		ModelFactory factory = new ModelFactory(ModelContextLibrary.getModelContext(PackageModel.class)); // we need to define factory to
		// instantiate PackageModel
		this.packageModel = factory.newInstance(PackageModel.class);
		this.path = path;

		packageModel.setName(Testing.pathToFilename(path));
		projectModel.addPackage(packageModel);

		// Looking for all classes
		// A class is defined as a .java file (at least for now)
		// TODO: this could be optimized, since we have already looked through the files in ProjectLink()

		File folder = new File(path);
		Collection<File> files = FileUtils.listFiles(folder, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

		System.out.println("");

		for (File file : files) {
			String filename = file.getName();
			if (FilenameUtils.getExtension(filename).equals("java")) {
				// If it is, then we assume the current file is a Java class
				String filePath = file.getPath();
				new ClassLink(packageModel, filePath); // This constructor will take care of modelizing the class and its contents
			}
		}
	}
}
