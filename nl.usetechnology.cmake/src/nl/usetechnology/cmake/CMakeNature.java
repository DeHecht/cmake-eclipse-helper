package nl.usetechnology.cmake;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

public class CMakeNature implements IProjectNature {

	public static final String ID = "nl.usetechnology.cmake.nature";
	
	private static final IPath CMAKELISTS_FILE = new Path("CMakeLists.txt");

	private IProject project;

	@Override
	public void configure() throws CoreException {

	}

	@Override
	public void deconfigure() throws CoreException {

	}

	@Override
	public IProject getProject() {
		return project;
	}

	@Override
	public void setProject(IProject project) {
		this.project = project;
	}
	
	private static void register(IProject project) throws CoreException {
		// Nature already present ... no action required
		if(project.hasNature(ID))
			return;
		
		Activator.logInfo("register P/libuse as CMake Project");
		IProjectDescription description = project.getDescription();
		String[] natures = description.getNatureIds();
		String[] newNatures = new String[natures.length + 1];
		if(natures.length > 0) {
			System.arraycopy(natures, 0, newNatures, 1, natures.length);
		}
		newNatures[0] = CMakeNature.ID;
		description.setNatureIds(newNatures);
		project.setDescription(description, null);
		
		scheduleIntegrityCheck(project);
	}

	private static void scheduleRegister(final IProject project) {
		WorkspaceJob job = new WorkspaceJob("Set CMake nature for project " + project) {
			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				register(project);
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	public static boolean isCMakeProject(final IProject project) {
		if(project == null || !project.exists())
			return false;
		
		try {
			// project contains nature?
			if(project.hasNature(ID)) {
				return true;
			}
			return false;
		} catch (CoreException e) {
			return false;
		}
	}
	
	public static boolean canConvertProject(final IProject project) {
		if(project == null || !project.exists())
			return false;
		try {
			// project already tagged?
			if(project.hasNature(ID)) {
				return false;
			}
			IFile file = project.getFile(CMAKELISTS_FILE);
			if(!file.exists())
				return false;
			return true;
		} catch (CoreException e) {
			return false;
		}
	}
	
	private static List<String> derivedDirectories = Collections
			.unmodifiableList(Arrays.asList("bin", "build", "[Targets]",
					"[Subprojects]"));
	
	private static List<String> derivedFiles = Collections.unmodifiableList(Arrays.asList(".cproject"));
	
	public static void scheduleIntegrityCheck(final IProject project) {
		try {
			project.refreshLocal(IResource.DEPTH_ZERO, null);
			if(!project.hasNature(ID)) {
				scheduleRegister(project);
				return;
			}
		} catch(CoreException e) {
			Activator.logError("Unable to readout nature of project!", e);
		}
		
		List<IResource> toBeDerived = new ArrayList<IResource>(derivedDirectories.size() + derivedFiles.size());
		// Check the integrity of the project
		for (String folder : derivedDirectories) {
			IResource resource = project.getFolder(folder);
			if(resource.exists() && !resource.isDerived()) {
				toBeDerived.add(resource);
			}
		}
		for (String file : derivedFiles) {
			IResource resource = project.getFile(file);
			if(resource.exists() && !resource.isDerived()) {
				toBeDerived.add(resource);
			}
		}
		if(!toBeDerived.isEmpty()) {
			scheduleSetDerivedToResources(toBeDerived);
		}
	}

	private static void scheduleSetDerivedToResources(final List<IResource> toBeDerived) {
		WorkspaceJob job = new WorkspaceJob("Mark resources as derived " + toBeDerived) {
			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor)
					throws CoreException {
				for (IResource resource : toBeDerived) {
					resource.setDerived(true, monitor);
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

}
