package nl.usetechnology.cmake;

import nl.usetechnology.cmake.event.CMakeProjectResourceChangeListener;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IStartup;

public class StartupHandler implements IStartup {

	private static CMakeProjectResourceChangeListener listener = new CMakeProjectResourceChangeListener();
	
	@Override
	public void earlyStartup() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.POST_CHANGE);
	}

}
