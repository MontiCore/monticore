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

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.google.inject.Inject;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.langeditor.injection.DIService;
import de.se_rwth.langeditor.injection.TextEditorScoped;
import de.se_rwth.langeditor.modelstates.ObservableModelStates;
import de.se_rwth.langeditor.texteditor.errorhighlighting.ErrorHighlighter;
import de.se_rwth.langeditor.util.Misc;

@TextEditorScoped
public class TextEditorImpl extends TextEditor {
  
  private IContentOutlinePage contentOutlinePage;
  
  private ObservableModelStates observableModelStates;
  
  @Inject
  private void injectMembers(SourceViewerConfigurationImpl sourceViewerConfiguration,
      IContentOutlinePage contentOutlinePage, TextEditorInputDistributer distributer,
      ObservableModelStates observableModelStates, ErrorHighlighter errorHighlighter) {
    setSourceViewerConfiguration(sourceViewerConfiguration);
    this.contentOutlinePage = contentOutlinePage;
    this.observableModelStates = observableModelStates;
    distributer.addTextEditor(this);
  }
  
  @Override
  public void init(IEditorSite site, IEditorInput input) throws PartInitException {
    super.init(site, input);
    Log.enableFailQuick(false);
    DIService.injectMembers(this, this);
  }
  
  @Override
  @SuppressWarnings("rawtypes")
  public Object getAdapter(Class adapter) {
    if (IContentOutlinePage.class.equals(adapter)) {
      return contentOutlinePage;
    }
    return super.getAdapter(adapter);
  }
  
  @Override
  public void dispose() {
    observableModelStates.removeStorageObservers(Misc.getStorage(getEditorInput()));
    super.dispose();
  }
}
