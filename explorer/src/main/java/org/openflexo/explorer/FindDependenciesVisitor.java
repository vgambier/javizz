package org.openflexo.explorer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;

/**
 * @author Lovett Li
 */
public class FindDependenciesVisitor extends CodeVisitorSupport {

	private int dependenceLineNum = -1;
	private int columnNum = -1;
	private List<GradleDependency> dependencies = new ArrayList<>();

	@Override
	public void visitMethodCallExpression(MethodCallExpression call) {
		System.out.println(call.getMethodAsString());
		if (!(call.getMethodAsString().equals("buildscript"))) {
			if (call.getMethodAsString().equals("dependencies")) {
				if (dependenceLineNum == -1) {
					dependenceLineNum = call.getLastLineNumber();
				}
			}

			super.visitMethodCallExpression(call);
		}
	}

	@Override
	public void visitArgumentlistExpression(ArgumentListExpression ale) {
		List<Expression> expressions = ale.getExpressions();

		if (expressions.size() == 1 && expressions.get(0) instanceof ConstantExpression) {
			String depStr = expressions.get(0).getText();
			String[] deps = depStr.split(":");

			if (deps.length == 3) {
				dependencies.add(new GradleDependency(deps[0], deps[1], deps[2]));
			}
		}

		super.visitArgumentlistExpression(ale);
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
		List<MapEntryExpression> mapEntryExpressions = expression.getMapEntryExpressions();
		Map<String, String> dependenceMap = new HashMap<>();

		for (MapEntryExpression mapEntryExpression : mapEntryExpressions) {
			String key = mapEntryExpression.getKeyExpression().getText();
			String value = mapEntryExpression.getValueExpression().getText();
			dependenceMap.put(key, value);
		}

		dependencies.add(new GradleDependency(dependenceMap));

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
