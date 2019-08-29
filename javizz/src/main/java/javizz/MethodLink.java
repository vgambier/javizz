package javizz;

import java.io.FileNotFoundException;

import org.openflexo.pamela.ModelContextLibrary;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.pamela.factory.ModelFactory;

@ModelEntity
public class MethodLink {

	private MethodModel methodModel;
	// TODO need another attribute that uniquely defines the method within the file system - for now the primary key is the name
	private String name;
	private String type; // the type of the attribute - will be obsolete once TypeLink is implemented
	private ClassLink classLink; // the parent classLink

	public MethodLink(ClassModel classModel, String name, String type) {

		// we need to define factory to instantiate AttributeModel
		ModelFactory factory = null;
		try {
			factory = new ModelFactory(ModelContextLibrary.getModelContext(MethodModel.class));
		} catch (ModelDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.methodModel = factory.newInstance(MethodModel.class);

		this.name = name;
		this.type = type;
		this.classLink = classModel.getClassLink();

		methodModel.setName(name);
		methodModel.setType(type);
		methodModel.setMethodLink(this);

		classModel.addMethod(methodModel);

		// TODO: handle actual types
	}

	/**
	 * Reads a .java file, compares it to the existing model, and updates the model
	 * 
	 * @throws ModelDefinitionException
	 * @throws FileNotFoundException
	 */
	public void updateModel() throws FileNotFoundException, ModelDefinitionException {

		// Generating a new model based on the input file
		MethodLink methodLinkFile = new MethodLink(classLink.getClassModel(), name, type);
		MethodModel methodModelFile = methodLinkFile.methodModel;

		// Updating the model
		methodModel.updateWith(methodModelFile);
	}

	// TODO

	/**
	 * Reads a directory containing .java files, compares it to the existing model, and updates the folder
	 * 
	 */
	public void updateFile() {

		// checks if the method should change, then does it

	}

}
