package org.openflexo.explorer.model;

import java.nio.file.Path;

public abstract class GradleComposite extends GradleDir {
	protected GradleComposite(String path) {
		super(path);
	}

	protected GradleComposite(Path path) {
		super(path);
	}
}
