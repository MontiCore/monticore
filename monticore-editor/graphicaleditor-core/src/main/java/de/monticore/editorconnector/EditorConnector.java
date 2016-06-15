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
package de.monticore.editorconnector;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import de.monticore.editorconnector.util.EditorUtils;
import de.monticore.genericgraphics.GenericGraphicsEditor;
import de.monticore.genericgraphics.controller.views.outline.CombinedGraphicsOutlinePage;
import de.monticore.genericgraphics.controller.views.outline.GraphicalOutlinePage;
import de.se_rwth.langeditor.texteditor.TextEditorImpl;

/**
 * Acts as a connection between the textual and graphical editor thus
 * avoiding direct dependencies between these two.
 * 
 * The following events are handled: <br>
 * <ul>
 *  <li>Textual editor opened: If outline view is shown, create a new {@link GraphicalOutlinePage} and assign it to 
 *      the textual editor's {@link CombinedTextOutlinePage}. See {@link #initTextEditor(TextEditorImpl)} </li>
 *  <li>Graphical editor opened: If outline view is shown, assign the graphical editor's {@code GraphicalOutlinePage}
 *      to the textual editor's {@code CombinedTextOutlinepage}. See {@link #initGraphicsEditor(GenericGraphicsEditor)} </li>
 *  <li>Textual editor closed: Close corresponding graphical editor. </li>
 *  <li>Graphical editor closed: If outline view is show, create a new {@code GraphicalOutlinePage} and assign it to
 *      the textual editor's {@code CombinedTextOutlinePage}</li>
 *  <li>Outline view opened: Call {@link #initOpenEditors(IWorkbenchPage)} which in turn calls 
 *      {@code initTextEditor(TextEditorImpl)} and {@code initGraphicsEditor(GenericGraphicsEditor)} on all open
 *      editors. </li>
 * </ul>
 * 
 * This class also acts as a command handler for the "Switch Outline" command.
 * <br><br>
 * 
 * @author Philipp Kehrbusch
 *
 */
public class EditorConnector extends AbstractHandler {

  private static EditorConnector INSTANCE;
  
  private Map<IEditorPart, Boolean> editorOutlineStates = new HashMap<IEditorPart, Boolean>();
  

  /**
   * @return A singleton instance of {@link EditorConnector}.
   */
  public static EditorConnector getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new EditorConnector();
    }   
    return INSTANCE;
  }
  
  
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
  
 
  /**
   * Returns the state of an editor's outline (textual/graphical outline being displayed).
   * @param editor
   * @return  True if the textual outline is being displayed, false if the graphical outline
   *          is being displayed or if no (textual/graphical) outline view is shown for this editor.
   */
  public boolean getOutlineState(IEditorPart editor) {
    if(editorOutlineStates.containsKey(editor))
      return editorOutlineStates.get(editor);
    
    IEditorPart otherE = EditorUtils.getCorrespondingEditor(editor);
    if(editorOutlineStates.containsKey(otherE))
      return editorOutlineStates.get(otherE);
    
    return false;
  }
  
  public boolean isGraphicalOutlineShown(IEditorPart editor) {
    if(editorOutlineStates.containsKey(editor))
      return !editorOutlineStates.get(editor);
    

    IEditorPart otherE = EditorUtils.getCorrespondingEditor(editor);
    if(editorOutlineStates.containsKey(otherE))
      return !editorOutlineStates.get(otherE);

    return false;
  }
  
}
