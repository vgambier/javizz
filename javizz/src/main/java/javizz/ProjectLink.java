package javizz;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.openflexo.pamela.ModelContextLibrary;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.pamela.factory.ModelFactory;

import models.ProjectModel;

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
	private boolean syncMode = false; // if set to true, the monitoring will lead to automatic synchronization

	/**
	 * The constructor. Takes the path to a folder containing subfolders and .java files, and modelizes that folder. Links an instance of
	 * ProjectLink with an instance of ProjectModel. Also calls the PackageModel constructor to modelize the packages contained within the
	 * folders.
	 * 
	 * @param path
	 *            the path of the folder that is going to be parsed and modelized as a project
	 * @param syncMode
	 *            a boolean that when true, enables the file monitoring process to automatically update the models
	 * @throws ModelDefinitionException
	 *             if something went wrong upon calling .getModelContext()
	 * @throws FileNotFoundException
	 *             if one of the package's files could not be parsed
	 */
	public ProjectLink(String path, boolean startMonitoring, boolean syncMode)
			throws ModelDefinitionException, FileNotFoundException, JavizzException {

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

		// Initializing a watch service (on a separate thread) to track file changes on disk on a separate thread
		if (startMonitoring)
			startFileSystemMonitoring();

	}

	/**
	 * Reads a directory containing .java files, compares it to the existing model, and updates the model
	 * 
	 * @throws ModelDefinitionException
	 * @throws FileNotFoundException
	 * @throws JavizzException
	 */
	public void updateModel() throws FileNotFoundException, ModelDefinitionException, JavizzException {

		// Generating a new model based on the input file
		ProjectLink projectLinkFile = new ProjectLink(path, false, false);
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

		if (!sourceFolder.renameTo(destFolder)) // This attempts to rename the folder, and returns true if the folder was renamed
			throw new JavizzException("Failed to rename directory");
	}

	public void startFileSystemMonitoring() {

		Executor runner = Executors.newFixedThreadPool(1);
		runner.execute(new Runnable() {

			@Override
			public void run() {

				// Defining the folder we want to monitor as a FileObject
				org.apache.commons.vfs2.FileObject listendir = null;
				try {
					FileSystemManager fsManager = VFS.getManager();
					String relativePath = "src/main"; // the folder we want to monitor
					String absolutePath = FileSystems.getDefault().getPath(relativePath).normalize().toAbsolutePath().toString();
					listendir = fsManager.resolveFile(absolutePath);
				} catch (org.apache.commons.vfs2.FileSystemException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// Defining what will happen upon file change detection
				DefaultFileMonitor fm = new DefaultFileMonitor(new CustomFileListener());

				fm.setRecursive(true); // enables monitoring for subfolders
				fm.addFile(listendir);
				fm.setDelay(50); // we'll check the filesystem for changes every X milliseconds
				fm.start(); // start monitoring

			}
		});
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

	/**
	 * @return the syncMode
	 */
	public boolean isSyncMode() {
		return syncMode;
	}

	/**
	 * @param syncMode
	 *            the syncMode to set
	 */
	public void setSyncMode(boolean syncMode) {
		this.syncMode = syncMode;
	}

	/**
	 * @return the projectLink itself
	 */
	public ProjectLink getProjectLink() {
		return this;
	}

	public class CustomFileListener implements FileListener {

		@Override
		public void fileDeleted(FileChangeEvent event) throws Exception {

			// Code here will trigger whenever the file monitoring detects a file has been deleted
			String fullPath = event.getFile().getName().getPath();
			String shortPath = fullPath.substring(fullPath.indexOf("main"));
			System.out.println("\t" + shortPath + " deleted.");
		}

		@Override
		public void fileCreated(FileChangeEvent event) throws Exception {

			// Code here will trigger whenever the file monitoring detects a file has been created
			String fullPath = event.getFile().getName().getPath();
			String shortPath = fullPath.substring(fullPath.indexOf("main"));
			System.out.println("\t" + shortPath + " created.");
		}

		@Override
		public void fileChanged(FileChangeEvent event) throws Exception {

			// Code here will trigger whenever the file monitoring detects a file has been edited

			String fullPath = event.getFile().getName().getPath();
			String shortPath = fullPath.substring(fullPath.indexOf("main"));
			System.out.println("\t" + shortPath + " changed.");

			if (isSyncMode()) { // Upon noticing the change, we only act if "sync mode" has been enabled
				System.out.println("Updating the model...");
				getProjectLink().updateModel();
			}
		}
	}
}
