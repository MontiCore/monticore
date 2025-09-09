/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check.helpers;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefiningSymbolSetter4CommonExpressionsTest {

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
  }

  @Test
  public void setDefiningSymbolForNameExprTest() {
    // Given
    DefiningSymbolSetter4CommonExpressions definingSymbolSetter = new DefiningSymbolSetter4CommonExpressions();

    ASTNameExpression expr = CombineExpressionsWithLiteralsMill
      .nameExpressionBuilder()
      .setName("Foo")
      .build();
    ISymbol symbol = CombineExpressionsWithLiteralsMill
      .typeSymbolBuilder()
      .setName("Foo")
      .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
      .build();

    // When
    definingSymbolSetter.setDefiningSymbol((ASTExpression) expr, symbol);

    // Then
    Assertions.assertTrue(expr.getDefiningSymbol().isPresent());
    Assertions.assertSame(symbol, expr.getDefiningSymbol().get());

  }

  @Test
  public void setDefiningSymbolForFieldAccessExprTest() {
    // Given
    DefiningSymbolSetter4CommonExpressions definingSymbolSetter = new DefiningSymbolSetter4CommonExpressions();

    ASTNameExpression qualExpr = CombineExpressionsWithLiteralsMill
      .nameExpressionBuilder()
      .setName("paccage")
      .build();
    ASTFieldAccessExpression fieldAccessExpr = CombineExpressionsWithLiteralsMill
      .fieldAccessExpressionBuilder()
      .setName("Foo")
      .setExpression(qualExpr)
      .build();
    ISymbol symbol = CombineExpressionsWithLiteralsMill
      .typeSymbolBuilder()
      .setName("Foo")
      .setSpannedScope(CombineExpressionsWithLiteralsMill.scope())
      .build();

    // When
    definingSymbolSetter.setDefiningSymbol((ASTExpression) fieldAccessExpr, symbol);

    // Then
    Assertions.assertTrue(fieldAccessExpr.getDefiningSymbol().isPresent());
    Assertions.assertSame(symbol, fieldAccessExpr.getDefiningSymbol().get());

    Assertions.assertFalse(qualExpr.getDefiningSymbol().isPresent());
  }

  @Test
  public void setDefiningSymbolForCallExprTest() {
    // Given
    DefiningSymbolSetter4CommonExpressions definingSymbolSetter = new DefiningSymbolSetter4CommonExpressions();

    ASTNameExpression methodNameExpr = CombineExpressionsWithLiteralsMill
      .nameExpressionBuilder()
      .setName("foo")
      .build();
    ASTCallExpression callExpr = CombineExpressionsWithLiteralsMill
      .callExpressionBuilder()
      .setExpression(methodNameExpr)
      .uncheckedBuild();
    ISymbol symbol = CombineExpressionsWithLiteralsMill
      .functionSymbolBuilder()
      .setName("foo")
      .build();

    // When
    definingSymbolSetter.setDefiningSymbol((ASTExpression) callExpr, symbol);

    // Then
    Assertions.assertTrue(callExpr.getDefiningSymbol().isPresent());
    Assertions.assertSame(symbol, callExpr.getDefiningSymbol().get());

    Assertions.assertFalse(methodNameExpr.getDefiningSymbol().isPresent());
  }
}

