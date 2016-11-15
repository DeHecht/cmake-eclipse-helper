package nl.usetechnology.cmake.wizards;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

import nl.usetechnology.cmake.Activator;

public class CMakeMainWizardPage extends WizardNewProjectCreationPage {

	private static String K_DESC = "DESCRIPTION";
	private static String K_URL = "TAR_URL";
	// Widgets
	private Tree tree;
	private Text right;
	private Label rightLabel;

	private TreeItem choice;
	
	public CMakeMainWizardPage(String pageName) {
		super(pageName);
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		Dialog.applyDialogFont(getControl());

		// add selection?

		createDynamicGroup((Composite) getControl());
		//switchTo(updateData(tree, right, showSup, CDTMainWizardPage.this, getWizard()), getDescriptor(tree));
	}

	private void createDynamicGroup(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		c.setLayout(new GridLayout(2, true));

		Label l1 = new Label(c, SWT.NONE);
		l1.setText("Templates");
		l1.setFont(parent.getFont());
		l1.setLayoutData(new GridData(GridData.BEGINNING));

		rightLabel = new Label(c, SWT.NONE);
		rightLabel.setFont(parent.getFont());
		rightLabel.setText("Description");
		rightLabel.setLayoutData(new GridData(GridData.BEGINNING));

		tree = new Tree(c, SWT.SINGLE | SWT.BORDER);
		tree.setLayoutData(new GridData(GridData.FILL_BOTH));
		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem[] tis = tree.getSelection();
				if (tis == null || tis.length == 0)
					return;
				
				String description = (String)tis[0].getData(K_DESC);
				if ( description == null ) {
					description = "";
				}
				choice = tis[0];
				right.setText(description);
				//switchTo((CWizardHandler) tis[0].getData(), (EntryDescriptor) tis[0].getData(DESC));
				setPageComplete(validatePage());
			}
		});
		
		right = new Text(c, SWT.WRAP | SWT.BORDER);
		right.setEditable(false);
		l1.setFont(parent.getFont());
		right.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		loadData();
	}

	public static class DirectoryEntry {
		public String name;
		public String description;
		public String file;
		public Map<String, String> patterns = new HashMap<>();
		public List<DirectoryEntry> children = new LinkedList<>();
		
		public DirectoryEntry(String name) {
			this.name = name;
		}
	}
	
	private List<DirectoryEntry> readTemplateDirectory()
	{
		Pattern linepattern = Pattern.compile("\\s*([^\\s]+)\\s*=\\s*(.*)");

		Map<String, DirectoryEntry> categoryMap = new HashMap<>();
		
		List<DirectoryEntry> categories = new ArrayList<>();
		
		String url = Activator.getTemplatesPath();
		if ( url == null || url.isEmpty() ) {
			return categories;
		}
		
		File baseDirectory = new File(url);
		File[] files = baseDirectory.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".txt");
			}
		});

		for( File file : files )
		{
			try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
				DirectoryEntry entry = new DirectoryEntry(null);
				String line = null;
				while ((line = br.readLine()) != null) {
					Matcher m = linepattern.matcher(line);
					if ( m.matches() )
					{
						String key = m.group(1);
						String value = m.group(2);
						switch( key.toLowerCase() ) {
						case "name":
							entry.name = value;
							break;
						case "category":
							DirectoryEntry category = categoryMap.get(value);
							if ( category == null )
							{
								category = new DirectoryEntry(value);
								categoryMap.put(value, category);
								categories.add(category);
							}
							category.children.add(entry);
							break;
						case "description":
							entry.description = value;
							break;
						case "file":
						{
							File f = new File(value);
							if ( !f.exists() )
							{
								f = new File(baseDirectory, value);
							}
							entry.file = f.getAbsolutePath();
							break;
						}
						default:
							if ( key.startsWith("repl.") ) {
								key = key.substring("repl.".length());
								entry.patterns.put(key, value);
							}
						}
					}
				}
			} catch (IOException ioe) {

			}
		}
		
		return categories;
	}
	
	private void loadData() {
		tree.removeAll();
		loadRealData();
	}
	
	private void loadRealData() {
		List<DirectoryEntry> entries = readTemplateDirectory();
		if( entries.size() > 0 )
		{
			for ( DirectoryEntry entry : entries )
			{
				createTreeItem(tree, entry);
			}
		}
		else
		{
			setErrorMessage("Templates are not configured. See https://github.com/USESystemEngineeringBV/cmake-eclipse-helper for more information.");
		}
	}

	private void createTreeItem( Tree parent, DirectoryEntry entry )
	{
		TreeItem item = new TreeItem(parent, SWT.NONE);
		fillTreeItem(item, entry);
		item.setExpanded(true);
	}

	private void createTreeItem( TreeItem parent, DirectoryEntry entry )
	{
		TreeItem item = new TreeItem(parent, SWT.NONE);
		fillTreeItem(item, entry);
		item.setExpanded(true);
	}

	private void fillTreeItem(TreeItem item, DirectoryEntry entry) {
		item.setText(entry.name);
		item.setData(K_DESC, entry.description);
		item.setData(K_URL, entry.file);
		item.setData(entry);
		for (DirectoryEntry child : entry.children) {
			createTreeItem(item, child);
		}
	}

	@Override
	protected boolean validatePage() {
		if (!super.validatePage()) {
			return false;
		}

		if (choice == null || choice.getData(K_URL) == null) {
			setErrorMessage("Template has to be selected");
			return false;
		}

		return true;
	}
	
	public DirectoryEntry getChoice() {
		return (DirectoryEntry) choice.getData();
	}
}