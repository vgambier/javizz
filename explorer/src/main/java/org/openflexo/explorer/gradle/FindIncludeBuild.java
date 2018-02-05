package org.openflexo.explorer.gradle;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;

/**
 * @author Fabien Dagnat
 */
public class FindIncludeBuild extends CodeVisitorSupport {
	private List<Path> result = new ArrayList<>();

	public List<Path> getResult() {
		return result;
	}

	@Override
	public void visitMethodCallExpression(MethodCallExpression call) {
		// System.out.println(call.getMethodAsString());
		String includedFile = ((TupleExpression) call.getArguments()).getExpression(0).getText();
		result.add(Paths.get(includedFile));
		super.visitMethodCallExpression(call);
	}
}
