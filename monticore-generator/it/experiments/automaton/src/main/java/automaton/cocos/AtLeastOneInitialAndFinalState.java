/* (c) Monticore license: https://github.com/MontiCore/monticore */
package automaton.cocos;

import automaton._ast.ASTAutomaton;
import automaton._ast.ASTState;
import automaton._cocos.AutomatonASTAutomatonCoCo;
import de.se_rwth.commons.logging.Log;

public class AtLeastOneInitialAndFinalState implements AutomatonASTAutomatonCoCo {
  
  @Override
  public void check(ASTAutomaton automaton) {
    boolean initialState = false;
    boolean finalState = false;
    
    for (ASTState state : automaton.getStateList()) {
      if (state.isInitial()) {
        initialState = true;
      }
      if (state.isFinal()) {
        finalState = true;
      }
    }
    
    if (!initialState || !finalState) {
      // Issue error...
      Log.error("0xA0114 An automaton must have at least one initial and one final state.",
          automaton.get_SourcePositionStart());
    }
  }
  
}
