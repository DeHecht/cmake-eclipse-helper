package nl.usetechnology.cmake.menus.listeners;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MenuItem;

import nl.usetechnology.cmake.CMakeLauncher;
import nl.usetechnology.cmake.menus.ProjectExplorerExtensionContributionFactory;

public class BuildTypeSelectionListener extends SelectionAdapter {

	CMakeLauncher launcher = new CMakeLauncher();

	@Override
	public void widgetSelected(SelectionEvent e) {
		try {
			MenuItem item = (MenuItem) e.widget;
			String itemName = item.getText().trim();
			launcher.changeBuildType(ProjectExplorerExtensionContributionFactory.retrieveSelectedProject(), itemName);
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
	}
}
