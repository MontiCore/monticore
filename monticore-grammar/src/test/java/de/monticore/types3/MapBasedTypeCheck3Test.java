/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.check.SymTypeExpression;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static de.monticore.types.check.SymTypeExpressionFactory.createGenerics;
import static de.monticore.types3.util.DefsTypesForTests._floatSymType;
import static de.monticore.types3.util.DefsTypesForTests._intSymType;
import static de.monticore.types3.util.DefsTypesForTests._unboxedListSymType;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MapBasedTypeCheck3Test extends AbstractTypeVisitorTest {

  // tests calling the same expression with typeof multiple times,
  // as this may happen due to CoCos

  @Test
  public void typeOfMultipleCallsNoTargetType() throws IOException {
    ASTExpression expr = parseExpr("8243721");
    TypeCheck3.typeOf(expr);
    TypeCheck3.typeOf(expr);
    assertNoFindings();
  }

  @Test
  public void typeOfMultipleCallsTargetTypeFirst() throws IOException {
    ASTExpression expr = parseExpr("[]");
    SymTypeExpression intList =
        createGenerics(_unboxedListSymType.getTypeInfo(), _intSymType);
    SymTypeExpression firstRes = TypeCheck3.typeOf(expr, intList);
    // Cached value gets reused here.
    // Thus, the target type is only required once.
    SymTypeExpression secondRes = TypeCheck3.typeOf(expr);
    assertTrue(secondRes.deepEquals(firstRes));
    assertNoFindings();
  }

  @Test
  public void typeOfMultipleCallsSameTargetType() throws IOException {
    ASTExpression expr = parseExpr("313");
    TypeCheck3.typeOf(expr, _intSymType);
    TypeCheck3.typeOf(expr, _intSymType);
    assertNoFindings();
  }

  @Test
  public void typeOfMultipleCallsTargetTypeSecond() throws IOException {
    ASTExpression expr = parseExpr("42");
    TypeCheck3.typeOf(expr);
    TypeCheck3.typeOf(expr, _intSymType);
    assertHasErrorCode("0xFD111");
  }

  @Test
  public void typeOfMultipleCallsDifferentTargetType() throws IOException {
    ASTExpression expr = parseExpr("4");
    TypeCheck3.typeOf(expr, _intSymType);
    TypeCheck3.typeOf(expr, _floatSymType);
    assertHasErrorCode("0xFD112");
  }

}
