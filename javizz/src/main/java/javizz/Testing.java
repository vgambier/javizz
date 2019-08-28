package javizz;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
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

public class Testing {

	/**
	 * Takes a path and returns the name of filename or folder that it points to, without the extension
	 * 
	 * @param path
	 *            the path that is going to be converted into a filename
	 * @return the bottom-most filename or folder, as a String
	 */
	public static String pathToFilename(String path) {
		String filenameWithExt = path.substring(path.lastIndexOf("/") + 1);
		return FilenameUtils.removeExtension(filenameWithExt);
	}

	public static void main(String[] args) throws Exception {

		// Reading a test folder
		String folderPath = "testFiles"; // a relative path, pointing to the testFiles directory included in the project
		ProjectLink projectLink = new ProjectLink(folderPath);

		// TODO Setting up a listener to automatically detect all changes made to the files on the disk

		// Testing to see if the data was properly gathered
		// TODO read the data to see if it works (via XML serialization)

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

		/* Testing updateModel() */

		// Modifying a file

		System.out.println("Editing file...");

		String editPath = "/homes/v17gambi/git/javizz/javizz/testFiles/firstPackage/HelloWorld.java";

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

		System.out.println("Done.");

		// Updating the model
		System.out.println("Updating the model...");
		projectLink.updateModel();
		System.out.println("Done. Here are the attributes as stored in the HelloWorld ClassModel:");

		// Showing that the model has changed

		for (PackageModel packageModel : packages) {
			List<ClassModel> classes = packageModel.getClasses();
			for (ClassModel classModel : classes) {
				if (classModel.getName().equals("HelloWorld")) {
					List<AttributeModel> attributes = classModel.getAttributes();
					for (AttributeModel attributeModel : attributes) {
						System.out.println("\t" + attributeModel.getName());
					}
				}
			}
		}

		// Quick tests of other updateModel methods

		PackageModel packageModel = projectModel.getPackages().get(0);
		PackageLink packageLink = packageModel.getPackageLink();
		packageLink.updateModel();

		ClassModel classModel = packageModel.getClasses().get(1);
		ClassLink classLink = classModel.getClassLink();
		classLink.updateModel();

		AttributeModel attributeModel = classModel.getAttributes().get(0);
		AttributeLink attributeLink = attributeModel.getAttributeLink();
		attributeLink.updateModel();

		// Detecting changes on the disk
		// TODO

		// Detecting changes in models
		// TODO

		// TODO: turn ClassModel into also FileModel which handles all classes and also imports. 3 attributs: la classe publique, la liste
		// des imports, la liste des classes non publiques (+ le nom, etc.)

		// TODO vérification cohérence : classe publique = nom fichier, nom dossier = déclaration package, etc. implique création de
		// nouveaux attributs, @Override + changement de nom

	}
}
