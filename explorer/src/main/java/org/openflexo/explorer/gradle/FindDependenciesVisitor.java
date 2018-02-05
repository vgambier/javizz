package org.openflexo.explorer.gradle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.openflexo.explorer.model.Project;

/**
 * @author Fabien Dagnat adapted from Lovett Li
 */
public class FindDependenciesVisitor extends CodeVisitorSupport {

	private int dependenceLineNum = -1;
	private int columnNum = -1;
	private List<GradleDependency> dependencies = new ArrayList<>();

	private GradleDependency.Kind kind;
	private boolean project;
	private List<Project> projects;
	private boolean openflexo;

	public FindDependenciesVisitor(List<Project> projects) {
		this.projects = projects;
	}

	@Override
	public void visitIfElse(IfStatement ifElse) {
	}

	@Override
	public void visitDeclarationExpression(DeclarationExpression expression) {
	}

	@Override
	public void visitMethodCallExpression(MethodCallExpression call) {
		if (call.getMethodAsString().equals("buildscript") || call.getMethodAsString().equals("sablecc")
				|| call.getMethodAsString().equals("sourceSets") || call.getMethodAsString().equals("apply")
				|| call.getMethodAsString().equals("task") || call.getMethodAsString().equals("withType")
				|| call.getMethodAsString().equals("compileJava") || call.getMethodAsString().equals("test")) {
			return;
		}
		if (call.getMethodAsString().equals("dependencies")) {
			if (dependenceLineNum == -1) {
				dependenceLineNum = call.getLastLineNumber();
			}
		}
		else if (call.getMethodAsString().equals("compile"))
			kind = GradleDependency.Kind.Compile;
		else if (call.getMethodAsString().equals("testCompile"))
			kind = GradleDependency.Kind.TestCompile;
		else if (call.getMethodAsString().equals("runtime"))
			kind = GradleDependency.Kind.Runtime;
		else if (call.getMethodAsString().equals("testRuntime"))
			kind = GradleDependency.Kind.TestRuntime;
		else if (call.getMethodAsString().equals("project")) {
			project = true;
		}
		else if (call.getMethodAsString().equals("openflexo")) {
			openflexo = true;
		}
		else {
			String projectName = getProjectName(call.getMethodAsString());
			if (isProject(projectName)) {
				dependencies.add(new InternalDependency(getProject(projectName), this.kind));
				return;
			}
			System.out.println(call.getMethodAsString());
		}
		super.visitMethodCallExpression(call);
		if (call.getMethodAsString().equals("project"))
			project = false;
		else if (call.getMethodAsString().equals("openflexo"))
			openflexo = false;
	}

	private boolean isProject(String name) {
		for (Project p : this.projects) {
			if (p.getName().equals(getProjectName(name)))
				return true;
		}
		return false;
	}

	private static String getProjectName(String name) {
		String temp = name;
		if (temp.equals("diana") || temp.equals("connie") || temp.equals("gina") || temp.equals("pamela"))
			temp = temp + "-core";
		else if (temp.endsWith("Utils"))
			temp = temp.toLowerCase();
		else {
			// Each part of the camel case are lowercased
			StringBuffer result = new StringBuffer();
			boolean first = true;
			for (String elt : splitCamelCaseString(name)) {
				if (first)
					first = false;
				else if (!elt.equals("Adapter") && !elt.equals("Help") && !elt.equals("Localization") && !elt.equals("Diagram")
						&& !elt.equals("Connector") && !elt.equals("Doc") && !elt.equals("Resource") && !elt.equals("Center")
						&& !elt.equals("Manager"))
					result.append("-");
				result.append(elt.toLowerCase());
			}
			temp = result.toString();
		}
		return temp;

	}

	// accept a string, like aCamelString
	// return a list containing strings, in this case, [a, Camel, String]
	public static LinkedList<String> splitCamelCaseString(String s) {
		LinkedList<String> result = new LinkedList<>();
		for (String w : s.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")) {
			result.add(w);
		}
		return result;
	}

	@Override
	public void visitArgumentlistExpression(ArgumentListExpression ale) {
		// System.out.println("=> ALE ");// + ale
		List<Expression> expressions = ale.getExpressions();

		if (expressions.size() == 1 && expressions.get(0) instanceof ConstantExpression) {
			String depStr = expressions.get(0).getText();
			String[] deps = depStr.split(":");

			if (deps.length == 3) {
				dependencies.add(new ExternalDependency(deps[0], deps[1], deps[2], this.kind));
			}
			else if (deps.length == 2 && project) {
				dependencies.add(new InternalDependency(getProject(deps[1]), this.kind));
			}
			else if (deps.length == 1 && openflexo) {
				dependencies.add(new InternalDependency(getProject(deps[0]), this.kind));
			}
		}

		super.visitArgumentlistExpression(ale);
	}

	private Project getProject(String name) {
		for (Project p : this.projects) {
			if (p.getName().equals(name.toLowerCase()))
				return p;
		}
		System.out.println("=> do not know of project " + name);
		return null;
	}

	@Override
	public void visitClosureExpression(ClosureExpression expression) {
		if (dependenceLineNum != -1 && expression.getLineNumber() == expression.getLastLineNumber()) {
			columnNum = expression.getLastColumnNumber();
		}

		super.visitClosureExpression(expression);
	}

	@Override
	public void visitMapExpression(MapExpression expression) {
		// System.out.println("=> EXP " + expression);
		List<MapEntryExpression> mapEntryExpressions = expression.getMapEntryExpressions();
		String group = null;
		String name = null;
		String version = null;

		for (MapEntryExpression mapEntryExpression : mapEntryExpressions) {
			String value = mapEntryExpression.getValueExpression().getText();
			if (mapEntryExpression.getKeyExpression().getText().equals("group"))
				group = value;
			else if (mapEntryExpression.getKeyExpression().getText().equals("name"))
				name = value;
			else if (mapEntryExpression.getKeyExpression().getText().equals("version"))
				version = value;
		}

		dependencies.add(new ExternalDependency(group, name, version, this.kind));
		super.visitMapExpression(expression);
	}

	public int getDependenceLineNum() {
		return dependenceLineNum;
	}

	public int getColumnNum() {
		return columnNum;
	}

	public List<GradleDependency> getDependencies() {
		return dependencies;
	}

}
