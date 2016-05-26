package nl.usetechnology.cmake;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class CategoryPage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	private ComboFieldEditor buildEnvironmentEditor;
	
	private StringFieldEditor makeArgsEditor;

	private StringFieldEditor cmakeArgsEditor;
	

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
		buildEnvironmentEditor = new ComboFieldEditor("BUILD_SYS", "Build System", new String[][]{{"Make", Activator.PREF_STORE_BUILD_SYS_MAKE_VALUE},{"Ninja", Activator.PREF_STORE_BUILD_SYS_NINJA_VALUE}}, getFieldEditorParent());
		buildEnvironmentEditor.setPreferenceName(Activator.PREF_STORE_BUILD_SYS);
		buildEnvironmentEditor.load();
		
		makeArgsEditor = new StringFieldEditor("MAKE_ARGS", "Make/Ninja Args", getFieldEditorParent());
		makeArgsEditor.setPreferenceName(Activator.PREF_STORE_MAKE_ARGS);
		makeArgsEditor.load();

		cmakeArgsEditor = new StringFieldEditor("CMAKE_ARGS", "CMake Args", getFieldEditorParent());
		cmakeArgsEditor.setPreferenceName(Activator.PREF_STORE_CMAKE_ARGS);
		cmakeArgsEditor.load();

		addField(buildEnvironmentEditor);
		addField(makeArgsEditor);
		addField(cmakeArgsEditor);

		buildEnvironmentEditor.setPropertyChangeListener(this);
		makeArgsEditor.setPropertyChangeListener(this);
		cmakeArgsEditor.setPropertyChangeListener(this);
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}
}
