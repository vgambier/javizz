/*
 * (c) Copyright 2013 Openflexo
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openflexo.technologyadapter.java.rm;

import java.util.logging.Logger;

import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.PamelaResourceFactory;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.technologyadapter.java.JAVATechnologyAdapter;
import org.openflexo.technologyadapter.java.model.JAVAFactory;
import org.openflexo.technologyadapter.java.model.JAVAFileModel;

/**
 * Implementation of PamelaResourceFactory for {@link JAVAResource}
 * 
 * @author sylvain
 *
 */
public class JAVAResourceFactory extends PamelaResourceFactory<JAVAResource, JAVAFileModel, JAVATechnologyAdapter, JAVAFactory> {

	private static final Logger logger = Logger.getLogger(JAVAResourceFactory.class.getPackage().getName());

	public static String JAVA_EXTENSION = ".java";

	public JAVAResourceFactory() throws ModelDefinitionException {
		super(JAVAResource.class);
	}

	@Override
	public JAVAFactory makeResourceDataFactory(JAVAResource resource,
			TechnologyContextManager<JAVATechnologyAdapter> technologyContextManager) throws ModelDefinitionException {
		return new JAVAFactory(resource, technologyContextManager.getServiceManager().getEditingContext());
	}

	@Override
	public JAVAFileModel makeEmptyResourceData(JAVAResource resource) {
		return resource.getFactory().makeJavaFileModel();
	}

	@Override
	public <I> boolean isValidArtefact(I serializationArtefact, FlexoResourceCenter<I> resourceCenter) {
		return resourceCenter.retrieveName(serializationArtefact).endsWith(JAVA_EXTENSION);
	}

	@Override
	protected <I> JAVAResource registerResource(JAVAResource resource, FlexoResourceCenter<I> resourceCenter,
			TechnologyContextManager<JAVATechnologyAdapter> technologyContextManager) {
		super.registerResource(resource, resourceCenter, technologyContextManager);

		// Register the resource in the DocXDocumentRepository of supplied resource center
		registerResourceInResourceRepository(resource,
				technologyContextManager.getTechnologyAdapter().getJAVAResourceRepository(resourceCenter));

		return resource;
	}
}
