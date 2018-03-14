/* (c)  https://github.com/MontiCore/monticore */package de.se_rwth.langeditor.texteditor;

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
