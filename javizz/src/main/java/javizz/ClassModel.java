package javizz;

import java.util.List;

import org.openflexo.pamela.annotations.Adder;
import org.openflexo.pamela.annotations.Embedded;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.Getter.Cardinality;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Remover;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.factory.AccessibleProxyObject;

/**
 * @author Victor Gambier
 *
 */

@ModelEntity
public interface ClassModel extends AccessibleProxyObject {

	// Attributes and methods regarding the name of the class

	String NAME = "name";

	@Getter(NAME)
	String getName();

	@Setter(NAME)
	void setName(String name);

	// Attributes and methods regarding the parent package:

	String PACKAGE = "package";

	@Getter(PACKAGE)
	PackageModel getPackage();

	@Setter(PACKAGE)
	void setPackage(PackageModel packageModel);

	// Attributes and methods regarding the children attributes:

	String ATTRIBUTES = "attributes";

	@Getter(value = ATTRIBUTES, cardinality = Cardinality.LIST)
	@Embedded // TODO: what is this?
	public List<AttributeModel> getAttributes();

	@Adder(ATTRIBUTES)
	public void addAttribute(AttributeModel c);

	@Remover(ATTRIBUTES)
	public void removeAttribute(AttributeModel c);

	// Attributes and methods regarding the children methods:

	String METHODS = "methods";

	@Getter(value = METHODS, cardinality = Cardinality.LIST)
	@Embedded // TODO: what is this?
	public List<MethodModel> getMethods();

	@Adder(METHODS)
	public void addMethod(MethodModel c);

	@Remover(METHODS)
	public void removeMethod(MethodModel c);

}
