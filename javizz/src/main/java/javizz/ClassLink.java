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

import model.ClassModel;
import model.PackageModel;

/**
 * Instances of this class are used to maintain a link between the class existing on the disk and the corresponding model.
 * 
 * @author Victor Gambier
 *
 */

@ModelEntity
public class ClassLink {

	private ClassModel classModel; // the corresponding model
	private String path; // the path where the class is located - uniquely defines the class within the file system
	private List<AttributeLink> attributeLinks; // the children AttributeLink
	private List<MethodLink> methodLinks; // the children methodLink
	private PackageLink packageLink; // the parent package

	/**
	 * The constructor. Takes the path to a .java file, and modelizes that file. Links an instance of ClassLink with an instance of
	 * ClassModel. Also calls the MethodModel and AttributeModel constructors to modelize the methods and attributes contained within the
	 * class.
	 * 
	 * @param packageModel
	 *            the parent package
	 * @param path
	 *            the path where the class is located
	 * @throws FileNotFoundException
	 *             if the file containing the class could not be parsed
	 * @throws ModelDefinitionException
	 *             if something went wrong upon calling .getModelContext()
	 */
	public ClassLink(PackageLink packageLink, String path) throws FileNotFoundException, ModelDefinitionException {

		PackageModel packageModel = packageLink.getPackageModel();

		// Instantiating attributes

		// We first need to define a factory to instantiate AttributeModel
		ModelFactory factory = new ModelFactory(ModelContextLibrary.getModelContext(ClassModel.class));

		this.classModel = factory.newInstance(ClassModel.class);
		this.path = path;
		attributeLinks = new ArrayList<AttributeLink>();
		methodLinks = new ArrayList<MethodLink>();
		this.packageLink = packageLink;

		classModel.setName(Demonstration.pathToFilename(path));
		classModel.setPath(path);
		classModel.setPackage(packageModel);

		packageModel.addClass(classModel);

		// Looking for methods and attributes within the file

		// Parsing the file into a compilation unit
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
				MethodLink methodLink = new MethodLink(getClassLink(), name, type);
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
		ClassLink classLinkFile = new ClassLink(packageLink, path);
		ClassModel classModelFile = classLinkFile.classModel;

		// Updating the model
		classModel.updateWith(classModelFile);
	}

	/**
	 * Reads a .java file, and changes the name of the class to match the input argument. Changes the model accordingly. For now, only edits
	 * the contents of the file and doesn't rename the filename. This may change in the future.
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
		String name = classModel.getName();

		// Editing the contents of the file
		TypeDeclaration<?> primaryClass = cu.getClassByName(name).orElse(null);
		primaryClass.setName(newName);

		// Writing all changes to the original file
		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
		writer.write(LexicalPreservingPrinter.print(cu));
		writer.close();

		// Modifying the name of the class in the model
		classModel.setName(newName);
	}

	/**
	 * @return the classModel
	 */
	public ClassModel getClassModel() {
		return classModel;
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
	 * @return the ClassLink itself
	 */
	public ClassLink getClassLink() {
		return this;
	}

}
