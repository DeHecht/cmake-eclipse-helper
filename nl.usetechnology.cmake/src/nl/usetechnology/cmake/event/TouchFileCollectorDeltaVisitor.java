package nl.usetechnology.cmake.event;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class TouchFileCollectorDeltaVisitor extends AbstractCMakeResourceDeltaVisitor {

	private static final IPath CMAKELISTS_FILE = new Path("CMakeLists.txt");

	private Set<IProject> projectsToTouch = new HashSet<>();
	
	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource resource = delta.getResource();
		if (isProject(resource)) {
			return true;
		}	
		if (resource.isDerived()) {
			return false;
		}
		
		if (resource.getType() == IResource.FILE && (delta.getKind() & (IResourceDelta.ADDED | IResourceDelta.REMOVED)) != 0) {
			projectsToTouch.add(resource.getProject());
		}
		return true;
	}
	
	public Set<IFile> getFiles() {
		HashSet<IFile> files = new HashSet<>(projectsToTouch.size());
		for( IProject project : projectsToTouch)
		{
			IFile file = project.getFile(CMAKELISTS_FILE);
			if (file.exists()) {
				files.add(file);
			}
		}
		return files;
	}

}
