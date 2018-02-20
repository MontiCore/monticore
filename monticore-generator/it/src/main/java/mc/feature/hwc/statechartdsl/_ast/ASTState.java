/* (c) https://github.com/MontiCore/monticore */

package mc.feature.hwc.statechartdsl._ast;

import java.util.List;
import java.util.Optional;

public class ASTState extends mc.feature.hwc.statechartdsl._ast.ASTStateTOP {
  
  protected ASTState() {
    super();
  }
  
  protected ASTState(
      Optional<ASTEntryAction> entryAction,
      Optional<ASTExitAction> exitAction,
      boolean initial,
      boolean r_final,
      String name,
      List<ASTState> states,
      List<ASTTransition> transitions
     ) {
    super(entryAction, exitAction, r_final, initial, name, states, transitions);
  }

}
