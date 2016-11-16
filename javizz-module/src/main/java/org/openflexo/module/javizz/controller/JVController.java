/**
 * 
 * Copyright (c) 2014-2015, Openflexo
 * 
 * This file is part of Javizz prototype, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.module.javizz.controller;

import javax.swing.ImageIcon;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.VirtualModelInstance;
import org.openflexo.foundation.fml.rt.action.FlexoBehaviourAction;
import org.openflexo.icon.IconLibrary;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.javizz.JVIconLibrary;
import org.openflexo.module.javizz.controller.action.JVControllerActionInitializer;
import org.openflexo.module.javizz.menu.JVMenuBar;
import org.openflexo.module.javizz.model.JavizzProject;
import org.openflexo.module.javizz.model.JavizzProjectNature;
import org.openflexo.selection.MouseSelectionManager;
import org.openflexo.view.FlexoMainPane;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.menu.FlexoMenuBar;

/**
 * Javizz-module specific FlexoController.
 * 
 * @author sylvain
 */
public class JVController extends FlexoController {

	public JVController(FlexoModule module) {
		super(module);
	}

	public JavizzProjectNature getJavizzNature() {
		return getApplicationContext().getProjectNatureService().getProjectNature(JavizzProjectNature.class);
	}

	public JavizzProject getJavizzProject() {
		return getJavizzNature().getJavizzProject(getProject());
	}

	@Override
	protected void initializePerspectives() {
	}

	@Override
	protected FlexoMenuBar createNewMenuBar() {
		return new JVMenuBar(this);
	}

	@Override
	public FlexoObject getDefaultObjectToSelect(FlexoProject project) {
		return project;
	}

	@Override
	protected FlexoMainPane createMainPane() {
		return new FlexoMainPane(this);
	}

	@Override
	public ImageIcon iconForObject(final Object object) {
		if (object instanceof JavizzProject) {
			return JVIconLibrary.JV_SMALL_ICON;
		}
		if (object instanceof VirtualModelInstance) {
			return IconLibrary.OPENFLEXO_NOTEXT_16;
		}
		return super.iconForObject(object);
	}

	@Override
	public ControllerActionInitializer createControllerActionInitializer() {
		return new JVControllerActionInitializer(this);
	}

	@Override
	public void willExecute(FlexoBehaviourAction<?, ?, ?> action) {
		super.willExecute(action);
	}

	@Override
	public void hasExecuted(FlexoBehaviourAction<?, ?, ?> action) {
		super.hasExecuted(action);
	}

	@Override
	public void objectWasDoubleClicked(Object object) {
		if (object instanceof FlexoConceptInstance) {
			FlexoConceptInstance fci = (FlexoConceptInstance) object;
		}
		super.objectWasDoubleClicked(object);
	}

	@Override
	protected MouseSelectionManager createSelectionManager() {
		return new JVSelectionManager(this);
	}
}
