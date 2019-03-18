package nl.usetechnology.cmake;

import java.io.File;
import java.util.List;

import org.eclipse.cdt.utils.Platform;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import nl.usetechnology.cmake.helper.PluginDataIO;

public class CategoryPage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	private ComboFieldEditor buildEnvironmentEditor;
	
	private StringFieldEditor makeArgsEditor;

	private StringFieldEditor cmakeArgsEditor;

	private BooleanFieldEditor touchEditor;
	
	private ComboFieldEditor defaultToolchain;

	private ListEditor defaultBuildtypes;
	
	private CMakeLauncher launcher = new CMakeLauncher();

	
	public CategoryPage() {
	}

	public CategoryPage(String title) {
		super(title, FLAT);
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected void createFieldEditors() {
		List<String> cmakeGenerators = launcher.retrieveCMakeGenerators();
		String[][] generators = new String[cmakeGenerators.size()][];
		int i = 0;
		for( String generator : cmakeGenerators ) {
			String name = generator.replaceAll("[^-]+-(.*)", "$1").trim();
			generators[i++] = new String[]{name,generator};
		}
		
		buildEnvironmentEditor = new ComboFieldEditor("BUILD_SYS", "Build System", generators, getFieldEditorParent());
		buildEnvironmentEditor.setPreferenceName(Activator.PREF_STORE_BUILD_SYS);
		buildEnvironmentEditor.load();
		
		makeArgsEditor = new StringFieldEditor("MAKE_ARGS", "Make/Ninja Args", getFieldEditorParent());
		makeArgsEditor.setPreferenceName(Activator.PREF_STORE_MAKE_ARGS);
		makeArgsEditor.load();

		cmakeArgsEditor = new StringFieldEditor("CMAKE_ARGS", "CMake Args", getFieldEditorParent());
		cmakeArgsEditor.setPreferenceName(Activator.PREF_STORE_CMAKE_ARGS);
		cmakeArgsEditor.load();

		touchEditor = new BooleanFieldEditor("CMAKE_TOUCH", "Touch CMakeLists.txt when files are added/removed", getFieldEditorParent());
		touchEditor.setPreferenceName(Activator.PREF_STORE_TOUCH_ARGS);
		touchEditor.load();
		
		String[][] entryNamesAndValues = { { "Toolchain path invalid or not set.", Platform.getOSArch() } };
		List<String> toolchains = PluginDataIO.getToolchainArchitectures();
		if (!toolchains.isEmpty()) {
			int index = 0;
			entryNamesAndValues = new String[toolchains.size()][2];
			for (String toolchain : toolchains) {
				entryNamesAndValues[index++] = new String[]{toolchain, toolchain};
			}
		}
		
		defaultToolchain = new ComboFieldEditor("DEF_TOOLCHAIN", "Default-Toolchain", entryNamesAndValues, getFieldEditorParent());
		defaultToolchain.setPreferenceName(Activator.PREF_STORE_DEFAULT_TOOLCHAIN);
		defaultToolchain.load();
		
		defaultBuildtypes = new ListEditor("DEF_BUILDTYPES", "Available Buildtypes", getFieldEditorParent()) {
			
			@Override
			protected String[] parseString(String stringList) {
				return stringList.split(File.pathSeparator);
			}
			
			@Override
			protected String getNewInputObject() {
				InputDialog dialog = new InputDialog(getShell(), "New Buildconfiguration", "Enter the name of the Buildconfiguration to add", "", null);
				if (dialog.open() == InputDialog.OK) {
					return dialog.getValue();
				}
				return null;
			}
			
			@Override
			protected String createList(String[] items) {
				StringBuilder sb = new StringBuilder();
				for (String string : items) {
					sb.append(string).append(File.pathSeparator);
				}
				return sb.toString();
			}
		};
		defaultBuildtypes.setPreferenceName(Activator.PREF_STORE_BUILD_CONF);
		defaultBuildtypes.load();
		
		addField(buildEnvironmentEditor);
		addField(makeArgsEditor);
		addField(cmakeArgsEditor);
		addField(touchEditor);
		addField(defaultToolchain);
		addField(defaultBuildtypes);

		buildEnvironmentEditor.setPropertyChangeListener(this);
		makeArgsEditor.setPropertyChangeListener(this);
		cmakeArgsEditor.setPropertyChangeListener(this);
		touchEditor.setPropertyChangeListener(this);
		defaultToolchain.setPropertyChangeListener(this);
		defaultBuildtypes.setPropertyChangeListener(this);
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}
}