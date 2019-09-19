package model;

import java.util.List;

import org.openflexo.pamela.annotations.Adder;
import org.openflexo.pamela.annotations.Embedded;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.Getter.Cardinality;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Remover;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;

import model.ProjectModel.ProjectModelImpl;

/**
 * Instances of this class represent a single project, i.e.: a collection of packages. For now that means a single folder (all packages are
 * necessarily in the same directory).
 * 
 * @author Victor Gambier
 *
 */

@ModelEntity
@XMLElement
@ImplementationClass(ProjectModelImpl.class)
public interface ProjectModel extends AbstractModelObject {

	// Attributes and methods regarding the name of the project

	String NAME = "name";

	@Getter(NAME)
	@XMLAttribute
	String getName();

	@Setter(NAME)
	void setName(String name);

	// A flag used to enable or disable the monitoring of projects
	// When set to true, all changes applies to any of the models (AttributeModel, CompilationUnitModel, etc.)
	// will result in a notification being sent.
	String WATCHING = "watching";

	@Getter(value = WATCHING, defaultValue = "false", isDerived = true) // isDerived flag is set so that updateModel doesn't overwrite
																		// isWatching
	boolean isWatching();

	@Setter(WATCHING)
	void setWatching(boolean isWatching);

	// Attributes and methods regarding the children packages:

	String PACKAGES = "packages";

	@Getter(value = PACKAGES, cardinality = Cardinality.LIST, inverse = PackageModel.PROJECT)
	@XMLElement
	@Embedded
	public List<PackageModel> getPackages();

	@Adder(PACKAGES)
	public void addPackage(PackageModel c);

	@Remover(PACKAGES)
	public void removePackage(PackageModel c);

	abstract class ProjectModelImpl extends AbstractModelObjectImpl implements ProjectModel {

		@Override
		public ProjectModel getProject() {
			return this;
		}
	}
}
