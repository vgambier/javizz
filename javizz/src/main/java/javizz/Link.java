package javizz;

import java.io.FileNotFoundException;

import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.pamela.factory.AccessibleProxyObject;

public abstract class Link<T extends AccessibleProxyObject> {
	
	protected T model;
	
	protected Link(T model) {
		this.model = model;
	}
	
	public void updateModel() throws FileNotFoundException, ModelDefinitionException {

		// Generating a new model based on the input file
		Link<T> link = create();
		T newModel = link.model;

		// Updating the model
		model.updateWith(newModel);

	}
	
	abstract Link<T> create() throws FileNotFoundException, ModelDefinitionException;
	
}
