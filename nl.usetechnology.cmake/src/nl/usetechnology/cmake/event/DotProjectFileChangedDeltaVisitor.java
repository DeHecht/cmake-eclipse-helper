package nl.usetechnology.cmake.event;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;

import nl.usetechnology.cmake.CMakeLauncher;
import nl.usetechnology.cmake.CMakeNature;

public class DotProjectFileChangedDeltaVisitor extends
		AbstractCMakeResourceDeltaVisitor {

	private CMakeLauncher launcher = CMakeLauncher.instance();
	
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

		IFile file = (IFile)resource;
		if(".project".equals(file.getName())) {
			// we have a project file
			if(file.getParent().getType() != IResource.PROJECT) {
				// it is a project file not positioned at project level
				switch (delta.getFlags()) {
				case IResourceDelta.CONTENT:
				case IResourceDelta.REPLACED:
					launcher.scheduleCopyProjectFiles(file.getProject());
					return false;
				}
			}
		}
		return true;
	}
}
