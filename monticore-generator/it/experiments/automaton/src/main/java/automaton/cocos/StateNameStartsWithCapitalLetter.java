package automaton.cocos;

import automaton._ast.ASTState;
import automaton._cocos.AutomatonASTStateCoCo;
import de.se_rwth.commons.logging.Log;

public class StateNameStartsWithCapitalLetter
			implements AutomatonASTStateCoCo {
  
  @Override
  public void check(ASTState state) {
    String stateName = state.getName();
    boolean startsWithUpperCase =
    		Character.isUpperCase(stateName.charAt(0));
    
    if (!startsWithUpperCase) {
      // Issue warning...
      Log.warn(
          String.format(
	    "0xAUT02 State name '%s' is not capitalized.",
	    stateName),
	  state.get_SourcePositionStart());
    }
  }
  
}
