package javizz;

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

import javizz.ProjectModel.ProjectModelImpl;

/**
 * @author Victor Gambier
 *
 */

// modélise l'espace de travail, c'est-à-dire le dossier sont les sous-dossiers sont gérés par la modélisation

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

	// Attributes and methods regarding the corresponding ProjectLink

	String LINK = "link";

	@Getter(LINK)
	ProjectLink getProjectLink();

	@Setter(LINK)
	void setProjectLink(ProjectLink projectLink);

	// pour faire la différence entre initialisation et changements
	String WATCHING = "isWatching";

	@Getter(value = WATCHING, defaultValue = "false")
	boolean isWatching();

	@Setter(WATCHING)
	void setWatching(boolean isWatching);

	// Attributes and methods regarding the children packages:

	String PACKAGES = "packages";

	@Getter(value = PACKAGES, cardinality = Cardinality.LIST)
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
