package nl.usetechnology.cmake.menus;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class MenuItemBuilder {
		private String name;
		private SelectionListener listener;
		private MenuItemPostProcessor postProcessor;
		private List<MenuItemBuilder> children = new LinkedList<MenuItemBuilder>();
		private int type;

		public MenuItemBuilder(String name) {
			this.name = name;
		}
		
		public MenuItemBuilder setSelectionListener(SelectionListener listener) {
			this.listener = listener;
			return this;
		}
		
		public MenuItemBuilder setSelector(MenuItemPostProcessor selector) {
			this.postProcessor = selector;
			return this;
		}
		
		public MenuItemBuilder setType(int type) {
			this.type = type;
			return this;
		}
		
		public MenuItemBuilder add(MenuItemBuilder subBuilder) {
			type = SWT.CASCADE;
			children.add(subBuilder);
			return this;
		}
		
		public String getName() {
			return name;
		}
		
		public void fill(Menu menu, int index) {
			IProject project = ProjectExplorerExtensionContributionFactory.retrieveSelectedProject();
			fill(menu, index, project);
		}

		protected void fill(Menu menu, IProject project) {
			fill(menu, menu.getItemCount(), project);
		}

		protected void fill(Menu menu, int index, IProject project) {
			MenuItem item;
			if (!children.isEmpty()) {
				item = new MenuItem(menu, type, index);
				Menu subMenu = new Menu(menu);
				item.setText(name);
				item.setMenu(subMenu);
				for (MenuItemBuilder descriptor : children) {
					descriptor.fill(subMenu, project);
				}
			} else {
				item = new MenuItem(menu, type, index);
				item.setText(name);
			}
			if (listener != null) {
				item.addSelectionListener(listener);
			}
			if(postProcessor != null) {
				postProcessor.postProcess(this, item, project);
			}
			
		}
}
