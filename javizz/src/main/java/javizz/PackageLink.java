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

import models.PackageModel;
import models.ProjectModel;

/**
 * @author Victor Gambier
 *
 */

@ModelEntity
public class PackageLink {

	private PackageModel packageModel; // the corresponding model
	private String path; // the path where the package is located
	private List<ClassLink> classLinks; // the children ClassLink

	public PackageLink(ProjectModel projectModel, String path) throws ModelDefinitionException, FileNotFoundException {

		// Instantiating attributes

		ModelFactory factory = new ModelFactory(ModelContextLibrary.getModelContext(PackageModel.class)); // we need to define factory to
		// instantiate PackageModel

		this.packageModel = factory.newInstance(PackageModel.class);
		this.path = path;
		classLinks = new ArrayList<ClassLink>();

		packageModel.setName(Demonstration.pathToFilename(path));
		projectModel.addPackage(packageModel);
		packageModel.setProject(projectModel);

		// Looking for all classes
		// A class is defined as a .java file (at least for now)
		// TODO: this could be optimized, since we have already looked through the files in ProjectLink()

		File folder = new File(path);
		Collection<File> files = FileUtils.listFiles(folder, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

		for (File file : files) {
			String filename = file.getName();
			if (FilenameUtils.getExtension(filename).equals("java")) {
				// If it is, then we assume the current file is a Java class
				String filePath = file.getPath();
				ClassLink classLink = new ClassLink(packageModel, filePath); // This constructor will take care of modelizing the class and
																				// its contents
				classLinks.add(classLink);
			}
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
		PackageLink packageLinkFile = new PackageLink(packageModel.getProject(), path);
		PackageModel packageModelFile = packageLinkFile.packageModel;

		// Updating the model
		packageModel.updateWith(packageModelFile);

		// TODO: The Link instance still has the old Model as an attribute

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
				packageModel.setName(newName);
			}
		}
		else {
			throw new JavizzException("Failed to rename directory");
		}
	}

	// TODO
	/**
	 * Reads a directory containing .java files, compares it to the existing model, and updates the folder
	 * 
	 */
	public void updateFolder() {

		// calls updateFolder() on all ClassLink, and also checks if the PackageModel itself should change

	}

	/**
	 * @return the classLinks
	 */
	public List<ClassLink> getClassLinks() {
		return classLinks;
	}

	/**
	 * @return the packageModel
	 */
	public PackageModel getPackageModel() {
		return packageModel;
	}

}
