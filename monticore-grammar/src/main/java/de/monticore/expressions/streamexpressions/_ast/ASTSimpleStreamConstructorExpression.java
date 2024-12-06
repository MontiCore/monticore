package de.monticore.expressions.streamexpressions._ast;

public class ASTSimpleStreamConstructorExpression extends ASTSimpleStreamConstructorExpressionTOP {

  @Override
  public boolean isDefaultTimed() {
    return getTiming() == ASTConstantsStreamExpressions.DEFAULT;
  }

  @Override
  public boolean isSyncTimed() {
    return getTiming() == ASTConstantsStreamExpressions.SYNC;
  }

  @Override
  public boolean isUntimed() {
    return getTiming() == ASTConstantsStreamExpressions.UNTIMED;
  }

}
