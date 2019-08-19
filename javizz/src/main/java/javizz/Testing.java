package javizz;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

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

		// Définition de la factory très utile
		ModelFactory factory = new ModelFactory(
				ModelContextLibrary.getCompoundModelContext(ClassModel.class, PackageModel.class, AttributeModel.class));

		// On initialise une instance de ClassModel
		// Instanciation - deviendra à terme une fonction qui pourra également être appelée par le code lors des synch, etc.

		ClassModel classModelTest = factory.newInstance(ClassModel.class);

		// Returns null
		System.out.println("The default name of any class is: " + classModelTest.getName());

		// Testing setName and getName
		classModelTest.setName("newName");
		System.out.println("The name of the test class has been set to: " + classModelTest.getName());

		Parser parser = new Parser();
		String testPath = "/homes/v17gambi/Documents/stage-ete-2019/resources/HelloWorld.java";
		parser.parseFile(testPath);

		// Testing ClassLink
		ClassLink classLink = new ClassLink();
		classLink.createModel(testPath);

		// Serializing
		// TODO: doesn't work, no XML element for interface ClassModel

		/*
		
		File file = File.createTempFile("PAMELA-TestSerialization", ".xml");
		FileOutputStream fos = new FileOutputStream(file);
		factory.serialize(classModelTest, fos, SerializationPolicy.EXTENSIVE, true);
		
		*/

	}
}
