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

import model.AttributeModel;
import model.ClassModel;
import model.CompilationUnitModel;
import model.MethodModel;
import model.PackageModel;
import model.ProjectModel;

/**
 * @author Victor Gambier
 *
 */

@ImplementationClass(FileSystemBasedResourceCenter.FileSystemBasedResourceCenterImpl.class)
public class Demonstration {

	final static int WAITING_TIME = 1000; // the number of milliseconds the program will stall after each file change to let the file
											// monitoring thread enough time to run. as such, this number should always be higher than the
											// delay between each file system check

	/**
	 * Takes a path and returns the name of filename or folder that it points to, without the extension
	 * 
	 * @param path'
	 *            the path that is going to be converted into a filename
	 * @return the name of the file or folder that the path points to, as a String
	 */
	public static String pathToFilename(String path) {
		String filenameWithExt = path.substring(path.lastIndexOf("/") + 1);
		return FilenameUtils.removeExtension(filenameWithExt);
	}

	/**
	 * This method will apply two changes in HelloWorld.java -1) rename the Empty class to VeryEmpty (and vice versa); and 2) rename the
	 * attributeBeta / attributeDefault attribute to attributeAlpha, or rename attributeAlpha to attributeBeta. That way, if called several
	 * times in a row, the class name will alternate between Empty and VeryEmpty, while the attribute name will alternate between
	 * attributeAlpha and attributeBeta
	 * 
	 * @throws IOException
	 *             if there was an issue during the file parsing or the file write
	 */
	public static void editFileTest() throws IOException {

		System.out.println("\nEditing file (changing the names of one attribute and one class)...");

		String editPath = "src/main/resources/firstPackage/HelloWorld.java"; // the file we'll modify

		CompilationUnit cuTest = StaticJavaParser.parse(new File(editPath));
		LexicalPreservingPrinter.setup(cuTest); // enables lexical preservation

		// Change 1 - Retrieving a class and changing its name
		ClassOrInterfaceDeclaration classDec;

		classDec = cuTest.getClassByName("Empty").orElse(null);

		if (classDec != null) // if the Empty class exists
			classDec.setName("VeryEmpty"); // we change its name
		else { // if it doesn't...
			classDec = cuTest.getClassByName("VeryEmpty").orElse(null); // we rename the VeryEmpty class instead
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

	/**
	 * Displays an overview of the attributes stored in the input CompilationUnitModel
	 * 
	 * @param projectLink
	 *            the ProjectLink in which the input CompilationUnitModel is modelized
	 * @param className
	 *            the name of the CompilationUnitModel whose attributes will be displayed
	 */
	public static void showClassModelAttributes(ProjectLink projectLink, String className) {

		// Retrieving the ClassModel

		CompilationUnitModel compilationUnitModel = null;

		ProjectModel projectModel = projectLink.getProjectModel();
		List<PackageModel> packages = projectModel.getPackages();
		for (PackageModel packageModel : packages) {
			List<CompilationUnitModel> compilationUnits = packageModel.getCompilationUnits();
			for (CompilationUnitModel currentCompilationUnitModel : compilationUnits) {
				String fileName = currentCompilationUnitModel.getName();
				if (fileName.equals(className))
					compilationUnitModel = currentCompilationUnitModel;
			}
		}

		// Displaying its attributes

		System.out.println("\nHere are the attributes as stored in the " + compilationUnitModel.getName() + " ClassModel ("
				+ compilationUnitModel.getClass() + "):");

		List<ClassModel> classes = compilationUnitModel.getClasses();
		for (ClassModel classModel : classes) {
			List<AttributeModel> attributes = classModel.getAttributes();
			for (AttributeModel attributeModel : attributes) {
				System.out.println("\t" + attributeModel.getType() + " " + attributeModel.getName());
			}
		}
	}

	/**
	 * Displays an overview of the methods stored in the input CompilationUnitModel
	 * 
	 * @param projectLink
	 *            the ProjectLink in which the input CompilationUnitModel is modelized
	 * @param className
	 *            the name of the CompilationUnitModel whose methods will be displayed
	 */
	public static void showClassModelMethods(ProjectLink projectLink, String className) {

		// Retrieving the ClasstModel

		CompilationUnitModel compilationUnitModel = null;

		ProjectModel projectModel = projectLink.getProjectModel();
		List<PackageModel> packages = projectModel.getPackages();
		for (PackageModel packageModel : packages) {
			List<CompilationUnitModel> compilationUnits = packageModel.getCompilationUnits();
			for (CompilationUnitModel currentCompilationUnitModel : compilationUnits) {
				String fileName = currentCompilationUnitModel.getName();
				if (fileName.equals(className))
					compilationUnitModel = currentCompilationUnitModel;
			}
		}

		// Displaying its attributes

		System.out.println("\nHere are the methods as stored in the " + compilationUnitModel.getName() + " ClassModel ("
				+ compilationUnitModel.getClass() + "):");

		List<ClassModel> classes = compilationUnitModel.getClasses();
		for (ClassModel classModel : classes) {
			List<MethodModel> attributes = classModel.getMethods();
			for (MethodModel methodModel : attributes) {
				System.out.println("\t" + methodModel.getType() + " " + methodModel.getName());
			}
		}
	}

	public static void main(String[] args) throws Exception {

		// We need to rename two folders that may have been renamed during the last iteration of this
		File firstFolder = new File("src/main/testing");
		if (firstFolder.exists()) {
			if (!firstFolder.renameTo(new File("src/main/resources"))) // Renaming the folder
				throw new JavizzException("Failed to rename directory"); // Triggered if the renaming failed
		}

		File secondFolder = new File("src/main/resources/betterPackage");
		if (secondFolder.exists()) {
			if (!secondFolder.renameTo(new File("src/main/resources/firstPackage")))
				throw new JavizzException("Failed to rename directory");
		}

		// Copying the file template onto the file we'll be modifying for the demonstration
		String templatePath = "src/main/resources/firstPackage/HelloWorldTemplate";
		String testPath = "src/main/resources/firstPackage/HelloWorld.java";
		System.out.println("Copying the template onto HelloWorld.java...");
		CompilationUnit cuTemplate = StaticJavaParser.parse(new File(templatePath));
		LexicalPreservingPrinter.setup(cuTemplate);
		BufferedWriter writer = new BufferedWriter(new FileWriter(testPath));
		writer.write(LexicalPreservingPrinter.print(cuTemplate));
		writer.close();

		templatePath = "src/main/resources/secondPackage/GoodbyeWorldTemplate";
		testPath = "src/main/resources/secondPackage/GoodbyeWorld.java";
		System.out.println("Copying the template onto GoodbyeWorld.java...");
		cuTemplate = StaticJavaParser.parse(new File(templatePath));
		LexicalPreservingPrinter.setup(cuTemplate);
		writer = new BufferedWriter(new FileWriter(testPath));
		writer.write(LexicalPreservingPrinter.print(cuTemplate));
		writer.close();

		// Reading a test folder
		String folderPath = "src/main/resources"; // a relative path, pointing to the resources directory included in the project
		ProjectLink projectLink = new ProjectLink(folderPath, true, false); // Instantiating ProjectLink, with file monitoring enabled

		// Testing to see if the data was properly gathered

		System.out.println("Here is an overview of the models:");
		ProjectModel projectModel = projectLink.getProjectModel();
		List<PackageModel> packages = projectModel.getPackages();

		for (PackageModel packageModel : packages) {
			System.out.println("\tpackage: " + packageModel.getName());

			List<CompilationUnitModel> compilationUnits = packageModel.getCompilationUnits();
			for (CompilationUnitModel compilationUnitModel : compilationUnits) {
				System.out.println("\t\tcompilation unit: " + compilationUnitModel.getName());

				List<ClassModel> classes = compilationUnitModel.getClasses();
				for (ClassModel classModel : classes) {
					System.out.println("\t\t\tclass: " + classModel.getName());

					List<AttributeModel> attributes = classModel.getAttributes();
					for (AttributeModel attributeModel : attributes) {
						System.out.println("\t\t\t\tattribute: " + attributeModel.getType() + " " + attributeModel.getName());
					}

					List<MethodModel> methods = classModel.getMethods();
					for (MethodModel methodModel : methods) {
						System.out.println("\t\t\t\tmethod: " + methodModel.getType() + " " + methodModel.getName());
					}
				}
			}
		}

		// XML serialization

		System.out.println("\nXML serialization...");

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

		Thread.sleep(WAITING_TIME);

		/* Testing other methods */

		projectModel.setWatching(true); // Enabling real-time model monitoring

		// We first need to reference the various Links and Models we'll be working with

		PackageLink packageLinkFirst = null;
		PackageLink packageLinkSecond = null;
		List<PackageLink> packageLinks = projectLink.getPackageLinks();
		for (PackageLink packageLink : packageLinks) {
			String packageName = packageLink.getModel().getName();
			if (packageName.equals("firstPackage")) {
				packageLinkFirst = packageLink;
			}
			else if (packageName.equals("secondPackage")) {
				packageLinkSecond = packageLink;
			}
		}

		CompilationUnitLink compilationUnitLinkHello = null;
		List<CompilationUnitLink> compilationUnitLinksFirst = packageLinkFirst.getCompilationUnitLinks();
		for (CompilationUnitLink compilationUnitLink : compilationUnitLinksFirst) {
			String compilationUnitName = compilationUnitLink.getModel().getName();
			if (compilationUnitName.equals("HelloWorld")) {
				compilationUnitLinkHello = compilationUnitLink;
				break;
			}
		}

		CompilationUnitLink compilationUnitLinkGoodbye = null;
		List<CompilationUnitLink> compilationUnitLinksSecond = packageLinkSecond.getCompilationUnitLinks();
		for (CompilationUnitLink compilationUnitLink : compilationUnitLinksSecond) {
			String compilationUnitName = compilationUnitLink.getModel().getName();
			if (compilationUnitName.equals("GoodbyeWorld")) {
				compilationUnitLinkGoodbye = compilationUnitLink;
				break;
			}
		}

		ClassLink classLinkTarget = null;
		List<ClassLink> classLinksHello = compilationUnitLinkHello.getClassLinks();
		for (ClassLink classLink : classLinksHello) {
			String className = classLink.getModel().getName();
			if (className.equals("HelloWorld")) {
				classLinkTarget = classLink;
				break;
			}
		}

		AttributeLink attributeLinkTarget = null;
		List<AttributeLink> attributeLinksHello = classLinkTarget.getAttributeLinks();
		for (AttributeLink attributeLink : attributeLinksHello) {
			String attributeName = attributeLink.getModel().getName();
			if (attributeName.equals("attributeDefault")) {
				attributeLinkTarget = attributeLink;
				break;
			}
		}

		AttributeLink attributeLinkSsn = null;
		for (AttributeLink attributeLink : attributeLinksHello) {
			String attributeName = attributeLink.getModel().getName();
			if (attributeName.equals("ssn")) {
				attributeLinkSsn = attributeLink;
				break;
			}
		}

		// Testing moveToNewFile
		System.out.println("\nUsing moveToNewFile to move the ssn attribute to GoodbyeWorld.java...");
		attributeLinkSsn.moveToNewFile(compilationUnitLinkGoodbye);
		Thread.sleep(WAITING_TIME);

		System.out.println("\nTesting non-automatic updateModel methods...");

		// For ProjectLink

		editFileTest(); // Modifying the name of a class and an attribute
		Thread.sleep(WAITING_TIME); // We wait in order to give time to the other thread to run and detect the changes on the file system
		System.out.println("Updating the attributeModel via the parent ProjectLink...");
		projectLink.updateModel();
		showClassModelAttributes(projectLink, "HelloWorld"); // Showing that the model has changed

		// For PackageLink

		editFileTest();
		Thread.sleep(WAITING_TIME);
		System.out.println("Updating the attributeModel via the parent PackageLink...");
		packageLinkFirst.updateModel();
		showClassModelAttributes(projectLink, "HelloWorld");

		// Testing file writes

		System.out.println("\nTesting file writes...");
		projectLink.setSyncMode(true); // Enabling the file monitoring to change the model in real-time

		System.out.println("\nUsing setNameInFile to edit the name of an attribute in the file to 'attributeDefault'...");
		attributeLinkTarget.setNameInFile("attributeDefault");
		Thread.sleep(WAITING_TIME);
		showClassModelAttributes(projectLink, "HelloWorld");

		// Since we've updated the general model, there is a new up-to-date attributeLink, and attributeLinkTarget is still the old link.
		// Therefore, we need to grab the relevant link once again.
		// Note: it might not make a lot of sense for the Link to be updated and rendered obsolete when we update the model, but for now
		// this has to happen as the AttributeLink and MethodLink contain a name attribute that is susceptible to change

		for (AttributeLink attributeLink : attributeLinksHello) {
			String attributeName = attributeLink.getModel().getName();
			if (attributeName.equals("attributeDefault")) {
				attributeLinkTarget = attributeLink;
				break;
			}
		}

		System.out.println("\nUsing setTypeInFile to edit the type of an attribute in the file to 'int'...");
		attributeLinkTarget.setTypeInFile("int");
		Thread.sleep(WAITING_TIME);
		showClassModelAttributes(projectLink, "HelloWorld");

		MethodLink methodLinkTarget = null;
		List<MethodLink> methodLinks = classLinkTarget.getMethodLinks();
		for (MethodLink methodLink : methodLinks) {
			String methodName = methodLink.getModel().getName();
			if (methodName.equals("uselessMethod")) {
				methodLinkTarget = methodLink;
				break;
			}
		}

		showClassModelMethods(projectLink, "HelloWorld");
		System.out.println("\nUsing setNameInFile to edit the name of a method in the file to 'veryFastMethod'...");
		methodLinkTarget.setNameInFile("veryFastMethod");
		Thread.sleep(WAITING_TIME);
		showClassModelMethods(projectLink, "HelloWorld");

		methodLinkTarget.setName("veryFastMethod");

		System.out.println("\nUsing setTypeInFile to edit the type of a method in the file to 'int'...");
		methodLinkTarget.setTypeInFile("int");
		Thread.sleep(WAITING_TIME);
		showClassModelMethods(projectLink, "HelloWorld");

		// Testing setNameInFile for FileLink
		System.out.println("\nUsing setNameInFile to edit the name of a class in the file...");
		compilationUnitLinkHello.setNameInFile("HelloWorldRemastered");
		Thread.sleep(WAITING_TIME);

		// Testing renameFolder for PackageLink
		System.out.println("\nUsing renameFolder to edit the name of a package folder...");
		packageLinkFirst.renameFolder("betterPackage");
		Thread.sleep(WAITING_TIME);

		// Testing renameFolder for ProjectLink
		System.out.println("\nUsing renameFolder to edit the name of a project folder...");
		projectLink.renameFolder("testing");
		Thread.sleep(WAITING_TIME);

		System.exit(0); // Terminating all threads

	}
}
