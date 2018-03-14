/* (c)  https://github.com/MontiCore/monticore */package de.se_rwth.langeditor.texteditor;

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
