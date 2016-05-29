package nl.usetechnology.cmake.menus;

import java.util.List;

import nl.usetechnology.cmake.helper.PluginDataIO;
import nl.usetechnology.cmake.menus.listeners.ToolchainSelectionListener;
import nl.usetechnology.cmake.menus.listeners.BuildTypeSelectionListener;
import nl.usetechnology.cmake.menus.listeners.SetupCMakeProjectSelectionListener;
import nl.usetechnology.cmake.menus.selectors.ToolchainConfigurationSelector;
import nl.usetechnology.cmake.menus.selectors.BuildTypeSelector;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;

public class CMakeContributionMenu extends ContributionItem {

	private MenuItemBuilder top;

	private int toolchainsHash = 0;
	
	public CMakeContributionMenu() {
		this(null);
		refresh();
	}

	public CMakeContributionMenu(String id) {
		super(id);
		refresh();
	}

	public void refresh()
	{
		top = new MenuItemBuilder("CMake");
		top.add(new MenuItemBuilder("Refresh").setSelectionListener(new SetupCMakeProjectSelectionListener()));
		MenuItemBuilder buildConfigurations = new MenuItemBuilder("Build Configurations");
		top.add(buildConfigurations);

		BuildTypeSelector buildSelector = new BuildTypeSelector();
		BuildTypeSelectionListener buildListener = new BuildTypeSelectionListener();
		
		for(String buildType : PluginDataIO.getBuildTypes()) {
			buildConfigurations.add(new MenuItemBuilder(" " + buildType).setType(SWT.CHECK).setSelector(buildSelector).setSelectionListener(buildListener));
		}

		MenuItemBuilder toolchainItems = new MenuItemBuilder("Toolchains");
		top.add(toolchainItems);

		ToolchainConfigurationSelector tcSelector = new ToolchainConfigurationSelector();
		ToolchainSelectionListener tcListener = new ToolchainSelectionListener();
		
		List<String> toolchains = PluginDataIO.getToolchainArchitectures();
		
		for(String toolchain : toolchains) {
			toolchainItems.add(new MenuItemBuilder(" " + toolchain).setType(SWT.CHECK).setSelector(tcSelector).setSelectionListener(tcListener));
		}
		//top.add(new MenuItemBuilder("Cleanup").setSelectionListener(new CleanupCMakeProjectSelectionListener()));
		toolchainsHash = toolchains.hashCode();
	}
	
	@Override
	public void fill(Menu menu, int index) {
		List<String> toolchains = PluginDataIO.getToolchainArchitectures();
		if (toolchainsHash != toolchains.hashCode()) {
			// refresh the menu (as the toolchains have been changed)
			refresh();
		}
		top.fill(menu, index);
	}

}
