package nl.usetechnology.cmake.menus;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.widgets.Menu;

import nl.usetechnology.cmake.menus.listeners.SetupCMakeProjectSelectionListener;

public class CMakeSetupContributionMenu extends ContributionItem {

	private static MenuItemBuilder top;

	public CMakeSetupContributionMenu() {
		this(null);
	}

	public CMakeSetupContributionMenu(String id) {
		super(id);
	}

	static {
		top = new MenuItemBuilder("CMake");
		top.add(new MenuItemBuilder("Setup").setSelectionListener(new SetupCMakeProjectSelectionListener()));
	}

	@Override
	public void fill(Menu menu, int index) {
		super.fill(menu, index);
		top.fill(menu, index);

	}

}
