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
