package hu.ngms.opencl.projectwizard.ui.messages;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    // Keys
    public static String OpenCLProjectWizard_name;
    public static String OpenCLProjectWizard_project;
    public static String OpenCLProjectWizard_description;
    public static String OpenCLProjectWizard_overrideOldProject;
    public static String OpenCLProjectWizard_existingSettingsOverriden;
    public static String OpenCLProjectWizard_creatingProject;
    public static String OpenCLProjectWizard_opDescription;
    public static String OpenCLProjectWizard_emptyProject;
    public static String OpenCLProjectWizard_others;

    public static String OpenCLMainWizardPage_projectType;
    public static String OpenCLMainWizardPage_supportedProjectTypes;
    public static String OpenCLMainWizardPage_projectAlreadyExists;
    public static String OpenCLMainWizardPage_fileAlreadyExists;
    public static String OpenCLMainWizardPage_dirAlreadyExists;
    public static String OpenCLMainWizardPage_noProjectTypes;
    public static String OpenCLMainWizardPage_projectCategorySelected;
    public static String OpenCLMainWizardPage_openClVersion;
    public static String OpenCLMainWizardPage_toolchains;
    
    public static String OpenCLConfigWizard_title;
    public static String OpenCLConfigWizard_message;
    public static String OpenCLConfigWizard_comment;
    public static String OpenCLConfigWizard_projectType;
    public static String OpenCLConfigWizard_toolchains;
    public static String OpenCLConfigWizard_configurations;
    public static String OpenCLConfigWizard_selectAll;
    public static String OpenCLConfigWizard_deselectAll;
    public static String OpenCLConfigWizard_advanced;

    public static String OpenCLMainWizardPage_error0;
    public static String OpenCLMainWizardPage_error1;
    
    public static String OpenCLKernelWizard_message;
    public static String OpenCLKernelWizard_title;
    public static String OpenCLKernelWizard_description;

    static {
	NLS.initializeMessages(Messages.class.getName(), Messages.class);
    }

    private Messages() {

    }
}
