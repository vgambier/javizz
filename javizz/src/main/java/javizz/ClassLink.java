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
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;

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
		ModelFactory factory = new ModelFactory(ModelContextLibrary.getCompoundModelContext(ClassModel.class, AttributeModel.class));

		// Retrieving and setting the class name
		String className = FilenameUtils.removeExtension(filepath.substring(filepath.lastIndexOf("/") + 1));
		ClassModel classModel = factory.newInstance(ClassModel.class);
		classModel.setName(className);

		// Finding the attributes and initializing the models

		// Parsing the file into a compilation unit
		CompilationUnit cu = StaticJavaParser.parse(new File(filepath));

		for (TypeDeclaration<?> typeDec : cu.getTypes()) {
			for (BodyDeclaration<?> member : typeDec.getMembers()) {
				member.toFieldDeclaration().ifPresent(field -> {
					for (VariableDeclarator variable : field.getVariables()) {

						AttributeModel attributeModel = factory.newInstance(AttributeModel.class);
						attributeModel.setName(variable.getName().asString());
						attributeModel.setClazz(classModel);
						new AttributeLink(attributeModel);

						// attributeModel.setType(variable.getType().asString()); // TODO: à partir du nom du type, trouver la classe
						// correspondante (avoir une fonction qui le fait) -
						// nécessaire sur le type

						classModel.addAttribute(attributeModel);

						System.out.println("One attribute was found: " + attributeModel.toString() + ":" + variable.getName().asString());

					}
				});
			}
		}

	}

	// sync (might become more than one method)
	// after checking if the two arguments match and differ, determines which one should change to match the other, and applies the needed
	// changes
	public void sync(String filepath, ClassModel classModel) {

	}

	// TODO shouldn't this be in ProjectModel?
}
