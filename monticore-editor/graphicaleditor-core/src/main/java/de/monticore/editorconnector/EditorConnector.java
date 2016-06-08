package de.monticore.editorconnector;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import de.monticore.editorconnector.util.EditorUtils;
import de.monticore.genericgraphics.GenericGraphicsEditor;
import de.monticore.genericgraphics.GenericGraphicsViewer;
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
public class EditorConnector extends AbstractHandler implements BundleActivator {

  private static EditorConnector INSTANCE;
  
  private final PartListener partListener = new PartListener();
  private Map<IEditorPart, Boolean> editorOutlineStates = new HashMap<IEditorPart, Boolean>();
  
  /**
   * Adds a PartListener to the workbench in order to recognize 
   * editors being opened and closed.
   */
  @Override
  public void start(BundleContext context) throws Exception {

    // needs to run in the UI thread, otherwise getWorkbench() returns null
    // at this point
    final IWorkbench workbench = PlatformUI.getWorkbench();
    workbench.getDisplay().asyncExec(new Runnable() {
      public void run() {
        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        IWorkbenchPage page = (window != null) ? window.getActivePage() : null;
        
        if (page != null) {          
          page.addPartListener(partListener);
        }
      }
    });
    
    INSTANCE = this;
  }

  /**
   * @return A singleton instance of {@link EditorConnector}.
   */
  public static EditorConnector getInstance() {
    return INSTANCE;
  }
  
  @Override
  public void stop(BundleContext context) throws Exception {
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow window = (workbench != null) ? workbench.getActiveWorkbenchWindow() : null;
    IWorkbenchPage page = (window != null) ? window.getActivePage() : null;
      
    if(page != null)
      page.removePartListener(partListener);
  }

  class PartListener implements IPartListener {
    @Override
    public void partOpened(final IWorkbenchPart part) {
    }

    @Override
    public void partClosed(IWorkbenchPart part) {
      if(part instanceof TextEditorImpl) {
        // Close corresponding graphical editor (don't save, the user will be asked if the textual editor is dirty).
        TextEditorImpl txtEditor = (TextEditorImpl)part;
        IEditorPart grEditor = EditorUtils.getCorrespondingEditor(txtEditor);
        
        boolean save = false;
        if(grEditor != null && !txtEditor.isDirty() && grEditor.isDirty()) {
          save = MessageDialog.openQuestion(grEditor.getEditorSite().getShell(), 
              "Save Resource", "'" +  grEditor.getTitle() + "' has been modified. Save changes?");
        }
        part.getSite().getPage().closeEditor(grEditor, save);
      }
    }
    
    @Override
    public void partActivated(IWorkbenchPart part) {
    }
    @Override
    public void partBroughtToTop(IWorkbenchPart part) {
    }
    @Override
    public void partDeactivated(IWorkbenchPart part) {
    }
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
  
  /**
   * Returns the {@link GenericGraphicsViewer} that belongs to an {@link TextEditorImpl}
   * or {@link GenericGraphicsEditor}
   * @param editor  The {@link TextEditorImpl} or {@link GenericGraphicsEditor} to find
   *                the {@link GenericGraphicsViewer} for.
   * @return        The {@link GenericGraphicsViewer} or null if no viewer could be found.
   */
  public GenericGraphicsViewer getViewerForEditor(IEditorPart editor) {
    if(editor instanceof GenericGraphicsEditor) {
      return (GenericGraphicsViewer) ((GenericGraphicsEditor)editor).getAdapter(GraphicalViewer.class);
    }
    else if(editor instanceof TextEditorImpl) {
      GenericGraphicsEditor gEditor = (GenericGraphicsEditor) EditorUtils.getCorrespondingEditor(editor);
      if (gEditor != null) {
        return (GenericGraphicsViewer) gEditor.getAdapter(GraphicalViewer.class);
      }
    }    
    return null;
  }
}
