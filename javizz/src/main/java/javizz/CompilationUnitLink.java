package javizz;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openflexo.pamela.ModelContextLibrary;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.pamela.factory.ModelFactory;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import model.CompilationUnitModel;
import model.PackageModel;

/**
 * Instances of this class are used to maintain a link between the compilation unit/file existing on the disk and the corresponding model.
 * 
 * @author Victor Gambier
 *
 */

@ModelEntity
public class CompilationUnitLink {

	private CompilationUnitModel compilationUnitModel; // the corresponding model
	private String path; // the path where the compilation unit is located - uniquely defines the file within the file system
	private List<AttributeLink> attributeLinks; // the children AttributeLink
	private List<MethodLink> methodLinks; // the children methodLink
	private PackageLink packageLink; // the parent package

	/**
	 * The constructor. Takes the path to a .java file, and modelizes that compilation unit/file. Links an instance of CompilationUnitLink
	 * with an instance of CompilationUnitModel. Also calls the MethodModel and AttributeModel constructors to modelize the methods and
	 * attributes contained within the file.
	 * 
	 * @param packageModel
	 *            the parent package
	 * @param path
	 *            the path where the compilation unit is located
	 * @throws FileNotFoundException
	 *             if the file containing the compilation unit could not be parsed
	 * @throws ModelDefinitionException
	 *             if something went wrong upon calling .getModelContext()
	 */
	public CompilationUnitLink(PackageLink packageLink, String path) throws FileNotFoundException, ModelDefinitionException {

		PackageModel packageModel = packageLink.getPackageModel();

		// Instantiating attributes

		// We first need to define a factory to instantiate AttributeModel
		ModelFactory factory = new ModelFactory(ModelContextLibrary.getModelContext(CompilationUnitModel.class));

		this.compilationUnitModel = factory.newInstance(CompilationUnitModel.class);
		this.path = path;
		attributeLinks = new ArrayList<AttributeLink>();
		methodLinks = new ArrayList<MethodLink>();
		this.packageLink = packageLink;

		compilationUnitModel.setName(Demonstration.pathToFilename(path));
		compilationUnitModel.setPackage(packageModel);

		packageModel.addCompilationUnits(compilationUnitModel);

		// Looking for methods and attributes within the compilation unit

		// Parsing the file into a javaparser compilation unit
		CompilationUnit cu = StaticJavaParser.parse(new File(path));

		// Finding the attributes and initializing the models

		for (TypeDeclaration<?> typeDec : cu.getTypes()) {
			for (BodyDeclaration<?> member : typeDec.getMembers()) {
				member.toFieldDeclaration().ifPresent(field -> {
					for (VariableDeclarator variable : field.getVariables()) {
						// Retrieving relevant data
						String name = variable.getNameAsString();
						String type = variable.getTypeAsString();
						// This constructor will take care of modelizing the attribute and its contents
						AttributeLink attributeLink = new AttributeLink(this, name, type);
						attributeLinks.add(attributeLink);
					}
				});
			}
		}

		// Finding the attributes and initializing the methods

		final class MethodNamePrinter extends VoidVisitorAdapter<Void> {
			@Override
			public void visit(MethodDeclaration md, Void arg) {
				super.visit(md, arg);

				// Grabbing relevant data
				String name = md.getNameAsString();
				String type = md.getTypeAsString();
				// This constructor will take care of modelizing the method and its contents
				MethodLink methodLink = new MethodLink(getCompilationUnitLink(), name, type);
				methodLinks.add(methodLink);

			}
		}

		VoidVisitor<?> methodNameVisitor = new MethodNamePrinter();
		methodNameVisitor.visit(cu, null);

	}

	/**
	 * Reads a .java file, compares it to the existing model, and updates the model accordingly
	 * 
	 * @throws ModelDefinitionException
	 * @throws FileNotFoundException
	 */
	public void updateModel() throws FileNotFoundException, ModelDefinitionException {

		// Generating a new model based on the input file
		CompilationUnitLink compilationUnitLinkFile = new CompilationUnitLink(packageLink, path);
		CompilationUnitModel compilationUnitModelFile = compilationUnitLinkFile.compilationUnitModel;

		// Updating the model
		compilationUnitModel.updateWith(compilationUnitModelFile);
	}

	/**
	 * Reads a .java file, and changes the name of the primary class to match the input argument. Changes the model accordingly. For now,
	 * only edits the contents of the file and doesn't rename the file. This may change in the future.
	 * 
	 * @param newName
	 *            the new name of the class
	 * @throws IOException
	 *             if there was an issue during the file parsing or the file write
	 * 
	 */
	public void setNameInFile(String newName) throws IOException {

		// Initializing the compilation unit
		CompilationUnit cu = StaticJavaParser.parse(new File(path));
		LexicalPreservingPrinter.setup(cu); // enables lexical preservation

		// Retrieving the name of the current class
		String name = compilationUnitModel.getName();

		// Editing the contents of the file
		TypeDeclaration<?> primaryClass = cu.getClassByName(name).orElse(null);
		primaryClass.setName(newName);

		// Writing all changes to the original file
		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
		writer.write(LexicalPreservingPrinter.print(cu));
		writer.close();

	}

	/**
	 * @return the compilationUnitModel
	 */
	public CompilationUnitModel getCompilationUnitModel() {
		return compilationUnitModel;
	}

	/**
	 * @return the attributeLinks
	 */
	public List<AttributeLink> getAttributeLinks() {
		return attributeLinks;
	}

	/**
	 * @return the methodLinks
	 */
	public List<MethodLink> getMethodLinks() {
		return methodLinks;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @return the CompilationUnitLink itself
	 */
	public CompilationUnitLink getCompilationUnitLink() {
		return this;
	}

}
