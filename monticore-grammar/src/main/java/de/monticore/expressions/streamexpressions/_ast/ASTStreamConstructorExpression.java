package de.monticore.expressions.streamexpressions._ast;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.streamexpressions.StreamExpressionsMill;
import de.monticore.expressions.streamexpressions._visitor.StreamExpressionsVisitor2;

import java.util.ArrayList;
import java.util.List;

public class ASTStreamConstructorExpression extends ASTStreamConstructorExpressionTOP {
  private ArrayList<ASTStreamElem> backing = new ArrayList<>();
  public List<ASTStreamElem> getStreamList() {
    var traverser = StreamExpressionsMill.traverser();
    traverser.add4StreamExpressions(new StreamExpressionsVisitor2() {
      @Override
      public void visit(ASTTick node) {
        backing.add(node);
      }

      @Override
      public void visit(ASTTickedElement node) {
        backing.add(node.getExpressionElement());
      }

      @Override
      public void visit(ASTExpressionElement node) {
        backing.add(node);
      }
    });

    accept(traverser);
    return backing;
  }
/*
  public boolean isEventTimed() {
    return getStreamType() == ASTConstantsStreamExpressions.EVENT;
  }

  public boolean isSyncTimed() {
    return getStreamType() == ASTConstantsStreamExpressions.SYNC;
  }

  public boolean isToptTimed() {
    return getStreamType() == ASTConstantsStreamExpressions.TOPT;
  }

  public boolean isUntimed() {
    return getStreamType() == ASTConstantsStreamExpressions.UNTIMED;
  }

 */
}
