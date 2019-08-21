package javizz;

import org.openflexo.pamela.ModelContextLibrary;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.pamela.factory.ModelFactory;

public class PackageLink {

	private PackageModel packageModel;
	private String path; // the path where the package is located

	public PackageLink(ProjectModel projectModel, String path) throws ModelDefinitionException {

		// Instantiating attributes

		ModelFactory factory = new ModelFactory(ModelContextLibrary.getModelContext(PackageModel.class)); // we need to define factory to
		// instantiate PackageModel
		this.packageModel = factory.newInstance(PackageModel.class);
		this.path = path;

		projectModel.addPackage(packageModel);

		// Looking for all classes
		// A class is defined as a .java file (at least for now)
		// TODO: parse the folder to find these files

		// When a file is found:
		// String filePath = ...
		// new ClassLink(packageModel, filePath); // This constructor will take care of modelizing the class and its contents

	}

}
