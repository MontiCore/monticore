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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DefiningSymbolSetter4CommonExpressionsTest {

  @Before
  public void setup() {
    CombineExpressionsWithLiteralsMill.init();
    LogStub.init();         // replace log by a side effect free variant
    Log.enableFailQuick(false);
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
    Assert.assertTrue(expr.getDefiningSymbol().isPresent());
    Assert.assertSame(symbol, expr.getDefiningSymbol().get());

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
    Assert.assertTrue(fieldAccessExpr.getDefiningSymbol().isPresent());
    Assert.assertSame(symbol, fieldAccessExpr.getDefiningSymbol().get());

    Assert.assertFalse(qualExpr.getDefiningSymbol().isPresent());
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
    Assert.assertTrue(callExpr.getDefiningSymbol().isPresent());
    Assert.assertSame(symbol, callExpr.getDefiningSymbol().get());

    Assert.assertFalse(methodNameExpr.getDefiningSymbol().isPresent());
  }
}

