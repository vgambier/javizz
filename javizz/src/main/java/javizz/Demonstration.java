package javizz;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
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

import models.AttributeModel;
import models.ClassModel;
import models.MethodModel;
import models.PackageModel;
import models.ProjectModel;

/**
 * @author Victor Gambier
 *
 */

@ImplementationClass(FileSystemBasedResourceCenter.FileSystemBasedResourceCenterImpl.class)
public class Demonstration {

	final static int WAITING_TIME = 1000; // the number of milliseconds the program will stall after each file change to let the file
											// monitoring
	// thread enough time to run

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

		String editPath = "src/main/resources/firstPackage/HelloWorld.java";

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
						String variableName = variable.getNameAsString();
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

	public static void showClassModelAttributes(ClassModel classModel) {

		System.out.println("\nHere are the attributes as stored in the HelloWorld ClassModel:");

		List<AttributeModel> attributes = classModel.getAttributes();
		for (AttributeModel attributeModel : attributes) {
			System.out.println("\t" + attributeModel.getType() + " " + attributeModel.getName());
		}
	}

	public static void showClassModelMethods(ClassModel classModel) {

		System.out.println("\nHere are the methods as stored in the HelloWorld ClassModel:");

		List<MethodModel> methods = classModel.getMethods();
		for (MethodModel methodModel : methods) {
			System.out.println("\t" + methodModel.getType() + " " + methodModel.getName());
		}
	}

	public static void main(String[] args) throws Exception {

		// Initializing a watch service to track file changes on disk on a separate thread

		Executor runner = Executors.newFixedThreadPool(1);
		runner.execute(new Runnable() {

			@Override
			public void run() {
				org.apache.commons.vfs2.FileObject listendir = null;
				try {
					FileSystemManager fsManager = VFS.getManager();
					String relativePath = "src/main";
					String absolutePath = FileSystems.getDefault().getPath(relativePath).normalize().toAbsolutePath().toString();
					listendir = fsManager.resolveFile(absolutePath);
				} catch (org.apache.commons.vfs2.FileSystemException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				DefaultFileMonitor fm = new DefaultFileMonitor(new FileListener() {

					@Override
					public void fileDeleted(FileChangeEvent event) throws Exception {
						String fullPath = event.getFile().getName().getPath();
						String shortPath = fullPath.substring(fullPath.indexOf("main"));
						System.out.println("\t" + shortPath + " deleted.");
					}

					@Override
					public void fileCreated(FileChangeEvent event) throws Exception {
						String fullPath = event.getFile().getName().getPath();
						String shortPath = fullPath.substring(fullPath.indexOf("main"));
						System.out.println("\t" + shortPath + " created.");
					}

					@Override
					public void fileChanged(FileChangeEvent event) throws Exception {
						String fullPath = event.getFile().getName().getPath();
						String shortPath = fullPath.substring(fullPath.indexOf("main"));
						System.out.println("\t" + shortPath + " changed.");
					}
				});

				fm.setRecursive(true);
				fm.addFile(listendir);
				fm.start();

			}
		});

		// In a separate thread, run the demonstration

		// Reading a test folder
		String folderPath = "src/main/resources"; // a relative path, pointing to the resources directory included in the project
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
					System.out.println("\t\t\tattribute: " + attributeModel.getType() + " " + attributeModel.getName());
				}

				List<MethodModel> methods = classModel.getMethods();
				for (MethodModel methodModel : methods) {
					System.out.println("\t\t\tmethod: " + methodModel.getType() + " " + methodModel.getName());
				}
			}
		}

		// XML serialization
		String xmlPath = "src/main/resources/XMLFiles/TestSerialization.xml";
		File xmlFile = new File(xmlPath);
		xmlFile.delete(); // deleting the previous instance
		xmlFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(xmlFile);
		ModelFactory projectFactory = new ModelFactory(ProjectModel.class);
		projectFactory.serialize(projectModel, fos, SerializationPolicy.EXTENSIVE, true);
		fos.close();

		// Copying the XML file
		File xmlFileCopy = new File("src/main/resources/XMLFiles/TestSerializationCopy.xml");
		FileUtils.copyFile(xmlFile, xmlFileCopy);

		// XML deserialization (of the copy) to obtain a model
		FileInputStream fis = new FileInputStream(xmlFileCopy);
		ProjectModel projectModelClone = (ProjectModel) projectFactory.deserialize(fis, DeserializationPolicy.RESTRICTIVE);
		xmlFileCopy.delete(); // we can delete the copy now
		fis.close();

		// XML reserialization of the model
		String xmlPath2 = "src/main/resources/XMLFiles/TestReserialization.xml";
		File xmlFile2 = new File(xmlPath2);
		xmlFile2.delete(); // deleting the previous instance
		xmlFile2.createNewFile();
		FileOutputStream fos2 = new FileOutputStream(xmlFile2);
		projectFactory.serialize(projectModelClone, fos2, SerializationPolicy.EXTENSIVE, true);
		fos2.close();

		/* Testing all updateModel() methods */

		projectModel.setIsWatching(true); // Enabling real-time model monitoring

		// We first need to reference the various Links and Models we'll be working with

		PackageLink packageLinkFirst = null;
		PackageLink packageLinkSecond = null;
		List<PackageLink> packageLinks = projectLink.getPackageLinks();
		for (PackageLink packageLink : packageLinks) {
			String packageName = packageLink.getPackageModel().getName();
			if (packageName.equals("firstPackage")) {
				packageLinkFirst = packageLink;
			}
			else if (packageName.equals("secondPackage")) {
				packageLinkSecond = packageLink;
			}
		}

		ClassLink classLinkHello = null;
		ClassModel classModelHello = null;
		List<ClassLink> classLinksFirst = packageLinkFirst.getClassLinks();
		for (ClassLink classLink : classLinksFirst) {
			String className = classLink.getClassModel().getName();
			if (className.equals("HelloWorld")) {
				classLinkHello = classLink;
				classModelHello = classLink.getClassModel();
				break;
			}
		}

		ClassLink classLinkGoodbye = null;
		List<ClassLink> classLinksSecond = packageLinkSecond.getClassLinks();
		for (ClassLink classLink : classLinksSecond) {
			String className = classLink.getClassModel().getName();
			if (className.equals("GoodbyeWorld")) {
				classLinkGoodbye = classLink;
				break;
			}
		}

		AttributeLink attributeLinkTarget = null;
		AttributeLink attributeLinkSsn = null;
		List<AttributeLink> attributeLinksHello = classLinkHello.getAttributeLinks();
		for (AttributeLink attributeLink : attributeLinksHello) {
			String attributeName = attributeLink.getAttributeModel().getName();
			if (attributeName.equals("attributeDefault")) {
				attributeLinkTarget = attributeLink;
			}
			else if (attributeName.equals("ssn")) {
				attributeLinkSsn = attributeLink;
			}
		}

		MethodLink methodLinkTarget = null;
		List<MethodLink> methodLinks = classLinkHello.getMethodLinks();
		for (MethodLink methodLink : methodLinks) {
			String methodName = methodLink.getMethodModel().getName();
			if (methodName.equals("uselessMethod")) {
				methodLinkTarget = methodLink;
				break;
			}
		}

		// For ProjectLink

		editFileTest(); // Modifying the name of a class and an attribute
		System.out.println("Updating the attributeModel via the parent ProjectLink...");
		projectLink.updateModel();
		showClassModelAttributes(classModelHello); // Showing that the model has changed

		Thread.sleep(WAITING_TIME); // We wait in order to give time to the other thread to run and detect the changes on the file system

		// For other classes

		editFileTest();
		System.out.println("Updating the attributeModel via the parent PackageLink...");
		packageLinkFirst.updateModel();
		showClassModelAttributes(classModelHello);

		Thread.sleep(WAITING_TIME);

		editFileTest();
		System.out.println("Updating the attributeModel via the parent ClassLink...");
		classLinkHello.updateModel();
		showClassModelAttributes(classModelHello);

		Thread.sleep(WAITING_TIME);

		// Note: calling attributeLinkTarget.updateModel(); would result in unexpected behavior
		// The underlying reason for this is that, as of now, an attribute is uniquely defined by its name
		// So while it's possible to update a classModel by recreating a model from scratch using its path and updating
		// everything that has changed
		// The same cannot be said of an attributeModel - if we change its name, any attempt at updating it will fail -
		// From the constructor's perspective, it's as if it was a completely different attribute
		// So, in fact, it doesn't make sense to call updateModel on attributeLink if the thing we want to update is the
		// attributeModel

		// TODO Test attributeLinkTarget.updateModel() and methodLinkTarget.updateModel() (with well thought-out tests)

		// Testing file writes

		System.out.println("\nUsing setNameInFile to edit the name of an attribute in the file...");
		attributeLinkTarget.setNameInFile("attributeDefault");
		System.out.println("Updating the file...");
		showClassModelAttributes(classModelHello);

		Thread.sleep(WAITING_TIME);

		System.out.println("\nUsing setTypeInFile to edit the type of an attribute in the file...");
		attributeLinkTarget.setTypeInFile("int");
		System.out.println("Updating the file...");
		showClassModelAttributes(classModelHello);
		attributeLinkTarget.setTypeInFile("long"); // Reverting the change

		Thread.sleep(WAITING_TIME);

		showClassModelMethods(classModelHello);
		System.out.println("\nUsing setNameInFile to edit the name of a method in the file...");
		methodLinkTarget.setNameInFile("veryFastMethod");
		System.out.println("Updating the file...");
		showClassModelMethods(classModelHello);

		Thread.sleep(WAITING_TIME);

		System.out.println("\nUsing setTypeInFile to edit the type of a method in the file...");
		methodLinkTarget.setTypeInFile("int");
		System.out.println("Updating the file...");
		showClassModelMethods(classModelHello);

		Thread.sleep(WAITING_TIME);

		// Reverting the change
		methodLinkTarget.setTypeInFile("long");
		methodLinkTarget.setNameInFile("uselessMethod");

		Thread.sleep(WAITING_TIME);

		// Testing setNameInFile for ClassLink
		System.out.println("\nUsing setTypeInFile to edit the name of a class in the file...");
		classLinkHello.setNameInFile("HelloWorldRemastered");
		Thread.sleep(WAITING_TIME);
		classLinkHello.setNameInFile("HelloWorld"); // Reverting the change

		// Testing renameFolder for PackageLink
		System.out.println("\nUsing renameFolder to edit the name of a package folder...");
		packageLinkFirst.renameFolder("betterPackage");
		Thread.sleep(WAITING_TIME);
		packageLinkFirst.renameFolder("firstPackage"); // Reverting the change

		// Testing renameFolder for ProjectLink
		System.out.println("\nUsing renameFolder to edit the name of a project folder...");
		projectLink.renameFolder("testing");
		Thread.sleep(WAITING_TIME);
		projectLink.renameFolder("resources"); // Reverting the change
		Thread.sleep(WAITING_TIME);

		// Testing moveToNewClass
		/*
		System.out.println("\nUsing moveToNewClass to move the ssn attribute to GoodbyeWorld...");
		attributeLinkSsn.moveToNewClass(classLinkGoodbye);
		Thread.sleep(WAITING_TIME);
		*/

		// TODO vérification cohérence : classe publique = nom fichier, nom dossier = déclaration package, etc. implique
		// création de
		// nouveaux attributs, @Override + changement de nom

		System.exit(0); // Terminating all threads

	}
}
