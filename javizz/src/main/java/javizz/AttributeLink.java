package javizz;

import org.openflexo.pamela.ModelContextLibrary;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.pamela.factory.ModelFactory;

public class AttributeLink {

	private AttributeModel attributeModel;
	// TODO need another attribute that uniquely defines the attribute within the file system - possibly an arbitrary key number
	// the implementation will be similar to that of the path attribute

	public AttributeLink(ClassModel classModel, String name, String type) {

		// Instantiating attributes

		// we need to define factory to instantiate AttributeModel
		ModelFactory factory = null;
		try {
			factory = new ModelFactory(ModelContextLibrary.getModelContext(AttributeModel.class));
		} catch (ModelDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.attributeModel = factory.newInstance(AttributeModel.class);

		classModel.addAttribute(attributeModel);

		attributeModel.setClazz(classModel);
		attributeModel.setName(name);
		attributeModel.setType(type);

		// TODO: à partir du nom du type, trouver la classe
		// correspondante (avoir une fonction qui le fait) -
		// nécessaire sur le type. à terme, ce ne sera plus une string mais un TypeModel

	}

}
