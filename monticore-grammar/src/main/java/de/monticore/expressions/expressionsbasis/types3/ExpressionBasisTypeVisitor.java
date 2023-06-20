/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.expressionsbasis.types3;

import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.expressions.expressionsbasis.types3.util.NameExpressionTypeCalculator;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.AbstractTypeVisitor;
import de.monticore.types3.util.TypeContextCalculator;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class ExpressionBasisTypeVisitor extends AbstractTypeVisitor
    implements ExpressionsBasisVisitor2 {

  protected NameExpressionTypeCalculator nameExpressionTypeCalculator;

  protected TypeContextCalculator typeContextCalculator;

  public ExpressionBasisTypeVisitor() {
    // default values
    nameExpressionTypeCalculator = new NameExpressionTypeCalculator();
    typeContextCalculator = new TypeContextCalculator();
  }

  protected NameExpressionTypeCalculator getNameExpressionTypeCalculator() {
    return nameExpressionTypeCalculator;
  }

  protected TypeContextCalculator getTypeContextCalculator() {
    return typeContextCalculator;
  }

  @Override
  public void endVisit(ASTNameExpression expr) {
    Optional<SymTypeExpression> wholeResult = calculateNameExpression(expr);
    if (wholeResult.isPresent()) {
      getType4Ast().setTypeOfExpression(expr, wholeResult.get());
    }
    else {
      Log.error("0xFD118 could not find type of name expression \""
              + expr.getName() + "\"",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
      getType4Ast().setTypeOfExpression(expr, SymTypeExpressionFactory.createObscureType());
    }
  }

  protected Optional<SymTypeExpression> calculateNameExpression(
      ASTNameExpression expr) {
    if (expr.getEnclosingScope() == null) {
      Log.error("0xFD161 internal error: "
              + "enclosing scope of expression expected",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
      return Optional.empty();
    }

    final String name = expr.getName();
    IBasicSymbolsScope enclosingScope =
        getAsBasicSymbolsScope(expr.getEnclosingScope());
    return getNameExpressionTypeCalculator().typeOfNameAsExpr(enclosingScope, name);
  }

  @Override
  public void endVisit(ASTLiteralExpression expr) {
    getType4Ast().setTypeOfExpression(expr,
        getType4Ast().getPartialTypeOfExpr(expr.getLiteral())
    );
  }
}
