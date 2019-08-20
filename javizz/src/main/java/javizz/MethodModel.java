package javizz;

import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Setter;

/**
 * @author Victor Gambier
 *
 */

@ModelEntity
public interface MethodModel {

	// Attributes and methods regarding the name of the method:

	String NAME = "name";

	@Getter(NAME)
	String getName();

	@Setter(NAME)
	void setName(String name);

	// Attributes and methods regarding the type of the method:

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

}
