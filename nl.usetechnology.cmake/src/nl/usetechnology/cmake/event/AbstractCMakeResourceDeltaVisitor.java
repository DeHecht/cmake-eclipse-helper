package nl.usetechnology.cmake.event;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDeltaVisitor;

public abstract class AbstractCMakeResourceDeltaVisitor implements IResourceDeltaVisitor {

	

	protected boolean isWorkspace(IResource resource) {
		return resource.getType() == IResource.ROOT;
	}
	
	
	protected boolean isProject(IResource resource) {
		return resource.getType() == IResource.PROJECT;
	}
	
}
