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

			/*
			
			// code qui se déclenche quand un changement est apporté au modèle
			if (getProject() != null && getProject().isWatching()) // pour faire la différence entre initialisation et changements
				System.out.println("@@@@@@@@@" + modified);
			if (getProject() != null && !getProject().isWatching())
				System.out.println("la classe" + getClass() + "dit que : project" + getProject());
				
			*/
		}
	}
}
