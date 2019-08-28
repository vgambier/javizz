package javizz;

import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.Implementation;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.pamela.factory.AccessibleProxyObject;

/**
 * @author Victor Gambier
 *
 */

@ModelEntity
@XMLElement
public interface AttributeModel extends AccessibleProxyObject {

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

	// Attributes and methods regarding the type of the attribute:

	String TYPE = "type";

	@Getter(TYPE)
	@XMLAttribute
	String getType();

	@Setter(TYPE)
	void setType(String type);

	// toString description method

	@Override
	public String toString();

	@Implementation
	abstract class AttributeModelImpl implements AttributeModel {

		@Override
		public String toString() {
			return this.getName() + ": " + this.getType();
		}
	}
}
