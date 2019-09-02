package javizz;

import java.util.List;

import org.openflexo.pamela.annotations.Adder;
import org.openflexo.pamela.annotations.Embedded;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.Getter.Cardinality;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Remover;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;

/**
 * @author Victor Gambier
 *
 */

@ModelEntity
@XMLElement
public interface PackageModel extends AbstractModelObject {

	// Attributes and methods regarding the name of the package

	String NAME = "name";

	@Getter(NAME)
	@XMLAttribute
	String getName();

	@Setter(NAME)
	void setName(String name);

	// Attributes and methods regarding the parent class project

	String PROJECT = "project";

	@Getter(value = PROJECT, isDerived = true) // isDerived flag is set, otherwise updateModel would cause a stack overflow
	ProjectModel getProject();

	@Setter(PROJECT)
	void setProject(ProjectModel projectModel);

	// Attributes and methods regarding the children classes:

	String CLASSES = "classes";

	@Getter(value = CLASSES, cardinality = Cardinality.LIST, inverse = ClassModel.PACKAGE)
	@XMLElement
	@Embedded
	public List<ClassModel> getClasses();

	@Adder(CLASSES)
	public void addClass(ClassModel c);

	@Remover(CLASSES)
	public void removeClass(ClassModel c);

}
