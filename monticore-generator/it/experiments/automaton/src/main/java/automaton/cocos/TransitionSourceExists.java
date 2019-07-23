/* (c) Monticore license: https://github.com/MontiCore/monticore */
package automaton.cocos;

import automaton._ast.ASTTransition;
import automaton._cocos.AutomatonASTTransitionCoCo;
import automaton._symboltable.IAutomatonScope;
import automaton._symboltable.StateSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class TransitionSourceExists
                implements AutomatonASTTransitionCoCo {

  @Override
  public void check(ASTTransition node) {
  
    IAutomatonScope enclosingScope = node.getEnclosingScope2();
    Optional<StateSymbol> sourceState =
        enclosingScope.resolveState(node.getFrom());

    if (!sourceState.isPresent()) {
      // Issue error...
      Log.error(
        "0xAUT03 Source state of transition missing.",
         node.get_SourcePositionStart());
    }
  }
}

