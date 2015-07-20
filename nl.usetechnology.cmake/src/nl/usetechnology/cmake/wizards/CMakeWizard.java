package nl.usetechnology.cmake.wizards;

import nl.usetechnology.cmake.CMakeLauncher;
import nl.usetechnology.cmake.CMakeNature;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

public class CMakeWizard extends BasicNewProjectResourceWizard {

	public CMakeWizard() {
		
	}

	@Override
	public boolean performFinish() {
		boolean finished = super.performFinish();

		IProject project = getNewProject();
		try {
			if (!project.hasNature(CMakeNature.ID)) {
				new CMakeLauncher().setupProject(project);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return finished;
	}


}
