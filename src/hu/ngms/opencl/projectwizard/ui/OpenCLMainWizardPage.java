package hu.ngms.opencl.projectwizard.ui;

import hu.ngms.opencl.projectwizard.Activator;
import hu.ngms.opencl.projectwizard.ui.messages.Messages;
import hu.ngms.opencl.projectwizard.util.OpenCLVersion;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.cdt.internal.ui.CPluginImages;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.cdt.ui.newui.CDTPrefUtil;
import org.eclipse.cdt.ui.newui.PageLayout;
import org.eclipse.cdt.ui.newui.UIMessages;
import org.eclipse.cdt.ui.wizards.CDTMainWizardPage;
import org.eclipse.cdt.ui.wizards.CNewWizard;
import org.eclipse.cdt.ui.wizards.CWizardHandler;
import org.eclipse.cdt.ui.wizards.EntryDescriptor;
import org.eclipse.cdt.ui.wizards.IWizardItemsListListener;
import org.eclipse.cdt.ui.wizards.IWizardWithMemory;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.osgi.util.TextProcessor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.internal.ide.IIDEHelpContextIds;
import org.eclipse.ui.internal.ide.dialogs.ProjectContentsLocationArea;

public class OpenCLMainWizardPage extends WizardNewProjectCreationPage
	implements IWizardItemsListListener {

    private static final Image IMG_CATEGORY = CPluginImages
	    .get(CPluginImages.IMG_OBJS_SEARCHFOLDER);
    private static final Image IMG_ITEM = CPluginImages
	    .get(CPluginImages.IMG_OBJS_VARIABLE);

    static final String PAGE_ID = "org.eclipse.cdt.managedbuilder.ui.wizard.NewModelProjectWizardPage"; //$NON-NLS-1$

    public static final String DESC = "EntryDescriptor"; //$NON-NLS-1$ 

    private Tree tree;
    private Composite right;
    private Button show_sup;
    private Label right_label;
    private OpenCLVersion selectedOpenCLVersion =  null;
    
    public CWizardHandler h_selected = null;
    private Label categorySelectedLabel;

    /**
     * Creates a new project creation wizard page.
     *
     * @param pageName
     *            the name of this page
     */
    public OpenCLMainWizardPage(String pageName) {
	super(pageName);
	setPageComplete(false);
    }

    /**
     * (non-Javadoc) Method declared on IDialogPage.
     */
    @Override
    public void createControl(Composite parent) {
	super.createControl(parent);
	createDynamicGroup((Composite) getControl());
	switchTo(
		updateData(tree, right, show_sup, OpenCLMainWizardPage.this,
			getWizard()), getDescriptor(tree));

	setPageComplete(validatePage());
	setImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "resources/opencl-64.jpg"));
	setErrorMessage(null);
	setMessage(null);
    }

    private void createDynamicGroup(Composite parent) {
	Composite c = new Composite(parent, SWT.NONE);
	c.setLayoutData(new GridData(GridData.FILL_BOTH));
	c.setLayout(new GridLayout(2, true));
	
	Label version = new Label(c, SWT.NONE);
	version.setText(Messages.OpenCLMainWizardPage_openClVersion);
	version.setFont(parent.getFont());
	version.setLayoutData(new GridData(GridData.BEGINNING));
	
	ComboViewer versionCombo = new ComboViewer(c, SWT.NONE);
	versionCombo.setContentProvider(ArrayContentProvider.getInstance());
	versionCombo.setLabelProvider(new LabelProvider() {
	    public String getText(Object element) {
		if (element instanceof OpenCLVersion) {
		    return ((OpenCLVersion)element).getStringForCombo();
		}
		return super.getText(element);
	    }
	});
	versionCombo.addSelectionChangedListener(new ISelectionChangedListener() {
	    
	    @Override
	    public void selectionChanged(SelectionChangedEvent event) {
		selectedOpenCLVersion =(OpenCLVersion)((IStructuredSelection)event.getSelection()).getFirstElement();
		setPageComplete(validatePage());
	    }
	});
	versionCombo.setInput(OpenCLVersion.values());
	versionCombo.getCombo().setLayoutData(new GridData(GridData.BEGINNING));
	
	Label l1 = new Label(c, SWT.NONE);
	l1.setText(Messages.OpenCLMainWizardPage_projectType); 
	l1.setFont(parent.getFont());
	l1.setLayoutData(new GridData(GridData.BEGINNING));

	right_label = new Label(c, SWT.NONE);
	right_label.setFont(parent.getFont());
	right_label.setLayoutData(new GridData(GridData.BEGINNING));

	tree = new Tree(c, SWT.SINGLE | SWT.BORDER);
	tree.setLayoutData(new GridData(GridData.FILL_BOTH));
	tree.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		TreeItem[] tis = tree.getSelection();
		if (tis == null || tis.length == 0)
		    return;
		switchTo((CWizardHandler) tis[0].getData(),
			(EntryDescriptor) tis[0].getData(DESC));
		setPageComplete(validatePage());
	    }
	});
	tree.getAccessible().addAccessibleListener(new AccessibleAdapter() {
	    @Override
	    public void getName(AccessibleEvent e) {
		for (int i = 0; i < tree.getItemCount(); i++) {
		    if (tree.getItem(i).getText().compareTo(e.result) == 0)
			return;
		}
		e.result = Messages.OpenCLMainWizardPage_projectType; 
	    }
	});
	right = new Composite(c, SWT.NONE);
	right.setLayoutData(new GridData(GridData.FILL_BOTH));
	right.setLayout(new PageLayout());

	show_sup = new Button(c, SWT.CHECK);
	show_sup.setText(Messages.OpenCLMainWizardPage_supportedProjectTypes); 
	GridData gd = new GridData(GridData.FILL_HORIZONTAL);
	gd.horizontalSpan = 2;
	show_sup.setLayoutData(gd);
	show_sup.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		if (h_selected != null)
		    h_selected.setSupportedOnly(show_sup.getSelection());
		switchTo(
			updateData(tree, right, show_sup,
				OpenCLMainWizardPage.this, getWizard()),
			getDescriptor(tree));
	    }
	});

	// restore settings from preferences
	show_sup.setSelection(!CDTPrefUtil.getBool(CDTPrefUtil.KEY_NOSUPP));
    }

    @Override
    public IWizardPage getNextPage() {
	return (h_selected == null) ? null : h_selected.getSpecificPage();
    }

    public URI getProjectLocation() {
	return useDefaults() ? null : getLocationURI();
    }

    /**
     * Returns whether this page's controls currently all contain valid values.
     *
     * @return <code>true</code> if all controls are valid, and
     *         <code>false</code> if at least one is invalid
     */
    @Override
    protected boolean validatePage() {
	setMessage(null);
	if (!super.validatePage())
	    return false;

	if (getProjectName().indexOf('#') >= 0) {
	    setErrorMessage(Messages.OpenCLMainWizardPage_error0); 
	    return false;
	}
	
	if (this.selectedOpenCLVersion == null) {
	    setErrorMessage(Messages.OpenCLMainWizardPage_error1); 
	    return false;
	}
	
	boolean bad = true; // should we treat existing project as error

	IProject handle = getProjectHandle();
	if (handle.exists()) {
	    if (getWizard() instanceof IWizardWithMemory) {
		IWizardWithMemory w = (IWizardWithMemory) getWizard();
		if (w.getLastProjectName() != null
			&& w.getLastProjectName().equals(getProjectName()))
		    bad = false;
	    }
	    if (bad) {
		setErrorMessage(Messages.OpenCLMainWizardPage_projectAlreadyExists); 
		return false;
	    }
	}

	if (bad) { // skip this check if project already created
	    try {
		IFileStore fs;
		URI p = getProjectLocation();
		if (p == null) {
		    fs = EFS.getStore(ResourcesPlugin.getWorkspace().getRoot()
			    .getLocationURI());
		    fs = fs.getChild(getProjectName());
		} else
		    fs = EFS.getStore(p);
		IFileInfo f = fs.fetchInfo();
		if (f.exists()) {
		    if (f.isDirectory()) {
			setMessage(
				Messages.OpenCLMainWizardPage_dirAlreadyExists, IMessageProvider.WARNING); 
		    } else {
			setErrorMessage(Messages.OpenCLMainWizardPage_fileAlreadyExists); 
			return false;
		    }
		}
	    } catch (CoreException e) {
		CUIPlugin.log(e.getStatus());
	    }
	}

	if (!useDefaults()) {
	    IStatus locationStatus = ResourcesPlugin.getWorkspace()
		    .validateProjectLocationURI(handle, getLocationURI());
	    if (!locationStatus.isOK()) {
		setErrorMessage(locationStatus.getMessage());
		return false;
	    }
	}

	if (tree.getItemCount() == 0) {
	    setErrorMessage(Messages.OpenCLMainWizardPage_noProjectTypes); 
	    return false;
	}

	// it is not an error, but we cannot continue
	if (h_selected == null) {
	    setErrorMessage(null);
	    return false;
	}

	String s = h_selected.getErrorMessage();
	if (s != null) {
	    setErrorMessage(s);
	    return false;
	}

	setErrorMessage(null);
	return true;
    }

    /**
     * 
     * @param tree
     * @param right
     * @param show_sup
     * @param ls
     * @param wizard
     * @return : selected Wizard Handler.
     */
    public static CWizardHandler updateData(Tree tree, Composite right,
	    Button show_sup, IWizardItemsListListener ls, IWizard wizard) {
	// remember selected item
	TreeItem[] sel = tree.getSelection();
	String savedStr = (sel.length > 0) ? sel[0].getText() : null;

	tree.removeAll();

	List<EntryDescriptor> items = new ArrayList<EntryDescriptor>();

	CNewWizard w = null;
	w = (CNewWizard) new OpenCLManagedBuildWizard();
	w.setDependentControl(right, ls);
	for (EntryDescriptor ed : w
		.createItems(show_sup.getSelection(), wizard))
	    items.add(ed);

	// If there is a EntryDescriptor which is default for category, make
	// sure it
	// is in the front of the list.
	for (int i = 0; i < items.size(); ++i) {
	    EntryDescriptor ed = items.get(i);
	    if (ed.isDefaultForCategory()) {
		items.remove(i);
		items.add(0, ed);
		break;
	    }
	}

	// bug # 211935 : allow items filtering.
	if (ls != null) // NULL means call from prefs
	    items = ls.filterItems(items);
	addItemsToTree(tree, items);

	if (tree.getItemCount() > 0) {
	    TreeItem target = null;
	    // try to search item which was selected before
	    if (savedStr != null) {
		TreeItem[] all = tree.getItems();
		for (TreeItem element : all) {
		    if (savedStr.equals(element.getText())) {
			target = element;
			break;
		    }
		}
	    }
	    if (target == null) {
		target = tree.getItem(0);
		if (target.getItemCount() != 0)
		    target = target.getItem(0);
	    }
	    tree.setSelection(target);
	    return (CWizardHandler) target.getData();
	}
	return null;
    }

    private static void addItemsToTree(Tree tree, List<EntryDescriptor> items) {
	// Sorting is disabled because of users requests
	// Collections.sort(items, CDTListComparator.getInstance());

	ArrayList<TreeItem> placedTreeItemsList = new ArrayList<TreeItem>(
		items.size());
	ArrayList<EntryDescriptor> placedEntryDescriptorsList = new ArrayList<EntryDescriptor>(
		items.size());
	for (EntryDescriptor wd : items) {
	    if (wd.getParentId() == null) {
		wd.setPath(wd.getId());
		TreeItem ti = new TreeItem(tree, SWT.NONE);
		ti.setText(TextProcessor.process(wd.getName()));
		ti.setData(wd.getHandler());
		ti.setData(DESC, wd);
		ti.setImage(calcImage(wd));
		placedTreeItemsList.add(ti);
		placedEntryDescriptorsList.add(wd);
	    }
	}
	while (true) {
	    boolean found = false;
	    Iterator<EntryDescriptor> it2 = items.iterator();
	    while (it2.hasNext()) {
		EntryDescriptor wd1 = it2.next();
		if (wd1.getParentId() == null)
		    continue;
		for (int i = 0; i < placedEntryDescriptorsList.size(); i++) {
		    EntryDescriptor wd2 = placedEntryDescriptorsList.get(i);
		    if (wd2.getId().equals(wd1.getParentId())) {
			found = true;
			wd1.setParentId(null);
			CWizardHandler h = wd2.getHandler();
			/*
			 * If neither wd1 itself, nor its parent (wd2) have a
			 * handler associated with them, and the item is not a
			 * category, then skip it. If it's category, then it's
			 * possible that children will have a handler associated
			 * with them.
			 */
			if (h == null && wd1.getHandler() == null
				&& !wd1.isCategory())
			    break;

			wd1.setPath(wd2.getPath() + "/" + wd1.getId()); //$NON-NLS-1$
			wd1.setParent(wd2);
			if (h != null) {
			    if (wd1.getHandler() == null && !wd1.isCategory())
				wd1.setHandler((CWizardHandler) h.clone());
			    if (!h.isApplicable(wd1))
				break;
			}

			TreeItem p = placedTreeItemsList.get(i);
			TreeItem ti = new TreeItem(p, SWT.NONE);
			ti.setText(wd1.getName());
			ti.setData(wd1.getHandler());
			ti.setData(DESC, wd1);
			ti.setImage(calcImage(wd1));
			placedTreeItemsList.add(ti);
			placedEntryDescriptorsList.add(wd1);
			break;
		    }
		}
	    }
	    // repeat iterations until all items are placed.
	    if (!found)
		break;
	}
	// orphan elements (with not-existing parentId) are ignored
    }

    private void switchTo(CWizardHandler h, EntryDescriptor ed) {
	if (h == null)
	    h = ed.getHandler();
	if (ed.isCategory())
	    h = null;
	try {
	    if (h != null)
		h.initialize(ed);
	} catch (CoreException e) {
	    h = null;
	}
	if (h_selected != null)
	    h_selected.handleUnSelection();
	h_selected = h;
	if (h == null) {
	    if (ed.isCategory()) {
		if (categorySelectedLabel == null) {
		    categorySelectedLabel = new Label(right, SWT.WRAP);
		    categorySelectedLabel.setText(Messages.OpenCLMainWizardPage_projectCategorySelected); //$NON-NLS-1$
		    right.layout();
		}
		categorySelectedLabel.setVisible(true);
	    }
	    return;
	}
	right_label.setText(Messages.OpenCLMainWizardPage_toolchains);
	if (categorySelectedLabel != null)
	    categorySelectedLabel.setVisible(false);
	h_selected.handleSelection();
	h_selected.setSupportedOnly(show_sup.getSelection());
    }

    public static EntryDescriptor getDescriptor(Tree _tree) {
	TreeItem[] sel = _tree.getSelection();
	if (sel == null || sel.length == 0)
	    return null;
	return (EntryDescriptor) sel[0].getData(DESC);
    }

    public void toolChainListChanged(int count) {
	setPageComplete(validatePage());
	getWizard().getContainer().updateButtons();
    }

    public boolean isCurrent() {
	return isCurrentPage();
    }

    private static Image calcImage(EntryDescriptor ed) {
	if (ed.getImage() != null)
	    return ed.getImage();
	if (ed.isCategory())
	    return IMG_CATEGORY;
	return IMG_ITEM;
    }

    @SuppressWarnings("unchecked")
    public List filterItems(List items) {
	return items;
    }
    
    public OpenCLVersion getSelectedOpenCLVersion() {
	return selectedOpenCLVersion;
    }

}
