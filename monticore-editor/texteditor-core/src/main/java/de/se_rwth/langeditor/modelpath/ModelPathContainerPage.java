/* (c)  https://github.com/MontiCore/monticore */package de.se_rwth.langeditor.modelpath;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPage;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPageExtension;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.google.common.collect.Lists;

import de.se_rwth.langeditor.global.Constants;
import de.se_rwth.langeditor.injection.DIService;
import de.se_rwth.langeditor.modelstates.ModelStateAssembler;
import de.se_rwth.langeditor.util.Misc;

/**
 * The framework always calls the methods of this class in the order {@link #initialize},
 * {@link #setSelection}, {@link #createControl} when the wizard is launched and {@link #finish},
 * then {@link #getSelection} when the wizard finishes.
 * 
 * @author Sebastian Oberhoff
 */
public class ModelPathContainerPage extends WizardPage implements IClasspathContainerPage,
    IClasspathContainerPageExtension {
  
  private Table table;
  
  private IJavaProject javaProject;
  
  private IClasspathEntry[] modelPathEntries;
  
  private boolean isConfigurable;
  
  public ModelPathContainerPage() {
    super("Modelpath Wizard");
  }
  
  @Override
  public void initialize(IJavaProject javaProject, IClasspathEntry[] currentEntries) {
    this.javaProject = javaProject;
    try {
      modelPathEntries =
          Optional.ofNullable(JavaCore.getClasspathContainer(Constants.MODELPATH, javaProject))
              .map(IClasspathContainer::getClasspathEntries)
              .orElse(new IClasspathEntry[] {});
    }
    catch (JavaModelException e) {
      throw new RuntimeException(e);
    }
  }
  
  @Override
  public void setSelection(IClasspathEntry modelPathContainerEntry) {
    try {
      boolean modelPathAlreadySet = Arrays.stream(javaProject.getRawClasspath()).anyMatch(entry ->
          entry.getPath().equals(Constants.MODELPATH));
      boolean shouldCreateNewModelpath = modelPathContainerEntry == null;
      isConfigurable = !(modelPathAlreadySet && shouldCreateNewModelpath);
    }
    catch (JavaModelException e) {
      throw new RuntimeException(e);
    }
  }
  
  @Override
  public void createControl(Composite parent) {
    Composite composite = createComposite(parent);
    createButtons(composite);
    table = createTable(composite);
    setControl(composite);
  }
  
  @Override
  public boolean finish() {
    if (!isConfigurable) {
      MessageDialog
          .openError(
              null,
              "Modelpath already set",
              "A new Modelpath can't be created while another Modelpath is already configured for the same project. Please use \"Configure Build Path\" instead.");
    }
    return isConfigurable;
  }
  
  @Override
  public IClasspathEntry getSelection() {
    IClasspathAttribute[] modelPathEntries = convertEntriesToAttributes();
    IClasspathEntry modelPathContainerEntry = JavaCore.newContainerEntry(Constants.MODELPATH, null,
        modelPathEntries, false);
    updateClasspath(modelPathContainerEntry);
    DIService.getInstance(ModelStateAssembler.class).scheduleFullRebuild();
    return modelPathContainerEntry;
  }
  
  private IClasspathAttribute[] convertEntriesToAttributes() {
    TableItem[] items = table.getItems();
    return Arrays.stream(items)
        .map(TableItem::getText)
        .map(itemText -> JavaCore.newClasspathAttribute(
            Constants.MODELPATH_ENTRY + File.separator + itemText, itemText))
        .collect(Collectors.toList())
        .toArray(new IClasspathAttribute[items.length]);
  }
  
  private void updateClasspath(IClasspathEntry newModelPathContainerEntry) {
    boolean classpathModified = Misc.removeFromClasspath(javaProject, entry ->
        entry.getPath().equals(Constants.MODELPATH));
    if (classpathModified) {
      Misc.addToClasspath(javaProject, Lists.newArrayList(newModelPathContainerEntry));
    }
  }
  
  private Composite createComposite(Composite parent) {
    Composite composite = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout(1, false);
    composite.setLayout(layout);
    return composite;
  }
  
  private void createButtons(Composite parent) {
    Composite composite = new Composite(parent, SWT.NONE);
    RowLayout layout = new RowLayout(SWT.HORIZONTAL);
    composite.setLayout(layout);
    createNewFileEntryButton(composite);
    createNewDirectoryEntryButton(composite);
    createRemoveEntryButton(composite);
  }
  
  private void createNewFileEntryButton(Composite parent) {
    Button newEntryButton = new Button(parent, SWT.NONE);
    newEntryButton.setText("Add file entry");
    newEntryButton.addMouseListener(new MouseListener() {
      
      @Override
      public void mouseDoubleClick(MouseEvent e) {
        // no op
      }
      
      @Override
      public void mouseDown(MouseEvent e) {
        // no op
      }
      
      @Override
      public void mouseUp(MouseEvent e) {
        FileDialog dialog = new FileDialog(parent.getShell(), parent.getStyle());
        String selection = dialog.open();
        createTableItem(table, selection);
        table.getColumn(0).pack();
      }
    });
  }
  
  private void createNewDirectoryEntryButton(Composite parent) {
    Button newEntryButton = new Button(parent, SWT.NONE);
    newEntryButton.setText("Add directory entry");
    newEntryButton.addMouseListener(new MouseListener() {
      
      @Override
      public void mouseDoubleClick(MouseEvent e) {
        // no op
      }
      
      @Override
      public void mouseDown(MouseEvent e) {
        // no op
      }
      
      @Override
      public void mouseUp(MouseEvent e) {
        DirectoryDialog dialog = new DirectoryDialog(parent.getShell(), parent.getStyle());
        String selection = dialog.open();
        createTableItem(table, selection);
        table.getColumn(0).pack();
      }
    });
  }
  
  private void createRemoveEntryButton(Composite parent) {
    Button removeEntryButton = new Button(parent, SWT.NONE);
    removeEntryButton.setText("Remove entry");
    removeEntryButton.addMouseListener(new MouseListener() {
      
      @Override
      public void mouseDoubleClick(MouseEvent e) {
        // no op
      }
      
      @Override
      public void mouseDown(MouseEvent e) {
        // no op
      }
      
      @Override
      public void mouseUp(MouseEvent e) {
        for (int selectionIndex : table.getSelectionIndices()) {
          table.remove(selectionIndex);
        }
      }
    });
  }
  
  private Table createTable(Composite parent) {
    Table table = new Table(parent, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
    table.setHeaderVisible(true);
    GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
    table.setLayoutData(data);
    
    TableColumn column = new TableColumn(table, SWT.NONE);
    column.setText("Modelpath entries");
    
    for (IClasspathEntry classpathEntry : modelPathEntries) {
      createTableItem(table, classpathEntry.getPath().toOSString());
    }
    column.pack();
    return table;
  }
  
  private void createTableItem(Table parent, String itemText) {
    TableItem tableItem = new TableItem(parent, SWT.NONE);
    tableItem.setText(itemText);
    Misc.loadImage("icons/jar_l_obj.gif").ifPresent(tableItem::setImage);
  }
}
