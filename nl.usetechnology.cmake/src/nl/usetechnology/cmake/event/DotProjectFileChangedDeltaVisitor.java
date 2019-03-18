package nl.usetechnology.cmake.event;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;

import nl.usetechnology.cmake.helper.PluginDataIO;

public class DotProjectFileChangedDeltaVisitor extends
		AbstractCMakeResourceDeltaVisitor {

	private Set<IProject> projectsToCopy = new HashSet<>();
	
	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource resource = delta.getResource();
		
		if (resource.getType() == IResource.FOLDER) {
			// only visit the binary directory and its sub directories
			return !isProject(resource.getParent()) || resource.getName().equals(PluginDataIO.getBinDirectory());
		}
		if (resource.getType() == IResource.FILE) {
			IFile file = (IFile)resource;
			if(".project".equals(file.getName()) || ".cproject".equals(file.getName())) {
				// we have a project file
				if(file.getParent().getType() != IResource.PROJECT) {
					// it is a project file not positioned at project level
					switch (delta.getFlags()) {
					case IResourceDelta.CONTENT:
					case IResourceDelta.REPLACED:
						projectsToCopy.add(file.getProject());
						return false;
					}
				}
			}
		}
		return isProject(resource);
	}
	
	public Set<IProject> getProjectsToCopy() {
		return projectsToCopy;
	}
}
