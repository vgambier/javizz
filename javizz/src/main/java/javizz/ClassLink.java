package javizz;

import java.io.File;
import java.io.FileNotFoundException;
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

// This object keeps a link between a file on the disk (defined by its filepath, assumed to be constant), and the corresponding model

@ModelEntity
public class ClassLink {

	private ClassModel classModel; // the corresponding model
	private String path; // the path where the class is located - uniquely defines the class within the file system
	private List<AttributeLink> attributeLinks; // the children AttributeLink
	private List<MethodLink> methodLinks; // the children methodLink

	public ClassLink(PackageModel packageModel, String path) throws FileNotFoundException, ModelDefinitionException {

		// Instantiating attributes

		ModelFactory factory = new ModelFactory(ModelContextLibrary.getModelContext(ClassModel.class)); // we need to define factory to
																										// instantiate ClassModel
		this.classModel = factory.newInstance(ClassModel.class);
		this.path = path;
		attributeLinks = new ArrayList<AttributeLink>();
		methodLinks = new ArrayList<MethodLink>();

		classModel.setName(Testing.pathToFilename(path));
		packageModel.addClass(classModel);
		classModel.setPackage(packageModel);

		// Looking for methods and attributes within the file

		// Parsing the file into a compilation unit
		CompilationUnit cu = StaticJavaParser.parse(new File(path));

		// Finding the attributes and initializing the models

		for (TypeDeclaration<?> typeDec : cu.getTypes()) {
			for (BodyDeclaration<?> member : typeDec.getMembers()) {
				member.toFieldDeclaration().ifPresent(field -> {
					for (VariableDeclarator variable : field.getVariables()) {
						// Grabbing relevant data
						String name = variable.getName().asString();
						String type = variable.getType().asString();
						// This constructor will take care of modelizing the attribute and its contents
						AttributeLink attributeLink = new AttributeLink(classModel, name, type);
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
				String type = md.getType().asString();
				// This constructor will take care of modelizing the method and its contents
				MethodLink methodLink = new MethodLink(classModel, name, type);
				methodLinks.add(methodLink);

			}
		}

		VoidVisitor<?> methodNameVisitor = new MethodNamePrinter();
		methodNameVisitor.visit(cu, null);

	}

	/**
	 * Reads a .java file, compares it to the existing model, and updates the model
	 * 
	 * @throws ModelDefinitionException
	 * @throws FileNotFoundException
	 */
	public void updateModel() throws FileNotFoundException, ModelDefinitionException {

		// Generating a new model based on the input file
		ClassLink classLinkFile = new ClassLink(classModel.getPackage(), path);
		ClassModel classModelFile = classLinkFile.classModel;

		// Updating the model
		classModel.updateWith(classModelFile);
	}

	// TODO, unless it's no longer necessary
	// creates a file corresponding to a given classModel:
	// by using the data contained in the classModel and its child models (like packageModel),
	// generates a file skeleton

	public void createFile(ClassModel classModel) {

	}

	// sync (might become more than one method)
	// after checking if the two arguments match and differ, determines which one should change to match the other, and applies the needed
	// changes

	// TODO

	/**
	 * Reads a directory containing .java files, compares it to the existing model, and updates the folder
	 * 
	 */
	public void updateFile() {

		// calls updateFile() on all AttributeLink and all MethodLink, and also checks if the ClassModel itself should change

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

}
