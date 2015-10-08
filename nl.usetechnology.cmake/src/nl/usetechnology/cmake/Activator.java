package nl.usetechnology.cmake;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "nl.usetechnology.cmake"; //$NON-NLS-1$

	public static final String PREF_STORE_TOOLCHAINS_KEY = "USE_CMAKE_TOOLCHAIN_PATH";
	public static final String PREF_STORE_MODULES_KEY = "USE_CMAKE_MODULE_PATH";
	public static final String PREF_STORE_BUILD_SYS = "USE_CMAKE_BUILD_SYSTEM";
	
	public static final String PREF_STORE_BUILD_SYS_MAKE_VALUE = "Eclipse CDT4 - Unix Makefiles";
	public static final String PREF_STORE_BUILD_SYS_NINJA_VALUE = "Eclipse CDT4 - Ninja";

	
	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
		getPreferenceStore().setDefault(PREF_STORE_BUILD_SYS, PREF_STORE_BUILD_SYS_MAKE_VALUE);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);

	}
	
	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public static CoreException newCoreException(String message) {
		return new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, message));
	}

	public static CoreException newCoreException(String message, Throwable t) {
		return new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, message, t));
	}
	
	public static void logInfo(String message) {
		plugin.log(IStatus.INFO, message, null);
	}
	
	public static void logWarning(String message) {
		logWarning(message, null);
	}

	public static void logWarning(String message, Throwable t) {
		plugin.log(IStatus.WARNING, message, t);
	}

	public static void logError(String message) {
		logError(message, null);
	}

	public static void logError(String message, Throwable t) {
		plugin.log(IStatus.ERROR, message, t);
	}

	private void log(int severity, String message, Throwable t) {
		getLog().log(new Status(severity, PLUGIN_ID, message, t));
	}

	public static MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}
	
	public static void showConsole(String name) {
		MessageConsole console = findConsole(name);
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		conMan.showConsoleView(console);
	}

	public static String getBuildSystemString() {
		return getDefault().getPreferenceStore().getString(PREF_STORE_BUILD_SYS);
	}
	
}
