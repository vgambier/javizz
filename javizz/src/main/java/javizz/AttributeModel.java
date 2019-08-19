package javizz;

import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.Implementation;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.factory.AccessibleProxyObject;

/**
 * @author Victor Gambier
 *
 */

@ModelEntity
public interface AttributeModel extends AccessibleProxyObject {

	// Attributes and methods regarding the name of the attribute

	String NAME = "name";

	@Getter(NAME)
	String getName();

	@Setter(NAME)
	void setName(String name);

	// Attributes and methods regarding the parent class:

	String CLASS = "class";

	@Getter(CLASS)
	ClassModel getClazz();

	@Setter(CLASS)
	void setClazz(ClassModel classModel);

	// Attributes and methods regarding the type of the attribute:

	String TYPE = "type"; // TODO: replace this with not a string but a TypeModel

	@Getter(TYPE)
	ClassModel getType();

	@Setter(TYPE)
	void setType(ClassModel name);

	// toString description method

	@Override
	public String toString();

	// TODO why is toString not called by the debugguer?

	@Implementation
	abstract class AttributeModelImpl implements AttributeModel {

		@Override
		public String toString() {
			return this.getName() + ": " + this.getType(); // TODO
		}
	}
}
