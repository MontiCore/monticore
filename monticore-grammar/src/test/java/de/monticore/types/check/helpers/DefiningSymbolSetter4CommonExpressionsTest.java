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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
    assertTrue(expr.getDefiningSymbol().isPresent());
    assertSame(symbol, expr.getDefiningSymbol().get());

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
    assertTrue(fieldAccessExpr.getDefiningSymbol().isPresent());
    assertSame(symbol, fieldAccessExpr.getDefiningSymbol().get());

    assertFalse(qualExpr.getDefiningSymbol().isPresent());
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
    assertTrue(callExpr.getDefiningSymbol().isPresent());
    assertSame(symbol, callExpr.getDefiningSymbol().get());

    assertFalse(methodNameExpr.getDefiningSymbol().isPresent());
  }
}

