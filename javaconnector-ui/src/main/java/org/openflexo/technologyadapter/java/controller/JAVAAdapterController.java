/*
 * (c) Copyright 2013- Openflexo
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

package org.openflexo.technologyadapter.java.controller;

import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.gina.utils.InspectorGroup;
import org.openflexo.technologyadapter.java.JAVATechnologyAdapter;
import org.openflexo.technologyadapter.java.library.JAVAIconLibrary;
import org.openflexo.technologyadapter.java.model.JAVAFileModel;
import org.openflexo.technologyadapter.java.view.JAVAFileView;
import org.openflexo.view.EmptyPanel;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.TechnologyAdapterController;
import org.openflexo.view.controller.TechnologyPerspective;
import org.openflexo.view.controller.model.FlexoPerspective;

public class JAVAAdapterController extends TechnologyAdapterController<JAVATechnologyAdapter> {

	static final Logger LOGGER = Logger.getLogger(JAVAAdapterController.class.getPackage().getName());

	private InspectorGroup javaInspectorGroup;

	@Override
	public Class<JAVATechnologyAdapter> getTechnologyAdapterClass() {
		return JAVATechnologyAdapter.class;
	}

	@Override
	public void initializeActions(ControllerActionInitializer actionInitializer) {
		// actionInitializer.getController().getModuleInspectorController().loadDirectory(ResourceLocator.locateResource("Inspectors/JAVA"));
	}

	@Override
	protected void initializeInspectors(FlexoController controller) {
		javaInspectorGroup = controller.loadInspectorGroup("Java", getTechnologyAdapter().getLocales(),
				getFMLTechnologyAdapterInspectorGroup());
	}

	@Override
	public InspectorGroup getTechnologyAdapterInspectorGroup() {
		return javaInspectorGroup;
	}

	@Override
	public ImageIcon getTechnologyBigIcon() {
		return JAVAIconLibrary.JAVA_TECHNOLOGY_BIG_ICON;
	}

	@Override
	public ImageIcon getTechnologyIcon() {
		return JAVAIconLibrary.JAVA_TECHNOLOGY_ICON;
	}

	@Override
	public ImageIcon getModelIcon() {
		return JAVAIconLibrary.JAVA_FILE_ICON;
	}

	@Override
	public ImageIcon getMetaModelIcon() {
		return JAVAIconLibrary.JAVA_FILE_ICON;
	}

	@Override
	public ImageIcon getIconForTechnologyObject(Class<? extends TechnologyObject<?>> arg0) {
		return JAVAIconLibrary.JAVA_TECHNOLOGY_ICON;
	}

	@Override
	public boolean hasModuleViewForObject(TechnologyObject<JAVATechnologyAdapter> object, FlexoController controller) {
		if (object instanceof JAVAFileModel) {
			return true;
		}
		// TODO: handle java folder
		return false;
	}

	@Override
	public ModuleView<?> createModuleViewForObject(final TechnologyObject<JAVATechnologyAdapter> object, final FlexoController controller,
			final FlexoPerspective perspective) {
		if (object instanceof JAVAFileModel) {
			return new JAVAFileView((JAVAFileModel) object, controller, perspective);
		}
		// TODO: handle java folder
		return new EmptyPanel<TechnologyObject<JAVATechnologyAdapter>>(controller, perspective, object);
	}

	/*public ModuleView<?> createModuleViewForRepository(final RepositoryFolder<JAVAResource> object, final FlexoController controller,
			final FlexoPerspective perspective) {
		if (object instanceof RepositoryFolder<?>) {
			return new JAVARepositoryView(object, controller, perspective);
		}
		return new EmptyPanel<RepositoryFolder<JAVAResource>>(controller, perspective, object);
	}*/

	@Override
	public ImageIcon getIconForFlexoRole(Class<? extends FlexoRole<?>> role) {
		return JAVAIconLibrary.JAVA_TECHNOLOGY_ICON;
	}

	@Override
	public String getWindowTitleforObject(TechnologyObject<JAVATechnologyAdapter> obj, FlexoController controller) {
		if (obj instanceof JAVAFileModel) {
			return ((JAVAFileModel) obj).getName();
		}
		return "";
	}

	@Override
	public TechnologyPerspective<JAVATechnologyAdapter> getTechnologyPerspective(FlexoController controller) {
		JAVATechnologyPerspective returned = new JAVATechnologyPerspective(getTechnologyAdapter(), controller);
		getTechnologyPerspectives().put(controller, returned);
		return returned;
	}

}
