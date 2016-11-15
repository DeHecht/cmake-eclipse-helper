package nl.usetechnology.cmake;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class WorkbenchPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	static class SubstitutionalDirectoryFieldEditor extends DirectoryFieldEditor {
		
		public SubstitutionalDirectoryFieldEditor(String name, String labelText, Composite parent) {
			super(name, labelText, parent);
		}
		
		@Override
		public boolean doCheckState() {
			VariablesPlugin variablesPlugin = VariablesPlugin.getDefault();
			
			IStringVariableManager manager = variablesPlugin.getStringVariableManager();
			// check if there are substitutions
			String result;
			try {
				result = manager.performStringSubstitution(getStringValue(), false);
				if(!result.equals(getStringValue())) {
					// string substitution present ... field editor cannot validate the value.
					return true;
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
			return super.doCheckState();
		}
	}
	
	static class SubstitutionalFileFieldEditor extends FileFieldEditor {

		public SubstitutionalFileFieldEditor(String name, String labelText, Composite parent) {
			super(name, labelText, parent);
		}

		@Override
		public boolean checkState() {
			VariablesPlugin variablesPlugin = VariablesPlugin.getDefault();
			
			IStringVariableManager manager = variablesPlugin.getStringVariableManager();
			// check if there are substitutions
			String result;
			try {
				result = manager.performStringSubstitution(getStringValue(), false);
				if(!result.equals(getStringValue())) {
					// string substitution present ... field editor cannot validate the value.
					return true;
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
			return super.doCheckState();
		}
	}
	
	
	private DirectoryFieldEditor toolchainsDirEditor;
	
	private DirectoryFieldEditor moduleDirEditor;

	private DirectoryFieldEditor templateDirEditor;
	
	private FileFieldEditor cmakeFileEditor;

	public WorkbenchPreferencePage() {
		super("Path configuration",GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("You can also use eclipse variables like ${workspace_loc}, but then the validation is disabled.");
	}

	@Override
	public void init(IWorkbench workbench) {
		
	}

	@Override
	protected void createFieldEditors() {
		
		toolchainsDirEditor = new SubstitutionalDirectoryFieldEditor("TOOLCHAINS_PATH", "CMake Toolchains Path", getFieldEditorParent());
		toolchainsDirEditor.setPreferenceName(Activator.PREF_STORE_TOOLCHAINS_KEY);
		toolchainsDirEditor.load();

		moduleDirEditor = new SubstitutionalDirectoryFieldEditor("MODULE_PATH", "CMake Module Path", getFieldEditorParent());
		moduleDirEditor.setPreferenceName(Activator.PREF_STORE_MODULES_KEY);
		moduleDirEditor.load();
		
		templateDirEditor = new SubstitutionalDirectoryFieldEditor("TEMPLATES_PATH", "CMake Project Templates Path", getFieldEditorParent());
		templateDirEditor.setPreferenceName(Activator.PREF_STORE_TEMPLATES_KEY);
		templateDirEditor.load();
		
		cmakeFileEditor = new SubstitutionalFileFieldEditor("CMAKE_PATH", "Optional path to CMake binary", getFieldEditorParent());
		cmakeFileEditor.setPreferenceName(Activator.PREF_STORE_CMAKE_PATH);
		cmakeFileEditor.load();
		
		addField(toolchainsDirEditor);
		addField(moduleDirEditor);
		addField(templateDirEditor);
		addField(cmakeFileEditor);
		
		toolchainsDirEditor.setPropertyChangeListener(this);
		moduleDirEditor.setPropertyChangeListener(this);
		templateDirEditor.setPropertyChangeListener(this);
		cmakeFileEditor.setPropertyChangeListener(this);
	}
	
}
