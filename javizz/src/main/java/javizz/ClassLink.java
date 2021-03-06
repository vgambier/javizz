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
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.symbolsolver.javaparser.Navigator;

import model.ClassModel;
import model.CompilationUnitModel;

/**
 * Instances of this class are used to maintain a link between the class on the disk and the corresponding model.
 * 
 * @author Victor Gambier
 *
 */

@ModelEntity
public class ClassLink extends Link<ClassModel> {

	private String name; // the name of the class
	private List<AttributeLink> attributeLinks; // the children AttributeLink
	private List<MethodLink> methodLinks; // the children methodLink
	private CompilationUnitLink compilationUnitLink; // the parent compilation unit

	// Assumes the file corresponding to the compilationUnitLink does have a class called className

	public ClassLink(CompilationUnitLink compilationUnitLink, String className) throws FileNotFoundException, ModelDefinitionException {

		super(new ModelFactory(ModelContextLibrary.getModelContext(ClassModel.class)).newInstance(ClassModel.class));

		CompilationUnitModel compilationUnitModel = compilationUnitLink.getModel();

		// Instantiating attributes

		this.name = className;
		attributeLinks = new ArrayList<AttributeLink>();
		methodLinks = new ArrayList<MethodLink>();
		this.compilationUnitLink = compilationUnitLink;

		model.setName(className);
		model.setCompilationUnit(compilationUnitModel);

		compilationUnitModel.addClass(model);

		// Looking for methods and attributes within the compilation unit

		// Parsing the file into a javaparser compilation unit
		CompilationUnit cu = StaticJavaParser.parse(new File(compilationUnitLink.getPath()));

		// Finding the attributes and initializing the models

		// Retrieving the relevant class
		TypeDeclaration<?> typeDec = cu.getClassByName(className).orElse(null);

		for (BodyDeclaration<?> member : typeDec.getMembers()) {
			member.toFieldDeclaration().ifPresent(field -> {
				for (VariableDeclarator variable : field.getVariables()) {
					// Retrieving relevant data
					String name = variable.getNameAsString();
					String type = variable.getTypeAsString();
					// This constructor will take care of modelizing the attribute and its contents

					AttributeLink attributeLink = null;
					try {
						attributeLink = new AttributeLink(this, name, type);
					} catch (ModelDefinitionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					attributeLinks.add(attributeLink);
				}
			});
		}

		// Finding the attributes and initializing the methods

		ClassOrInterfaceDeclaration cls = Navigator.demandClassOrInterface(cu, className);
		List<MethodDeclaration> methods = cls.findAll(MethodDeclaration.class);
		for (MethodDeclaration method : methods) {
			String name = method.getNameAsString();
			String type = method.getTypeAsString();
			MethodLink methodLink = new MethodLink(getClassLink(), name, type);
			methodLinks.add(methodLink);
		}
	}

	@Override
	public ClassLink create() throws FileNotFoundException, ModelDefinitionException {
		return new ClassLink(compilationUnitLink, name);
	}

	public CompilationUnitLink getCompilationUnitLink() {
		return compilationUnitLink;
	}

	public ClassLink getClassLink() {
		return this;
	}

	public List<AttributeLink> getAttributeLinks() {
		return attributeLinks;
	}

	public List<MethodLink> getMethodLinks() {
		return methodLinks;
	}

	public String getName() {
		return name;
	}

}
