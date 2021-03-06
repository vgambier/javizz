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
public interface CompilationUnitModel extends AbstractModelObject {

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

	// Attributes and methods regarding the children classes

	String CLASSES = "classes";

	@Getter(value = CLASSES, cardinality = Cardinality.LIST)
	@Embedded
	@XMLElement
	public List<ClassModel> getClasses();

	@Adder(CLASSES)
	public void addClass(ClassModel c);

	@Remover(CLASSES)
	public void removeClass(ClassModel c);

	// Attributes and methods regarding the external import statements

	String EXTERNAL_IMPORTS = "externalImports";

	@Getter(value = EXTERNAL_IMPORTS, cardinality = Cardinality.LIST)
	@Embedded
	// TODO Ajouter une annotation @XMLElement ici résulte en une erreur Pamela
	public List<String> getExternalImports();

	@Adder(EXTERNAL_IMPORTS)
	public void addExternalImport(String externalImport);

	@Remover(EXTERNAL_IMPORTS)
	public void removeExternalImport(String externalImport);

	// Attributes and methods regarding the internal import statements

	String INTERNAL_IMPORTS = "internalImports";

	@Getter(value = INTERNAL_IMPORTS, cardinality = Cardinality.LIST)
	@Embedded
	@XMLElement
	public List<CompilationUnitModel> getInternalImports();

	@Adder(INTERNAL_IMPORTS)
	public void addInternalImport(CompilationUnitModel internalImport);

	@Remover(INTERNAL_IMPORTS)
	public void removeInternalImport(CompilationUnitModel internalImport);

	abstract class CompilationUnitModelImpl extends AbstractModelObjectImpl implements CompilationUnitModel {

		@Override
		public ProjectModel getProject() {
			return getPackage().getProject(); // returns the parent project
		}

		@Override
		public String toString() {
			return getPackage().getName() + "." + getName();
		}

	}
}
