/* (c) Monticore license: https://github.com/MontiCore/monticore */
package sm2.cocos;

import sm2._ast.ASTAutomaton;
import sm2._ast.ASTState;
import sm2._cocos.SM2ASTAutomatonCoCo;
import de.se_rwth.commons.logging.Log;

public class AtLeastOneInitialState implements SM2ASTAutomatonCoCo {
  
  @Override
  public void check(ASTAutomaton automaton) {
    boolean initialState = false;
    
    for (ASTState state : automaton.getStateList()) {
      if (state.isInitial()) {
        initialState = true;
      }
    }
    
    if (!initialState) {
      // Issue error...
      Log.error("0xA5114 An automaton must have at least one initial state.",
          automaton.get_SourcePositionStart());
    }
  }
  
}
