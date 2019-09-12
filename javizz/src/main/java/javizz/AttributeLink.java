package javizz;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.openflexo.pamela.ModelContextLibrary;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.pamela.factory.ModelFactory;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import models.AttributeModel;
import models.ClassModel;

/**
 * @author Victor Gambier
 *
 */

@ModelEntity
public class AttributeLink {

	private AttributeModel attributeModel; // the corresponding model
	// TODO need another attribute that uniquely defines the attribute within the file system - possibly an arbitrary key number
	// the implementation will be similar to that of the path attribute. for now the primary key is the name
	private String name;

	public AttributeLink(ClassModel classModel, String name) {

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

		attributeModel.setName(name);
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
		AttributeLink attributeLinkFile = new AttributeLink(attributeModel.getClazz(), name);
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
						if (oldName.equals(attributeModel.getName())) {

							variable.setName(newName);
							if (true) { // TODO: global attribute check - only change the model if "synch mode" is enabled
								this.name = newName;
								attributeModel.setName(newName);
							}
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
	 * @throws IOException
	 * 
	 */

	public void setTypeInFile(String newType) throws IOException {

		String path = attributeModel.getClazz().getPath(); // Retrieving the path of the file where the attribute is located

		// Initializing the compilation unit
		CompilationUnit cu = StaticJavaParser.parse(new File(path));
		LexicalPreservingPrinter.setup(cu); // enables lexical preservation

		// Retrieving the attribute (based on its current name)
		for (TypeDeclaration<?> typeDec : cu.getTypes()) {
			for (BodyDeclaration<?> member : typeDec.getMembers()) {
				member.toFieldDeclaration().ifPresent(field -> {
					for (VariableDeclarator variable : field.getVariables()) {

						String oldName = variable.getNameAsString();
						if (oldName.equals(attributeModel.getName())) {

							variable.setType(newType);
							if (true) { // TODO: global attribute check - only change the model if "synch mode" is enabled
								attributeModel.setType(newType);
							}
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
	 * Moves the attribute from its current class to another class (i.e.: in a different file). Uses lexical preservation.
	 * 
	 * @param newClass
	 *            the ClassLink corresponding to the class where the attribute will be moved
	 * @throws IOException
	 */
	// TODO: this method doesn't work properly if there are comments in the line where the attribute is declared
	public void moveToNewClass(ClassLink newClass) throws IOException {

		/* Retrieve and remove the attribute from the original file */

		String pathOld = attributeModel.getClazz().getPath(); // Retrieving the path of the file where the attribute is located

		// Initializing the compilation unit
		CompilationUnit cuOld = StaticJavaParser.parse(new File(pathOld));
		LexicalPreservingPrinter.setup(cuOld); // enables lexical preservation

		// Retrieving the attribute (based on its current name)
		BodyDeclaration<?> memberWanted = null;
		boolean found = false; // is set to true when the attribute is found
		for (TypeDeclaration<?> typeDec : cuOld.getTypes()) {
			Iterator<BodyDeclaration<?>> iterator = typeDec.getMembers().iterator();
			while (!found && iterator.hasNext()) {
				BodyDeclaration<?> member = iterator.next();
				if (member.toFieldDeclaration() != null) {
					FieldDeclaration fieldDec = member.toFieldDeclaration().orElse(null);
					for (VariableDeclarator variable : fieldDec.getVariables()) {
						if (name.equals(variable.getNameAsString())) { // If we have found the right attribute
							found = true;
							memberWanted = member; // We remember the attribute declaration
							member.remove(); // Then remove it
							break;
						}
					}
				}
			}
		}

		// Writing this change to the file
		BufferedWriter writerOld = new BufferedWriter(new FileWriter(pathOld));
		writerOld.write(LexicalPreservingPrinter.print(cuOld));
		writerOld.close();

		/* Retrieve the new class and add the attribute to it */

		String pathNew = newClass.getPath(); // Retrieving the path of the file where the attribute is located

		// Initializing the compilation unit
		CompilationUnit cuNew = StaticJavaParser.parse(new File(pathNew));
		LexicalPreservingPrinter.setup(cuNew); // enables lexical preservation

		TypeDeclaration<?> typeDec = cuNew.getPrimaryType().orElse(null); // We'll add the attribute to the primary class
		typeDec.addMember(memberWanted); // We add the attribute that was previously retrieved

		// Writing this change to the file
		BufferedWriter writerNew = new BufferedWriter(new FileWriter(pathNew));
		writerNew.write(LexicalPreservingPrinter.print(cuNew));
		writerNew.close();

	}

	/**
	 * @return the attributeModel
	 */
	public AttributeModel getAttributeModel() {
		return attributeModel;
	}

}
