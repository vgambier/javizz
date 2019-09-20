package model;

import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;

import model.MethodModel.MethodModelImpl;

/**
 * Instances of this class represent a single method.
 * 
 * @author Victor Gambier
 *
 */

@ModelEntity
@XMLElement
@ImplementationClass(MethodModelImpl.class)
public interface MethodModel extends AbstractModelObject {

	// Attributes and methods regarding the name of the method:

	String NAME = "name";

	@Getter(NAME)
	@XMLAttribute
	String getName();

	@Setter(NAME)
	void setName(String name);

	// Attributes and methods regarding the parent compilation unit

	String CLASS = "class";

	@Getter(value = CLASS, isDerived = true) // isDerived flag is set, otherwise updateModel would cause a stack overflow
	ClassModel getClazz();

	@Setter(CLASS)
	void setClazz(ClassModel classModel);

	// Attributes and methods regarding the type of the method:

	String TYPE = "type";

	@Getter(TYPE)
	@XMLAttribute
	String getType();

	@Setter(TYPE)
	void setType(String type);

	abstract class MethodModelImpl extends AbstractModelObjectImpl implements MethodModel {

		@Override
		public ProjectModel getProject() {
			return getClazz() == null ? null : getClazz().getProject(); // returns the parent project
		}
	}
}
