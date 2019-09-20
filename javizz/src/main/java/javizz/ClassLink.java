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

import model.ClassModel;
import model.CompilationUnitModel;

/**
 * Instances of this class are used to maintain a link between the class on the disk and the corresponding model.
 * 
 * @author Victor Gambier
 *
 */

@ModelEntity
public class ClassLink {

	private ClassModel classModel; // the corresponding model
	private List<AttributeLink> attributeLinks; // the children AttributeLink
	private List<MethodLink> methodLinks; // the children methodLink
	private CompilationUnitLink compilationUnitLink; // the parent compilation unit

	// Assumes the file corresponding to the compilationUnitLink does have a class called className

	public ClassLink(CompilationUnitLink compilationUnitLink, String className) throws FileNotFoundException, ModelDefinitionException {

		CompilationUnitModel compilationUnitModel = compilationUnitLink.getCompilationUnitModel();

		// Instantiating attributes

		// We first need to define a factory to instantiate AttributeModel
		ModelFactory factory = new ModelFactory(ModelContextLibrary.getModelContext(ClassModel.class));

		this.classModel = factory.newInstance(ClassModel.class);
		attributeLinks = new ArrayList<AttributeLink>();
		methodLinks = new ArrayList<MethodLink>();
		this.compilationUnitLink = compilationUnitLink;

		classModel.setName(className);
		classModel.setCompilationUnit(compilationUnitModel);

		compilationUnitModel.addClass(classModel);

		// Looking for methods and attributes within the compilation unit

		// Parsing the file into a javaparser compilation unit
		CompilationUnit cu = StaticJavaParser.parse(new File(compilationUnitLink.getPath()));

		// Retrieving the relevant class
		TypeDeclaration<?> typeDec = cu.getClassByName(className).orElse(null);

		// Finding the attributes and initializing the models

		for (BodyDeclaration<?> member : typeDec.getMembers()) {
			member.toFieldDeclaration().ifPresent(field -> {
				for (VariableDeclarator variable : field.getVariables()) {
					// Retrieving relevant data
					String name = variable.getNameAsString();
					String type = variable.getTypeAsString();
					// This constructor will take care of modelizing the attribute and its contents
					AttributeLink attributeLink = new AttributeLink(this, name, type);
					attributeLinks.add(attributeLink);
				}
			});
		}

		// Finding the attributes and initializing the methods
		// TODO: this actually grabs all methods, not just the one from the class

		final class MethodNamePrinter extends VoidVisitorAdapter<Void> {
			@Override
			public void visit(MethodDeclaration md, Void arg) {
				super.visit(md, arg);

				// Grabbing relevant data
				String name = md.getNameAsString();
				String type = md.getTypeAsString();
				// This constructor will take care of modelizing the method and its contents
				MethodLink methodLink = new MethodLink(getClassLink(), name, type);
				methodLinks.add(methodLink);

			}
		}

		VoidVisitor<?> methodNameVisitor = new MethodNamePrinter();
		methodNameVisitor.visit(cu, null);

	}

	public ClassModel getClassModel() {
		return classModel;
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

}
