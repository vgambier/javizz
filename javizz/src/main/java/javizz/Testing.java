package javizz;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.openflexo.pamela.ModelContextLibrary;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.pamela.factory.ModelFactory;

/**
 * @author Victor Gambier
 *
 */

public class Testing {

	public static void main(String[] args) throws ModelDefinitionException, ModelException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException, IOException {

		// Defining a factory
		ModelFactory factory = new ModelFactory(
				ModelContextLibrary.getCompoundModelContext(ClassModel.class, PackageModel.class, AttributeModel.class));

		// Initializing a ClassModel
		// Instanciation - deviendra à terme une fonction qui pourra également être appelée par le code lors des synch, etc.

		ClassModel classModelTest = factory.newInstance(ClassModel.class);

		// Returns null
		System.out.println("The default name of any class model is: " + classModelTest.getName());

		// Testing setName and getName
		classModelTest.setName("newName");
		System.out.println("The name of the test class has been set to: " + classModelTest.getName());

		String testPath = "/homes/v17gambi/Documents/stage-ete-2019/resources/HelloWorld.java";

		// Testing ClassLink
		ClassLink classLink = new ClassLink();
		classLink.createModel(testPath);

		// Retrieving and printing data
		ClassModel classModel = classLink.getClassModel();
		System.out.println("Class found: " + classModel.getName());

		List<AttributeModel> attributes = classModel.getAttributes();
		for (AttributeModel attribute : attributes) {
			System.out.println(
					"\tOne attribute of type " + attribute.getType() + " was found: " + attribute.toString() + " : " + attribute.getName());
		}

		List<MethodModel> methods = classModel.getMethods();
		for (MethodModel method : methods) {
			System.out.println("\tOne method of type " + method.getType() + " was found: " + method.getName());
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
