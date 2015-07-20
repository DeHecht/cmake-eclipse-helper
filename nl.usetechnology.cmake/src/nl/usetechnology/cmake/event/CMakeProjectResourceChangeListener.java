package nl.usetechnology.cmake.event;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

public class CMakeProjectResourceChangeListener implements IResourceChangeListener {

	IResourceDeltaVisitor[] reusableVisitors = new IResourceDeltaVisitor[] {
			new DotProjectFileChangedDeltaVisitor()
	};
	
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		try {
			IResourceDelta delta = event.getDelta();
			
			CMakeTouchDeltaVisitor touchVisitor = new CMakeTouchDeltaVisitor();
			
			delta.accept(touchVisitor);
			touchVisitor.postProcess();
			
			for(IResourceDeltaVisitor visitor : reusableVisitors) {
				delta.accept(visitor);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

}
