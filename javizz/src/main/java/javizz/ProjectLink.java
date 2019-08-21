package javizz;

import org.openflexo.pamela.ModelContextLibrary;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.pamela.factory.ModelFactory;

public class ProjectLink {

	private ProjectModel projectModel;
	private String path; // the path where the project is located

	/**
	 * The constructor. Takes the path to a folder containing subfolders and .java files, and modelizes them. Links an instance of
	 * ProjectLink with an instance of ProjectModel. Also calls other package model constructors to modelize the packages contained within
	 * the folders.
	 * 
	 * @param path
	 *            the path of the folder that is going to be parsed and modelized as a project
	 * @throws ModelDefinitionException
	 */
	public ProjectLink(String path) throws ModelDefinitionException {

		// Instantiating attributes

		ModelFactory factory = new ModelFactory(ModelContextLibrary.getModelContext(ProjectModel.class)); // we need to define factory to
																											// instantiate ProjectModel
		this.projectModel = factory.newInstance(ProjectModel.class);
		this.path = path;

		projectModel.setName(Testing.pathToFilename(path));

		// Looking for all packages
		// A package is defined as a folder which contains .java files at its root
		// TODO: parse the folder to find these subfolders

		// When a package is found:
		// String packagePath = ...
		// new PackageLink(projectModel, packagePath); // This constructor will take care of modelizing the package and its contents
	}

}
