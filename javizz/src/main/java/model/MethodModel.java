package model;

import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;

import javizz.MethodLink;
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

<<<<<<< HEAD:javizz/src/main/java/model/MethodModel.java
	// Attributes and methods regarding the parent class
=======
	// Attributes and methods regarding the corresponding MethodLink

	String LINK = "link";

	@Getter(LINK)
	MethodLink getMethodLink();

	@Setter(LINK)
	void setMethodLink(MethodLink methodLink);

	// Attributes and methods regarding the parent file
>>>>>>> exp:javizz/src/main/java/models/MethodModel.java

	String FILE = "file";

	@Getter(value = FILE, isDerived = true) // isDerived flag is set, otherwise updateModel would cause a stack overflow
	FileModel getFile();

	@Setter(FILE)
	void setFile(FileModel fileModel);

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
			return getFile() == null ? null : getFile().getProject(); // returns the parent project
		}
	}
}
