/* (c) https://github.com/MontiCore/monticore */
package automata.cocos;

import automata._ast.ASTTransition;
import automata._cocos.AutomataASTTransitionCoCo;
import automata._symboltable.IAutomataScope;
import automata._symboltable.StateSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class TransitionSourceExists
                 implements AutomataASTTransitionCoCo {

  @Override
  public void check(ASTTransition node) {
  
    IAutomataScope enclosingScope = node.getEnclosingScope();
    Optional<StateSymbol> sourceState =
        enclosingScope.resolveState(node.getFrom());

    if (!sourceState.isPresent()) {
      // Issue error...
      Log.error(
        "0xADD03 Source state of transition missing.",
         node.get_SourcePositionStart());
    }
  }
}

