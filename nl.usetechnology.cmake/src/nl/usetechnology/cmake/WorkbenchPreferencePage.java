package nl.usetechnology.cmake;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class WorkbenchPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	private DirectoryFieldEditor toolchainsDirEditor;
	
	private DirectoryFieldEditor moduleDirEditor;

	private ComboFieldEditor buildEnvironmentEditor;
	
	public WorkbenchPreferencePage() {
	}

	public WorkbenchPreferencePage(String title) {
		super(title, FLAT);
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createFieldEditors() {

		toolchainsDirEditor = new DirectoryFieldEditor("TOOLCHAINS_PATH", "CMake Toolchains Path", getFieldEditorParent());
		toolchainsDirEditor.setPreferenceName(Activator.PREF_STORE_TOOLCHAINS_KEY);
		toolchainsDirEditor.load();

		moduleDirEditor = new DirectoryFieldEditor("MODULE_PATH", "CMake Module Path", getFieldEditorParent());
		moduleDirEditor.setPreferenceName(Activator.PREF_STORE_MODULES_KEY);
		moduleDirEditor.load();
		
		buildEnvironmentEditor = new ComboFieldEditor("BUILD_SYS", "Build System", new String[][]{{"Make", Activator.PREF_STORE_BUILD_SYS_MAKE_VALUE},{"Ninja", Activator.PREF_STORE_BUILD_SYS_NINJA_VALUE}}, getFieldEditorParent());
		buildEnvironmentEditor.setPreferenceName(Activator.PREF_STORE_BUILD_SYS);
		buildEnvironmentEditor.load();
		
		addField(toolchainsDirEditor);
		addField(moduleDirEditor);
		addField(buildEnvironmentEditor);
		
		toolchainsDirEditor.setPropertyChangeListener(this);
		moduleDirEditor.setPropertyChangeListener(this);
		buildEnvironmentEditor.setPropertyChangeListener(this);
	}
	
	
	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}
	
}
