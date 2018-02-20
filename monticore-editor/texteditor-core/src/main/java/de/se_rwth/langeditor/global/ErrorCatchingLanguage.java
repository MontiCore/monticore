/* (c)  https://github.com/MontiCore/monticore */package de.se_rwth.langeditor.global;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.templates.TemplateProposal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.SymbolKind;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.langeditor.language.Language;
import de.se_rwth.langeditor.language.OutlineElementSet;
import de.se_rwth.langeditor.language.ParserConfig;
import de.se_rwth.langeditor.modelstates.ModelState;

final class ErrorCatchingLanguage implements Language {
  
  private final Language language;
  
  ErrorCatchingLanguage(Language language) {
    this.language = language;
  }
  
  @Override
  public String getExtension() {
    return language.getExtension();
  }
  
  @Override
  public ParserConfig<?> getParserConfig() {
    return language.getParserConfig();
  }
  
  public void buildProject(IProject project, ImmutableSet<ModelState> modelStates,
      ImmutableList<Path> modelPath) {
    try {
      language.buildProject(project, modelStates, modelPath);
    }
    catch (Exception e) {
      Log.error("0xA1115 Error while building project.", e);
    }
  }
  
  public void buildModel(ModelState modelState) {
    try {
      language.buildModel(modelState);
    }
    catch (Exception e) {
      Log.error("0xA1116 Error while building model.", e);
    }
  }
  
  public ImmutableList<String> getKeywords() {
    try {
      return language.getKeywords();
    }
    catch (Exception e) {
      Log.error("0xA1117 Error while retrieving keywords.", e);
      return ImmutableList.of();
    }
  }
  
  public OutlineElementSet getOutlineElementSet() {
    try {
      return language.getOutlineElementSet();
    }
    catch (Exception e) {
      Log.error("0xA1118 Error determining outline elements.", e);
      return OutlineElementSet.empty();
    }
  }
  
 
  @Override
  public Collection<? extends SymbolKind> getCompletionKinds() {
    try {
      return language.getCompletionKinds();
    }
    catch (Exception e) {
      Log.error("0xA1123 Error determining completion kinds.", e);
      return Sets.newHashSet();
    }
  }

  public Optional<Supplier<Optional<ASTNode>>> createResolver(ASTNode astNode) {
    try {
      return language.createResolver(astNode);
    }
    catch (Exception e) {
      Log.error("0xA1119 Error while creating hyperlink.", e);
      return Optional.empty();
    }
  }

  /**
   * @see de.se_rwth.langeditor.language.Language#getScope()
   */
  @Override
  public Optional<ArtifactScope> getScope(ASTNode node) {
    return language.getScope(node);
  }

  /**
   * @see de.se_rwth.langeditor.language.Language#getTemplateProposals(org.eclipse.jface.text.ITextViewer, int)
   */
  @Override
  public List<TemplateProposal> getTemplateProposals(ITextViewer viewer, int offset, String prefix) {
    return language.getTemplateProposals(viewer, offset, prefix);
  }
  
  
}
