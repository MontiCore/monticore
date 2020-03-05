/* (c) https://github.com/MontiCore/monticore */
package automata.cocos;


import automata._ast.ASTAutomaton;
import automata._ast.ASTState;
import automata._cocos.AutomataASTAutomatonCoCo;
import de.se_rwth.commons.logging.Log;

public class AtLeastOneInitialAndFinalState implements AutomataASTAutomatonCoCo {
  
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
      Log.error("0xA0116 An automaton must have at least one initial and one final state.",
          automaton.get_SourcePositionStart());
    }
  }
  
}
