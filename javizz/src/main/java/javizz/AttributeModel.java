package javizz;

import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;

import javizz.AttributeModel.AttributeModelImpl;

/**
 * @author Victor Gambier
 *
 */

@ModelEntity
@XMLElement
@ImplementationClass(AttributeModelImpl.class)
public interface AttributeModel extends AbstractModelObject {

	// Attributes and methods regarding the name of the attribute:

	String NAME = "name";

	@Getter(NAME)
	@XMLAttribute
	String getName();

	@Setter(NAME)
	void setName(String name);

	// Attributes and methods regarding the corresponding AttributeLink

	String LINK = "link";

	@Getter(LINK)
	AttributeLink getAttributeLink();

	@Setter(LINK)
	void setAttributeLink(AttributeLink attributeLink);

	// Attributes and methods regarding the parent class

	String CLASS = "class";

	@Getter(value = CLASS, isDerived = true)
	ClassModel getClazz();

	@Setter(CLASS)
	void setClazz(ClassModel classModel);

	// Attributes and methods regarding the type of the attribute:

	String TYPE = "type";

	@Getter(TYPE)
	@XMLAttribute
	String getType();

	@Setter(TYPE)
	void setType(String type);

	abstract class AttributeModelImpl extends AbstractModelObjectImpl implements AttributeModel {

		// si on veut ajouter des méthodes à AttributeModel non gérées par Pamela

		@Override
		public String toString() {
			return this.getName() + ": " + this.getType();
		}

		@Override
		public ProjectModel getProject() {
			return getClazz() == null ? null : getClazz().getProject();
		}
	}
}
