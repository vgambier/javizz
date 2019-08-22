package javizz;

import org.openflexo.pamela.ModelContextLibrary;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.pamela.factory.ModelFactory;

public class MethodLink {

	private MethodModel methodModel;
	// TODO need another attribute that uniquely defines the method within the file system

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

		classModel.addMethod(methodModel);

		methodModel.setClazz(classModel);
		methodModel.setName(name);
		methodModel.setType(type);

		// TODO: handle actual types
	}

}
