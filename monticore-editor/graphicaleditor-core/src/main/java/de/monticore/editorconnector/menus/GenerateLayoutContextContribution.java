package de.monticore.editorconnector.menus;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import de.monticore.editorconnector.EditorConnector;
import de.monticore.genericgraphics.GenericGraphicsEditor;
import de.monticore.genericgraphics.GenericGraphicsViewer;
import de.se_rwth.langeditor.texteditor.TextEditorImpl;

public class GenerateLayoutContextContribution extends ContributionItem {

  @Override
  public void fill(Menu menu, int index) {
//    IWorkbench workbench = PlatformUI.getWorkbench();
//    IWorkbenchWindow window = (workbench != null) ? workbench.getActiveWorkbenchWindow() : null;
//    final IWorkbenchPage page = (window != null) ? window.getActivePage() : null;
//    IEditorPart editor = (page != null) ? page.getActiveEditor() : null;
//    
//    if((editor instanceof TextEditorImpl || editor instanceof GenericGraphicsEditor) && EditorConnector.getInstance().isGraphicalOutlineShown(editor)) {
//      MenuItem item = new MenuItem(menu, SWT.PUSH, index);
//      item.setText("Generate Layout");
//      
//      item.addSelectionListener(new SelectionAdapter() {
//        @Override
//        public void widgetSelected(SelectionEvent event) {
//          IWorkbench workbench = PlatformUI.getWorkbench();
//          IWorkbenchWindow window = (workbench != null) ? workbench.getActiveWorkbenchWindow() : null;
//          final IWorkbenchPage page = (window != null) ? window.getActivePage() : null;
//          IEditorPart activeE = (page != null) ? page.getActiveEditor() : null;
//          
//          GenericGraphicsViewer viewer = EditorConnector.getInstance().getViewerForEditor(activeE);
//          if(viewer != null)
//            viewer.applyGeneratedLayout();
//        }
//      });
//    }
  }
}
