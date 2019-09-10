package javizz;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import org.openflexo.pamela.ModelContextLibrary;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.pamela.factory.ModelFactory;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import models.ClassModel;
import models.MethodModel;

@ModelEntity
public class MethodLink {

	private MethodModel methodModel;
	// TODO need another attribute that uniquely defines the method within the file system - for now the primary key is the name
	private String name;

	public MethodLink(ClassModel classModel, String name, String type) {

		// we need to define factory to instantiate AttributeModel
		ModelFactory factory = null;
		try {
			factory = new ModelFactory(ModelContextLibrary.getModelContext(MethodModel.class));
		} catch (ModelDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.methodModel = factory.newInstance(MethodModel.class);

		this.name = name;

		methodModel.setName(name);
		methodModel.setType(type);
		methodModel.setClazz(classModel);
		classModel.addMethod(methodModel);

		methodModel.setMethodLink(this);

		// TODO: handle actual types
	}

	/**
	 * Reads a .java file, compares it to the existing model, and updates the model
	 * 
	 * @throws ModelDefinitionException
	 * @throws FileNotFoundException
	 */
	public void updateModel() throws FileNotFoundException, ModelDefinitionException {

		// Generating a new model based on the input file
		MethodLink methodLinkFile = new MethodLink(methodModel.getClazz(), name, methodModel.getType());
		MethodModel methodModelFile = methodLinkFile.methodModel;

		// Updating the model
		methodModel.updateWith(methodModelFile);
	}

	/**
	 * Reads a .java file, and changes the name of the current method to match the input argument. Does not affect the model.
	 * 
	 * @param newName
	 *            the new name of the attribute
	 * @throws IOException
	 * 
	 */
	public void setNameInFile(String newName) throws IOException {

		String path = methodModel.getClazz().getPath(); // Retrieving the path of the file where the attribute is located

		// Initializing the compilation unit
		CompilationUnit cu = StaticJavaParser.parse(new File(path));
		LexicalPreservingPrinter.setup(cu); // enables lexical preservation

		final class MethodNamePrinter extends VoidVisitorAdapter<Void> {
			@Override
			public void visit(MethodDeclaration md, Void arg) {
				super.visit(md, arg);

				String oldName = md.getNameAsString();
				if (oldName.equals(methodModel.getName()))
					md.setName(newName);
			}
		}

		VoidVisitor<?> methodNameVisitor = new MethodNamePrinter();
		methodNameVisitor.visit(cu, null);

		// Writing all changes to the original file
		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
		writer.write(LexicalPreservingPrinter.print(cu));
		writer.close();
	}

	/**
	 * Reads a .java file, and changes the type of the current method to match the input argument. Does not affect the model.
	 * 
	 * @param newName
	 *            the new name of the method
	 * @throws IOException
	 * 
	 */
	public void setTypeInFile(String newType) throws IOException {

		String path = methodModel.getClazz().getPath(); // Retrieving the path of the file where the attribute is located

		// Initializing the compilation unit
		CompilationUnit cu = StaticJavaParser.parse(new File(path));
		LexicalPreservingPrinter.setup(cu); // enables lexical preservation

		final class MethodNamePrinter extends VoidVisitorAdapter<Void> {
			@Override
			public void visit(MethodDeclaration md, Void arg) {
				super.visit(md, arg);

				String oldName = md.getNameAsString();
				if (oldName.equals(methodModel.getName()))
					md.setType(newType);
			}
		}

		VoidVisitor<?> methodNameVisitor = new MethodNamePrinter();
		methodNameVisitor.visit(cu, null);

		// Writing all changes to the original file
		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
		writer.write(LexicalPreservingPrinter.print(cu));
		writer.close();
	}

	// TODO

	/**
	 * Reads a directory containing .java files, compares it to the existing model, and updates the folder
	 * 
	 */
	public void updateFile() {

		// checks if the method should change, then does it

	}

	/**
	 * @return the methodModel
	 */
	public MethodModel getMethodModel() {
		return methodModel;
	}

}
