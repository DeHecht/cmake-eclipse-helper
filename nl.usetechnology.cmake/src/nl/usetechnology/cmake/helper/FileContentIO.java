package nl.usetechnology.cmake.helper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class FileContentIO {

	public static CharSequence readFileContent(IFile file) throws IOException, CoreException {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(file.getContents()));
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line).append('\n');
		}
		return sb;
	}
	
	public static void writeFileContent(IFile file, CharSequence sequence, IProgressMonitor monitor) throws IOException, CoreException {
		ByteArrayInputStream bis = new ByteArrayInputStream(sequence.toString().getBytes());
		if(!file.exists()) {
			file.create(bis, true, monitor);
		} else {
			file.setContents(bis, IResource.NONE, monitor);
		}
	}
}
