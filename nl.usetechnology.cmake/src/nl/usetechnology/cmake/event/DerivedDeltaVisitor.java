package nl.usetechnology.cmake.event;

import java.util.LinkedList;
import java.util.List;

import nl.usetechnology.cmake.CMakeNature;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class DerivedDeltaVisitor extends AbstractCMakeResourceDeltaVisitor {
	private List<IResource> listToDerive = new LinkedList<IResource>();

	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource resource = delta.getResource();
		switch(resource.getType()) {
		case IResource.FOLDER:
		case IResource.ROOT:
			return true;
		case IResource.PROJECT:
			return CMakeNature.isCMakeProject((IProject)resource);
		default:
			break;
		}

		if(delta.getKind() != IResourceDelta.ADDED) {
			return false;
		}
		
		IResource parent = resource.getParent();
		while(parent.getType() != IResource.PROJECT) {
			if(parent.isDerived()) {
				listToDerive.add(resource);
				return false;
			}
		}
		return false;
	}

	public void postProcess() {
		WorkspaceJob job = new WorkspaceJob("Update derived sub-files") {
			
			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor)
					throws CoreException {
				
				for(IResource resource : listToDerive) {
					resource.setDerived(true, monitor);
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}
}
