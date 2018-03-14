/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.views.outline;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import de.monticore.editorconnector.menus.OutlineMenuContribution;
import de.monticore.genericgraphics.GenericFormEditor;
import de.se_rwth.langeditor.texteditor.outline.OutlinePage;

/**
 * A ContentOutlipePage that manages a graphical and a textual outline as child controls of a
 * {@link PageBook} control. Only one of the two outlines is displayed at a time and it is 
 * possible to switch between the two by calling {@link #changeOutlineType(boolean)}.
 * 
 * @author Philipp Kehrbusch
 *
 */
public class CombinedGraphicsOutlinePage extends Page implements IContentOutlinePage {

  private PageBook rootControl;
  private Control graphicalControl;
  private Control textualControl;
  private GraphicalOutlinePage graphicalOutline;
  private OutlinePage textualOutline;
  
  private boolean showTextualOutline = false;
  
  public CombinedGraphicsOutlinePage(GraphicalOutlinePage graphicalOutline, OutlinePage textualOutline) {
    this.graphicalOutline = graphicalOutline;
    this.textualOutline = textualOutline;
  }
  
  @Override
  public void createControl(Composite parent) {
    rootControl = new PageBook(parent, SWT.NONE);
    
    textualOutline.init(getSite());
    textualOutline.createControl(rootControl);
    textualControl = textualOutline.getControl();
    
    graphicalOutline.init(getSite());
    graphicalOutline.createControl(rootControl);
    graphicalControl = graphicalOutline.getControl();
        
    MenuManager manager = new MenuManager();
    Menu menu =  manager.createContextMenu(textualControl);
    textualControl.setMenu(menu);
    getSite().registerContextMenu("outlineTextContext", manager, this);
        
    // add menu for selecting default outline type
    manager.add(new OutlineMenuContribution(false));
    manager.add(new OutlineMenuContribution(true));
     
    // initialize outline type
    final IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode("graphical-editor-core");
    showTextualOutline = !prefs.getBoolean(OutlineMenuContribution.PREF_NAME, true);

    changeOutlineType();
  }
  
  public void changeOutlineType() {
    this.showTextualOutline = !showTextualOutline;
    
    if(rootControl != null) {
      IEditorPart activeE = getSite().getPage().getActiveEditor();
      
      if(showTextualOutline && textualOutline != null) {
        rootControl.showPage(textualControl);
        
        // important!!! otherwise, the selection provider could be set
        // even though this outline page is not even being displayed
        // and hence the selection provider of the active outline page
        // will be overwritten
        if(activeE instanceof GenericFormEditor) {
          getSite().setSelectionProvider(textualOutline.getTree());
        }
      }
      else if(!showTextualOutline) {
        rootControl.showPage(graphicalControl);
        
        if(activeE instanceof GenericFormEditor) {
          getSite().setSelectionProvider(graphicalOutline.getViewer());
        }
      }
    }
  }
  
  
  @Override
  public Composite getControl() {
    return rootControl;
  }
  
  @Override
  public void dispose() {
    graphicalOutline.dispose();
    if (textualOutline != null) {
      textualOutline.dispose();
    }
    super.dispose();
  }
  
  @Override
  public void addSelectionChangedListener(ISelectionChangedListener listener) {
    if(textualOutline != null)
      textualOutline.addSelectionChangedListener(listener);
    graphicalOutline.addSelectionChangedListener(listener);
  }

  @Override
  public ISelection getSelection() {
    if(showTextualOutline && textualOutline != null)
      return textualOutline.getSelection();
    else
      return graphicalOutline.getSelection(); 
  }

  @Override
  public void removeSelectionChangedListener(ISelectionChangedListener listener) {
    if(textualOutline != null)
      textualOutline.removeSelectionChangedListener(listener);
    graphicalOutline.removeSelectionChangedListener(listener);
  }

  public void setTextualOutlineSelection(ISelection selection) {
    if(textualOutline != null && textualOutline.getTree() != null)
      textualOutline.getTree().setSelection(selection);
  }
  
  @Override
  public void setSelection(ISelection selection) {
    
  }

  @Override
  public void setFocus() {
    if(showTextualOutline && textualOutline != null)
      textualOutline.setFocus();
    else
      graphicalOutline.setFocus();
  }
  
  public GraphicalOutlinePage getGraphicalOutline() {
    return graphicalOutline;
  }
  
  public IContentOutlinePage getTextualOutline() {
    return textualOutline;
  }
}
