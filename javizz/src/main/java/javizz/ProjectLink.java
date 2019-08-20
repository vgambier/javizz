package javizz;

public class ProjectLink {

	private ProjectModel ProjectModel;
	private String path; // the filepath where the project is located

	// given a filepath, this method instantiates a projectModel, links it to the filepath via projectLink, and calls ClassLink.createModel
	// to instantiate the classes and all the child models
	public void readFolder(String filepath) {

		// instantiate a projectModel

		// looks for all folders containing .java files

		// calls PackageLink

		// instantiates a packageModel for each one

		// sets the name of the package models

		// links the packageModels and the packageLinks

		// calls ClassLink.createModel() to read each file

		// links the packageModels and the packageLinks and the ClassLinks. not the ClassModels

		// Note: some of this logic may be better suited in PackageLink

	}

}
