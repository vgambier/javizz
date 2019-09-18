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

import model.ProjectModel;

/**
 * Instances of this class are used to maintain a link between the project existing on the disk and the corresponding model.
 * 
 * @author Victor Gambier
 *
 */

@ModelEntity
public class ProjectLink {

	private ProjectModel projectModel; // the corresponding model
	private String path; // the path where the project is located
	private List<PackageLink> packageLinks; // the children PackageLink

	/**
	 * The constructor. Takes the path to a folder containing subfolders and .java files, and modelizes that folder. Links an instance of
	 * ProjectLink with an instance of ProjectModel. Also calls the PackageModel constructor to modelize the packages contained within the
	 * folders.
	 * 
	 * @param path
	 *            the path of the folder that is going to be parsed and modelized as a project
	 * @throws ModelDefinitionException
	 *             if something went wrong upon calling .getModelContext()
	 * @throws FileNotFoundException
	 *             if one of the package's files could not be parsed
	 */
	public ProjectLink(String path) throws ModelDefinitionException, FileNotFoundException {

		// Instantiating attributes

		// We first need to define a factory to instantiate ProjectModel

		ModelFactory factory = new ModelFactory(ModelContextLibrary.getModelContext(ProjectModel.class));
		this.projectModel = factory.newInstance(ProjectModel.class);
		this.path = path;
		packageLinks = new ArrayList<PackageLink>();

		projectModel.setName(Demonstration.pathToFilename(path));

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
			PackageLink packageLink = new PackageLink(this, packagePath); // This constructor will take care of modelizing the
																			// package and its contents
			packageLinks.add(packageLink);
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

	/**
	 * Renames the current package's folder in the file system. Changes the model accordingly.
	 * 
	 * @param newName
	 *            the new name of the folder
	 * @throws JavizzException
	 *             if the rename was not successful
	 * 
	 */
	public void renameFolder(String newName) throws JavizzException {

		File sourceFolder = new File(path);
		String newPath = path.substring(0, path.lastIndexOf("/") + 1) + newName; // same path but with the folder at the end changed
		File destFolder = new File(newPath);

		if (sourceFolder.renameTo(destFolder)) { // This attempts to rename the folder, and returns true if the folder was renamed

			// If the rename was successful, we can change the models accordingly

			if (true) { // TODO: global attribute check - only change the model if "synch mode" is enabled
				this.path = newPath;
				projectModel.setName(newName);
			}
		}
		else {
			throw new JavizzException("Failed to rename directory");
		}
	}

	/**
	 * @return the packageLinks
	 */
	public List<PackageLink> getPackageLinks() {
		return packageLinks;
	}

	/**
	 * @return the projectModel
	 */
	public ProjectModel getProjectModel() {
		return projectModel;
	}

}
