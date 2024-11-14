package de.monticore.expressions.streamexpressions._ast;

public class ASTStreamConstructorExpression extends ASTStreamConstructorExpressionTOP {
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
}
