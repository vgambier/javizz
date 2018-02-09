package org.openflexo.explorer.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.builder.AstBuilder;

public class GradleDir extends Dir {

	protected GradleDir(Path path) {
		super(path);
	}

	protected GradleDir(String path) {
		super(path);
	}

	protected void visitSettings(CodeVisitorSupport visitor) throws IOException {
		visitGradleFile("settings", visitor);
	}

	protected void visitBuild(CodeVisitorSupport visitor) throws IOException {
		visitGradleFile("build", visitor);
	}

	private void visitGradleFile(String fileName, CodeVisitorSupport visitor) throws IOException {
		Path gradleFile = this.getPath().resolve(fileName + ".gradle");
		if (Files.exists(gradleFile)) {
			String fileContent = new String(Files.readAllBytes(gradleFile));
			if (!fileContent.equals("")) {
				List<ASTNode> nodes = new AstBuilder().buildFromString(fileContent);
				for (ASTNode node : nodes) {
					node.visit(visitor);
				}
			}
			return;
		}
		throw new IOException("Found no file " + gradleFile);
	}
}
