package sm2.cocos;

import sm2._ast.ASTState;
import sm2._cocos.SM2ASTStateCoCo;
import de.se_rwth.commons.logging.Log;

public class StateNameStartsWithCapitalLetter implements SM2ASTStateCoCo {
  
  @Override
  public void check(ASTState state) {
    String stateName = state.getName();
    boolean startsWithUpperCase = Character.isUpperCase(stateName.charAt(0));
    
    if (!startsWithUpperCase) {
      // Issue warning...
      Log.warn(
          String.format("0xAUT02 State name '%s' should start with a capital letter.", stateName),
          state.get_SourcePositionStart());
    }
  }
  
}
