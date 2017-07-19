/*******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2017, MontiCore, All rights reserved.
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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import de.monticore.genericgraphics.controller.views.outline.CombinedGraphicsOutlinePage;

/**

 * This class acts as a command handler for the "Switch Outline" command.
 * <br><br>
 * 
 * @author Philipp Kehrbusch
 *
 */
public class SwitchOutlineCommand extends AbstractHandler {  
  
  /**
   * Toggles the current outline type of the textual and graphical editor
   * (graphical/textual outline).
   */
  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();

    IContentOutlinePage combinedGrOutline = null;
    combinedGrOutline = activeEditor.getAdapter(IContentOutlinePage.class);
 
    if (combinedGrOutline instanceof CombinedGraphicsOutlinePage) {
      ((CombinedGraphicsOutlinePage) combinedGrOutline).changeOutlineType();
    }

    return null;
  }
  
}
