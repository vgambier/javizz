package javizz;

import java.util.List;

import org.openflexo.pamela.annotations.Adder;
import org.openflexo.pamela.annotations.Embedded;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.Getter.Cardinality;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Remover;
import org.openflexo.pamela.annotations.Setter;

/**
 * @author Victor Gambier
 *
 */

@ModelEntity
public interface PackageModel {

	// Attributes and methods regarding the name of the package

	String NAME = "name";

	@Getter(NAME)
	String getName();

	@Setter(NAME)
	void setName(String name);

	// Attributes and methods regarding the children classes:

	String CLASSES = "classes";

	@Getter(value = CLASSES, cardinality = Cardinality.LIST)
	@Embedded // TODO: what is this?
	public List<ClassModel> getClasses();

	@Adder(CLASSES)
	public void addClass(ClassModel c);

	@Remover(CLASSES)
	public void removeClass(ClassModel c);

}