package hu.ngms.opencl.projectwizard.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

public class OpenCLKernelWizard extends BasicNewResourceWizard {
    
    private OpenCLKernelWizardPage newFileWizardPage;
    
    @Override
    public void addPages() {

        newFileWizardPage = new OpenCLKernelWizardPage(selection);
        addPage(newFileWizardPage);
    }
    
    @Override
    public boolean performFinish() {
	IFile file = newFileWizardPage.createNewFile();
        if (file != null)
            return true;
        else
            return false;
    }

}
