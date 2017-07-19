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
package de.se_rwth.langeditor.texteditor;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import com.google.inject.Inject;

import de.se_rwth.langeditor.injection.TextEditorScoped;
import de.se_rwth.langeditor.modelstates.ModelStateAssembler;
import de.se_rwth.langeditor.util.Misc;

@TextEditorScoped
public class TextEditorInputDistributer {
  
  private final ModelStateAssembler assembler;
  
  @Inject
  public TextEditorInputDistributer(ModelStateAssembler assembler) {
    this.assembler = assembler;
  }
  
  public void addTextEditor(ITextEditor textEditor) {
    try {
      IDocument document = textEditor.getDocumentProvider()
          .getDocument(textEditor.getEditorInput());
      IStorage storage = ((IStorageEditorInput) textEditor.getEditorInput()).getStorage();
      IProject project = Misc.getProject(storage);
      assembler.scheduleRebuild(storage, project, document.get());
      document.addDocumentListener(new DocumentListenerImpl(storage, project));
    }
    catch (CoreException e) {
      throw new RuntimeException(e);
    }
  }
  
  private class DocumentListenerImpl implements IDocumentListener {
    
    private final IStorage storage;
    
    private final IProject project;
    
    private DocumentListenerImpl(IStorage storage, IProject project) {
      this.storage = storage;
      this.project = project;
    }
    
    @Override
    public void documentAboutToBeChanged(DocumentEvent event) {
      // no op
    }
    
    @Override
    public void documentChanged(DocumentEvent event) {
      assembler.scheduleRebuild(storage, project, event.getDocument().get());
    }
  }
  
}
