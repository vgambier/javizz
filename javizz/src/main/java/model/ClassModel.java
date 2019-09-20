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
 * Instances of this class represent a single class.
 * 
 * @author Victor Gambier
 *
 */

@ModelEntity
@XMLElement
public interface ClassModel extends TypeModel {

	// Attributes and methods regarding the name of the class

	String NAME = "name";

	@Getter(NAME)
	@XMLAttribute
	String getName();

	@Setter(NAME)
	void setName(String name);

	// Attributes and methods regarding the parent compilation unit

	String COMPILATION_UNIT = "compilationUnit";

	@Getter(value = COMPILATION_UNIT, isDerived = true) // isDerived flag is set, otherwise updateModel would cause a stack overflow
	CompilationUnitModel getCompilationUnit();

	@Setter(COMPILATION_UNIT)
	void setCompilationUnit(CompilationUnitModel compilationUnitModel);

	// Attributes and methods regarding the children attributes

	String ATTRIBUTES = "attributes";

	@Getter(value = ATTRIBUTES, cardinality = Cardinality.LIST, inverse = AttributeModel.CLASS)
	@Embedded
	@XMLElement
	public List<AttributeModel> getAttributes();

	@Adder(ATTRIBUTES)
	public void addAttribute(AttributeModel c);

	@Remover(ATTRIBUTES)
	public void removeAttribute(AttributeModel c);

	// Attributes and methods regarding the children methods

	String METHODS = "methods";

	@Getter(value = METHODS, cardinality = Cardinality.LIST)
	@Embedded
	@XMLElement
	public List<MethodModel> getMethods();

	@Adder(METHODS)
	public void addMethod(MethodModel c);

	@Remover(METHODS)
	public void removeMethod(MethodModel c);

	abstract class ClassModelImpl extends AbstractModelObjectImpl implements ClassModel {

		@Override
		public ProjectModel getProject() {
			return getCompilationUnit().getProject(); // returns the parent project
		}
	}
}
