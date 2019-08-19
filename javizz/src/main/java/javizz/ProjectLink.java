package javizz;

import java.io.File;
import java.io.FileNotFoundException;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

public class ProjectLink {

	private ProjectModel ProjectModel;
	private String path; // the filepath where the project is located

	// given a filepath, this method instantiates a projectModel, links it to the filepath via projectLink, and calls ClassLink.createModel
	// to instantiate the classes and all the child models
	public void createModel(String filepath) throws FileNotFoundException {

		// Parsing the file into a compilation unit
		CompilationUnit cu = StaticJavaParser.parse(new File(filepath));

	}

}
