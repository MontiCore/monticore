/* (c) https://github.com/MontiCore/monticore */
package sm2.cocos;

import de.se_rwth.commons.logging.Log;
import sm2._ast.ASTTransition;
import sm2._cocos.SM2ASTTransitionCoCo;
import sm2._symboltable.ISM2Scope;
import sm2._symboltable.StateSymbol;

import java.util.Optional;

public class TransitionSourceExists implements SM2ASTTransitionCoCo {
  
  @Override
  public void check(ASTTransition node) {
    
    ISM2Scope enclosingScope = node.getEnclosingScope();
    Optional<StateSymbol> sourceState = enclosingScope.resolveState(node.getFrom());
    
    if (!sourceState.isPresent()) {
      // Issue error...
      Log.error("0xAUT03 The source state of the transition does not exist.",
          node.get_SourcePositionStart());
    }
  }
}
