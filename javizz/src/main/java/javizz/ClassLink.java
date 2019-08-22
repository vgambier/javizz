package javizz;

import java.io.File;
import java.io.FileNotFoundException;

import org.openflexo.pamela.ModelContextLibrary;
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

public class ClassLink {

	private ClassModel classModel;
	private String path; // the path where the class is located - uniquely defines the class within the file system

	public ClassLink(PackageModel packageModel, String path) throws FileNotFoundException, ModelDefinitionException {

		// Instantiating attributes

		ModelFactory factory = new ModelFactory(ModelContextLibrary.getModelContext(ClassModel.class)); // we need to define factory to
																										// instantiate ClassModel
		this.classModel = factory.newInstance(ClassModel.class);
		this.path = path;

		classModel.setName(Testing.pathToFilename(path));
		packageModel.addClass(classModel);

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
						new AttributeLink(classModel, name, type);
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
				new MethodLink(classModel, name, type);

			}
		}

		VoidVisitor<?> methodNameVisitor = new MethodNamePrinter();
		methodNameVisitor.visit(cu, null);

	}

	// creates a file corresponding to a given classModel:
	// by using the data contained in the classModel and its child models (like packageModel),
	// generates a file skeleton

	public void createFile(ClassModel classModel) {

	}

	// sync (might become more than one method)
	// after checking if the two arguments match and differ, determines which one should change to match the other, and applies the needed
	// changes

	/**
	 * Reads a .java file, compares it to the existing model, and updates the .java file based on said model using lexical preservation
	 * 
	 * @param filepath
	 *            the path to the .java file, including the filename and the extension
	 * @param classModel
	 *            the reference model
	 */
	public void syncFile() {

	}

	/**
	 * @return the classModel
	 */
	public ClassModel getClassModel() {
		return classModel;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

}