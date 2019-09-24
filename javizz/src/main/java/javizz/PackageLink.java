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

import model.PackageModel;
import model.ProjectModel;

/**
 * Instances of this class are used to maintain a link between the package existing on the disk and the corresponding model.
 * 
 * @author Victor Gambier
 *
 */

@ModelEntity
public class PackageLink extends Link<PackageModel> {

	private String path; // the path where the package is located
	private List<CompilationUnitLink> compilationUnitLinks; // the children CompilationUnitLink
	private ProjectLink projectLink; // the parent project

	/**
	 * The constructor. Takes the path to a folder containing .java files, and modelizes that folder. Links an instance of PackageLink with
	 * an instance of PackageModel. Also calls the FileModel constructor to modelize the files contained within the folders.
	 * 
	 * @param projectModel
	 *            the parent project
	 * @param path
	 *            the path pointing to the package
	 * @throws ModelDefinitionException
	 *             if something went wrong upon calling .getModelContext()
	 * @throws FileNotFoundException
	 *             if one of the package files could not be parsed
	 */
	public PackageLink(ProjectLink projectLink, String path) throws FileNotFoundException, ModelDefinitionException {

		super(new ModelFactory(ModelContextLibrary.getModelContext(PackageModel.class)).newInstance(PackageModel.class));

		ProjectModel projectModel = projectLink.getProjectModel();

		// Instantiating attributes

		this.path = path;
		compilationUnitLinks = new ArrayList<CompilationUnitLink>();
		this.projectLink = projectLink;

		model.setName(Demonstration.pathToFilename(path));
		model.setProject(projectModel);

		projectModel.addPackage(model);

		// Looking for all .java files
		// TODO: this could be optimized, since we have already looked through the files in ProjectLink()

		File folder = new File(path);
		Collection<File> files = FileUtils.listFiles(folder, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

		for (File file : files) {
			String filename = file.getName();
			if (FilenameUtils.getExtension(filename).equals("java")) {
				// If it is, then we assume the current file is a Java file
				String filePath = file.getPath();
				CompilationUnitLink compilationUnitLink = new CompilationUnitLink(this, filePath); // This constructor will take care of
																									// modelizing the file and its contents
				compilationUnitLinks.add(compilationUnitLink);
			}
		}
	}

	@Override
	public PackageLink create() throws FileNotFoundException, ModelDefinitionException {
		return new PackageLink(projectLink, path);
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

		if (!sourceFolder.renameTo(destFolder)) // This attempts to rename the folder, and returns true if the folder was not renamed
			throw new JavizzException("Failed to rename directory");
	}

	/**
	 * @return the CompilationUnitLink list
	 */
	public List<CompilationUnitLink> getCompilationUnitLinks() {
		return compilationUnitLinks;
	}

}
