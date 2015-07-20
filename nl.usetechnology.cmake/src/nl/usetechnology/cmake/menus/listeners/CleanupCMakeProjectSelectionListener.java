package nl.usetechnology.cmake.menus.listeners;

import nl.usetechnology.cmake.CMakeNature;
import nl.usetechnology.cmake.helper.PluginDataIO;
import nl.usetechnology.cmake.menus.ProjectExplorerExtensionContributionFactory;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class CleanupCMakeProjectSelectionListener extends SelectionAdapter {

	private final static String[] EMPTY_STRING_ARRAY = new String[0];
	
	@Override
	public void widgetSelected(SelectionEvent e) {
		IProject project = ProjectExplorerExtensionContributionFactory.retrieveSelectedProject();
		IFolder binDir = project.getFolder(PluginDataIO.BIN_DIR);
		try {
			binDir.delete(true, null);
			// delete all natures
			IProjectDescription description = project.getDescription();
			description.setNatureIds(EMPTY_STRING_ARRAY);

			project.setDescription(description, null);
			
			
			
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
			
			// Now add the cmake nature again!
			CMakeNature.scheduleIntegrityCheck(project);
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
	}

}
