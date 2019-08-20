package javizz;

import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.Implementation;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Setter;

/**
 * @author Victor Gambier
 *
 */

@ModelEntity
public interface AttributeModel {

	// Attributes and methods regarding the name of the attribute:

	String NAME = "name";

	@Getter(NAME)
	String getName();

	@Setter(NAME)
	void setName(String name);

	// Attributes and methods regarding the type of the attribute:

	String TYPE = "type";

	@Getter(TYPE)
	String getType();

	@Setter(TYPE)
	void setType(String type);

	// Attributes and methods regarding the parent class:

	String CLASS = "class";

	@Getter(CLASS)
	ClassModel getClazz();

	@Setter(CLASS)
	void setClazz(ClassModel classModel);

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
