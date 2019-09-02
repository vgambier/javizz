package javizz;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
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

		System.out.println("Editing file...");

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
						if (variableName.equals("newAttribute")) {
							variable.setName("veryNewAttribute");
						}
						else if (variableName.equals("veryNewAttribute")) {
							variable.setName("newAttribute");
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

		List<AttributeModel> attributes = classModel.getAttributes();
		for (AttributeModel attributeModel : attributes) {
			System.out.println("\t" + attributeModel.getName());
		}
	}

	public static void main(String[] args) throws Exception {

		// Reading a test folder
		String folderPath = "testFiles"; // a relative path, pointing to the testFiles directory included in the project
		ProjectLink projectLink = new ProjectLink(folderPath);

		// Testing to see if the data was properly gathered

		System.out.println("Here is an overview of the models:");
		ProjectModel projectModel = projectLink.getProjectModel();
		List<PackageModel> packages = projectModel.getPackages();

		for (PackageModel packageModel : packages) {
			System.out.println("package: " + packageModel.getName());

			List<ClassModel> classes = packageModel.getClasses();
			for (ClassModel classModel : classes) {
				System.out.println("\tclass: " + classModel.getName());

				List<AttributeModel> attributes = classModel.getAttributes();
				for (AttributeModel attributeModel : attributes) {
					System.out.println("\t\tattribute: " + attributeModel.getName());
				}

				List<MethodModel> methods = classModel.getMethods();
				for (MethodModel methodModel : methods) {
					System.out.println("\t\tmethod: " + methodModel.getName());
				}
			}
		}

		System.out.println("");

		// XML serialization
		String xmlPath = "testFiles/XMLFiles/TestSerialization.xml";
		File xmlFile = new File(xmlPath);
		xmlFile.delete(); // deleting the previous instance
		xmlFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(xmlFile);
		ModelFactory projectFactory = new ModelFactory(ProjectModel.class);
		projectFactory.serialize(projectModel, fos, SerializationPolicy.EXTENSIVE, true);

		// Copying the XML file
		File xmlFileCopy = new File("testFiles/XMLFiles/TestSerializationCopy.xml");
		FileUtils.copyFile(xmlFile, xmlFileCopy);

		// XML deserialization (of the copy) to obtain a model
		FileInputStream fis = new FileInputStream(xmlFileCopy);
		ProjectModel projectModelClone = (ProjectModel) projectFactory.deserialize(fis, DeserializationPolicy.RESTRICTIVE);
		xmlFileCopy.delete(); // we can delete the copy now

		// XML reserialization of the model
		String xmlPath2 = "testFiles/XMLFiles/TestReserialization.xml";
		File xmlFile2 = new File(xmlPath2);
		xmlFile2.delete(); // deleting the previous instance
		xmlFile2.createNewFile();
		FileOutputStream fos2 = new FileOutputStream(xmlFile2);
		projectFactory.serialize(projectModelClone, fos2, SerializationPolicy.EXTENSIVE, true);

		// TODO Setting up a listener to automatically detect all changes made to the files on the disk

		/*
		 * 
		 * 		regarder les méthodes de java modernes plutôt qu'openflexo
		par exemple : java nio
		directory watcher
		
		PamelaResourceFactory resourceFactory = new PamelaResourceFactory();
		FileSystemBasedResourceCenter resourceCenter = new PamelaResourceModelFactory(FileSystemBasedResourceCenter.class);
		;
		
		File folder = new File(folderPath);
		
		resourceCenter.fileModified(folder); // Called when a {@link File} has been modified in directory representing this ResourceCenter
		resourceCenter.fileAdded(folder); // When one has been discovered
		resourceCenter.fileDeleted(folder); // When one has been deleted
		
		*/

		/* Testing all updateModel() methods */

		projectModel.setIsWatching(true);

		System.out.println("________");
		System.out.println(projectModel);
		System.out.println(projectModel.getName());
		System.out.println(projectModel.getIsWatching());
		System.out.println("________");

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
			if (attributeName.equals("newAttribute") || attributeName.equals("veryNewAttribute")) {
				attributeLinkTarget = attributeLink;
				break;
			}
		}

		// For ProjectLink

		System.out.println("Here are the attributes as stored in the HelloWorld ClassModel:");
		showClassModel(classModelTarget); // Showing the current model
		editFileTest(); // Modifying a file
		System.out.println("Updating the model...");
		projectLink.updateModel();
		System.out.println("Here are the attributes as stored in the HelloWorld ClassModel:");
		showClassModel(classModelTarget); // Showing that the model has changed

		// For other classes

		editFileTest();
		System.out.println("Updating the attributeModel via the parent PackageLink...");
		packageLinkTarget.updateModel();
		System.out.println("Here are the attributes as stored in the HelloWorld ClassModel:");
		showClassModel(classModelTarget);

		editFileTest();
		System.out.println("Updating the attributeModel via the parent ClassLink...");
		classLinkTarget.updateModel();
		System.out.println("Here are the attributes as stored in the HelloWorld ClassModel:");
		showClassModel(classModelTarget);

		editFileTest();
		System.out.println("Updating the attributeModel via AttributeLink...");
		attributeLinkTarget.updateModel();
		System.out.println("Here are the attributes as stored in the HelloWorld ClassModel:");
		showClassModel(classModelTarget);

		classLinkTarget.getMethodLinks().get(0).updateModel();

		// Detecting changes on the disk
		// TODO

		// TODO vérification cohérence : classe publique = nom fichier, nom dossier = déclaration package, etc. implique création de
		// nouveaux attributs, @Override + changement de nom

	}
}
