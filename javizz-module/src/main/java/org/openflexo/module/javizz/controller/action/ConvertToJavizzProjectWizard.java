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

import java.awt.Dimension;
import java.awt.Image;
import java.util.logging.Logger;

import org.openflexo.ApplicationContext;
import org.openflexo.components.wizard.FlexoWizard;
import org.openflexo.components.wizard.WizardStep;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.rt.VirtualModelInstance;
import org.openflexo.gina.annotation.FIBPanel;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconLibrary;
import org.openflexo.module.javizz.JVIconLibrary;
import org.openflexo.module.javizz.model.action.ConvertToJavizzProject;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.view.controller.FlexoController;

public class ConvertToJavizzProjectWizard extends FlexoWizard {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ConvertToJavizzProjectWizard.class.getPackage().getName());

	private final ConvertToJavizzProject action;

	private final ConfigureJavizzProject configureJavizzProject;

	private static final Dimension DIMENSIONS = new Dimension(600, 400);

	public ConvertToJavizzProjectWizard(ConvertToJavizzProject action, FlexoController controller) {
		super(controller);
		this.action = action;
		addStep(configureJavizzProject = new ConfigureJavizzProject());
	}

	@Override
	public Dimension getPreferredSize() {
		return DIMENSIONS;
	}

	@Override
	public String getWizardTitle() {
		return action.getLocales().localizedForKey("create_new_javizz_project");
	}

	@Override
	public Image getDefaultPageImage() {
		return IconFactory.getImageIcon(JVIconLibrary.JV_BIG_ICON, IconLibrary.NEW_32_32).getImage();
	}

	public ConfigureJavizzProject getConfigureJavizzProject() {
		return configureJavizzProject;
	}

	/**
	 * This step is used to set {@link VirtualModel} to be used, as well as name and title of the {@link VirtualModelInstance}
	 * 
	 * @author sylvain
	 *
	 */
	@FIBPanel("Fib/Wizard/ConfigureJavizzProject.fib")
	public class ConfigureJavizzProject extends WizardStep {

		public ApplicationContext getServiceManager() {
			return getController().getApplicationContext();
		}

		public ConvertToJavizzProject getAction() {
			return action;
		}

		public FlexoProject getProject() {
			return getAction().getFocusedObject();
		}

		@Override
		public String getTitle() {
			return action.getLocales().localizedForKey("configure_project");
		}

		@Override
		public boolean isValid() {

			if (StringUtils.isEmpty(getDescription())) {
				setIssueMessage(action.getLocales().localizedForKey("it_is_recommanded_to_describe_your_new_project"),
						IssueMessageType.WARNING);
			}

			return true;

		}

		public String getProjectName() {
			return action.getProjectName();
		}

		public void setProjectName(String projectName) {
			if (!projectName.equals(getProjectName())) {
				String oldValue = getProjectName();
				action.setProjectName(projectName);
				getPropertyChangeSupport().firePropertyChange("projectName", oldValue, projectName);
				checkValidity();
			}
		}

		public String getDescription() {
			return action.getDescription();
		}

		public void setDescription(String newDescription) {
			if (!newDescription.equals(getDescription())) {
				String oldValue = getDescription();
				action.setDescription(newDescription);
				getPropertyChangeSupport().firePropertyChange("newDescription", oldValue, newDescription);
				checkValidity();
			}
		}

	}

}
