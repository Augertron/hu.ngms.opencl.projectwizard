package hu.ngms.opencl.projectwizard.util;

import hu.ngms.opencl.projectwizard.Activator;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;

public class OpenCLProjectHelper {

    public static final String KERNEL_FOLDER = "Kernel";
    public static final String RESOURCES_FOLDER = "Resources";
    public static final String HOST_FOLDER = "Host";

    public static void addFoldersToProjectStructure(IProject newProject)
	    throws CoreException {
	String[] paths = { KERNEL_FOLDER, RESOURCES_FOLDER, HOST_FOLDER };
	for (String path : paths) {
	    IFolder etcFolders = newProject.getFolder(path);
	    createFolder(etcFolders);
	}
    }

    private static void createFolder(IFolder folder) throws CoreException {
	IContainer parent = folder.getParent();
	if (parent instanceof IFolder) {
	    createFolder((IFolder) parent);
	}
	if (!folder.exists()) {
	    folder.create(false, true, null);
	}
    }
    
}
