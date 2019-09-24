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

import model.AttributeModel;
import model.ClassModel;

/**
 * Instances of this class are used to maintain a link between the attribute existing on the disk and the corresponding model.
 * 
 * @author Victor Gambier
 *
 */

@ModelEntity
public class AttributeLink extends Link<AttributeModel> {

	private String name; // the name of the attribute
	private ClassLink classLink; // the parent class

	/**
	 * The constructor. Takes information about the attribute and modelizes it. Links an instance of AttributeLink with an instance of
	 * AttributeModel
	 * 
	 * @param classLink
	 *            the parent class
	 * @param name
	 *            the name of the attribute
	 * @param type
	 *            the type of the attribute
	 * @throws ModelDefinitionException
	 */
	public AttributeLink(ClassLink classLink, String name, String type) throws ModelDefinitionException {

		super(new ModelFactory(ModelContextLibrary.getModelContext(AttributeModel.class)).newInstance(AttributeModel.class));

		ClassModel classModel = classLink.getModel();

		// Instantiating attributes

		this.name = name;
		this.classLink = classLink;

		model.setName(name);
		model.setType(type);
		model.setClazz(classModel);

		classModel.addAttribute(model);

	}

	@Override
	public AttributeLink create() throws FileNotFoundException, ModelDefinitionException {
		return new AttributeLink(classLink, name, model.getType());
	}

	/**
	 * Reads a .java file, and changes the name of the current attribute to match the input argument. Does not affect the model.
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

		// Retrieving the attribute (based on its current name)
		for (TypeDeclaration<?> typeDec : cu.getTypes()) {
			for (BodyDeclaration<?> member : typeDec.getMembers()) {
				member.toFieldDeclaration().ifPresent(field -> {
					for (VariableDeclarator variable : field.getVariables()) {

						String oldName = variable.getName().asString();
						if (oldName.equals(model.getName()))
							variable.setName(newName);
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
	 *             if there was an issue during the file parsing or the file write
	 * 
	 */

	public void setTypeInFile(String newType) throws IOException {

		String path = classLink.getCompilationUnitLink().getPath(); // Retrieving the path of the file where the attribute is located

		// Initializing the compilation unit
		CompilationUnit cu = StaticJavaParser.parse(new File(path));
		LexicalPreservingPrinter.setup(cu); // enables lexical preservation

		// Retrieving the attribute (based on its current name)
		for (TypeDeclaration<?> typeDec : cu.getTypes()) {
			for (BodyDeclaration<?> member : typeDec.getMembers()) {
				member.toFieldDeclaration().ifPresent(field -> {
					for (VariableDeclarator variable : field.getVariables()) {

						String oldName = variable.getNameAsString();
						if (oldName.equals(model.getName()))
							variable.setType(newType);
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
	 * Moves the attribute from its current file to another file. Uses lexical preservation?
	 * 
	 * @param newCompilationUnit
	 *            the CompilationUnitLink corresponding to the file where the attribute will be moved
	 * @throws IOException
	 *             if there was an issue during the file parsing or the file write
	 */
	// TODO: this method doesn't work properly if there are comments in the line where the attribute is declared
	public void moveToNewFile(CompilationUnitLink newCompilationUnit) throws IOException {

		/* Retrieve and remove the attribute from the original file */

		String pathOld = classLink.getCompilationUnitLink().getPath(); // Retrieving the path of the file where the attribute is located

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

		/* Retrieving the new file and adding the attribute to it */

		String pathNew = newCompilationUnit.getPath(); // Retrieving the path of the file where the attribute is located

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
}
