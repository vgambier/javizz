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
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

@ModelEntity
public class AttributeLink {

	private AttributeModel attributeModel; // the corresponding model
	// TODO need another attribute that uniquely defines the attribute within the file system - possibly an arbitrary key number
	// the implementation will be similar to that of the path attribute. for now the primary key is the name
	private String name;
	private String type; // the type of the attribute - will be obsolete once TypeLink is implemented

	public AttributeLink(ClassModel classModel, String name, String type) {

		// Instantiating attributes

		// We need to define a factory to instantiate AttributeModel
		ModelFactory factory = null;
		try {
			factory = new ModelFactory(ModelContextLibrary.getModelContext(AttributeModel.class));
		} catch (ModelDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.attributeModel = factory.newInstance(AttributeModel.class);

		this.name = name;
		this.type = type;

		attributeModel.setName(name);
		attributeModel.setType(type);
		attributeModel.setClazz(classModel);

		classModel.addAttribute(attributeModel);

	}

	/**
	 * Reads a .java file, compares it to the existing model, and updates the model
	 * 
	 * @throws ModelDefinitionException
	 * @throws FileNotFoundException
	 */

	public void updateModel() throws FileNotFoundException, ModelDefinitionException {

		// Generating a new model based on the existing file
		AttributeLink attributeLinkFile = new AttributeLink(attributeModel.getClazz(), name, type);
		AttributeModel attributeModelFile = attributeLinkFile.attributeModel;

		// Updating the model
		attributeModel.updateWith(attributeModelFile);

	}

	/**
	 * Reads a .java file, and changes the name of the current attribute to match the input argument. Does not affect the model.
	 * 
	 * @param newName
	 *            the new name of the attribute
	 * @throws IOException
	 * 
	 */
	public void setNameInFile(String newName) throws IOException {

		String path = attributeModel.getClazz().getPath(); // Retrieving the path of the file where the attribute is located

		// Initializing the compilation unit
		CompilationUnit cu = StaticJavaParser.parse(new File(path));
		LexicalPreservingPrinter.setup(cu); // enables lexical preservation

		// Retrieving the attribute (based on its current name)
		for (TypeDeclaration<?> typeDec : cu.getTypes()) {
			for (BodyDeclaration<?> member : typeDec.getMembers()) {
				member.toFieldDeclaration().ifPresent(field -> {
					for (VariableDeclarator variable : field.getVariables()) {
						String oldName = variable.getName().asString();
						if (oldName.equals(getAttributeModel().getName())) {
							System.out
									.println("this being printed means that the name of the attribute in the file is now attributeDefault");
							variable.setName(newName);
						}
					}
				});
			}
		}

		// Writing all changes to the original file
		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
		writer.write(LexicalPreservingPrinter.print(cu));
		writer.close();

	}

	/**
	 * Reads a .java file, and changes the type of the current attribute to match the input argument. Does not affect the model.
	 * 
	 * @param newType
	 *            the new type of the attribute
	 * @throws FileNotFoundException
	 * 
	 */
	public void updateTypeInFile(String newType) throws FileNotFoundException {

		String path = attributeModel.getClazz().getPath(); // Retrieving the path of the file where the attribute is located

		// Initializing the compilation unit
		CompilationUnit cu = StaticJavaParser.parse(new File(path));
		LexicalPreservingPrinter.setup(cu); // enables lexical preservation

		// Retrieving the attribute (based on its current name)
		for (TypeDeclaration<?> typeDec : cu.getTypes()) {
			for (BodyDeclaration<?> member : typeDec.getMembers()) {
				member.toFieldDeclaration().ifPresent(field -> {
					for (VariableDeclarator variable : field.getVariables()) {
						String oldType = variable.getType().asString();
						if (oldType.equals(this.type))
							variable.setType(newType);
					}
				});
			}
		}
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the attributeModel
	 */
	public AttributeModel getAttributeModel() {
		return attributeModel;
	}

}
