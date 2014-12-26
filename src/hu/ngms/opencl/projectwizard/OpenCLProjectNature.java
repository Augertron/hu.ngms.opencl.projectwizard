package hu.ngms.opencl.projectwizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class OpenCLProjectNature implements IProjectNature {

    private IProject project;
    public static final String OPENCL_NATURE_ID= "hu.ngms.opencl.openclnature"; //$NON-NLS-1$

    public void configure() throws CoreException {
	// TODO Auto-generated method stub

    }

    public void deconfigure() throws CoreException {
	// TODO Auto-generated method stub

    }

    public IProject getProject() {
	// TODO Auto-generated method stub
	return this.project;
    }

    public void setProject(IProject project) {
	this.project = project;

    }

}
