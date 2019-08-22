package javizz;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.pamela.factory.ModelFactory;
import org.openflexo.pamela.factory.SerializationPolicy;

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
					System.out.println("\t\tmethod: " + methodModel.getName());
				}
			}
		}

		// Testing listeners
		// TODO

		// XML serialization

		ModelFactory factory = new ModelFactory(ClassModel.class);
		ClassModel classModel = factory.newInstance(ClassModel.class);
		classModel.setName("toto");
		AttributeModel attribute = factory.newInstance(AttributeModel.class);
		attribute.setName("titi");
		classModel.addAttribute(attribute);
		File file = File.createTempFile("PAMELA-TestSerialization", ".xml"); // TODO regular file
		System.out.println(file); // useful to see the path since it's a temp file
		FileOutputStream fos = new FileOutputStream(file);
		factory.serialize(classModel, fos, SerializationPolicy.EXTENSIVE, true);

	}
}
