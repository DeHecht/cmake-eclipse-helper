package nl.usetechnology.cmake.wizards;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.utils.IOUtils;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;

import nl.usetechnology.cmake.CMakeLauncher;
import nl.usetechnology.cmake.CMakeNature;
import nl.usetechnology.cmake.wizards.CMakeMainWizardPage.DirectoryEntry;

public class CMakeWizard extends Wizard implements IWorkbenchWizard {

	private CMakeMainWizardPage mainPage;
	private IProject newProject;
	
	public CMakeWizard() {
		
	}

	@Override
	public boolean performFinish() {
		final IProject project = createNewProject();
		if (mainPage != null) {
			final DirectoryEntry choice = mainPage.getChoice();

			WorkspaceJob job = new WorkspaceJob("Extract template for " + project) {
				@Override
				public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
					extract(project, choice);
					if (!project.hasNature(CMakeNature.ID)) {
						new CMakeLauncher().setupProject(project);
					}
					return Status.OK_STATUS;
				}
			};
			job.schedule();
		}
		return true;
	}
	
	private void extract(IProject project, final DirectoryEntry choice)
	{
		File archive = new File(choice.file);
		File outputDir = project.getLocation().makeAbsolute().toFile();
		
		for(Entry<String, String> patternEntry : choice.patterns.entrySet())
		{
			String key = patternEntry.getKey();
			String value = patternEntry.getValue();
			System.out.println("Analyse " + key + ": " + value);
			if ( project.getName().matches(value) )
			{
				Pattern p = Pattern.compile(value);
				Matcher m = p.matcher(project.getName());
				System.out.println("Regular expression matches, group count: " + m.groupCount());
				if (m.matches() && m.groupCount() > 0)
				{
					StringBuilder sb = new StringBuilder();
					for( int i = 1; i <= m.groupCount(); i++) {
						sb.append(m.group(i));
					}
					value = sb.toString();
					System.out.println("Re-add " + key + ": " + value);
					choice.patterns.put(key, value);
				}
			}
		}

		
		
		try (ArchiveInputStream input = new ArchiveStreamFactory().createArchiveInputStream(new BufferedInputStream(new FileInputStream(archive)))) {
			ArchiveEntry entry = null;
			while ((entry = input.getNextEntry()) != null) {
				final File outputFile = new File(outputDir, entry.getName());
				if (entry.isDirectory()) {
					if (!outputFile.exists()) {
						if (!outputFile.mkdirs()) {
							throw new IllegalStateException(
									String.format("Couldn't create directory %s.", outputFile.getAbsolutePath()));
						}
					}
				} else {
					final OutputStream outputFileStream = new FileOutputStream(outputFile);
					IOUtils.copy(input, outputFileStream);
				}
			}
		} catch (Exception e) {
			System.err.println("Input: " + archive.getAbsolutePath());
			System.err.println("Output: " + outputDir.getAbsolutePath());
			e.printStackTrace();
		}
		
		try {
			Files.walkFileTree(outputDir.toPath(), new FileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					StringBuilder sb = new StringBuilder();
					
					try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file.toFile())))) {
						String line = null;
						while ((line = br.readLine()) != null) {
							sb.append(replace(line, choice)).append('\n');
						}
					}
					
					try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.toFile())))) {
						bw.write(sb.toString());
					}
					
					renamePath(file, choice);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					renamePath(dir, choice);
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String replace(String line, DirectoryEntry entry)
	{
		for(Entry<String, String> dirEntry : entry.patterns.entrySet()) {
			line = line.replaceAll(dirEntry.getKey(), dirEntry.getValue());
		}
		return line;
	}
	
	public String newFilename(Path path, DirectoryEntry entry) {
		return replace(path.getFileName().toString(), entry);
	}
	
	public void renamePath(Path path, DirectoryEntry entry) {
		String currentFilename = path.getFileName().toString();
		String newFilename =  newFilename(path, entry);
		
		if( !currentFilename.equals(newFilename) ) {
			path.toFile().renameTo(new File(path.getParent().toFile(), newFilename));
		}
	}
	
	/**
	 * Creates a new project resource with the selected name.
	 * <p>
	 * In normal usage, this method is invoked after the user has pressed Finish
	 * on the wizard; the enablement of the Finish button implies that all
	 * controls on the pages currently contain valid values.
	 * </p>
	 * <p>
	 * Note that this wizard caches the new project once it has been
	 * successfully created; subsequent invocations of this method will answer
	 * the same project resource without attempting to create it again.
	 * </p>
	 *
	 * @return the created project resource, or <code>null</code> if the
	 *         project was not created
	 */
	private IProject createNewProject() {
		if (newProject != null) {
			return newProject;
		}

		// get a project handle
		final IProject newProjectHandle = mainPage.getProjectHandle();

		// get a project descriptor
		URI location = null;
		if (!mainPage.useDefaults()) {
			location = mainPage.getLocationURI();
		}

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProjectDescription description = workspace
				.newProjectDescription(newProjectHandle.getName());
		description.setLocationURI(location);

		// create the new project operation
		IRunnableWithProgress op = new IRunnableWithProgress() {
			@Override
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				CreateProjectOperation op = new CreateProjectOperation(
						description, "Create CMake Project");
				try {
					// see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=219901
					// directly execute the operation so that the undo state is
					// not preserved.  Making this undoable resulted in too many
					// accidental file deletions.
					op.execute(monitor, WorkspaceUndoUtil
						.getUIInfoAdapter(getShell()));
				} catch (ExecutionException e) {
					throw new InvocationTargetException(e);
				}
			}
		};

		// run the new project creation operation
		try {
			getContainer().run(true, true, op);
		} catch (InterruptedException e) {
			return null;
		} catch (InvocationTargetException e) {
			return null;
		}

		newProject = newProjectHandle;

		return newProject;
}

	@Override
	public void addPages() {
		mainPage = new CMakeMainWizardPage("basicNewCMakeProjectPage");
		mainPage.setTitle("CMake Project");
		mainPage.setDescription("Create new CMake Project based on template.");
		this.addPage(mainPage);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
	}

}
