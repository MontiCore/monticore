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

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import de.monticore.editorconnector.util.ExtensionRegistryUtils;
import de.se_rwth.langeditor.texteditor.TextEditorImpl;

public class TextEditorContextContribution extends ContributionItem {

  @Override
  public void fill(Menu menu, int index) {
    
    /* 
     * Only contribute to context menu if current editor is an
     * TextEditorImpl and if there is a GenericGraphicsEditor
     * registered for the file extension of the text editor's
     * input file.
     */
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow window = (workbench != null) ? workbench.getActiveWorkbenchWindow() : null;
    final IWorkbenchPage page = (window != null) ? window.getActivePage() : null;
    IEditorPart editor = (page != null) ? page.getActiveEditor() : null;
    
    if(editor instanceof TextEditorImpl && ExtensionRegistryUtils.hasGraphicalViewer((TextEditorImpl)editor)) {
      ShowGrViewMenuItem.createMenuItem(menu, index, page);
    }
  }
}
