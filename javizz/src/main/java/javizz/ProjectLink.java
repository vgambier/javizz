package javizz;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.openflexo.pamela.ModelContextLibrary;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.pamela.factory.ModelFactory;

@ModelEntity
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
	 * @throws FileNotFoundException
	 */
	public ProjectLink(String path) throws ModelDefinitionException, FileNotFoundException {

		// Instantiating attributes

		ModelFactory factory = new ModelFactory(ModelContextLibrary.getModelContext(ProjectModel.class)); // we need to define factory to
																											// instantiate ProjectModel
		this.projectModel = factory.newInstance(ProjectModel.class);
		this.path = path;

		projectModel.setName(Testing.pathToFilename(path));
		projectModel.setProjectLink(this);

		/* Looking for all packages
		 * A package is defined as a folder which contains .java files at its root
		 */

		List<String> packagePathList = new ArrayList<String>();

		// Listing all files in the folder and its subfolders, recursively
		// TODO: doesn't handle nested subfolders well
		File folder = new File(path);
		Collection<File> files = FileUtils.listFiles(folder, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

		// For each file, we check if it's a .java file
		for (File file : files) {
			String filename = file.getName();
			if (FilenameUtils.getExtension(filename).equals("java")) {
				// If it is, then we assume the current folder is a package
				String currentPath = file.getParent();

				// We add it to the list of packages, unless it's already in there
				if (!packagePathList.contains(currentPath))
					packagePathList.add(currentPath);
			}
		}

		// For each package we've found...
		for (String packagePath : packagePathList) {
			new PackageLink(projectModel, packagePath); // This constructor will take care of modelizing the package and its contents
		}
	}

	/**
	 * Reads a directory containing .java files, compares it to the existing model, and updates the model
	 * 
	 * @throws ModelDefinitionException
	 * @throws FileNotFoundException
	 */
	public void updateModel() throws FileNotFoundException, ModelDefinitionException {

		// Generating a new model based on the input file
		ProjectLink projectLinkFile = new ProjectLink(path);
		ProjectModel projectModelFile = projectLinkFile.projectModel;

		// Updating the model
		projectModel.updateWith(projectModelFile);
	}

	// TODO: same methods for the other classes
	// TODO: updateFolder method

	/**
	 * @return the projectModel
	 */
	public ProjectModel getProjectModel() {
		return projectModel;
	}

}
