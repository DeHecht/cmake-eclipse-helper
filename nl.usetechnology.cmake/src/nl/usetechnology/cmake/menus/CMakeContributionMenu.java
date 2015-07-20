package nl.usetechnology.cmake.menus;

import nl.usetechnology.cmake.helper.PluginDataIO;
import nl.usetechnology.cmake.menus.listeners.ArchSelectionListener;
import nl.usetechnology.cmake.menus.listeners.BuildTypeSelectionListener;
import nl.usetechnology.cmake.menus.listeners.CleanupCMakeProjectSelectionListener;
import nl.usetechnology.cmake.menus.selectors.ArchConfigurationSelector;
import nl.usetechnology.cmake.menus.selectors.BuildTypeSelector;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;

public class CMakeContributionMenu extends ContributionItem {

	private static MenuItemBuilder top;

	public CMakeContributionMenu() {
		this(null);
	}

	public CMakeContributionMenu(String id) {
		super(id);
	}

	static {
		top = new MenuItemBuilder("CMake");
		top.add(new MenuItemBuilder("Cleanup").setSelectionListener(new CleanupCMakeProjectSelectionListener()));

		MenuItemBuilder buildConfigurations = new MenuItemBuilder("Build Configurations");
		top.add(buildConfigurations);

		BuildTypeSelector buildSelector = new BuildTypeSelector();
		BuildTypeSelectionListener buildListener = new BuildTypeSelectionListener();
		
		for(String buildType : PluginDataIO.getBuildTypes()) {
			buildConfigurations.add(new MenuItemBuilder(" " + buildType).setType(SWT.CHECK).setSelector(buildSelector).setSelectionListener(buildListener));
		}

		MenuItemBuilder architectures = new MenuItemBuilder("Architectures");
		top.add(architectures);

		ArchConfigurationSelector archSelector = new ArchConfigurationSelector();
		ArchSelectionListener archListener = new ArchSelectionListener();
		
		for(String arch : PluginDataIO.getToolchainArchitectures()) {
			architectures.add(new MenuItemBuilder(" " + arch).setType(SWT.CHECK).setSelector(archSelector).setSelectionListener(archListener));
		}
	}

	@Override
	public void fill(Menu menu, int index) {
		super.fill(menu, index);
		top.fill(menu, index);

	}

}
