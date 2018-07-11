/* (c) Monticore license: https://github.com/MontiCore/monticore */
package sm2._symboltable;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Optional;

import sm2._ast.ASTAutomaton;
import sm2._ast.ASTState;
import sm2._ast.ASTTransition;
import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;

import de.se_rwth.commons.logging.Log;

public class SM2SymbolTableCreator extends SM2SymbolTableCreatorTOP {
  
  public SM2SymbolTableCreator(
      final ResolvingConfiguration resolvingConfig,
      final MutableScope enclosingScope) {
    super(resolvingConfig, enclosingScope);
  }

  @Override
  public Scope createFromAST(sm2._ast.ASTSM2Node rootNode) {
      requireNonNull(rootNode);

      final ArtifactScope artifactScope = new ArtifactScope(Optional.empty(), "", new ArrayList<>());
      putOnStack(artifactScope);

      rootNode.accept(this);

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
    
    Log.info("StateSymbol defined for " + stateSymbol.getName(), SM2SymbolTableCreator.class.getName());

    addToScopeAndLinkWithNode(stateSymbol, stateNode);
  }

  @Override
  public void visit(final ASTTransition transitionNode) {
    transitionNode.setEnclosingScope(currentScope().get());
  }
}
