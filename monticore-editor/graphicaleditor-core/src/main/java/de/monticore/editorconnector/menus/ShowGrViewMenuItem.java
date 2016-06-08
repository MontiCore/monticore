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
