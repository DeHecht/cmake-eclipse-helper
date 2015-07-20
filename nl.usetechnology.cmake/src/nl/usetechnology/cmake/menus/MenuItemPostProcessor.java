package nl.usetechnology.cmake.menus;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.MenuItem;

public interface MenuItemPostProcessor {

	void postProcess(MenuItemBuilder builder, MenuItem item, IProject project);

}
