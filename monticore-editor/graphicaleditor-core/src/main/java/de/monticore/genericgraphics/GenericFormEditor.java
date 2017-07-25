/*******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
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

package de.monticore.genericgraphics;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import de.monticore.genericgraphics.GenericGraphicsEditor;
import de.monticore.genericgraphics.GenericGraphicsViewer;
import de.monticore.genericgraphics.controller.views.outline.CombinedGraphicsOutlinePage;
import de.monticore.genericgraphics.controller.views.outline.GraphicalOutlinePage;
import de.se_rwth.langeditor.texteditor.TextEditorImpl;
import de.se_rwth.langeditor.texteditor.outline.OutlinePage;

public class GenericFormEditor extends FormEditor {
  
  private CombinedGraphicsOutlinePage outlinePage;
  
  private TextEditorImpl textEditor;

  private GenericGraphicsEditor graphicalEditor;
  
  /**
   * @see de.se_rwth.langeditor.texteditor.TextEditorImpl#getAdapter(java.lang.Class)
   */
  @Override
  public Object getAdapter(Class adapter) {
    if (adapter == IContentOutlinePage.class || adapter == CombinedGraphicsOutlinePage.class) {
      if (outlinePage == null) {
        IFile file = ((IFileEditorInput) getEditorInput()).getFile();
        GenericGraphicsViewer viewer = graphicalEditor.getGraphicalViewer();
        GraphicalOutlinePage graphicalOutline = new GraphicalOutlinePage(viewer, file);
        OutlinePage textualOutline = (OutlinePage) textEditor.getAdapter(adapter);
        outlinePage = new CombinedGraphicsOutlinePage(graphicalOutline, textualOutline);
      }
      return outlinePage;
    }
    return super.getAdapter(adapter);
  }
  
  /**
   * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
   */
  @Override
  protected void addPages() {
    textEditor = new TextEditorImpl();
    graphicalEditor = new GenericGraphicsEditor(textEditor);

    try {
      int textEditorIndex = addPage((IEditorPart) textEditor, getEditorInput());
      setPageText(textEditorIndex, "TextEditor");
      
      int graphicalEditorIndex = addPage(graphicalEditor, getEditorInput());
      setPageText(graphicalEditorIndex, "GraphicalView");
    }
    catch (PartInitException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
  }
  
  /**
   * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
   */
  @Override
  public void doSave(IProgressMonitor monitor) {
    textEditor.doSave(monitor);
    graphicalEditor.doSave(monitor);
  }
  
  /**
   * @see org.eclipse.ui.part.EditorPart#doSaveAs()
   */
  @Override
  public void doSaveAs() {
    // It is not allowed
  }
  
  /**
   * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
   */
  @Override
  public boolean isSaveAsAllowed() {
    return false;
  }
  
  /**
   * @return the outlinePage
   */
  public CombinedGraphicsOutlinePage getOutlinePage() {
    return this.outlinePage;
  }

  
  /**
   * @return the textEditor
   */
  public TextEditorImpl getTextEditor() {
    return this.textEditor;
  }

  
  /**
   * @return the graphicalEditor
   */
  public GenericGraphicsEditor getGraphicalEditor() {
    return this.graphicalEditor;
  }
  
  
}
