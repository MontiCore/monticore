/*******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, 2016, MontiCore, All rights reserved.
 *  
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.monticore.genericgraphics.controller.views.outline;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import de.monticore.editorconnector.menus.OutlineMenuContribution;
import de.monticore.genericgraphics.GenericGraphicsEditor;
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

  private IWorkbenchPart outlinePart;
  
  public CombinedGraphicsOutlinePage(GraphicalOutlinePage graphicalOutline) {
    this.graphicalOutline = graphicalOutline;
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
    getSite().registerContextMenu("outlineContext", manager, this);
        
    // add menu for selecting default outline type
    manager.add(new OutlineMenuContribution(false));
    manager.add(new OutlineMenuContribution(true));
    
    // initialize outline type
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
        if(activeE instanceof GenericGraphicsEditor) {
          getSite().setSelectionProvider(textualOutline.getTree());
        }
      }
      else if(!showTextualOutline) {
        rootControl.showPage(graphicalControl);
        
        if(activeE instanceof GenericGraphicsEditor) {
          getSite().setSelectionProvider(graphicalOutline.getViewer());
        }
      }
    }
  }
  
  /**
   * Assigns the textual outline to this combined outline. The outline will
   * be stored and initialized when createControl(Composite) has been called.
   * @param outline IContentOutlinePage to show as the textual outline
   */
  public void setTextualOutline(OutlinePage outline) {
    textualOutline = outline;
    
    if(rootControl != null)
      initTextualOutline();
  }
 
  /**
   * Initializes the textual outline. This includes assigning an IPageSite,
   * creating the control and creating the context menu.
   */
  private void initTextualOutline() {
    if(textualOutline != null && rootControl != null) {
      // Assign PageSite to avoid NullPointerExceptions!
      if(textualOutline instanceof Page)
        ((Page)textualOutline).init(getSite());
      
      textualOutline.createControl(rootControl);
      textualControl = textualOutline.getControl();
      
      // create context menu for textual outline
      MenuManager managerTxt = new MenuManager();
      Menu menuTxt =  managerTxt.createContextMenu(textualControl);
      textualControl.setMenu(menuTxt);
      getSite().registerContextMenu("outlineContext", managerTxt, this);
      
//  TODO fix me
    /* Somehow, if the outline was opened after the graphical editor has been opened, outline selection changes 
     * are only recognized by the GraphicsTextSelectionListener if the textual editor has been activated once
     * after the textual outline has been displayed.
     */
      textualOutline.getTree().addSelectionChangedListener(new ISelectionChangedListener() {
        @Override
        public void selectionChanged(SelectionChangedEvent event) {
          if(outlinePart == null)
            outlinePart = getSite().getPage().findView("org.eclipse.ui.views.ContentOutline"); 
          graphicalOutline.getViewer().getSelectionListener().selectionChanged(outlinePart, event.getSelection());
        }
      });
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
