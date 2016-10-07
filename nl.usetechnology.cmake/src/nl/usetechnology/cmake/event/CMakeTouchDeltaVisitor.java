package nl.usetechnology.cmake.event;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import nl.usetechnology.cmake.Activator;
import nl.usetechnology.cmake.CMakeNature;
import nl.usetechnology.cmake.helper.FileContentIO;

public class CMakeTouchDeltaVisitor extends AbstractCMakeResourceDeltaVisitor {
	
	private static final IPath CMAKELISTS_FILE = new Path("CMakeLists.txt");
	
	private List<IFile> toTouch = new LinkedList<>();
	
	private List<CMakeListsContainer> containers = new ArrayList<>(10);

	class CMakeListsContainer {
		private IPath parentPath;
		private IFile cMakeFile;
		
		public CMakeListsContainer(IFile cMakeFile) {
			this.cMakeFile = cMakeFile;
			this.parentPath = cMakeFile.getParent().getFullPath();
		}
		
		public boolean isTouched() {
			return cMakeFile == null;
		}
		
		public void touch() {
			if(cMakeFile != null) {
				toTouch.add(cMakeFile);
				cMakeFile = null;
			}
		}
		
		public boolean contains(IResource resource){
			return parentPath.isPrefixOf(resource.getFullPath());
		}

		public boolean process(IResource resource, int delta) {
			if (isTouched() || !contains(resource)) {
				return false;
			}

			if (resource.getType() == IResource.FILE) {
				switch (delta) {
				case IResourceDelta.ADDED:
				case IResourceDelta.REMOVED:
					touch();
					return false;
				default:
					break;
				}
			}
			return true;
		}
	}
	
	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource resource = delta.getResource();

		if(isWorkspace(resource)) {
			return true;
		}
		
		if(isProject(resource)) {
			IProject project = (IProject)resource;
			if(!CMakeNature.isCMakeProject(project)) {
				return false;
			}
			CMakeNature.scheduleIntegrityCheck(project);
		}
		
		CMakeListsContainer container = addOrGetContainer(resource);
		if(container == null) {
			return false;
		}
		return container.process(resource, delta.getKind());
	}


	private CMakeListsContainer addOrGetContainer(IResource resource) {
		if (resource.getType() == IResource.FOLDER || resource.getType() == IResource.PROJECT) {
			addCMakeListsIfAvailable((IContainer) resource);
		}

		int index;
		for(index = containers.size() - 1;  index >= 0; index--) {
			CMakeListsContainer container = containers.get(index);
			if(!container.contains(resource)) {
				containers.remove(index);
			} else {
				break;
			}
		}
		if(index >= 0) {
			return containers.get(index);
		}
		return null;
	}

	private void addCMakeListsIfAvailable(IContainer folder) {
		if(!folder.exists())
			return;
		
		IFile file = folder.getFile(CMAKELISTS_FILE);
		if (file.exists()) {
			containers.add(new CMakeListsContainer(file));
		}
	}
	
	public void postProcess() {
		WorkspaceJob job = new WorkspaceJob("Update derived sub-files") {
			
			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor)
					throws CoreException {
				
				for(IFile file : toTouch) {
					// FIXME: Check if there is a more lightweight method to achieve good touching
					Activator.logInfo("touch " + file);
					try {
						CharSequence sequence = FileContentIO.readFileContent(file);
						FileContentIO.writeFileContent(file, sequence, monitor);
					} catch(Exception ce) {
						ce.printStackTrace();
					}
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

}
