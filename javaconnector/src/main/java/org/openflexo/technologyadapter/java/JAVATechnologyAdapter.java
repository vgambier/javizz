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

package org.openflexo.technologyadapter.java;

import java.util.logging.Logger;

import org.openflexo.foundation.fml.annotations.DeclareModelSlots;
import org.openflexo.foundation.fml.annotations.DeclareRepositoryType;
import org.openflexo.foundation.fml.annotations.DeclareResourceTypes;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterBindingFactory;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterInitializationException;
import org.openflexo.technologyadapter.java.rm.JAVAResourceRepository;
import org.openflexo.technologyadapter.java.rm.JAVAResourceFactory;

/**
 * This class defines and implements the JAVA technology adapter
 * 
 * @author wei
 * 
 */

@DeclareModelSlots({ JAVAModelSlot.class })
@DeclareRepositoryType({ JAVAResourceRepository.class })
@DeclareResourceTypes({ JAVAResourceFactory.class })
public class JAVATechnologyAdapter extends TechnologyAdapter {

	private static final Logger LOGGER = Logger.getLogger(JAVATechnologyAdapter.class.getPackage().getName());

	public JAVATechnologyAdapter() throws TechnologyAdapterInitializationException {
	}

	@Override
	public String getName() {
		return new String("JAVA Technology Adapter");
	}

	@Override
	public String getLocalizationDirectory() {
		return "FlexoLocalization/JAVATechnologyAdapter";
	}

	@Override
	public JAVATechnologyContextManager createTechnologyContextManager(FlexoResourceCenterService service) {
		return new JAVATechnologyContextManager(this, service);
	}

	@Override
	public JAVATechnologyContextManager getTechnologyContextManager() {
		return (JAVATechnologyContextManager) super.getTechnologyContextManager();
	}

	@Override
	public TechnologyAdapterBindingFactory getTechnologyAdapterBindingFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <I> boolean isIgnorable(FlexoResourceCenter<I> resourceCenter, I contents) {
		// TODO Auto-generated method stub
		return false;
	}

	public <I> JAVAResourceRepository<I> getJAVAResourceRepository(FlexoResourceCenter<I> resourceCenter) {
		JAVAResourceRepository<I> returned = resourceCenter.retrieveRepository(JAVAResourceRepository.class, this);
		if (returned == null) {
			returned = new JAVAResourceRepository<I>(this, resourceCenter);
			resourceCenter.registerRepository(returned, JAVAResourceRepository.class, this);
		}
		return returned;
	}

	/**
	 * Create a new {@link DocXDocumentResource} using supplied configuration options<br>
	 * 
	 * @param project
	 * @param filename
	 * @param modelUri
	 * @param createEmptyDocument
	 *            a flag indicating if created resource should encodes an empty (but existing) document or if resource data should remain
	 *            empty
	 * @return
	 */
	/*@Deprecated
	public DocXDocumentResource createNewDocXDocumentResource(FlexoResourceCenter<?> rc, String filename, boolean createEmptyDocument,
			IdentifierManagementStrategy idStrategy) {
	
		if (rc instanceof FileSystemBasedResourceCenter) {
			return createNewDocXDocumentResource((FileSystemBasedResourceCenter) rc, File.separator + "DocX", filename, createEmptyDocument,
					idStrategy);
		}
		else {
			logger.warning(
					"INVESTIGATE: not implemented yet, not able to create a DocX file in a Rc that is not fileBased: " + rc.toString());
			return null;
		}
	
	}*/

	/**
	 * Create a new {@link DocXDocumentResource} using supplied configuration options<br>
	 * 
	 * @param resourceCenter
	 * @param relativePath
	 * @param filename
	 * @param createEmptyDocument
	 *            a flag indicating if created resource should encodes an empty (but existing) document or if resource data should remain
	 *            empty
	 * @return
	 */
	/*@Deprecated
	public DocXDocumentResource createNewDocXDocumentResource(FileSystemBasedResourceCenter resourceCenter, String relativePath,
			String filename, boolean createEmptyDocument, IdentifierManagementStrategy idStrategy) {
	
		if (!relativePath.startsWith(File.separator)) {
			relativePath = File.separator + relativePath;
		}
	
		File docXFile = new File(resourceCenter.getDirectory() + relativePath, filename);
	
		DocXDocumentResource docXDocumentResource = null;
		try {
			docXDocumentResource = getDocXDocumentResourceFactory().makeResource(docXFile, resourceCenter, getTechnologyContextManager(),
					true);
		} catch (SaveResourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ModelDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		// DocXDocumentResource docXDocumentResource = DocXDocumentResourceImpl.makeDocXDocumentResource(docXFile,
		// getTechnologyContextManager(), resourceCenter, idStrategy);
	
		// referenceResource(docXDocumentResource, resourceCenter);
	
		return docXDocumentResource;
	}*/

	/*@Override
	public void initFMLModelFactory(FMLModelFactory fMLModelFactory) {
		super.initFMLModelFactory(fMLModelFactory);
	
		fMLModelFactory.addConverter(new DocXFragmentConverter());
		fMLModelFactory.addConverter(new DocXElementConverter());
	}*/

	@Override
	public String getIdentifier() {
		return "JAVA";
	}

	public JAVAResourceFactory getJavaResourceFactory() {
		return getResourceFactory(JAVAResourceFactory.class);
	}

	//

	/*@Override
	public <I> void initializeResourceCenter(FlexoResourceCenter<I> resourceCenter) {
		JAVAResourceRepository currentRepository = resourceCenter.getRepository(JAVAResourceRepository.class, this);
		if (currentRepository == null) {
			currentRepository = this.createNewJAVARepository(resourceCenter);
		}
	
		Iterator<I> it = resourceCenter.iterator();
	
		while (it.hasNext()) {
			I item = it.next();
			JAVAResource javaRes = tryToLookupJAVAFile(resourceCenter, item);
	
		}
	
		// Call it to update the current repositories
		getPropertyChangeSupport().firePropertyChange("getAllRepositories()", null, resourceCenter);
	
	}
	
	protected JAVAResource tryToLookupJAVAFile(FlexoResourceCenter<?> resourceCenter, Object candidateElement) {
		if (isValidateJAVAFile(candidateElement, resourceCenter.getName())) {
			JAVAResource javaRes = retrieveJAVAResource(candidateElement);
			JAVAResourceRepository javaRepository = resourceCenter.getRepository(JAVAResourceRepository.class, this);
			if (javaRes != null) {
				try {
					final RepositoryFolder<JAVAResource> folder = javaRepository.getRepositoryFolder(candidateElement, true);
					javaRepository.registerResource(javaRes, folder);
				} catch (IOException e) {
					final String msg = "Error during get JAVA resource RepositoryFolder";
					LOGGER.log(Level.SEVERE, msg, e);
				}
				referenceResource(javaRes, resourceCenter);
				return javaRes;
			}
		}
		return null;
	}
	
	public JAVAResource retrieveJAVAResource(Object javaFile) {
	
		JAVAResource returned = getTechnologyContextManager().getJAVAResource(javaFile);
		if (returned == null) {
			if (javaFile instanceof File) {
				returned = JAVAResourceImpl.retrieveJAVAResource((File) javaFile, getTechnologyContextManager());
			}
			if (returned != null) {
				getTechnologyContextManager().registerJAVAFile(returned);
			}
			else {
				LOGGER.warning("Cannot retrieve JAVAFile resource for " + javaFile);
			}
		}
	
		return returned;
	}
	
	private <I> void initializeJAVAFile(final FlexoResourceCenter<I> resourceCenter, final File candidateFile) {
		final JAVAResourceImpl javaResourceFile = (JAVAResourceImpl) JAVAResourceImpl.retrieveJAVAResource(candidateFile,
				this.getTechnologyContextManager());
		final JAVAResourceRepository resourceRepository = resourceCenter.getRepository(JAVAResourceRepository.class, this);
		if (javaResourceFile != null) {
			try {
				final RepositoryFolder<JAVAResource> folder = resourceRepository.getRepositoryFolder(candidateFile, true);
				resourceRepository.registerResource(javaResourceFile, folder);
				this.referenceResource(javaResourceFile, resourceCenter);
			} catch (final IOException e) {
				final String msg = "Error during getting JAVA resource folder";
				LOGGER.log(Level.SEVERE, msg, e);
			}
		}
	}
	
	@Override
	public <I> void contentsAdded(FlexoResourceCenter<I> resourceCenter, I contents) {
		if (contents instanceof File) {
			this.initializeJAVAFile(resourceCenter, (File) contents);
		}
	
	}
	
	@Override
	public <I> void contentsDeleted(FlexoResourceCenter<I> resourceCenter, I contents) {
		if (contents instanceof File) {
			File candidateFile = (File) contents;
			this.deleteJAVAFile(resourceCenter, candidateFile);
		}
	}
	
	private <I> void deleteJAVAFile(final FlexoResourceCenter<I> resourceCenter, final File candidateFile) {
		final JAVAResourceRepository resourceRepository = resourceCenter.getRepository(JAVAResourceRepository.class, this);
		for (JAVAResource javaResource : resourceRepository.getAllResources()) {
			if (javaResource.getResourceFile() != null) {
				if (candidateFile.getAbsolutePath().equals(javaResource.getResourceFile().getAbsolutePath())) {
					resourceRepository.unregisterResource(javaResource);
				}
			}
		}
	
	}
	
	public JAVAResource createNewJAVAModel(FlexoProject project, String filename, String modelUri) {
		final File file = new File(FlexoProject.getProjectSpecificModelsDirectory(project), filename);
		final JAVAResourceImpl javaResourceFile = (JAVAResourceImpl) JAVAResourceImpl.makeJAVAResource(modelUri, file,
				this.getTechnologyContextManager());
		this.getTechnologyContextManager().registerResource(javaResourceFile);
		return javaResourceFile;
	}
	
	private JAVAResourceRepository createNewJAVARepository(final FlexoResourceCenter<?> resourceCenter) {
		final JAVAResourceRepository repo = new JAVAResourceRepository(this, resourceCenter);
		resourceCenter.registerRepository(repo, JAVAResourceRepository.class, this);
		return repo;
	}
	
	private boolean isValidateJAVAFile(Object candidateElement, String resourceCenter) {
		if (candidateElement instanceof File && isValidateJAVAFileName(((File) candidateElement).getName())
				&& isValidateJAVAFile(((File) candidateElement), resourceCenter)) {
			return true;
		}
		return false;
	}
	
	private boolean isValidateJAVAFile(File file, String resourceCenter) {
		// vérifier que les fichiers ne sont pas des fichiers binaire
		if (file.isHidden() || "target".equals(file.getName()) || "build".equals(file.getName())) {
			return false;
		}
		else if (resourceCenter.equals(file.getParent())) {
			return true;
		}
		else {
			return isValidateJAVAFile(file.getParentFile(), resourceCenter);
		}
	}
	
	private boolean isValidateJAVAFileName(String fileName) {
		// pour l'instant, les fichers .java sont considérés comme JAVAResource
		// si ajouter d'autres fichiers comme JAVAResource, ajouter ||fileName.endsWith("")
		return fileName.endsWith(".java");
	}*/

}
