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

		attributeModel.setName(name);
		attributeModel.setType(type);
		attributeModel.setClazz(classModel);

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
		AttributeLink attributeLinkFile = new AttributeLink(attributeModel.getClazz(), name, type);
		AttributeModel attributeModelFile = attributeLinkFile.attributeModel;

		// Updating the model
		attributeModel.updateWith(attributeModelFile);
	}

	/**
	 * Reads a .java file, compares it to the existing model, and updates the file
	 * 
	 */
	public void updateFile() {

		// As of now, since an attribute is uniquely defined by its name and type, it can't ever change
		// There is a similar situation for methods, classes, and packages.
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

}
