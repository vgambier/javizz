// TODO: Obsolete class

package javizz;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;

import org.openflexo.pamela.ModelContextLibrary;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.pamela.factory.ModelFactory;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;

public class Parser {

	private LinkedList<PackageModel> listOfPackages = new LinkedList<PackageModel>(); // sera dans ??? conteneur principal, ProjectModel? =
																						// une liste de packages, modélise l'espace de
																						// travail (des dossiers) et donc forcément :
																						// ProjectLink à utiliser probablement dans le main,
																						// créé à partir de rien (lien "primaire")

	// TODO the parser should only return data
	// another method in a separate file calls parseFile and instantiates models

	// folder or file lsit as input?
	public void parseFile(String filepath) throws ParseProblemException, FileNotFoundException, ModelDefinitionException {

		// Parsing the file into a compilation unit
		CompilationUnit cu = StaticJavaParser.parse(new File(filepath));

		/* Gathering data about the file */

		// Finding the package name
		// TODO: finding the package name using folder names rather than reading files
		PackageDeclaration pckDec = cu.getPackageDeclaration().orElse(null);
		String packageName = (pckDec == null) ? null : pckDec.getNameAsString(); // if there is no package declaration, then the String is
																					// set to null

		// Creating and initializing several models using the gathered data
		ModelFactory factory = new ModelFactory(ModelContextLibrary.getModelContext(PackageModel.class));

		// Creating and initializing a model for the package
		PackageModel packageModel = factory.newInstance(PackageModel.class);
		packageModel.setName(packageName);
		this.listOfPackages.add(packageModel);

		// Finding the methods and initializing the models
		// TODO: implement the methods query, which implies creating MethodLink and MethodModel

		// Linking the models together
		// TODO: use the setters of ClassModel and PackageModel
		// something like:
		// ClassLink classLink = new ClassLink(filepath; classModel);
		// PackageLink packageLink = new PackageLink(filepath; packageModel);
		// and
		// classModel.setPackage(packageModel);
		// packageModel.addClass(classModel); attention à la cardinalité
		// etc.

	}

}
