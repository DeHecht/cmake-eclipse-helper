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
		
		addField(toolchainsDirEditor);
		addField(moduleDirEditor);
		
		toolchainsDirEditor.setPropertyChangeListener(this);
		moduleDirEditor.setPropertyChangeListener(this);
	}
	
	
	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}
	
}
