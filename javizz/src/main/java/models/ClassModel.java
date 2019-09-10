package models;

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

// résoudre le problème de l'héritage / unicité modèle
// pool methode commun, check si héritage, accès classe mère
// ou : simplement un attribut qui dit que cette classe est fille de telle classe. donc une méthode est rattachée à une unique classe

// TODO: turn ClassModel into also FileModel which handles all classes and also imports. 3 attributs: la classe publique, la liste
// des imports, la liste des classes non publiques (+ le nom, etc.)

/**
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

	// Attributes and methods regarding the path of the file

	String PATH = "path";

	@Getter(PATH)
	String getPath();

	@Setter(PATH)
	void setPath(String path);

	// Attributes and methods regarding the parent package

	String PACKAGE = "package";

	@Getter(value = PACKAGE, isDerived = true) // isDerived flag is set, otherwise updateModel would cause a stack overflow
	PackageModel getPackage();

	@Setter(PACKAGE)
	void setPackage(PackageModel packageModel);

	// Attributes and methods regarding the children attributes:

	String ATTRIBUTES = "attributes";

	@Getter(value = ATTRIBUTES, cardinality = Cardinality.LIST, inverse = AttributeModel.CLASS)
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

	abstract class ClassModelImpl extends AbstractModelObjectImpl implements ClassModel {

		@Override
		public ProjectModel getProject() {
			return getPackage().getProject();
		}
	}
}
