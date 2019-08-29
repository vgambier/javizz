package javizz;

import java.io.FileNotFoundException;

import org.openflexo.pamela.ModelContextLibrary;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.pamela.factory.ModelFactory;

@ModelEntity
public class AttributeLink {

	private AttributeModel attributeModel; // the corresponding model
	// TODO need another attribute that uniquely defines the attribute within the file system - possibly an arbitrary key number
	// the implementation will be similar to that of the path attribute. for now the primary key is the name
	private String name;
	private String type; // the type of the attribute - will be obsolete once TypeLink is implemented
	private ClassLink classLink; // the parent classLink

	public AttributeLink(ClassModel classModel, String name, String type) {

		// Instantiating attributes

		// We need to define a factory to instantiate AttributeModel
		ModelFactory factory = null;
		try {
			factory = new ModelFactory(ModelContextLibrary.getModelContext(AttributeModel.class));
		} catch (ModelDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.attributeModel = factory.newInstance(AttributeModel.class);

		this.name = name;
		this.type = type;
		this.classLink = classModel.getClassLink();

		attributeModel.setName(name);
		attributeModel.setType(type);
		attributeModel.setAttributeLink(this);

		classModel.addAttribute(attributeModel);

	}

	/**
	 * Reads a .java file, compares it to the existing model, and updates the model
	 * 
	 * @throws ModelDefinitionException
	 * @throws FileNotFoundException
	 */
	public void updateModel() throws FileNotFoundException, ModelDefinitionException {

		// Generating a new model based on the existing file
		AttributeLink attributeLinkFile = new AttributeLink(classLink.getClassModel(), name, type);
		AttributeModel attributeModelFile = attributeLinkFile.attributeModel;

		// Updating the model
		attributeModel.updateWith(attributeModelFile);
	}

	// TODO

	/**
	 * Reads a directory containing .java files, compares it to the existing model, and updates the folder
	 * 
	 */
	public void updateFile() {

		// Generating a new model based on the input file
		AttributeLink attributeLinkFile = new AttributeLink(classLink.getClassModel(), name, type);
		AttributeModel attributeModelFile = attributeLinkFile.attributeModel;

		// checks if the attribute should change, then does it

	}

}
