package nl.usetechnology.cmake;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class WorkbenchPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	private DirectoryFieldEditor dirEditor;

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

		dirEditor = new DirectoryFieldEditor("CMAKE_PATH", "CMake Environment Path", getFieldEditorParent());
		dirEditor.setPreferenceName("USE_CMAKE_PATH");
		dirEditor.load();
		addField(dirEditor);
		
		dirEditor.setPropertyChangeListener(this);
	}
	
	
	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}
	
}
