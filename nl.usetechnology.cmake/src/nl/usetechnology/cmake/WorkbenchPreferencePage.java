package nl.usetechnology.cmake;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class WorkbenchPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	private DirectoryFieldEditor toolchainsDirEditor;
	
	private DirectoryFieldEditor moduleDirEditor;

	private DirectoryFieldEditor templateDirEditor;

	public WorkbenchPreferencePage() {
	}

	public WorkbenchPreferencePage(String title) {
		super(title, FLAT);
	}

	@Override
	public void init(IWorkbench workbench) {
		
	}

	@Override
	protected void createFieldEditors() {

		toolchainsDirEditor = new DirectoryFieldEditor("TOOLCHAINS_PATH", "CMake Toolchains Path", getFieldEditorParent());
		toolchainsDirEditor.setPreferenceName(Activator.PREF_STORE_TOOLCHAINS_KEY);
		toolchainsDirEditor.load();

		moduleDirEditor = new DirectoryFieldEditor("MODULE_PATH", "CMake Module Path", getFieldEditorParent());
		moduleDirEditor.setPreferenceName(Activator.PREF_STORE_MODULES_KEY);
		moduleDirEditor.load();
		
		templateDirEditor = new DirectoryFieldEditor("TEMPLATES_PATH", "CMake Project Templates Path", getFieldEditorParent());
		templateDirEditor.setPreferenceName(Activator.PREF_STORE_TEMPLATES_KEY);
		templateDirEditor.load();
		
		addField(toolchainsDirEditor);
		addField(moduleDirEditor);
		addField(templateDirEditor);
		
		toolchainsDirEditor.setPropertyChangeListener(this);
		moduleDirEditor.setPropertyChangeListener(this);
		templateDirEditor.setPropertyChangeListener(this);
	}
	
	
	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}
	
}
