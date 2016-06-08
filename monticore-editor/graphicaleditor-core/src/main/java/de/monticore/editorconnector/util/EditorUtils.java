package de.monticore.editorconnector.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.monticore.genericgraphics.GenericGraphicsEditor;
import de.se_rwth.langeditor.texteditor.TextEditorImpl;

public class EditorUtils {

  /**
   * Find the {@link IEditorReference IEditorReferences} for a certain
   * {@link IFile}.
   * 
   * @param f The {@link IFile}.
   * @return All {@link IEditorReference IEditorReferences} that are opened an
   *         having the {@link IFile file} as input.
   */
  public static List<IEditorReference> findEditorsForFile(IFile f) {
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow window = workbench == null ? null : workbench.getActiveWorkbenchWindow();
    IWorkbenchPage activePage = window == null ? null : window.getActivePage();
    
    if (activePage == null) {
      return Collections.emptyList();
    }
    List<IEditorReference> refs = new ArrayList<IEditorReference>();
    
    for (IEditorReference er : activePage.getEditorReferences()) {
      try {
        if (er.getEditorInput() instanceof IFileEditorInput) {
          IFile file = ((IFileEditorInput) er.getEditorInput()).getFile();
          if (f.equals(file)) {
            refs.add(er);
          }
        }
      }
      catch (PartInitException e) {
        e.printStackTrace();
      }
    }
    return refs;
  }
  
  /**
   * @param refs A list of {@link IEditorReference IEditorReferences} to search
   *          in.
   * @return The first {@link IEditorReference} in <code>refs</code> that is a
   *         {@link TextEditorImpl}.
   */
  public static TextEditorImpl getTextEditor(List<IEditorReference> refs) {
    for (IEditorReference ref : refs) {
      IEditorPart part = ref.getEditor(false);
      // only take the first one you find
      if (part instanceof TextEditorImpl) {
        return (TextEditorImpl) part;
      }
    }
    return null;
  }
  
  /**
   * @param refs A list of {@link IEditorReference IEditorReferences} to search
   *          in.
   * @return The first {@link IEditorReference} in <code>refs</code> that is a
   *         {@link GenericGraphicsEditor}.
   */
  public static GenericGraphicsEditor getGraphicsEditor(List<IEditorReference> refs) {
    for (IEditorReference ref : refs) {
      IEditorPart part = ref.getEditor(false);
      // only take the first one you find
      if (part instanceof GenericGraphicsEditor) {
        return (GenericGraphicsEditor) part;
      }
    }
    return null;
  }
  
  /**
   * For a given textual editor, this method finds the
   * corresponding graphical editor and vice versa.
   * @param editor
   * @return
   *    The corresponding textual/graphical editor for a given
   *    editor.
   */
  public static IEditorPart getCorrespondingEditor(IEditorPart editor) {
    if(editor.getEditorInput() instanceof IFileEditorInput) {
      IFile file = ((IFileEditorInput) editor.getEditorInput()).getFile();
      List<IEditorReference> refs = findEditorsForFile(file); 

      if(editor instanceof TextEditorImpl)
        return getGraphicsEditor(refs);
      else if(editor instanceof GenericGraphicsEditor)
        return getTextEditor(refs);
    }
    
    return null;
  }
  
  /**
   * Opens the graphical editor that is associated with a specified
   * textual editor.
   * @param editor  The textual editor
   * @return        The graphical editor that was opened
   */
  public static GenericGraphicsEditor openGraphicalEditor(TextEditorImpl editor, boolean activate) {
    IWorkbenchPage page = editor.getSite().getPage();
    
    IEditorDescriptor[] editors = 
        PlatformUI.getWorkbench().getEditorRegistry().getEditors(editor.getEditorInput().getName());

    for(IEditorDescriptor descr : editors) {
      // TODO MB Find better solution
      if(descr.getId().contains("Graphic")) {
        try {
          IEditorPart res = page.openEditor(editor.getEditorInput(), descr.getId(), activate, IWorkbenchPage.MATCH_NONE);
          
          if(res instanceof GenericGraphicsEditor)
            return (GenericGraphicsEditor)res;
          else
            page.closeEditor(res, false); // wrong editor, close it (this should never happen)
        } catch (PartInitException e) {
          e.printStackTrace();
        }
      }
    }
    
    return null;
  }
  
  public static TextEditorImpl openTextualEditor(GenericGraphicsEditor editor, boolean activate) {
    IWorkbenchPage page = editor.getSite().getPage();
    
    IEditorDescriptor[] editors = 
        PlatformUI.getWorkbench().getEditorRegistry().getEditors(editor.getEditorInputFile().getRawLocation().toString());
    
    for(IEditorDescriptor descr : editors) {
      // TODO MB Find better solution
      if(!descr.getId().contains("Graphic")) {
        try {
          IEditorPart res = page.openEditor(editor.getEditorInput(), descr.getId(), activate, IWorkbenchPage.MATCH_NONE);
          
          if(res instanceof TextEditorImpl)
            return (TextEditorImpl)res;
          else
            page.closeEditor(res, false); // wrong editor, close it (this should not happen)
        } catch (PartInitException e) {
          e.printStackTrace();
        }
      }
    }
    
    JOptionPane.showMessageDialog(null, "ERROR: no textual editor found");
    
    return null;
  }
  
  /**
   * Checks whether the corresponding (textual/graphical) editor for
   * a given editor is opened.
   * @param editor
   * @return  True if <b>editor</b> is a {@link GenericGraphicsEditor} or if <b>editor</b>
   *          is a {@link TextEditorImpl} and the corresponding {@link GenericGraphicsEditor}
   *          is opened. False otherwise. 
   */
  public static boolean isGraphicalEditorOpen(IEditorPart editor) {
    if(editor instanceof GenericGraphicsEditor)
      return true;
    else return (getCorrespondingEditor(editor) != null);
  }
}
