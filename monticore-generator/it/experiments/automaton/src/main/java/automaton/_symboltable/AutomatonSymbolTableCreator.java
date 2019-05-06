/* (c) Monticore license: https://github.com/MontiCore/monticore */
package automaton._symboltable;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Optional;

import automaton._ast.ASTAutomaton;
import automaton._ast.ASTState;
import automaton._ast.ASTTransition;
import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;

public class AutomatonSymbolTableCreator extends AutomatonSymbolTableCreatorTOP {
  
  public AutomatonSymbolTableCreator(
      final ResolvingConfiguration resolverConfig,
      final Scope enclosingScope) {
    super(resolverConfig, enclosingScope);
  }

  public Scope createFromAST(ASTAutomaton rootNode) {
      requireNonNull(rootNode);

      final ArtifactScope artifactScope = new ArtifactScope(Optional.empty(), "", new ArrayList<>());
      putOnStack(artifactScope);

      rootNode.accept(this);

      if (artifactScope.getTopLevelSymbol().isPresent()) {
        return artifactScope.getTopLevelSymbol().get().getSpannedScope();
      }

      return artifactScope;
  }

  @Override
  public void visit(final ASTAutomaton automatonNode) {
    final AutomatonSymbol automaton = new AutomatonSymbol(automatonNode.getName());
    addToScopeAndLinkWithNode(automaton, automatonNode);
  }
   
  @Override
  public void visit(final ASTState stateNode) {
    final StateSymbol stateSymbol = new StateSymbol(stateNode.getName());

    addToScopeAndLinkWithNode(stateSymbol, stateNode);
  }

 
  @Override
  public void visit(final ASTTransition transitionNode) {
    transitionNode.setEnclosingScope(currentScope().get());
  }
}
