/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.expressionsbasis.types3;

import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.AbstractTypeVisitor;
import de.monticore.types3.util.NameExpressionTypeCalculator;
import de.monticore.types3.util.WithinScopeBasicSymbolsResolver;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class ExpressionBasisTypeVisitor extends AbstractTypeVisitor
    implements ExpressionsBasisVisitor2 {

  protected WithinScopeBasicSymbolsResolver withinScopeResolver;

  public ExpressionBasisTypeVisitor() {
    // default values
    withinScopeResolver = new WithinScopeBasicSymbolsResolver();
  }

  public void setWithinScopeResolver(
      WithinScopeBasicSymbolsResolver withinScopeResolver) {
    this.withinScopeResolver = withinScopeResolver;
  }

  /**
   * @deprecated use {@link #setWithinScopeResolver}
   */
  @Deprecated
  public void setNameExpressionTypeCalculator(
      NameExpressionTypeCalculator nameExpressionTypeCalculator) {
    setWithinScopeResolver(nameExpressionTypeCalculator);
  }

  protected WithinScopeBasicSymbolsResolver getWithinScopeResolver() {
    return withinScopeResolver;
  }

  /**
   * note: this will not be called in a ASTFieldAccessExpression
   * given the default ASTFieldAccessExpression traversal;
   * given expr. a.b.c, "a" is a ASTNameExpression,
   * however, in FieldAccessExpressions, the "a" is not required to have a type,
   * as such the traversal is customized.
   * Thus, here an expression type has to be calculated.
   */
  @Override
  public void endVisit(ASTNameExpression expr) {
    // check if inference already calculated something
    if (getType4Ast().hasPartialTypeOfExpression(expr)) {
      return;
    }
    Optional<SymTypeExpression> wholeResult =
        calculateNameExpressionOrLogError(expr);
    if (wholeResult.isPresent()) {
      getType4Ast().setTypeOfExpression(expr, wholeResult.get());
    }
    else {
      getType4Ast().setTypeOfExpression(expr, SymTypeExpressionFactory.createObscureType());
    }
  }

  protected Optional<SymTypeExpression> calculateNameExpressionOrLogError(
      ASTNameExpression expr
  ) {
    Optional<SymTypeExpression> wholeResult = calculateNameExpression(expr);
    if (wholeResult.isEmpty()) {
      Log.error("0xFD118 could not find symbol for expression \""
              + expr.getName() + "\"",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
      );
    }
    return wholeResult;
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
    return getWithinScopeResolver().resolveNameAsExpr(enclosingScope, name);
  }

  @Override
  public void endVisit(ASTLiteralExpression expr) {
    getType4Ast().setTypeOfExpression(expr,
        getType4Ast().getPartialTypeOfExpr(expr.getLiteral())
    );
  }
}
