package javizz;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.io.FilenameUtils;
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
	private String path; // the filepath where the class is located

	/* 
	methods : */

	// creates a file corresponding to a given classModel:
	// by using the data contained in the classModel and its child models (like packageModel),
	// generates a file skeleton

	public void createFile(ClassModel classModel) {

	}

	// creates an instance of classModel corresponding to a given file
	// as well as all the relevant child models
	public void createModel(String filepath) throws ModelDefinitionException, FileNotFoundException {

		// Defining a factory for both class and attribute models
		ModelFactory factory = new ModelFactory(
				ModelContextLibrary.getCompoundModelContext(ClassModel.class, AttributeModel.class, MethodModel.class));

		// Retrieving and setting the class name
		String className = FilenameUtils.removeExtension(filepath.substring(filepath.lastIndexOf("/") + 1));
		ClassModel classModel = factory.newInstance(ClassModel.class);
		classModel.setName(className);

		// Linking it to the file
		this.classModel = classModel;
		// TODO: defining the other attribute

		// Parsing the file into a compilation unit
		CompilationUnit cu = StaticJavaParser.parse(new File(filepath));

		// Finding the attributes and initializing the models

		for (TypeDeclaration<?> typeDec : cu.getTypes()) {
			for (BodyDeclaration<?> member : typeDec.getMembers()) {
				member.toFieldDeclaration().ifPresent(field -> {
					for (VariableDeclarator variable : field.getVariables()) {

						AttributeModel attributeModel = factory.newInstance(AttributeModel.class);
						new AttributeLink(attributeModel);
						classModel.addAttribute(attributeModel);
						attributeModel.setClazz(classModel);
						attributeModel.setName(variable.getName().asString());
						attributeModel.setType(variable.getType().asString()); // TODO: à partir du nom du type, trouver la classe
						// correspondante (avoir une fonction qui le fait) -
						// nécessaire sur le type. à terme, ce ne sera plus une string mais un TypeModel

					}
				});
			}
		}

		// Finding the attributes and initializing the methods

		final class MethodNamePrinter extends VoidVisitorAdapter<Void> {
			@Override
			public void visit(MethodDeclaration md, Void arg) {
				super.visit(md, arg);
				String type = md.getType().asString();

				MethodModel methodModel = factory.newInstance(MethodModel.class);
				new MethodLink(methodModel);
				classModel.addMethod(methodModel);
				methodModel.setClazz(classModel);
				methodModel.setName(md.getNameAsString());
				methodModel.setType(md.getType().asString()); // TODO: see above

			}
		}

		VoidVisitor<?> methodNameVisitor = new MethodNamePrinter();
		methodNameVisitor.visit(cu, null);

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

}
