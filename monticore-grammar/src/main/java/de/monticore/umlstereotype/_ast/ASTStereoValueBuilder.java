/* (c) https://github.com/MontiCore/monticore */
package de.monticore.umlstereotype._ast;

import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.literals.mccommonliterals.MCCommonLiteralsMill;

public class ASTStereoValueBuilder extends ASTStereoValueBuilderTOP {

  @Deprecated
  public ASTStereoValueBuilder setContent(String content) {
    this.setExpression(ExpressionsBasisMill
      .literalExpressionBuilder()
      .setLiteral(MCCommonLiteralsMill
        .stringLiteralBuilder()
        .setSource(content)
        .build())
      .build());
    return this.realBuilder;
  }
}
