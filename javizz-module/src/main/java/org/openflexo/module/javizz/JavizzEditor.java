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

import org.openflexo.module.NatureSpecificModule;
import org.openflexo.module.javizz.model.JavizzProject;
import org.openflexo.module.javizz.model.JavizzProjectNature;
import org.openflexo.module.javizz.utils.JVPreferences;

/**
 * A module dedicated to {@link JavizzProject} edition
 * 
 * @author fdagnat
 *
 */
public class JavizzEditor extends NatureSpecificModule<JVModule, JavizzProjectNature> {

	public static JavizzEditor INSTANCE;

	public JavizzEditor() {
		super(JVModule.JV_MODULE_NAME, JVModule.JV_MODULE_SHORT_NAME, JVModule.class, JVPreferences.class, "javizz", "", "JV",
				JVIconLibrary.JV_SMALL_ICON, JVIconLibrary.JV_MEDIUM_ICON, JVIconLibrary.JV_MEDIUM_ICON, JVIconLibrary.JV_BIG_ICON,
				JavizzProjectNature.class);
		// use that way because the iterator.next in ModuleLoader (line 124) explicitly call constructor : can't use a private constructor
		// with
		// public static final INSTANCE;
		// For more info see comment in FME Module class
		INSTANCE = this;
	}

}
