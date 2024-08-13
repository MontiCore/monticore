package de.monticore.expressions.streamexpressions._ast;

public class ASTStreamConstructorExpressionBuilder extends ASTStreamConstructorExpressionBuilderTOP {
  public ASTStreamConstructorExpressionBuilder() {
    // the timing keyword is optional requiring a manual setup for the default event timing
    setTiming(ASTConstantsStreamExpressions.EVENT);
  }
}
