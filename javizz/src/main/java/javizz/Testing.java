package javizz;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.openflexo.foundation.resource.FileSystemBasedResourceCenter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.factory.DeserializationPolicy;
import org.openflexo.pamela.factory.ModelFactory;
import org.openflexo.pamela.factory.SerializationPolicy;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

/**
 * @author Victor Gambier
 *
 */

@ImplementationClass(FileSystemBasedResourceCenter.FileSystemBasedResourceCenterImpl.class)
public class Testing {

	/**
	 * Takes a path and returns the name of filename or folder that it points to, without the extension
	 * 
	 * @param path'
	 *            the path that is going to be converted into a filename
	 * @return the bottom-most filename or folder, as a String
	 */
	public static String pathToFilename(String path) {
		String filenameWithExt = path.substring(path.lastIndexOf("/") + 1);
		return FilenameUtils.removeExtension(filenameWithExt);
	}

	public static void editFileTest() throws IOException {

		System.out.println("\nEditing file...");

		String editPath = "testFiles/firstPackage/HelloWorld.java";

		CompilationUnit cuTest = StaticJavaParser.parse(new File(editPath));
		LexicalPreservingPrinter.setup(cuTest); // enables lexical preservation

		// Change 1 - Retrieving a class and changing its name
		ClassOrInterfaceDeclaration classDec;
		try {
			classDec = cuTest.getClassByName("Empty").orElse(null);
			classDec.setName("VeryEmpty");
		} catch (NullPointerException e) { // If there is no class by the name of Empty
			classDec = cuTest.getClassByName("VeryEmpty").orElse(null);
			classDec.setName("Empty");
		}

		// Change 2 - Modifying an attribute

		for (TypeDeclaration<?> typeDec : cuTest.getTypes()) {
			for (BodyDeclaration<?> member : typeDec.getMembers()) {
				member.toFieldDeclaration().ifPresent(field -> {
					for (VariableDeclarator variable : field.getVariables()) {
						String variableName = variable.getName().asString();
						if (variableName.equals("attributeAlpha")) {
							variable.setName("attributeBeta");
						}
						else if (variableName.equals("attributeBeta") || variableName.equals("attributeDefault")) {
							variable.setName("attributeAlpha");
						}
					}
				});
			}
		}

		// Writing all changes to the original file
		BufferedWriter writer = new BufferedWriter(new FileWriter(editPath));
		writer.write(LexicalPreservingPrinter.print(cuTest));
		writer.close();
	}

	public static void showClassModel(ClassModel classModel) {

		System.out.println("\nHere are the attributes as stored in the HelloWorld ClassModel:");

		List<AttributeModel> attributes = classModel.getAttributes();
		for (AttributeModel attributeModel : attributes) {
			System.out.println("\t" + attributeModel.getType() + " " + attributeModel.getName());
		}
	}

	public static void main(String[] args) throws Exception {

		// TODO: Initializing a watch service to track file changes on disk

		new Thread(new Runnable() {
			public void run() {

				Executor runner = Executors.newFixedThreadPool(1);
				runner.execute(new Runnable() {

					@Override
					public void run() {
						org.apache.commons.vfs2.FileObject listendir = null;
						try {
							FileSystemManager fsManager = VFS.getManager();
							String absolutePath = "/homes/v17gambi/git/javizz/javizz/testFiles/firstPackage/HelloWorld.java";
							listendir = fsManager.resolveFile(absolutePath);
						} catch (org.apache.commons.vfs2.FileSystemException e) {
							e.printStackTrace();
						}
						DefaultFileMonitor fm = new DefaultFileMonitor(new FileListener() {

							@Override
							public void fileDeleted(FileChangeEvent event) throws Exception {
								System.out.println(event.getFile().getName().getPath() + " Deleted.");
							}

							@Override
							public void fileCreated(FileChangeEvent event) throws Exception {
								System.out.println(event.getFile().getName().getPath() + " Created.");
							}

							@Override
							public void fileChanged(FileChangeEvent event) throws Exception {
								System.out.println(event.getFile().getName().getPath() + " Changed.");
							}
						});

						fm.setRecursive(true);
						fm.addFile(listendir);
						fm.start();

					}
				});

			}
		}).start();

		// In a separate thread, run the demonstration
		new Thread(new Runnable() {
			public void run() {
				try {

					// Reading a test folder
					String folderPath = "testFiles"; // a relative path, pointing to the testFiles directory included in the project
					ProjectLink projectLink = new ProjectLink(folderPath);

					// Testing to see if the data was properly gathered

					System.out.println("Here is an overview of the models:");
					ProjectModel projectModel = projectLink.getProjectModel();
					List<PackageModel> packages = projectModel.getPackages();

					for (PackageModel packageModel : packages) {
						System.out.println("\tpackage: " + packageModel.getName());

						List<ClassModel> classes = packageModel.getClasses();
						for (ClassModel classModel : classes) {
							System.out.println("\t\tclass: " + classModel.getName());

							List<AttributeModel> attributes = classModel.getAttributes();
							for (AttributeModel attributeModel : attributes) {
								System.out.println("\t\t\tattribute: " + attributeModel.getName());
							}

							List<MethodModel> methods = classModel.getMethods();
							for (MethodModel methodModel : methods) {
								System.out.println("\t\t\tmethod: " + methodModel.getName());
							}
						}
					}

					System.out.println("___________");

					// XML serialization
					String xmlPath = "testFiles/XMLFiles/TestSerialization.xml";
					File xmlFile = new File(xmlPath);
					xmlFile.delete(); // deleting the previous instance
					xmlFile.createNewFile();
					FileOutputStream fos = new FileOutputStream(xmlFile);
					ModelFactory projectFactory = new ModelFactory(ProjectModel.class);
					projectFactory.serialize(projectModel, fos, SerializationPolicy.EXTENSIVE, true);
					fos.close();

					// Copying the XML file
					File xmlFileCopy = new File("testFiles/XMLFiles/TestSerializationCopy.xml");
					FileUtils.copyFile(xmlFile, xmlFileCopy);

					// XML deserialization (of the copy) to obtain a model
					FileInputStream fis = new FileInputStream(xmlFileCopy);
					ProjectModel projectModelClone = (ProjectModel) projectFactory.deserialize(fis, DeserializationPolicy.RESTRICTIVE);
					xmlFileCopy.delete(); // we can delete the copy now
					fis.close();

					// XML reserialization of the model
					String xmlPath2 = "testFiles/XMLFiles/TestReserialization.xml";
					File xmlFile2 = new File(xmlPath2);
					xmlFile2.delete(); // deleting the previous instance
					xmlFile2.createNewFile();
					FileOutputStream fos2 = new FileOutputStream(xmlFile2);
					projectFactory.serialize(projectModelClone, fos2, SerializationPolicy.EXTENSIVE, true);
					fos2.close();

					/* Testing all updateModel() methods */

					projectModel.setIsWatching(true);

					// We first need to reference the PackageLink, ClassLink, ClassModel, and AttributeLink we'll be working with

					PackageLink packageLinkTarget = null;
					List<PackageLink> packageLinks = projectLink.getPackageLinks();
					for (PackageLink packageLink : packageLinks) {
						if (packageLink.getPackageModel().getName().equals("firstPackage")) {
							packageLinkTarget = packageLink;
							break;
						}
					}

					ClassLink classLinkTarget = null;
					ClassModel classModelTarget = null;
					List<ClassLink> classLinks = packageLinkTarget.getClassLinks();
					for (ClassLink classLink : classLinks) {
						if (classLink.getClassModel().getName().equals("HelloWorld")) {
							classLinkTarget = classLink;
							classModelTarget = classLink.getClassModel();
							break;
						}
					}

					AttributeLink attributeLinkTarget = null;
					List<AttributeLink> attributeLinks = classLinkTarget.getAttributeLinks();
					for (AttributeLink attributeLink : attributeLinks) {
						String attributeName = attributeLink.getAttributeModel().getName();
						if (attributeName.equals("attributeDefault")) {
							attributeLinkTarget = attributeLink;
							break;
						}
					}

					// For ProjectLink

					showClassModel(classModelTarget); // Showing the current model
					editFileTest(); // Modifying the name of a class and an attribute
					System.out.println("Updating the attributeModel via the parent ProjectLink...");
					projectLink.updateModel();
					showClassModel(classModelTarget); // Showing that the model has changed

					Thread.sleep(1000); // to give time to the other thread to run and detect the changes

					// For other classes

					editFileTest();
					System.out.println("Updating the attributeModel via the parent PackageLink...");
					packageLinkTarget.updateModel();
					showClassModel(classModelTarget);

					Thread.sleep(1000);

					editFileTest();
					System.out.println("Updating the attributeModel via the parent ClassLink...");
					classLinkTarget.updateModel();
					showClassModel(classModelTarget);

					Thread.sleep(1000);

					// Note: calling attributeLinkTarget.updateModel(); would result in unexpected behavior
					// The underlying reason for this is that, as of now, an attribute is uniquely defined by its name
					// So while it's possible to update a classModel by recreating a model from scratch using its path and updating
					// everything
					// that has
					// changed
					// The same cannot be said of an attributeModel - if we change its name, any attempt at updating it will fail -
					// From the constructor's perspective, it's as if it was a completely different attribute
					// So, in fact, it doesn't make sense to call updateModel on attributeLink if the thing we want to update is the
					// attributeModel

					// TODO Test attributeLinkTarget.updateModel() and methodLinkTarget.updateModel() (with well thought-out tests)

					// Testing file writes

					System.out.println("\nUsing setNameInFile to edit the file...");
					attributeLinkTarget.setNameInFile("attributeDefault");
					System.out.println("Updating the model...");
					classLinkTarget.updateModel();
					showClassModel(classModelTarget);

					Thread.sleep(1000);

					System.out.println("\nUsing setTypeInFile to edit the file...");
					attributeLinkTarget.setTypeInFile("int");
					System.out.println("Updating the model...");
					classLinkTarget.updateModel();
					showClassModel(classModelTarget);
					attributeLinkTarget.setTypeInFile("long"); // Reverting the change

					Thread.sleep(1000);

					// TODO vérification cohérence : classe publique = nom fichier, nom dossier = déclaration package, etc. implique
					// création de
					// nouveaux attributs, @Override + changement de nom

					System.exit(0); // Terminating all threads

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}).start();

	}
}
