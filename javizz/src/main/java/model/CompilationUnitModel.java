package model;

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
 * Instances of this class represent a single compilation unit; the contents of a file.
 * 
 * @author Victor Gambier
 *
 */

@ModelEntity
@XMLElement
public interface CompilationUnitModel extends TypeModel {

	// Attributes and methods regarding the name of the compilation unit

	String NAME = "name";

	@Getter(NAME)
	@XMLAttribute
	String getName();

	@Setter(NAME)
	void setName(String name);

	// Attributes and methods regarding the parent package

	String PACKAGE = "package";

	@Getter(value = PACKAGE, isDerived = true) // isDerived flag is set, otherwise updateModel would cause a stack overflow
	PackageModel getPackage();

	@Setter(PACKAGE)
	void setPackage(PackageModel packageModel);

	// Attributes and methods regarding the children attributes:

	String ATTRIBUTES = "attributes";

	@Getter(value = ATTRIBUTES, cardinality = Cardinality.LIST, inverse = AttributeModel.COMPILATION_UNIT)
	@Embedded
	@XMLElement
	public List<AttributeModel> getAttributes();

	@Adder(ATTRIBUTES)
	public void addAttribute(AttributeModel c);

	@Remover(ATTRIBUTES)
	public void removeAttribute(AttributeModel c);

	// Attributes and methods regarding the children methods:

	String METHODS = "methods";

	@Getter(value = METHODS, cardinality = Cardinality.LIST)
	@Embedded
	@XMLElement
	public List<MethodModel> getMethods();

	@Adder(METHODS)
	public void addMethod(MethodModel c);

	@Remover(METHODS)
	public void removeMethod(MethodModel c);

	abstract class FileModelImpl extends AbstractModelObjectImpl implements CompilationUnitModel {

		@Override
		public ProjectModel getProject() {
			return getPackage().getProject(); // returns the parent project
		}
	}
}
