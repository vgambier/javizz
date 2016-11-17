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

package org.openflexo.module.javizz.model.action;

import java.util.Vector;

import org.openflexo.ApplicationContext;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.module.javizz.JavizzEditor;
import org.openflexo.module.javizz.model.JavizzProjectNature;

/**
 * @author fdagnat
 */
public class ConvertToJavizzProject extends FlexoAction<ConvertToJavizzProject, FlexoProject, FlexoObject> {

	public static final ConvertToJavizzProjectActionType ACTION_TYPE = new ConvertToJavizzProjectActionType();

	static {
		FlexoObjectImpl.addActionForClass(ACTION_TYPE, FlexoProject.class);
	}

	public static final class ConvertToJavizzProjectActionType extends FlexoActionType<ConvertToJavizzProject, FlexoProject, FlexoObject> {

		private ConvertToJavizzProjectActionType() {
			super("convert_to_Javizz_project", FlexoActionType.convertMenu, FlexoActionType.defaultGroup,
					FlexoActionType.NORMAL_ACTION_TYPE);
		}

		@Override
		public ConvertToJavizzProject makeNewAction(final FlexoProject focusedObject, final Vector<FlexoObject> globalSelection,
				final FlexoEditor editor) {
			return new ConvertToJavizzProject(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(final FlexoProject project, final Vector<FlexoObject> globalSelection) {
			return !project.hasNature(project.getServiceManager().getProjectNatureService().getProjectNature(JavizzProjectNature.class));
		}

		@Override
		public boolean isEnabledForSelection(final FlexoProject project, final Vector<FlexoObject> globalSelection) {
			return isVisibleForSelection(project, globalSelection);
		}
	}

	// ====================
	// FIELDS
	// ====================
	private String projectName;
	private String description;

	public ConvertToJavizzProject(final FlexoProject focusedObject, final Vector<FlexoObject> globalSelection, final FlexoEditor editor) {
		super(ACTION_TYPE, focusedObject, globalSelection, editor);
	}

	@Override
	public LocalizedDelegate getLocales() {
		if (getServiceManager() instanceof ApplicationContext) {
			return ((ApplicationContext) getServiceManager()).getModuleLoader().getModule(JavizzEditor.class).getLoadedModuleInstance()
					.getLocales();
		}
		return super.getLocales();
	}

	@Override
	protected void doAction(final Object context) throws FlexoException {
		final JavizzProjectNature nature = getServiceManager().getProjectNatureService().getProjectNature(JavizzProjectNature.class);

		/*	if (!nature.authorizeInit(getFocusedObject())) {
				throw new FlexoException("Javizz_viewpoint_not_found");
			}
		
			getFocusedObject().setDescription(getDescription());
		
			JavizzProject JavizzProject = nature.getJavizzProject(getFocusedObject());
		
			// We have now to notify project of nature modifications
			getFocusedObject().getPropertyChangeSupport().firePropertyChange("asNature(String)", false, true);
			getFocusedObject().getPropertyChangeSupport().firePropertyChange("hasNature(String)", false, true);
		
			JavizzProject.getPropertyChangeSupport().firePropertyChange("JavizzVirtualModelInstance", null,
					JavizzProject.getJavizzVirtualModelInstance());*/

	}

	@Override
	public boolean isValid() {
		if (!super.isValid()) {
			return false;
		}
		return true;
	}

	// ====================
	// GETTERS AND SETTERS
	// ====================

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		if ((projectName == null && this.projectName != null) || (projectName != null && !projectName.equals(this.projectName))) {
			String oldValue = this.projectName;
			this.projectName = projectName;
			getPropertyChangeSupport().firePropertyChange("projectName", oldValue, projectName);
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		if ((description == null && this.description != null) || (description != null && !description.equals(this.description))) {
			String oldValue = this.description;
			this.description = description;
			getPropertyChangeSupport().firePropertyChange("description", oldValue, description);
		}
	}
}
