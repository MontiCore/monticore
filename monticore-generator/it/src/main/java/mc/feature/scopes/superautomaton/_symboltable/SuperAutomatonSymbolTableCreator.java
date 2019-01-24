/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package mc.feature.scopes.superautomaton._symboltable;

import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import mc.feature.scopes.superautomaton._ast.ASTAutomaton;
import mc.feature.scopes.superautomaton._ast.ASTState;
import mc.feature.scopes.superautomaton._ast.ASTTransition;


import java.util.ArrayList;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class SuperAutomatonSymbolTableCreator extends SuperAutomatonSymbolTableCreatorTOP {
  
  public SuperAutomatonSymbolTableCreator(
      final ResolvingConfiguration resolverConfig,
      final MutableScope enclosingScope) {
    super(resolverConfig, enclosingScope);
  }

  public ArtifactScope createFromAST(ASTAutomaton rootNode) {
      requireNonNull(rootNode);

      final ArtifactScope artifactScope = new ArtifactScope(Optional.empty(), "", new ArrayList<>());
      putOnStack(artifactScope);

      rootNode.accept(this);
      artifactScope.setExportsSymbols(true);

      return artifactScope;
  }

  @Override
  public void visit(final ASTAutomaton automatonNode) {
    final AutomatonSymbol automaton = new AutomatonSymbol(automatonNode.getName());
    addToScopeAndLinkWithNode(automaton, automatonNode);
  }
  
  @Override
  public void endVisit(final ASTAutomaton automatonNode) {
    removeCurrentScope();
  }
  
  @Override
  public void visit(final ASTState stateNode) {
    final StateSymbol stateSymbol = new StateSymbol(stateNode.getName());

    addToScopeAndLinkWithNode(stateSymbol, stateNode);
  }

  @Override
  public void endVisit(final ASTState node) {
    removeCurrentScope();
  }

  @Override
  public void visit(final ASTTransition transitionNode) {
    transitionNode.setEnclosingScope(currentScope().get());
  }

}
