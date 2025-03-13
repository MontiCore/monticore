/* (c) https://github.com/MontiCore/monticore */
package de.monticore.umlstereotype._ast;

import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.literals.mccommonliterals.MCCommonLiteralsMill;
import de.monticore.literals.mccommonliterals._ast.ASTStringLiteral;

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

  @Deprecated
  public ASTStereoValueBuilder setText(ASTStringLiteral text) {
    this.setExpression(ExpressionsBasisMill
      .literalExpressionBuilder()
      .setLiteral(text)
      .build());
    return this.realBuilder;
  }
}
