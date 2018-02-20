/* (c)  https://github.com/MontiCore/monticore */package de.se_rwth.langeditor.modelstates;

import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import org.antlr.v4.runtime.ParserRuleContext;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;

import com.google.common.collect.ImmutableMultimap;

import de.monticore.ast.ASTNode;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.langeditor.language.Language;

public final class ModelState {
  
  static class ModelStateBuilder {
    
    private Optional<IStorage> storage = Optional.empty();
    
    private Optional<IProject> project = Optional.empty();
    
    private Optional<String> content = Optional.empty();
    
    private Optional<Language> language = Optional.empty();
    
    private Optional<ASTNode> rootNode = Optional.empty();
    
    private Optional<ParserRuleContext> rootContext = Optional.empty();
    
    private Optional<ImmutableMultimap<SourcePosition, String>> syntaxErrors = Optional.empty();
    
    private ModelState lastModelState;
    
    public Optional<IStorage> getStorage() {
      return storage;
    }
    
    public Optional<IProject> getProject() {
      return project;
    }
    
    public Optional<String> getContent() {
      return content;
    }
    
    public Optional<Language> getLanguage() {
      return language;
    }
    
    public Optional<ASTNode> getRootNode() {
      return rootNode;
    }
    
    public Optional<ImmutableMultimap<SourcePosition, String>> getSyntaxErrors() {
      return syntaxErrors;
    }
    
    public Optional<ModelState> getLastModelState() {
      return Optional.ofNullable(lastModelState);
    }
    
    public ModelStateBuilder setStorage(IStorage storage) {
      checkState(!this.storage.isPresent());
      this.storage = Optional.of(storage);
      return this;
    }
    
    public ModelStateBuilder setProject(IProject project) {
      checkState(!this.project.isPresent());
      this.project = Optional.of(project);
      return this;
    }
    
    public ModelStateBuilder setContent(String content) {
      checkState(!this.content.isPresent());
      this.content = Optional.of(content);
      return this;
    }
    
    public ModelStateBuilder setLanguage(Language language) {
      checkState(!this.language.isPresent());
      this.language = Optional.of(language);
      return this;
    }
    
    public ModelStateBuilder setRootNode(ASTNode rootNode) {
      checkState(!this.rootNode.isPresent());
      this.rootNode = Optional.of(rootNode);
      return this;
    }
    
    public ModelStateBuilder setRootContext(ParserRuleContext rootContext) {
      checkState(!this.rootContext.isPresent());
      this.rootContext = Optional.of(rootContext);
      return this;
    }
    
    public ModelStateBuilder setSyntaxErrors(ImmutableMultimap<SourcePosition, String> syntaxErrors) {
      checkState(!this.syntaxErrors.isPresent());
      this.syntaxErrors = Optional.of(syntaxErrors);
      return this;
    }
    
    public ModelStateBuilder setLastModelState(ModelState lastModelState) {
      checkState(this.lastModelState == null);
      this.lastModelState = lastModelState;
      return this;
    }
    
    public ModelState build() {
      return new ModelState(storage.get(), project.get(), content.get(), language.get(),
          rootNode.get(), rootContext.get(), syntaxErrors.get(), lastModelState);
    }
  }
  
  private final IStorage storage;
  
  private final IProject project;
  
  private final String content;
  
  private final Language language;
  
  private final ASTNode rootNode;
  
  private final ParserRuleContext rootContext;
  
  private final ImmutableMultimap<SourcePosition, String> syntaxErrors;
  
  private final List<Finding> additionalErrors = new ArrayList<>();
  
  private final Optional<ModelState> lastLegalState;
  
  private ModelState(
      IStorage storage,
      IProject project,
      String content,
      Language language,
      ASTNode rootNode,
      ParserRuleContext rootContext,
      ImmutableMultimap<SourcePosition, String> syntaxErrors,
      @Nullable ModelState lastState) {
    this.storage = storage;
    this.project = project;
    this.content = content;
    this.language = language;
    this.rootNode = rootNode;
    this.rootContext = rootContext;
    this.syntaxErrors = syntaxErrors;
    this.lastLegalState = syntaxErrors.isEmpty() ? Optional.empty() :
        Optional.ofNullable(lastState);
  }
  
  public IStorage getStorage() {
    return storage;
  }
  
  public IProject getProject() {
    return project;
  }
  
  public String getContent() {
    return content;
  }
  
  public Optional<String> getLastLegalContent() {
    return isLegal() ? Optional.of(getContent()) :
        lastLegalState.flatMap(ModelState::getLastLegalContent);
  }
  
  public Language getLanguage() {
    return language;
  }
  
  public ASTNode getRootNode() {
    return rootNode;
  }
  
  public Optional<ASTNode> getLastLegalRootNode() {
    return isLegal() ? Optional.of(getRootNode()) :
      lastLegalState.flatMap(ModelState::getLastLegalRootNode);
  }
  
  public ParserRuleContext getRootContext() {
    return rootContext;
  }
  
  public Optional<ParserRuleContext> getLastLegalRootContext() {
    return isLegal() ? Optional.of(getRootContext()) :
      lastLegalState.flatMap(ModelState::getLastLegalRootContext);
  }
  
  public ImmutableMultimap<SourcePosition, String> getSyntaxErrors() {
    return syntaxErrors;
  }
  
  public List<Finding> getAdditionalErrors() {
    return additionalErrors;
  }
  
  public void addAdditionalError(Finding finding) {
    additionalErrors.add(finding);
  }
  
  public boolean isLegal() {
    return syntaxErrors.isEmpty();
  }
  
  public Optional<ModelState> getLastLegalState(){
    return lastLegalState;
  }
  
  @Override
  public String toString() {
    return getStorage().toString();
  }
}
