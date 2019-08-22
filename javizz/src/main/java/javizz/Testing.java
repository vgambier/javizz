package javizz;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.openflexo.pamela.exceptions.ModelDefinitionException;

/**
 * @author Victor Gambier
 *
 */

public class Testing {

	/**
	 * Takes a path and returns the bottom-most filename or folder
	 * 
	 * @param path
	 *            the path that is going to be converted into a filename
	 * @return the bottom-most filename or folder, as a String
	 */
	public static String pathToFilename(String path) {
		String filenameWithExt = path.substring(path.lastIndexOf("/") + 1);
		return FilenameUtils.removeExtension(filenameWithExt);
	}

	public static void main(String[] args) throws ModelDefinitionException, ModelException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException, IOException {

		// Reading a test folder
		String folderPath = "testFiles"; // a relative path, pointing to the testFiles directory included in the project
		ProjectLink projectLink = new ProjectLink(folderPath);

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
					System.out.println("\t\tattribute: " + methodModel.getName());
				}
			}
		}

		// Serializing
		// TODO: doesn't work, no XML element for interface ClassModel

		/*
		
		File file = File.createTempFile("PAMELA-TestSerialization", ".xml");
		FileOutputStream fos = new FileOutputStream(file);
		factory.serialize(classModelTest, fos, SerializationPolicy.EXTENSIVE, true);
		
		*/

		/*
		
		ModelFactory factory33 = new ModelFactory(FlexoProcess.class);
		FlexoProcess process = (FlexoProcess) factory33.newInstance(FlexoProcess.class).init();
		File file = File.createTempFile("PAMELA-TestSerialization", ".xml");
		FileOutputStream fos = new FileOutputStream(file);
		factory33.serialize(process, fos, SerializationPolicy.EXTENSIVE, true);
		
		*/

		/* copying an existing example - doesn't work
		File file;
		ModelFactory factory555;
		file = File.createTempFile("PAMELA-TestSerialization", ".xml");
		factory555 = new ModelFactory(FlexoProcess.class);
		FlexoProcess process = (FlexoProcess) factory555.newInstance(FlexoProcess.class).init();
		FileOutputStream fos = new FileOutputStream(file);
		factory555.serialize(process, fos, SerializationPolicy.EXTENSIVE, true);
		*/

	}
}
