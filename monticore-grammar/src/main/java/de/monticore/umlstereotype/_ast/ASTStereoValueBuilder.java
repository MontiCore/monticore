/* (c) https://github.com/MontiCore/monticore */
package de.monticore.umlstereotype._ast;

import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.literals.mccommonliterals.MCCommonLiteralsMill;
import de.monticore.literals.mccommonliterals._ast.ASTStringLiteral;
import org.apache.commons.lang3.StringEscapeUtils;

public class ASTStereoValueBuilder extends ASTStereoValueBuilderTOP {

  @Deprecated
  public ASTStereoValueBuilder setContent(String content) {
    return this.setText(MCCommonLiteralsMill
        .stringLiteralBuilder()
            // We have to escape the string, as #getContent() decodes it
        .setSource(StringEscapeUtils.escapeJava(content))
        .build());
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
