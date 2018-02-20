/* (c)  https://github.com/MontiCore/monticore */package de.se_rwth.langeditor.injection;

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.name.Named;

import de.se_rwth.langeditor.global.LanguageLocator;
import de.se_rwth.langeditor.language.Language;
import de.se_rwth.langeditor.language.OutlineElementSet;
import de.se_rwth.langeditor.language.ParserConfig;
import de.se_rwth.langeditor.texteditor.contentassist.ContentAssistProcessorImpl;
import de.se_rwth.langeditor.texteditor.hyperlinks.HyperlinkDetectorImpl;
import de.se_rwth.langeditor.texteditor.outline.LabelProviderImpl;
import de.se_rwth.langeditor.texteditor.outline.OutlinePage;
import de.se_rwth.langeditor.texteditor.outline.TreeContentProviderImpl;
import de.se_rwth.langeditor.texteditor.syntaxhighlighting.SyntaxHighlightScanner;
import de.se_rwth.langeditor.util.Misc;
import de.se_rwth.langeditor.util.ResourceLocator;

public class GuiceConfig extends AbstractModule {
  
  private final ScopeImpl projectScope = new ScopeImpl();
  
  private final ScopeImpl textEditorScope = new ScopeImpl();
  
  private IProject project;
  
  private ITextEditor textEditor;
  
  @Override
  protected void configure() {
    bindScope(ProjectScoped.class, projectScope);
    bindScope(TextEditorScoped.class, textEditorScope);
    
    ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(
        Runtime.getRuntime().availableProcessors());
    
    bind(ScheduledExecutorService.class).toInstance(threadPool);
    bind(ExecutorService.class).toInstance(threadPool);
    bind(Executor.class).toInstance(threadPool);
    
    bind(IContentOutlinePage.class).to(OutlinePage.class);
    bind(ITokenScanner.class).to(SyntaxHighlightScanner.class);
    bind(ILabelProvider.class).to(LabelProviderImpl.class);
    bind(ITreeContentProvider.class).to(TreeContentProviderImpl.class);
    bind(IHyperlinkDetector.class).to(HyperlinkDetectorImpl.class);
    bind(IContentAssistProcessor.class).to(ContentAssistProcessorImpl.class);
  }
  
  public void enter(IProject project) {
    this.project = project;
    projectScope.enter(project);
  }
  
  public void enter(ITextEditor textEditor) {
    this.textEditor = textEditor;
    textEditorScope.enter(textEditor);
    enter(Misc.getProject(Misc.getStorage(textEditor.getEditorInput())));
  }
  
  public void leave() {
    projectScope.leave();
    textEditorScope.leave();
  }
  
  @Override
  public <T> Provider<T> getProvider(Key<T> key) {
    System.err.println("Serving provider");
    Optional<IProject> currentProject = Optional.ofNullable(project);
    Optional<ITextEditor> currentTextEditor = Optional.ofNullable(textEditor);
    return () -> {
      currentProject.ifPresent(this::enter);
      currentTextEditor.ifPresent(this::enter);
      T object = super.getProvider(key).get();
      leave();
      return object;
    };
  }
  
  @Override
  public <T> Provider<T> getProvider(Class<T> type) {
    System.err.println("Serving provider");
    Optional<IProject> currentProject = Optional.ofNullable(project);
    Optional<ITextEditor> currentTextEditor = Optional.ofNullable(textEditor);
    return () -> {
      currentProject.ifPresent(this::enter);
      currentTextEditor.ifPresent(this::enter);
      T object = super.getProvider(type).get();
      leave();
      return object;
    };
  }
  
  @Provides
  @Named("modelpath")
  private ImmutableList<Path> getModelPath() {
    return ResourceLocator.assembleModelPath(project);
  }
  
  @Provides
  private IProject getProject() {
    return project;
  }
  
  @Provides
  private Language getLanguage(LanguageLocator locator) {
    return locator.findLanguage(getStorage().getFullPath().getFileExtension()).get();
  }
  
  @Provides
  private ITextEditor getTextEditor() {
    return textEditor;
  }
  
  @Provides
  private IDocument getDocument() {
    ITextEditor textEditor = getTextEditor();
    return textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
  }
  
  @Provides
  private IStorage getStorage() {
    return Misc.getStorage(textEditor.getEditorInput());
  }
  
  @Provides
  private IAnnotationModel getAnnotationModel() {
    return getTextEditor().getDocumentProvider().getAnnotationModel(
        getTextEditor().getEditorInput());
  }
  
  @Provides
  private ParserConfig<?> getParserConfig(Language language) {
    return language.getParserConfig();
  }
  
  @Provides
  private OutlineElementSet getOutlineElements(Language language) {
    return language.getOutlineElementSet();
  }
  
}
