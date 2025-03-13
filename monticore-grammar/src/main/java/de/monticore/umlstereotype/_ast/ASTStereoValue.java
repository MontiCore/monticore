/* (c) https://github.com/MontiCore/monticore */
package de.monticore.umlstereotype._ast;

import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.literals.mccommonliterals.MCCommonLiteralsMill;
import de.monticore.literals.mccommonliterals._ast.ASTStringLiteral;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class ASTStereoValue extends ASTStereoValueTOP {

  protected Optional<ASTStringLiteral> text = Optional.empty();

  public boolean isPresentText() {
    if (text.isEmpty() && isPresentExpression() &&
      ExpressionsBasisMill
        .typeDispatcher()
        .isExpressionsBasisASTLiteralExpression(expression.get())
      && MCCommonLiteralsMill.typeDispatcher()
      .isMCCommonLiteralsASTStringLiteral(ExpressionsBasisMill
        .typeDispatcher().asExpressionsBasisASTLiteralExpression(expression.get())
        .getLiteral())) {
      text = Optional.of(MCCommonLiteralsMill.typeDispatcher()
        .asMCCommonLiteralsASTStringLiteral(ExpressionsBasisMill.typeDispatcher()
          .asExpressionsBasisASTLiteralExpression(expression.get())
          .getLiteral()));
    }
    return text.isPresent();
  }

  public ASTStringLiteral getText() {
    if (isPresentText()) {
      return this.text.get();
    }
    Log.error("0xA7003x52066 get for Text can't return a value. Attribute is empty.");
    // Normally this statement is not reachable
    throw new IllegalStateException();
  }

  public String getValue() {
    if (isPresentText()) {
      return getText().getValue();
    }
    return "";
  }

  public void setContent(String content) {
    text = Optional.of(MCCommonLiteralsMill.stringLiteralBuilder().setSource("\"" + content + "\"").build());
  }
}
