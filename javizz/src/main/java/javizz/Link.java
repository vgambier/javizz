package javizz;

import java.io.FileNotFoundException;

import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.pamela.factory.AccessibleProxyObject;

// Parent class of all other Link classes (AttributeLink, CompilationUnitLink, etc.)

public abstract class Link<T extends AccessibleProxyObject> {

	protected T model;

	protected Link(T model) {
		this.model = model;
	}

	/**
	 * Reads a directory or a file, compares it to the existing model, and updates the model accordingly
	 * 
	 * @throws ModelDefinitionException
	 * @throws FileNotFoundException
	 * 
	 */
	public void updateModel() throws FileNotFoundException, ModelDefinitionException {

		// Generating a new model based on the input file
		Link<T> link = create();
		T newModel = link.model;

		// Updating the model
		model.updateWith(newModel);

	}

	abstract Link<T> create() throws FileNotFoundException, ModelDefinitionException;

	public T getModel() {
		return model;
	}

}
