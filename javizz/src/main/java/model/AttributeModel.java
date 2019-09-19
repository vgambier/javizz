package model;

import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;

import model.AttributeModel.AttributeModelImpl;

/**
 * Instances of this class represent a single attribute.
 * 
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

	// Attributes and methods regarding the type of the attribute:

	String TYPE = "type";

	@Getter(TYPE)
	@XMLAttribute
	String getType();

	@Setter(TYPE)
	void setType(String type);

	// Attributes and methods regarding the parent file

	String FILE = "file";

	@Getter(value = FILE, isDerived = true) // isDerived flag is set, otherwise updateModel would cause a stack overflow
	FileModel getFile();

	@Setter(FILE)
	void setFile(FileModel fileModel);

	abstract class AttributeModelImpl extends AbstractModelObjectImpl implements AttributeModel {

		@Override
		public ProjectModel getProject() {
			return getFile() == null ? null : getFile().getProject(); // returns the parent project
		}
	}
}
