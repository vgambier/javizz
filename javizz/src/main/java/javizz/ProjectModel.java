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

// modélise l'espace de
// travail (des dossiers); contient une liste de packages;
// va avec ProjectLink
// à utiliser probablement dans le main,
// créé à partir de rien (lien "primaire")
// folder or file lsit as input?

@ModelEntity
public interface ProjectModel {

	// Attributes and methods regarding the name of the project

	String NAME = "name";

	@Getter(NAME)
	String getName();

	@Setter(NAME)
	void setName(String name);

	// Attributes and methods regarding the children packages:

	String PACKAGES = "packages";

	@Getter(value = PACKAGES, cardinality = Cardinality.LIST)
	@Embedded
	public List<PackageModel> getPackages();

	@Adder(PACKAGES)
	public void addPackage(PackageModel c);

	@Remover(PACKAGES)
	public void removePackage(PackageModel c);

}
