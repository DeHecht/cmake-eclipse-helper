package nl.usetechnology.cmake.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import nl.usetechnology.cmake.Activator;

public class PluginDataIO {
	public static Pattern toolchainPattern = Pattern.compile("toolchain\\.(.+)\\.cmake", Pattern.CASE_INSENSITIVE);
	
    public static List<String> fileList(Path directory) {
        List<String> fileNames = new ArrayList<>();
        try {
        	DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory);
            for (Path path : directoryStream) {
                fileNames.add(path.getFileName().toString());
            }
        } catch (IOException ex) {}
        return fileNames;
    }
    
	public static String getBinDirectory() {
		return Activator.getBinPath();
	}
	
	public static Path getDataDirectory() {
		String url = Activator.getCMakePath();
		if (url == null)
		{
			// create a dialog with ok and cancel buttons and a question icon
			DirectoryDialog dialog = new DirectoryDialog(Display.getDefault().getActiveShell(), SWT.ICON_QUESTION | SWT.OK| SWT.CANCEL);
			dialog.setText("Unable to find CMakeEnvironment");
			dialog.setMessage("Please specify path to the CMakeEnvironment");

			// open dialog and await user selection
			String returnCode = dialog.open();
			if(returnCode != null)
			{
				Activator.getDefault().getPreferenceStore().setValue("USE_CMAKE_PATH", returnCode);
			}
		}
			
		return new File(url).toPath();
	}
	
	public static Path getPathToToolchains() {
		String url = Activator.getToolchainPath();
		if (url == null) {
			return new File("").toPath();
		}
		return new File(url).toPath();
	}
	
	public static Path getPathToModules() {
		String url = Activator.getCMakeModulesPath();
		if (url == null) {
			return new File("").toPath();
		}
		return new File(url).toPath();
	}
	
	public static List<String> getToolchainArchitectures() {
		Path pathToToolchains = getPathToToolchains();
		if(pathToToolchains == null || !pathToToolchains.toFile().exists()) {
			return Collections.emptyList();
		}
		List<String> fileNames = fileList(pathToToolchains);
		List<String> results = new ArrayList<>(fileNames.size());
		for(String fileName : fileNames) {
			Matcher m = toolchainPattern.matcher(fileName);
			if(m.matches()) {
				results.add(m.group(1));
			}
		}
		Collections.sort(results);
		
		return results;
	}
	
	public static List<String> getBuildTypes() {
		return Activator.getBuildConfigurations();
	}
	
	public static Path getToolchainPathForArchitecture(String architecture) {
		Path pathToToolchains = getPathToToolchains();
		String fileName = "toolchain." + architecture + ".cmake";
		return pathToToolchains.resolve(fileName).toAbsolutePath();
	}
	
}
	
