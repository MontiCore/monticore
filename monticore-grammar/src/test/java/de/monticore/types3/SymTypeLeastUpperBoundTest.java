/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types3;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsScope;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.util.DefsTypesForTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static de.monticore.types.check.SymTypeExpressionFactory.createBottomType;
import static de.monticore.types.check.SymTypeExpressionFactory.createIntersection;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeArray;
import static de.monticore.types.check.SymTypeExpressionFactory.createUnion;
import static de.monticore.types3.util.DefsTypesForTests._carSymType;
import static de.monticore.types3.util.DefsTypesForTests._childSymType;
import static de.monticore.types3.util.DefsTypesForTests._csStudentSymType;
import static de.monticore.types3.util.DefsTypesForTests._personSymType;
import static de.monticore.types3.util.DefsTypesForTests._studentSymType;
import static org.junit.Assert.assertEquals;

public class SymTypeLeastUpperBoundTest extends AbstractTypeTest {

  protected ICombineExpressionsWithLiteralsScope scope;

  @BeforeEach
  public void setup() {
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    DefsTypesForTests.setup();
    SymTypeRelations.init();
  }

  @Test
  public void leastUpperBound() {
    checkLub(_personSymType, "Person");
    checkLub(createUnion(_personSymType, _studentSymType), "Person");
    checkLub(createUnion(_childSymType, _studentSymType), "Person & Teachable");
    checkLub(createUnion(_childSymType, _csStudentSymType), "Person & Teachable");
    checkLub(createUnion(_childSymType, _carSymType), "Obscure");
    checkLub(
        createIntersection(
            _personSymType,
            createUnion(_childSymType, _studentSymType)
        ),
        "Person & Teachable"
    );
    checkLub(createUnion(
            createTypeArray(_childSymType, 2),
            createTypeArray(_csStudentSymType, 2)
        ),
        createBottomType().printFullName()
    );
  }

  protected void checkLub(SymTypeExpression type, String expectedPrint) {
    Optional<SymTypeExpression> lubOpt = SymTypeRelations.leastUpperBound(type);
    String printed = lubOpt.map(SymTypeExpression::printFullName).orElse("");
    assertNoFindings();
    Assertions.assertEquals(expectedPrint, printed);
  }
}
