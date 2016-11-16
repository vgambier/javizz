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

package org.openflexo.module.javizz;

import java.util.logging.Logger;

import org.openflexo.ApplicationContext;
import org.openflexo.foundation.fml.FMLTechnologyAdapter;
import org.openflexo.foundation.fml.rt.FMLRTTechnologyAdapter;
import org.openflexo.foundation.task.Progress;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.Module;
import org.openflexo.module.javizz.controller.JVController;
import org.openflexo.module.javizz.model.JavizzProject;
import org.openflexo.view.controller.FlexoController;

/**
 * A module dedicated to {@link JavizzProject} edition
 * 
 * @author fdagnat
 *
 */
public class JVModule extends FlexoModule<JVModule> {

	public static final String JV_MODULE_SHORT_NAME = "JV";
	public static final String JV_MODULE_NAME = "Javizz";
	private static final Logger logger = Logger.getLogger(JVModule.class.getPackage().getName());

	public JVModule(ApplicationContext applicationContext) {
		super(applicationContext);
		Progress.progress(FlexoLocalization.getMainLocalizer().localizedForKey("build_editor"));
	}

	@Override
	public Module<JVModule> getModule() {
		return JavizzEditor.INSTANCE;
	}

	@Override
	public String getLocalizationDirectory() {
		return "FlexoLocalization/Javizz";
	}

	@Override
	public void initModule() {
		super.initModule();
		TechnologyAdapterService taService = getApplicationContext().getTechnologyAdapterService();
		taService.activateTechnologyAdapter(taService.getTechnologyAdapter(FMLTechnologyAdapter.class));
		taService.activateTechnologyAdapter(taService.getTechnologyAdapter(FMLRTTechnologyAdapter.class));
		Progress.progress(getLocales().localizedForKey("load_javizz_viewpoint"));
		initJavizzViewpoint();
	}

	/**
	 * Create a binded editor controller.
	 *
	 * @return a freshly created CEController.
	 */
	@Override
	protected FlexoController createControllerForModule() {
		return new JVController(this);
	}

	@Override
	public boolean close() {
		if (getApplicationContext().getResourceManager().getUnsavedResources().size() == 0) {
			return super.close();
		}
		else {
			return getFlexoController().reviewModifiedResources() && super.close();
		}
	}

	private void initJavizzViewpoint() {
		System.out.println("Ok, loaded javizz module");
	}
}
