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

package org.openflexo.module.javizz.controller;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.selection.ContextualMenuManager;
import org.openflexo.selection.MouseSelectionManager;

/**
 * Javizz prototype specific MouseSelectionManager Implementation.
 */
public class JVSelectionManager extends MouseSelectionManager {

	public JVSelectionManager(JVController controller) {
		super(controller);
		_contextualMenuManager = new ContextualMenuManager(this, controller);
	}

	public JVController getJVController() {
		return (JVController) getController();
	}

	/**
	 * Returns the root object that can be currently edited
	 *
	 * @return FlexoObject
	 */
	@Override
	public FlexoObject getRootFocusedObject() {
		return getJVController().getCurrentDisplayedObjectAsModuleView();
	}

}
