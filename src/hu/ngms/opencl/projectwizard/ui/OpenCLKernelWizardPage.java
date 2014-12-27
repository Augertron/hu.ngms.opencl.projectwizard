package hu.ngms.opencl.projectwizard.ui;

import hu.ngms.opencl.projectwizard.ui.messages.Messages;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

public class OpenCLKernelWizardPage extends WizardNewFileCreationPage {

    public OpenCLKernelWizardPage(IStructuredSelection selection) {
	super(Messages.OpenCLKernelWizard_message, selection);
	setTitle(Messages.OpenCLKernelWizard_title);
	setDescription(Messages.OpenCLKernelWizard_description);
	setFileExtension("cl");
	
    }

    @Override
    protected InputStream getInitialContents() {
	String template = "kernel void sampleKernel( global char* param )\n{\n}\n";
	return new ByteArrayInputStream(template.getBytes());
    }

}
