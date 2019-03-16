package nl.usetechnology.cmake.event;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;

import nl.usetechnology.cmake.Activator;

public class CMakeProjectResourceChangeListener implements IResourceChangeListener {

	private int counter = 0;
	
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta delta = event.getDelta();
		Activator.logInfo("(" + counter + ") resourceChanged called ");
		counter += 1;
		
		try {
			ResourcesChangedVisitor visitor = new ResourcesChangedVisitor();
			delta.accept(visitor);
			visitor.postProcess();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

}
