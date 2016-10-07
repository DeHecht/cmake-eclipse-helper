package nl.usetechnology.cmake.menus;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.ExtensionContributionFactory;
import org.eclipse.ui.menus.IContributionRoot;
import org.eclipse.ui.services.IServiceLocator;

import nl.usetechnology.cmake.CMakeNature;


public class ProjectExplorerExtensionContributionFactory extends
		ExtensionContributionFactory {

	public ProjectExplorerExtensionContributionFactory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createContributionItems(IServiceLocator serviceLocator,
			IContributionRoot additions) {
		
		IProject project = retrieveSelectedProject();
		// Now check if project is a CMakeProject
		if(CMakeNature.isCMakeProject(project)) {
			additions.addContributionItem(new CMakeContributionMenu(), null);
		} else if(CMakeNature.canConvertProject(project)) 
			additions.addContributionItem(new CMakeSetupContributionMenu(), null);
	}

	public static IProject retrieveSelectedProject() {
		IProject project = null;
		ISelectionService selectionService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
		ISelection selection = selectionService.getSelection();
		if(selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection)selection;
			for(Object obj : structuredSelection.toList()) {
				// transform cdt elements
				if(obj instanceof ICElement) {
					ICElement element = (ICElement)obj;
					obj = element.getResource();
				}
				
				if(obj instanceof IResource) {
					IResource resource = (IResource) obj;
					if(project == null) {
						project = resource.getProject();
					} else if(!project.equals(resource.getProject())) {
						System.err.println("Multiple Projects");
						return null;
					}
				}
			}
		}
		return project;
	}

}
