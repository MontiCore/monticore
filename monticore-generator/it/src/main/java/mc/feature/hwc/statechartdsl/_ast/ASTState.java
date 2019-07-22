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
      String name,
      List<ASTState> states,
      List<ASTTransition> transitions,
      boolean initial,
      boolean r_final
     ) {
    super(entryAction, exitAction,  name, states, transitions, r_final, initial);
  }

}
