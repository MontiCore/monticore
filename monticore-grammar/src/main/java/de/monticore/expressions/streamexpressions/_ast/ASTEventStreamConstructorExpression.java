package de.monticore.expressions.streamexpressions._ast;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.streamexpressions.StreamExpressionsMill;
import de.monticore.expressions.streamexpressions._ast.util.ASTNodeSiblingComparator;
import de.monticore.expressions.streamexpressions._util.IStreamExpressionsTypeDispatcher;

import java.util.ArrayList;
import java.util.List;

public class ASTEventStreamConstructorExpression
    extends ASTEventStreamConstructorExpressionTOP {

  @Override
  public boolean isEventTimed() {
    return true;
  }

  /**
   * each inner List represents a time slice,
   * each time slice may hold any number of ASTExpressions.
   */
  public List<List<ASTExpression>> getExpressionsPerTimeSlice() {
    List<List<ASTExpression>> result =
        new ArrayList<>(sizeTickInStreamConstructors() + 1);
    List<ASTNode> nodes = new ArrayList<>();
    nodes.addAll(getExpressionList());
    nodes.addAll(getTickInStreamConstructorList());
    nodes.sort(new ASTNodeSiblingComparator());
    IStreamExpressionsTypeDispatcher dispatcher =
        StreamExpressionsMill.typeDispatcher();
    List<ASTExpression> currentTimeSlice = new ArrayList();
    for (int i = 0; i < nodes.size(); i++) {
      ASTNode node = nodes.get(i);
      if (dispatcher.isExpressionsBasisASTExpression(node)) {
        currentTimeSlice.add(dispatcher.asExpressionsBasisASTExpression(node));
      }
      // Tick
      else {
        result.add(currentTimeSlice);
        currentTimeSlice = new ArrayList();
      }
    }
    result.add(currentTimeSlice);
    return result;
  }

}
