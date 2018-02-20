/* (c)  https://github.com/MontiCore/monticore */package de.monticore.editorconnector.menus;

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
