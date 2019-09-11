package models;

import org.openflexo.pamela.annotations.Implementation;
import org.openflexo.pamela.factory.AccessibleProxyObject;

/**
 * @author Victor Gambier
 *
 */

public interface AbstractModelObject extends AccessibleProxyObject {

	ProjectModel getProject();

	String getName();

	@Implementation
	abstract class AbstractModelObjectImpl implements AbstractModelObject {

		@Override
		public void setModified(boolean modified) {
			performSuperSetModified(modified);

			// Code here will triggers whenever one of the models is changed (if the flag isWatching is set to true)
			// isWatching is useful to differentiate between initializations and actual changes
			if (getProject() != null && getProject().getIsWatching()) {
				String simpleName = getClass().getSimpleName();
				String shortName = simpleName.substring(0, simpleName.indexOf("$"));
				System.out.println("A change in the " + getName() + " " + shortName + " has been detected!");
			}
		}
	}
}
