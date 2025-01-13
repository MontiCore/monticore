package de.monticore.expressions.streamexpressions._ast;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ASTStreamConstructorExpression
    extends ASTStreamConstructorExpressionTOP {

  public boolean isEventTimed() {
    return getTiming() == ASTConstantsStreamExpressions.EVENT ||
        getTiming() == ASTConstantsStreamExpressions.DEFAULT;
  }

  public boolean isSyncTimed() {
    return getTiming() == ASTConstantsStreamExpressions.SYNC;
  }

  public boolean isToptTimed() {
    return getTiming() == ASTConstantsStreamExpressions.TOPT;
  }

  public boolean isUntimed() {
    return getTiming() == ASTConstantsStreamExpressions.UNTIMED;
  }

  /**
   * @return all ASTExpressions directly found in the constructor.
   */
  public List<ASTExpression> getExpressionList() {
    return streamStreamConstructorElements()
        .filter(ASTStreamConstructorElement::isPresentExpression)
        .map(ASTStreamConstructorElement::getExpression)
        .collect(Collectors.toList());
  }

  /**
   * Each inner List represents a time slice,
   * each time slice may hold any number of ASTExpressions.
   * May only be called for EventStreams
   */
  public List<List<ASTExpression>> getExpressionsPerTimeSlice() {
    if (!isEventTimed()) {
      Log.error("0xFD575 internal error: "
              + "called getExpressionsPerTimeSlice() on non-event stream",
          get_SourcePositionStart(),
          get_SourcePositionEnd()
      );
      return Collections.emptyList();
    }
    List<List<ASTExpression>> result = new ArrayList<>();
    List<ASTExpression> currentTimeSlice = new ArrayList();
    for (ASTStreamConstructorElement elem : getStreamConstructorElementList()) {
      if (elem.isPresentTick()) {
        result.add(currentTimeSlice);
        currentTimeSlice = new ArrayList();
      }
      else {
        currentTimeSlice.add(elem.getExpression());
      }
    }
    result.add(currentTimeSlice);
    return result;
  }
}
