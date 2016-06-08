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
package de.monticore.editorconnector.menus;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;

import de.monticore.editorconnector.util.EditorUtils;
import de.monticore.genericgraphics.GenericGraphicsEditor;
import de.se_rwth.langeditor.texteditor.TextEditorImpl;

public class ShowGrViewMenuItem {

  /**
   * Factory method that generates a {@link MenuItem} for showing/hiding the graphical
   * view.
   * 
   * @return  The generated {@link MenuItem}.
   */
  public static MenuItem createMenuItem(Menu menu, int index, final IWorkbenchPage page) {
    assert(page != null);
    assert(menu != null);
    MenuItem item = new MenuItem(menu, SWT.CHECK, index);

    item.setText("Show Graphical View");
    
    // add check mark in front of item if graphical editor is opened
       IEditorPart graphicalEditor = EditorUtils.getCorrespondingEditor(page.getActiveEditor());
       if(graphicalEditor != null)
         item.setSelection(true);
       
       item.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
           
           IEditorPart activeE = page.getActiveEditor();
           IEditorPart graphicalEditor;
           
           if(activeE instanceof TextEditorImpl)
             graphicalEditor = EditorUtils.getCorrespondingEditor(page.getActiveEditor());
           else if(activeE instanceof GenericGraphicsEditor)
             graphicalEditor = activeE;
           else
             return;

           // If graphical view is opened, close it - open it otherwise. 
           if(page != null && graphicalEditor != null) {
             page.closeEditor(graphicalEditor, false);
           }
           else {
             EditorUtils.openGraphicalEditor((TextEditorImpl)page.getActiveEditor(), false);
           }
         }
       });
    
    return item;
  }
}
