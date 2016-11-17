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

package org.openflexo.module.javizz.model;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.fml.ViewPoint;
import org.openflexo.foundation.fml.rt.View;
import org.openflexo.foundation.fml.rt.rm.ViewResource;
import org.openflexo.foundation.nature.ProjectNature;
import org.openflexo.foundation.nature.ProjectNatureService;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.gina.ApplicationFIBLibrary.ApplicationFIBLibraryImpl;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.swing.utils.JFIBDialog;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.module.javizz.JVConstants;
import org.openflexo.module.javizz.model.action.ConvertToJavizzProject;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.controller.FlexoFIBController;

/**
 * This class is used to interpret a {@link FlexoProject} as a {@link JavizzProject}<br>
 * 
 * A {@link FlexoProject} has the {@link JavizzProject} nature if it contains at least a view conform to Javizz viewpoint<br>
 * The first found view will be considered as the Javizz View
 * 
 * @author fdagnat
 */
public class JavizzProjectNature implements ProjectNature<JavizzProjectNature, JavizzProject> {

	private static final Logger LOGGER = Logger.getLogger(JavizzProjectNature.class.getPackage().getName());

	private static final Resource ERROR_DIALOG = ResourceLocator.locateResource("Fib/Dialog/ErrorProjectNatureDialog.fib");

	private ProjectNatureService projectNatureService;

	private final Map<FlexoProject, JavizzProject> projectsMap;

	public JavizzProjectNature() {
		projectsMap = new HashMap<>();
	}

	/**
	 * Show a popup in case of no correct viewpoint is found
	 * 
	 * @param project
	 *            that tsy to get nature
	 * @return true if a VP and a a DSFolder are found. False other way.
	 */
	public boolean authorizeInit(FlexoProject project) {
		// if no VP in ResourceCenters, add VP resources in project.
		if (getJavizzViewPoint(project.getServiceManager()) == null) {
			// Do dialog
			FIBComponent fibComponent = ApplicationFIBLibraryImpl.instance().retrieveFIBComponent(ERROR_DIALOG);
			JFIBDialog.instanciateAndShowDialog(fibComponent, null, FlexoFrame.getActiveFrame(), true,
					new FlexoFIBController(fibComponent, SwingViewFactory.INSTANCE, FlexoFrame.getActiveFrame().getController()));
			return false;
		}
		return true;
	}

	@Override
	public void givesNature(final FlexoProject project, final FlexoEditor editor) {
		ConvertToJavizzProject.ACTION_TYPE.makeNewAction(project, null, editor).doAction();
	}

	@Override
	public JavizzProject getProjectWrapper(final FlexoProject files) {
		return getJavizzProject(files);
	}

	@Override
	public ProjectNatureService getProjectNatureService() {
		return this.projectNatureService;
	}

	@Override
	public void setProjectNatureService(ProjectNatureService projectNatureService) {
		this.projectNatureService = projectNatureService;
	}

	/**
	 * Return boolean indicating if supplied concept might be interpreted according to this nature A {@link FlexoProject} has the
	 * {@link JavizzProject} nature if it contains at least a view conform to Javizz viewpoint<br>
	 * 
	 * @param project
	 * @return
	 */
	@Override
	public boolean hasNature(final FlexoProject project) {
		if (project == null || project.getViewLibrary().getAllResources().size() == 0) {
			return false;
		}

		ViewPoint JavizzViewPoint = getJavizzViewPoint(project.getServiceManager());
		if (JavizzViewPoint == null) {
			return false;
		}

		for (ViewResource viewResource : project.getViewLibrary().getAllResources()) {
			if (viewResource.getViewPointResource() != null && viewResource.getViewPointResource() == JavizzViewPoint.getResource()) {
				return true;
			}
		}

		return false;
	}

	public ViewPoint getJavizzViewPoint(FlexoServiceManager serviceManager) {
		final List<FlexoResourceCenter<?>> lst = serviceManager.getResourceCenterService().getResourceCenters();
		ViewPoint ceViewPoint = null;
		for (FlexoResourceCenter<?> resourceCenter : lst) {
			ceViewPoint = resourceCenter.getViewPointRepository().getViewPointLibrary().getViewPoint(JVConstants.JAVIZZ_VIEWPOINT_URI);
			if (ceViewPoint != null) {
				break;
			}
		}
		return ceViewPoint;
	}

	/**
	 * If project has nature and is not already referenced, will reference it. Either way return CEContext.
	 * 
	 * @param project
	 *            with or without a set context
	 * @return Context associated to project
	 */
	public JavizzProject getJavizzProject(FlexoProject project) {
		JavizzProject returned = projectsMap.get(project);
		if (returned == null) {
			try {
				ModelFactory factory = new ModelFactory(JavizzProject.class);
				returned = factory.newInstance(JavizzProject.class);
				returned.init(project, this);
				projectsMap.put(project, returned);
			} catch (ModelDefinitionException e) {
				LOGGER.log(Level.SEVERE, "Error while initializing new JavizzProject", e);
			}
		}
		return returned;
	}

	public View getJavizzView(final FlexoProject project) {
		if (project.hasNature(this)) {
			ViewPoint JavizzViewPoint = getJavizzViewPoint(project.getServiceManager());
			if (JavizzViewPoint == null) {
				return null;
			}
			for (ViewResource viewResource : project.getViewLibrary().getAllResources()) {
				if (viewResource.getViewPointResource() != null && viewResource.getViewPointResource() == JavizzViewPoint.getResource()) {
					try {
						return viewResource.getResourceData(null);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ResourceLoadingCancelledException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (FlexoException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}

}
