package nl.usetechnology.cmake.menus.listeners;

import nl.usetechnology.cmake.CMakeLauncher;
import nl.usetechnology.cmake.menus.ProjectExplorerExtensionContributionFactory;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MenuItem;

public class ArchSelectionListener extends SelectionAdapter {
	
	CMakeLauncher launcher = new CMakeLauncher();

	@Override
	public void widgetSelected(SelectionEvent e) {
		try {
			MenuItem item = (MenuItem) e.widget;
			String itemName = item.getText().trim();
			launcher.changeArchitecture(ProjectExplorerExtensionContributionFactory.retrieveSelectedProject(), itemName);
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
	}
}
