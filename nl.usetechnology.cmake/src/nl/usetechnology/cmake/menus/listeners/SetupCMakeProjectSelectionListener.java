package nl.usetechnology.cmake.menus.listeners;

import nl.usetechnology.cmake.CMakeLauncher;
import nl.usetechnology.cmake.CMakeNature;
import nl.usetechnology.cmake.menus.ProjectExplorerExtensionContributionFactory;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class SetupCMakeProjectSelectionListener extends SelectionAdapter {

	CMakeLauncher launcher = new CMakeLauncher();
	
	@Override
	public void widgetSelected(SelectionEvent e) {
		IProject project = ProjectExplorerExtensionContributionFactory.retrieveSelectedProject();
		CMakeNature.scheduleIntegrityCheck(project);
		launcher.setupProject(project);
	}
	
}
