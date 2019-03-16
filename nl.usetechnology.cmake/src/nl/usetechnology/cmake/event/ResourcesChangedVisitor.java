package nl.usetechnology.cmake.event;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import nl.usetechnology.cmake.Activator;
import nl.usetechnology.cmake.CMakeLauncher;
import nl.usetechnology.cmake.CMakeNature;
import nl.usetechnology.cmake.helper.FileContentIO;

public class ResourcesChangedVisitor extends AbstractCMakeResourceDeltaVisitor {

	private CMakeLauncher launcher = CMakeLauncher.instance();
	private TouchFileCollectorDeltaVisitor touchVisitor = new TouchFileCollectorDeltaVisitor();
	private DotProjectFileChangedDeltaVisitor projectVisitor = new DotProjectFileChangedDeltaVisitor();
	
	public ResourcesChangedVisitor() {
	}
	
	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource resource = delta.getResource();

		if(isProject(resource) && CMakeNature.isCMakeProject((IProject)resource)) {
			delta.accept(projectVisitor);
			delta.accept(touchVisitor);
		}
		
		return isWorkspace(resource);
	}

	public void postProcess() {

		WorkspaceJob job = new WorkspaceJob("Process changed resources") {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {

				for (IFile file : touchVisitor.getFiles()) {
					Activator.logInfo("touch " + file);
					touchFile(file, monitor);
				}

				for (IProject project : projectVisitor.getProjectsToCopy()) {
					Activator.logInfo("copy .project and .cproject in project " + project);
					launcher.copyProjectFiles(project, monitor);
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}
	
	private void touchFile(IFile file, IProgressMonitor monitor) {
		// FIXME: Check if there is a more lightweight method to achieve good touching
		// touch on IFile did not work as desired
		try {
			CharSequence sequence = FileContentIO.readFileContent(file);
			FileContentIO.writeFileContent(file, sequence, monitor);
		} catch (Exception ce) {
			ce.printStackTrace();
		}
	}

}
