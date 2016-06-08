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
