package hu.ngms.opencl.projectwizard.ui;

import hu.ngms.opencl.projectwizard.ui.messages.Messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.SortedMap;

import org.eclipse.cdt.internal.ui.CPluginImages;
import org.eclipse.cdt.managedbuilder.buildproperties.IBuildPropertyManager;
import org.eclipse.cdt.managedbuilder.buildproperties.IBuildPropertyType;
import org.eclipse.cdt.managedbuilder.buildproperties.IBuildPropertyValue;
import org.eclipse.cdt.managedbuilder.core.BuildListComparator;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IProjectType;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.ui.wizards.AbstractCWizard;
import org.eclipse.cdt.ui.newui.CDTPrefUtil;
import org.eclipse.cdt.ui.wizards.EntryDescriptor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.graphics.Image;

public class OpenCLManagedBuildWizard extends AbstractCWizard {
    private static final Image IMG = CPluginImages
	    .get(CPluginImages.IMG_OBJS_CONTAINER);

    /**
     * Creates and returns an array of items to be displayed
     */
    public EntryDescriptor[] createItems(boolean supportedOnly, IWizard wizard) {
	IBuildPropertyManager bpm = ManagedBuildManager
		.getBuildPropertyManager();
	IBuildPropertyType bpt = bpm
		.getPropertyType(OpenCLWizardHandler.ARTIFACT);
	IBuildPropertyValue[] vs = new IBuildPropertyValue[1];
	vs[0] = bpt
		.getSupportedValue("org.eclipse.cdt.build.core.buildArtefactType.exe");

	ArrayList<EntryDescriptor> items = new ArrayList<EntryDescriptor>();
	// new style project types
	for (int i = 0; i < vs.length; i++) {
	    IToolChain[] tcs = ManagedBuildManager.getExtensionsToolChains(
		    OpenCLWizardHandler.ARTIFACT, vs[i].getId(), false);
	    if (tcs == null || tcs.length == 0)
		continue;
	    OpenCLWizardHandler h = new OpenCLWizardHandler(vs[i], parent,
		    wizard);
	    for (int j = 0; j < tcs.length; j++) {
		if (isValid(tcs[j], supportedOnly, wizard))
		    h.addTc(tcs[j]);
	    }
	    if (h.getToolChainsCount() > 0) {
		items.add(new EntryDescriptor(vs[i].getId(), null, vs[i]
			.getName(), true, h, null));
		EntryDescriptor entryDescriptor = new EntryDescriptor(
			vs[i].getId() + ".default", vs[i].getId(), //$NON-NLS-1$
			Messages.OpenCLProjectWizard_emptyProject, false, h,
			null);
		entryDescriptor.setDefaultForCategory(true);
		items.add(entryDescriptor);
	    }
	}

	return (EntryDescriptor[]) items.toArray(new EntryDescriptor[items
		.size()]);
    }
}
