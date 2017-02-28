package nl.usetechnology.cmake;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import nl.usetechnology.cmake.helper.PluginDataIO;

public class ProjectSettingsAccessor {

	public static final Pattern projectNamePattern = Pattern.compile(".*<projectDescription>\\s*<name>([^@]+)@([^<]+).*", Pattern.MULTILINE | Pattern.DOTALL);

	public static final Pattern replacePattern = Pattern.compile("(.*<key>org\\.eclipse\\.cdt\\.make\\.core\\.build\\.arguments</key>\\s*<value>)([^<]*)(.*)", Pattern.MULTILINE | Pattern.DOTALL);

	public static final Pattern buildTypePattern = Pattern.compile("\\s*CMAKE_BUILD_TYPE:STRING=(.*)");

	public static String retrieveToolchain(IProject project) {
		try {
			String projectName = project.getDescription().getName();
			int atIndex = projectName.indexOf('@');
			if(atIndex >= 0) {
				return projectName.substring(atIndex+1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		List<String> toolchains = PluginDataIO.getToolchainArchitectures();

		// Check if the default toolchain is still available!
		if (toolchains.contains(Activator.getDefaultToolchain())) {
			return Activator.getDefaultToolchain();
		}
		
		if (!toolchains.isEmpty()) {
			return toolchains.get(0);
		}
		return Activator.getDefaultToolchain();
	}

	public static IFile getFileFromProject(IProject project, String... entries) {
		IContainer container = project;
		for(int i=0;i<entries.length - 1;i++) {
			container = container.getFolder(new org.eclipse.core.runtime.Path(entries[i]));
		}
		return container.getFile(new org.eclipse.core.runtime.Path(entries[entries.length - 1]));
	}
	
	public static String retrieveBuildType(IProject project) {
		List<String> validBuildTypes = PluginDataIO.getBuildTypes();
		String buildType = validBuildTypes.get(0); // Initialize with "valid" type
		
		String architecture = retrieveToolchain(project);
		IFile cacheFile = getFileFromProject(project, PluginDataIO.getBinDirectory(), architecture, "CMakeCache.txt");
		try {
			InputStream is = cacheFile.getContents(true);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			while((line = br.readLine()) != null) {
				Matcher m = buildTypePattern.matcher(line);
				if(m.matches()) {
					buildType = m.group(1);
					break;
				}
			}
			br.close();
		} catch (Exception e) {
			Activator.logInfo("Unable to retrieve build type from file: " + e.getLocalizedMessage());
		}
		if(!validBuildTypes.contains(buildType))  {
			Activator.logWarning(buildType + " is not a valid build type");
			return validBuildTypes.get(0);
		}
		return buildType;
	}

	/**
	 * This method exchanges the absolute paths to the project with the
	 * project environment variable. This way the project file can safely be
	 * checked in.
	 * For all other references it tries to convert to workspace variables.
	 * @param project
	 */
	public static void removeAbsoluteProjectPath(IProject project) {
		// It is not possible, to access all items within the .project file
		// via the description accessor, thus we have to access the file directly.
		IFile dotProjectFile = project.getFile(".project");
		String absoluteProjectPath = project.getLocation().toOSString();
		String absoluteWorkspacePath = project.getWorkspace().getRoot().getLocation().toOSString();
		if(!dotProjectFile.exists()) {
			return; // nothing to do (file does not exist)
		}
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(dotProjectFile.getContents(true)));
			StringBuilder sb = new StringBuilder();
			String line;
			while((line = br.readLine()) != null) {
				line = line.replace(absoluteProjectPath, "${PROJECT_LOC}");
				line = line.replace(absoluteWorkspacePath, "${WORKSPACE_LOC}");
				sb.append(line).append('\n');
			}
			br.close();
			
			// now we have to replace the content of this file
			dotProjectFile.setContents(new ByteArrayInputStream(sb.toString().getBytes()), IResource.NONE, null);
		} catch (Exception e) {
			Activator.logInfo("Unable to modify .project file: " + e.getLocalizedMessage());
		}


	}
	
}
