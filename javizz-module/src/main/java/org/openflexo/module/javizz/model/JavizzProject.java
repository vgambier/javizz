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

package org.openflexo.module.javizz.model;

import java.util.List;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.fml.ViewPoint;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.rt.View;
import org.openflexo.foundation.fml.rt.VirtualModelInstance;
import org.openflexo.foundation.nature.ProjectWrapper;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.module.javizz.JVConstants;
import org.openflexo.module.javizz.model.JavizzProject.JavizzProjectImpl;

/**
 * A "Javizz" project<br>
 * Note that a "Javizz" project is based on a classical {@link FlexoProject} which has the {@link JavizzProjectNature}
 *
 * @author fdagnat
 */
@ModelEntity
@ImplementationClass(value = JavizzProjectImpl.class)
public interface JavizzProject extends FlexoObject, ProjectWrapper<JavizzProjectNature>, ResourceData<JavizzProject> {

	public static final String PROJECT_KEY = "project";
	public static final String PROJECT_NATURE_KEY = "projectNature";

	@Override
	@Getter(value = PROJECT_KEY, ignoreType = true)
	public FlexoProject getProject();

	@Setter(PROJECT_KEY)
	public void setProject(FlexoProject project);

	@Override
	@Getter(value = PROJECT_NATURE_KEY, ignoreType = true)
	public JavizzProjectNature getProjectNature();

	@Setter(PROJECT_NATURE_KEY)
	public void setProjectNature(JavizzProjectNature projectNature);

	public View getJavizzView();

	public void init(FlexoProject project, JavizzProjectNature nature);

	public String getName();

	public VirtualModel getJavizzVirtualModel();

	/**
	 * @return the Javizz Viewpoint
	 */
	public ViewPoint getJavizzViewPoint();

	public VirtualModelInstance getRequirementDocumentVirtualModelInstance();

	public VirtualModelInstance getJavizzVirtualModelInstance();

	/**
	 * Default base implementation for {@link JavizzProject}
	 * 
	 * @author sylvain
	 *
	 */
	public static abstract class JavizzProjectImpl extends FlexoObjectImpl implements JavizzProject {

		/**
		 * Initialize project with all setter and resource loader with some protection. No exception are thrown.
		 * 
		 * @param project
		 *            FlexoProject
		 * @param nature
		 *            Nature of Javizz project project.hasNature(nature) has to be true
		 */
		@Override
		public void init(FlexoProject project, JavizzProjectNature nature) {
			this.setProject(project);
			this.setProjectNature(nature);
		}

		@Override
		public String getName() {
			return getProject().getName();
		}

		@Override
		public View getJavizzView() {
			return getProjectNature().getJavizzView(getProject());
		}

		@Override
		public ViewPoint getJavizzViewPoint() {
			final List<FlexoResourceCenter<?>> lst = getProject().getServiceManager().getResourceCenterService().getResourceCenters();
			ViewPoint ceViewPoint = null;
			for (FlexoResourceCenter<?> resourceCenter : lst) {
				ceViewPoint = resourceCenter.getViewPointRepository().getViewPointLibrary().getViewPoint(JVConstants.JAVIZZ_VIEWPOINT_URI);
				if (ceViewPoint != null) {
					break;
				}
			}
			return ceViewPoint;
		}

		@Override
		public VirtualModel getJavizzVirtualModel() {
			return getJavizzViewPoint().getVirtualModelNamed(JVConstants.JAVIZZ_VM_NAME);
		}

		@Override
		public VirtualModelInstance getJavizzVirtualModelInstance() {
			if (getJavizzView() != null) {
				if (getJavizzView().getVirtualModelInstancesForVirtualModel(getJavizzVirtualModel()).size() > 0) {
					return (VirtualModelInstance) getJavizzView().getVirtualModelInstancesForVirtualModel(getJavizzVirtualModel()).get(0);
				}
			}
			return null;
		}

	}
}
