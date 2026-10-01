/* (c) https://github.com/MontiCore/monticore */
package de.monticore.umlstereotype._ast;

import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.literals.mccommonliterals.MCCommonLiteralsMill;
import de.monticore.literals.mccommonliterals._ast.ASTStringLiteral;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.lang3.StringEscapeUtils;

public class ASTStereoValue extends ASTStereoValueTOP {

  public boolean isPresentText() {
    if (isPresentExpression()) {
      return expression.get() instanceof ASTLiteralExpression literalExpression
          && literalExpression.getLiteral() instanceof ASTStringLiteral;
    }
    return false;
  }

  public ASTStringLiteral getText() {
    if (isPresentText() && expression.get() instanceof ASTLiteralExpression literalExpression
        && literalExpression.getLiteral() instanceof ASTStringLiteral stringLiteral) {
      return stringLiteral;
    }
    Log.error("0xA7003x52066 get for Text can't return a value. Attribute is empty.");
    // Normally this statement is not reachable
    throw new IllegalStateException();
  }

  /**
   * @return the content of the String or an empty string if no text is present
   */
  public String getValue() {
    if (isPresentText()) {
      return getText().getValue();
    }
    return "";
  }

  @Deprecated
  public String getContent() {
    return getValue();
  }

  @Deprecated
  public void setContent(String content) {
    this.setExpression(ExpressionsBasisMill
      .literalExpressionBuilder()
      .setLiteral(MCCommonLiteralsMill
        .stringLiteralBuilder()
        .setSource(StringEscapeUtils.escapeJava(content))
        .build())
      .build());
  }
}
