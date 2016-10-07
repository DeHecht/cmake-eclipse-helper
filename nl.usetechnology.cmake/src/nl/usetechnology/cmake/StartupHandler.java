package nl.usetechnology.cmake;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IStartup;

import nl.usetechnology.cmake.event.CMakeProjectResourceChangeListener;

public class StartupHandler implements IStartup {

	private static CMakeProjectResourceChangeListener listener = new CMakeProjectResourceChangeListener();
	
	@Override
	public void earlyStartup() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.POST_CHANGE);
	}

}
