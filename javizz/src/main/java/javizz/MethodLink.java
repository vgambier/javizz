package javizz;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.openflexo.pamela.ModelContextLibrary;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.pamela.factory.ModelFactory;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;
import com.github.javaparser.symbolsolver.javaparser.Navigator;

import model.ClassModel;
import model.MethodModel;

/**
 * Instances of this class are used to maintain a link between the method existing on the disk and the corresponding model.
 * 
 * @author Victor Gambier
 *
 */

@ModelEntity
public class MethodLink {

	private MethodModel methodModel; // the corresponding model
	private String name; // the name of the method
	private ClassLink classLink; // the parent class

	/**
	 * The constructor. Takes information about the method and modelizes it. Links an instance of MethodLink with an instance of MethodModel
	 * 
	 * @param classLink
	 *            the parent class
	 * @param name
	 *            the name of the method
	 * @param type
	 *            the type of the method
	 */
	public MethodLink(ClassLink classLink, String name, String type) {

		ClassModel classModel = classLink.getClassModel();

		// We first need to define a factory to instantiate AttributeModel
		ModelFactory factory = null;
		try {
			factory = new ModelFactory(ModelContextLibrary.getModelContext(MethodModel.class));
		} catch (ModelDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.methodModel = factory.newInstance(MethodModel.class);
		this.name = name;
		this.classLink = classLink;

		methodModel.setName(name);
		methodModel.setType(type);
		methodModel.setClazz(classModel);

		classModel.addMethod(methodModel);

	}

	/**
	 * Reads a .java file, compares it to the existing model, and updates the model accordingly
	 * 
	 */
	public void updateModel() {

		// Generating a new model based on the input file
		MethodLink methodLinkFile = new MethodLink(classLink, name, methodModel.getType());
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
	 *             if there was an issue during the file parsing or the file write
	 * 
	 */
	public void setNameInFile(String newName) throws IOException {

		String path = classLink.getCompilationUnitLink().getPath(); // Retrieving the path of the file where the attribute is located

		// Initializing the compilation unit
		CompilationUnit cu = StaticJavaParser.parse(new File(path));
		LexicalPreservingPrinter.setup(cu); // enables lexical preservation

		ClassOrInterfaceDeclaration cls = Navigator.demandClassOrInterface(cu, classLink.getName());
		List<MethodDeclaration> methods = cls.findAll(MethodDeclaration.class);
		for (MethodDeclaration method : methods) {
			String nameInFile = method.getNameAsString();
			if (nameInFile.equals(name))
				method.setName(newName);
		}

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
	 *             if there was an issue during the file parsing or the file write
	 * 
	 */
	public void setTypeInFile(String newType) throws IOException {

		String path = classLink.getCompilationUnitLink().getPath(); // Retrieving the path of the file where the attribute is located

		// Initializing the compilation unit
		CompilationUnit cu = StaticJavaParser.parse(new File(path));
		LexicalPreservingPrinter.setup(cu); // enables lexical preservation

		ClassOrInterfaceDeclaration cls = Navigator.demandClassOrInterface(cu, classLink.getName());
		List<MethodDeclaration> methods = cls.findAll(MethodDeclaration.class);
		for (MethodDeclaration method : methods) {
			String nameInFile = method.getNameAsString();
			System.out.println(nameInFile);
			if (nameInFile.equals(name))
				method.setType(newType);
		}

		// Writing all changes to the original file
		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
		writer.write(LexicalPreservingPrinter.print(cu));
		writer.close();
	}

	/**
	 * @return the methodModel
	 */
	public MethodModel getMethodModel() {
		return methodModel;
	}

	public void setName(String name) {
		this.name = name;
	}

}
