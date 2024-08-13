package de.monticore.expressions.streamexpressions._ast;

public class ASTStreamConstructorExpression extends ASTStreamConstructorExpressionTOP {
  public boolean isEventTimed() {
    return getTiming() == ASTConstantsStreamExpressions.EVENT;
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
}
