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

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.DefaultAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.texteditor.ITextEditor;

import com.google.inject.Inject;

import de.se_rwth.langeditor.injection.DIService;
import de.se_rwth.langeditor.injection.TextEditorScoped;

@TextEditorScoped
public class SourceViewerConfigurationImpl extends SourceViewerConfiguration {
  
  private final ITextEditor textEditor;
  
  private final IHyperlinkDetector hyperlinkDetector;
  
  private final IContentAssistProcessor contentAssistProcessor;
  
  @Inject
  public SourceViewerConfigurationImpl(
      ITextEditor textEditor,
      IHyperlinkDetector hyperlinkDetector,
      IContentAssistProcessor contentAssistProcessor) {
    this.textEditor = textEditor;
    this.hyperlinkDetector = hyperlinkDetector;
    this.contentAssistProcessor = contentAssistProcessor;
  }
  
  @Override
  public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
    return new String[] {IDocument.DEFAULT_CONTENT_TYPE};
  }
  
  @Override
  public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
    ITokenScanner scanner = DIService.getInstance(textEditor, ITokenScanner.class);
    DefaultDamagerRepairer repairer = new DefaultDamagerRepairer(scanner);
    PresentationReconciler reconciler = new PresentationReconciler();
    reconciler.setDamager(repairer, IDocument.DEFAULT_CONTENT_TYPE);
    reconciler.setRepairer(repairer, IDocument.DEFAULT_CONTENT_TYPE);
    return reconciler;
  }
  
  @Override
  public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
    return new DefaultAnnotationHover();
  }
  
  @Override
  public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
    return new IHyperlinkDetector[] { hyperlinkDetector };
  }
  
  @Override
  public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
    ContentAssistant contentAssistant = new ContentAssistant();
    contentAssistant.setContentAssistProcessor(contentAssistProcessor, IDocument.DEFAULT_CONTENT_TYPE);
    return contentAssistant;
  }
}
