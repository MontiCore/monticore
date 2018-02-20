/* (c)  https://github.com/MontiCore/monticore */package de.se_rwth.langeditor.texteditor.errorhighlighting;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.texteditor.MarkerUtilities;

import com.google.common.collect.ImmutableMultimap;
import com.google.inject.Inject;

import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.langeditor.injection.TextEditorScoped;
import de.se_rwth.langeditor.modelstates.ModelState;
import de.se_rwth.langeditor.modelstates.ObservableModelStates;

@TextEditorScoped
public class ErrorHighlighter {
  
  @Inject
  public ErrorHighlighter(
      IStorage storage,
      ObservableModelStates observableModelStates) {
    observableModelStates.getModelStates().stream()
        .filter(modelState -> modelState.getStorage().equals(storage))
        .forEach(this::acceptModelState);
    observableModelStates.addStorageObserver(storage, this::acceptModelState);
  }
  
  public void acceptModelState(ModelState modelState) {
    IResource resource = null;
    if (modelState.getStorage() instanceof IResource) {
      resource = (IFile) modelState.getStorage();
      try {
        resource.deleteMarkers(IMarker.PROBLEM, true, 0);
      }
      catch (CoreException e) {
        Log.error("0xA1120 Error while deleting problem markers", e);
      }
    }
    displaySyntaxErrors(resource, modelState);
    displayAdditionalErrors(resource, modelState);
  }
  
  private void displaySyntaxErrors(IResource resource, ModelState modelState) {
    ImmutableMultimap<SourcePosition, String> syntaxErrors = modelState.getSyntaxErrors();
    for (Entry<SourcePosition, String> entry: syntaxErrors.entries()) {
      addMarker(resource, entry.getValue(), entry.getKey());
    }
  }
  
  private void displayAdditionalErrors(IResource resource, ModelState modelState) {
    for (Finding finding : modelState.getAdditionalErrors()) {
      addMarker(resource, finding);
    }
  }
  
  private void addMarker(IResource resource, Finding report) {
    Map<String, Object> theMap = new HashMap<>();
    theMap.put(IMarker.MESSAGE, report.getMsg());
    if (report.getSourcePosition().isPresent()) {
      theMap.put(IMarker.LINE_NUMBER, report.getSourcePosition().get().getLine());
    }
    else {
      theMap.put(IMarker.LINE_NUMBER, 1);
    }
    if (report.getType() == Finding.Type.ERROR) {
      theMap.put(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
    }
    else if (report.getType() == Finding.Type.WARNING) {
      theMap.put(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
    }
    try {
      if (resource != null) {
        MarkerUtilities.createMarker(resource, theMap, IMarker.PROBLEM);
      }
    }
    catch (CoreException e) {
      Log.error("0xA1121 Error while creating problem marker", e);
    }
  }
  
  private void addMarker(IResource resource, String message, SourcePosition pos) {
    Map<String, Object> theMap = new HashMap<>();
    theMap.put(IMarker.MESSAGE, message);
    theMap.put(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
    theMap.put(IMarker.LINE_NUMBER, pos.getLine());
    try {
      if (resource != null) {
        MarkerUtilities.createMarker(resource, theMap, IMarker.PROBLEM);
      }
    }
    catch (CoreException e) {
      Log.error("0xA1122 Error while creating problem marker", e);
    }
  }
}
