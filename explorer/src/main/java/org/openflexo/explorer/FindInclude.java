package org.openflexo.explorer;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;

/**
 * @author Fabien Dagnat
 */
public class FindInclude extends CodeVisitorSupport {
	private List<String> result = new ArrayList<>();

	public List<String> getResult() {
		return result;
	}

	@Override
	public void visitMethodCallExpression(MethodCallExpression call) {
		// System.out.println(call.getMethodAsString());

		for (Expression e : ((TupleExpression) call.getArguments()).getExpressions()) {
			result.add(e.getText());
		}
		super.visitMethodCallExpression(call);
	}
}
