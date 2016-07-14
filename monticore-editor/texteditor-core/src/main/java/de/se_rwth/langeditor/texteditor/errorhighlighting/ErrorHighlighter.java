/*******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, 2016, MontiCore, All rights reserved.
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
package de.se_rwth.langeditor.texteditor.errorhighlighting;

import java.util.Set;

import javax.annotation.Nullable;

import org.antlr.v4.runtime.misc.Interval;
import org.eclipse.core.resources.IStorage;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.swt.widgets.Display;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Finding.Type;
import de.se_rwth.langeditor.injection.TextEditorScoped;
import de.se_rwth.langeditor.modelstates.ModelState;
import de.se_rwth.langeditor.modelstates.ObservableModelStates;
import de.se_rwth.langeditor.util.Misc;

@TextEditorScoped
public class ErrorHighlighter {
  
  private final IAnnotationModel annotationModel;
  
  private final Set<Annotation> annotations = Sets.newConcurrentHashSet();
  
  @Inject
  public ErrorHighlighter(
      @Nullable IAnnotationModel annotationModel,
      IStorage storage,
      ObservableModelStates observableModelStates) {
    this.annotationModel = annotationModel;
    if (annotationModel != null) {
      observableModelStates.getModelStates().stream()
          .filter(modelState -> modelState.getStorage().equals(storage))
          .forEach(this::acceptModelState);
      observableModelStates.addStorageObserver(storage, this::acceptModelState);
    }
  }
  
  public void acceptModelState(ModelState modelState) {
    for (Annotation annotation : annotations) {
      annotationModel.removeAnnotation(annotation);
      annotations.remove(annotation);
    }
    displaySyntaxErrors(modelState);
    displayAdditionalErrors(modelState);
  }
  
  private void displaySyntaxErrors(ModelState modelState) {
    ImmutableMultimap<Interval, String> syntaxErrors = modelState.getSyntaxErrors();
    for (Interval interval : syntaxErrors.keys()) {
      for (String message : syntaxErrors.get(interval)) {
        Display.getDefault().asyncExec(() -> displayError(interval, Type.ERROR, message));
      }
    }
  }
  
  private void displayAdditionalErrors(ModelState modelState) {
    for (Finding finding : modelState.getAdditionalErrors()) {
      SourcePosition position = finding.getSourcePosition().orElse(SourcePosition.getDefaultSourcePosition());
      int index = Misc.convertLineAndColumnToLinearIndex(
          modelState.getContent(), position.getLine(), position.getColumn());
      Interval interval = new Interval(index, index);
      Display.getDefault().asyncExec(() -> displayError(interval, finding.getType(), finding.getMsg()));
    }
  }
  
  private void displayError(Interval interval, Type type, String message) {
    int startIndex = interval.a;
    int stopIndex = interval.b + 1;
    String annoType = (type == Type.ERROR) ? "org.eclipse.ui.workbench.texteditor.error" : "org.eclipse.ui.workbench.texteditor.warning";
    Annotation annotation = new Annotation(annoType, false, message);
    annotations.add(annotation);
    annotationModel.addAnnotation(annotation, new Position(startIndex, stopIndex - startIndex));
  }
}
