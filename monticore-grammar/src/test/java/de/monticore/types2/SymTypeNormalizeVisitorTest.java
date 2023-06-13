// (c) https://github.com/MontiCore/monticore
package de.monticore.types2;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfIntersection;
import de.monticore.types.check.SymTypeOfUnion;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.types.check.SymTypeExpressionFactory.createFunction;
import static de.monticore.types.check.SymTypeExpressionFactory.createIntersection;
import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeArray;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeObject;
import static de.monticore.types.check.SymTypeExpressionFactory.createUnion;
import static de.monticore.types2.DefsTypeBasic._IntegerSymType;
import static de.monticore.types2.DefsTypeBasic._carSymType;
import static de.monticore.types2.DefsTypeBasic._intSymType;
import static de.monticore.types2.DefsTypeBasic._personSymType;
import static de.monticore.types2.DefsTypeBasic._shortSymType;
import static de.monticore.types2.DefsTypeBasic._studentSymType;
import static org.junit.Assert.assertEquals;

public class SymTypeNormalizeVisitorTest extends AbstractTypeTest {

  SymTypeNormalizeVisitor visitor;

  @Before
  public void init() {
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    DefsTypeBasic.setup();
    visitor = new SymTypeNormalizeVisitor();
    visitor.setTypeRelations(new TypeRelations2());
    assertNoFindings();
  }

  @Test
  public void doNotChangeSimpleTypes() {
    check(_IntegerSymType, "java.lang.Integer");
    check(_intSymType, "int");
    check(_personSymType, "Person");
    assertNoFindings();
  }

  @Test
  public void normalizeSimpleUnions() {
    // Tests normalization that is at max changing one level
    check(createUnion(_personSymType, _carSymType), "(Car | Person)");
    check(createUnion(_personSymType), "Person");
    check(createUnion(
        _personSymType, createTypeObject(_personSymType.getTypeInfo())
    ), "Person");
    check(createUnion(_personSymType, createObscureType()), "Person");
    check(createUnion(_personSymType, _studentSymType), "Person");
    check(createUnion(
            createFunction(_personSymType, _shortSymType),
            createFunction(_studentSymType, _intSymType)
        ),
        "(int) -> Student"
    );
    assertNoFindings();
  }

  @Test
  public void normalizeSimpleIntersections() {
    // Tests normalization that is at max changing one level
    check(createIntersection(_personSymType, _carSymType), "(Car & Person)");
    check(createIntersection(_personSymType), "Person");
    check(createIntersection(
        _personSymType, createTypeObject(_personSymType.getTypeInfo())
    ), "Person");
    check(createIntersection(_personSymType, createObscureType()), "Obscure");
    check(createIntersection(_personSymType, _studentSymType), "Student");
    check(createIntersection(
            createFunction(_personSymType, _shortSymType),
            createFunction(_studentSymType, _intSymType)
        ),
        "(short) -> Person"
    );
    assertNoFindings();
  }

  @Test
  public void normalizeArrays() {
    check(createTypeArray(createObscureType(), 2), "Obscure");
    check(createTypeArray(_personSymType, 2), "Person[][]");
    check(createTypeArray(_personSymType, 0), "Person");
    check(
        createTypeArray(createTypeArray(_personSymType, 1), 2),
        "Person[][][]"
    );
    assertNoFindings();
  }

  @Test
  public void moveArraysBelowSetOperations() {
    check(
        createTypeArray(
            createUnion(
                createTypeArray(_personSymType, 1),
                createTypeArray(_carSymType, 1)
            ),
            1
        ),
        "(Car[][] | Person[][])"
    );
    check(
        createTypeArray(
            createIntersection(
                createTypeArray(_personSymType, 1),
                createTypeArray(_carSymType, 1)
            ),
            1
        ),
        "(Car[][] & Person[][])"
    );
    check(
        createTypeArray(
            createIntersection(
                createTypeArray(
                    createUnion(
                        createTypeArray(_personSymType, 1),
                        _personSymType
                    ),
                    1
                ),
                createTypeArray(_personSymType, 1)
            ),
            1
        ),
        "Person[][]"
    );
    assertNoFindings();
  }

  @Test
  public void normalizeComplexTypes() {
    SymTypeOfUnion union1 = createUnion(_personSymType, _carSymType);
    SymTypeOfUnion union2 = createUnion(_personSymType, _intSymType);
    SymTypeOfIntersection inter1 = createIntersection(_personSymType, _carSymType);
    SymTypeOfIntersection inter2 = createIntersection(_personSymType, _intSymType);

    // unions
    check(createUnion(union1, _personSymType), "(Car | Person)");
    check(createUnion(union1, _intSymType), "(Car | Person | int)");
    check(createUnion(union1, union2), "(Car | Person | int)");

    // intersections
    check(createIntersection(inter1, _personSymType), "(Car & Person)");
    check(createIntersection(inter1, inter2), "(Car & Person & int)");

    // combine unions and intersections
    check(createUnion(inter1, _personSymType), "Person");
    check(createUnion(inter1, _intSymType), "((Car & Person) | int)");
    check(createUnion(inter1, union2), "(Person | int)");
    check(createUnion(inter1, union1), "(Car | Person)");
    check(createUnion(inter1, inter2), "((Car & Person) | (Person & int))");

    check(createIntersection(union1, _personSymType), "Person");
    check(createIntersection(union1, _intSymType), "((Car & int) | (Person & int))");
    check(createIntersection(union1, union2), "((Car & int) | Person)");
    check(createIntersection(union1, inter1), "(Car & Person)");
    check(createIntersection(union1, inter2), "(Person & int)");

    // normalize within arrays
    check(
        createTypeArray(
            createTypeArray(
                createIntersection(union1, _personSymType),
                2
            ),
            1
        ),
        "Person[][][]"
    );
    assertNoFindings();
  }

  public void check(SymTypeExpression type, String expectedPrint) {
    SymTypeExpression normalized = visitor.calculate(type);
    assertNoFindings();
    assertEquals(expectedPrint, normalized.printFullName());
  }

}
