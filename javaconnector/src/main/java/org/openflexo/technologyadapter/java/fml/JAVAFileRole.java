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

package org.openflexo.technologyadapter.java.fml;

import java.lang.reflect.Type;

import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.rt.AbstractVirtualModelInstanceModelFactory;
import org.openflexo.foundation.fml.rt.ActorReference;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.ModelObjectActorReference;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.java.JAVATechnologyAdapter;
import org.openflexo.technologyadapter.java.fml.JAVAFileRole.JAVAFileRoleImpl;
import org.openflexo.technologyadapter.java.model.JAVAFileModel;

@ModelEntity
@ImplementationClass(value = JAVAFileRoleImpl.class)
@XMLElement
public interface JAVAFileRole extends FlexoRole<JAVAFileModel> {

	public JAVATechnologyAdapter getModelSlotTechnologyAdapter();

	public abstract static class JAVAFileRoleImpl extends FlexoRoleImpl<JAVAFileModel> implements JAVAFileRole {

		public JAVAFileRoleImpl() {
			super();
		}

		@Override
		public Type getType() {
			return JAVAFileModel.class;
		}

		@Override
		public RoleCloningStrategy defaultCloningStrategy() {
			return RoleCloningStrategy.Reference;
		}

		@Override
		public boolean defaultBehaviourIsToBeDeleted() {
			return false;
		}

		@Override
		public ActorReference<JAVAFileModel> makeActorReference(final JAVAFileModel object, final FlexoConceptInstance fci) {
			AbstractVirtualModelInstanceModelFactory<?> factory = fci.getFactory();
			final ModelObjectActorReference<JAVAFileModel> returned = factory.newInstance(ModelObjectActorReference.class);
			returned.setFlexoRole(this);
			returned.setFlexoConceptInstance(fci);
			returned.setModellingElement(object);
			return returned;
		}

		/**
		 * 
		 * @return JAVA technology adapter in service manager.
		 */
		@Override
		public JAVATechnologyAdapter getModelSlotTechnologyAdapter() {
			return getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(JAVATechnologyAdapter.class);
		}
	}
}
