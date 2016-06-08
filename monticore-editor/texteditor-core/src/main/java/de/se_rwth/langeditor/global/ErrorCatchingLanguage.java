package de.se_rwth.langeditor.global;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.core.resources.IProject;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.monticore.ast.ASTNode;
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
      Log.error("Error while building project.", e);
    }
  }
  
  public void buildModel(ModelState modelState) {
    try {
      language.buildModel(modelState);
    }
    catch (Exception e) {
      Log.error("Error while building model.", e);
    }
  }
  
  public ImmutableList<String> getKeywords() {
    try {
      return language.getKeywords();
    }
    catch (Exception e) {
      Log.error("Error while retrieving keywords.", e);
      return ImmutableList.of();
    }
  }
  
  public OutlineElementSet getOutlineElementSet() {
    try {
      return language.getOutlineElementSet();
    }
    catch (Exception e) {
      Log.error("Error determining outline elements.", e);
      return OutlineElementSet.empty();
    }
  }
  
  public Optional<Supplier<Optional<ASTNode>>> createResolver(ASTNode astNode) {
    try {
      return language.createResolver(astNode);
    }
    catch (Exception e) {
      Log.error("Error while creating hyperlink.", e);
      return Optional.empty();
    }
  }
}
