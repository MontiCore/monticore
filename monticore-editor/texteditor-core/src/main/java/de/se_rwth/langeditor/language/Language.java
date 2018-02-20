/* (c)  https://github.com/MontiCore/monticore */package de.se_rwth.langeditor.language;

import java.nio.file.Path;
import java.util.ArrayList;
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
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.types.JTypeSymbol;
import de.se_rwth.langeditor.modelstates.ModelState;

public interface Language {
  
  /**
   * @return the file extension for which this language is responsible (no leading dot). E.g. "cd"
   */
  String getExtension();
  
  /**
   * @return the parser configuration that this language should be using
   */
  ParserConfig<?> getParserConfig();
  
  /**
   * Called on startup or when the user (or another part of the Eclipse IDE) requests a full
   * rebuild. This should be taken as a strong hint to invalidate any existing data structures and
   * rebuild from scratch.
   * 
   * @param project the project to rebuild
   * @param modelStates the collection of newly parsed models. Any syntactically invalid models are filtered from this set.
   * @param modelPath the currently configured modelpath
   */
  default void buildProject(IProject project, ImmutableSet<ModelState> modelStates,
      ImmutableList<Path> modelPath) {
  }
  
  /**
   * Called when only a single file is rebuilt (usually because the user edited its content).
   * 
   * @param modelState newly parsed syntactically valid model
   */
  default void buildModel(ModelState modelState) {
  }
  
  /**
   * @return the list of all words that should be highlighted in text
   */
  default ImmutableList<String> getKeywords() {
    return ImmutableList.of();
  }
  
  /**
   * @return the set of elements that should be listed in the outline of each file
   */
  default OutlineElementSet getOutlineElementSet() {
    return OutlineElementSet.empty();
  }
  
  /**
   * @return the set of elements that should be listed in the outline of each file
   */
  default Collection<? extends SymbolKind> getCompletionKinds() {
    return Sets.newHashSet(JTypeSymbol.KIND);
  }

  /**
   * The outer Optional indicates whether resolving should be attempted (this dictates whether a
   * hyperlink will be displayed). The inner Optional is the result of an attempted resolving
   * process. This is an instance of the proxy pattern.
   * 
   * @param astNode the narrowest enclosing ASTNode where the user is currently hovering with the mouse
   * @return an optional resolver (the supplier can be thought of as the resolver, which has been
   * preconfigured with the destination).
   */
  default Optional<Supplier<Optional<ASTNode>>> createResolver(ASTNode astNode) {
    return Optional.empty();
  }
  
  default Optional<ArtifactScope> getScope(ASTNode node) {
    if (node.getEnclosingScope().isPresent()) {
      if (node.getEnclosingScope().get() instanceof ArtifactScope) {
        return Optional.of((ArtifactScope) node.getEnclosingScope().get());
      }
      Optional<? extends Scope> scope = node.getEnclosingScope().get().getEnclosingScope();
      if (scope.isPresent() && scope.get() instanceof ArtifactScope) {
        return Optional.of((ArtifactScope) scope.get());
      }
      if (scope.get().getAstNode().isPresent()) {
        return getScope(scope.get().getAstNode().get());
      }
    }
    return Optional.empty();
  }
  
  default List<TemplateProposal> getTemplateProposals(ITextViewer viewer, int offset, String prefix) {
    return new ArrayList<>();
  }
  
}
