package javizz;

import org.openflexo.pamela.annotations.Implementation;
import org.openflexo.pamela.factory.AccessibleProxyObject;

public interface AbstractModelObject extends AccessibleProxyObject {

	ProjectModel getProject();

	@Implementation
	abstract class AbstractModelObjectImpl implements AbstractModelObject {

		@Override
		public void setModified(boolean modified) {
			performSuperSetModified(modified);

			// Code here will triggers whenever one of the models is changed (if the flag isWatching is set to true)
			// isWatching is useful to differentiate between initializations and actual changes
			if (getProject() != null && getProject().getIsWatching()) {
				System.out.println("A change in the model has been detected!");
			}
		}
	}
}
