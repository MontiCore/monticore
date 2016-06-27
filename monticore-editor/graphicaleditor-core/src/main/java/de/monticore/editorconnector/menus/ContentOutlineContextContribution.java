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

import de.monticore.genericgraphics.GenericGraphicsEditor;
import de.se_rwth.langeditor.texteditor.TextEditorImpl;

public class ContentOutlineContextContribution extends ContributionItem {

  @Override
  public void fill(Menu menu, int index) {
    /* Contribute to menu if the Outline is showing a GraphicalOutlinePage
     * (graphical outline) or a TheContentOutlinePage (textual outline).
     */
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow window = (workbench != null) ? workbench.getActiveWorkbenchWindow() : null;
    final IWorkbenchPage page = (window != null) ? window.getActivePage() : null;
    IEditorPart activeE = (page != null) ? page.getActiveEditor() : null;
    
    if(activeE instanceof GenericGraphicsEditor || activeE instanceof TextEditorImpl) {
      ShowGrViewMenuItem.createMenuItem(menu, index, page);
    }
  }
}
