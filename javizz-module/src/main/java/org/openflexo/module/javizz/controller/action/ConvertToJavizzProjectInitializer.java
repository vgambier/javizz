/**
 * 
 * Copyright (c) 2016, Openflexo
 * 
 * This file is part of Javizz, a java application visualization.
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

package org.openflexo.module.javizz.controller.action;

import java.util.EventObject;

import javax.swing.Icon;

import org.openflexo.components.wizard.Wizard;
import org.openflexo.components.wizard.WizardDialog;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.gina.controller.FIBController.Status;
import org.openflexo.module.javizz.JVIconLibrary;
import org.openflexo.module.javizz.model.JavizzProjectNature;
import org.openflexo.module.javizz.model.action.ConvertToJavizzProject;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

/**
 * @author fdagnat
 */
public class ConvertToJavizzProjectInitializer extends ActionInitializer<ConvertToJavizzProject, FlexoProject, FlexoObject> {

	public ConvertToJavizzProjectInitializer(final ControllerActionInitializer controllerActionInitializer) {
		super(ConvertToJavizzProject.ACTION_TYPE, controllerActionInitializer);
	}

	@Override
	protected FlexoActionInitializer<ConvertToJavizzProject> getDefaultInitializer() {
		return new FlexoActionInitializer<ConvertToJavizzProject>() {
			@Override
			public boolean run(EventObject e, ConvertToJavizzProject action) {
				final JavizzProjectNature javizzNature = action.getServiceManager().getProjectNatureService()
						.getProjectNature(JavizzProjectNature.class);

				if (javizzNature.authorizeInit(action.getFocusedObject())) {
					Wizard wizard = new ConvertToJavizzProjectWizard(action, getController());
					WizardDialog dialog = new WizardDialog(wizard, getController());
					dialog.showDialog();
					if (dialog.getStatus() != Status.VALIDATED) {
						// Operation cancelled
						return false;
					}
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<ConvertToJavizzProject> getDefaultFinalizer() {
		return new FlexoActionFinalizer<ConvertToJavizzProject>() {
			@Override
			public boolean run(EventObject e, ConvertToJavizzProject action) {
				getController().selectAndFocusObject(action.getFocusedObject());
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return JVIconLibrary.JV_SMALL_ICON;
	}

}
