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

			// Code here will triggers whenever one of the models is changed
			if (getProject() != null && getProject().getIsWatching()) // pour faire la différence entre initialisation et changements
				System.out.println("@@@@@@@@@" + modified);
			if (getProject() != null && !getProject().getIsWatching())
				System.out.println("la classe " + getClass() + " dit que : project = " + getProject());

		}
	}
}
