// (c) https://github.com/MontiCore/monticore
package de.monticore.types3;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfIntersection;
import de.monticore.types.check.SymTypeOfUnion;
import de.monticore.types3.util.DefsTypesForTests;
import de.monticore.types3.util.SymTypeNormalizeVisitor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.monticore.types.check.SymTypeExpressionFactory.createBottomType;
import static de.monticore.types.check.SymTypeExpressionFactory.createFunction;
import static de.monticore.types.check.SymTypeExpressionFactory.createIntersection;
import static de.monticore.types.check.SymTypeExpressionFactory.createNumericWithSIUnit;
import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;
import static de.monticore.types.check.SymTypeExpressionFactory.createSIUnit;
import static de.monticore.types.check.SymTypeExpressionFactory.createSIUnitBasic;
import static de.monticore.types.check.SymTypeExpressionFactory.createTuple;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeArray;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeObject;
import static de.monticore.types.check.SymTypeExpressionFactory.createUnion;
import static de.monticore.types3.util.DefsTypesForTests.*;
import static org.junit.Assert.assertEquals;

public class SymTypeNormalizeVisitorTest extends AbstractTypeTest {

  SymTypeNormalizeVisitor visitor;

  @BeforeEach
  public void init() {
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    DefsTypesForTests.setup();
    SymTypeRelations.init();
    visitor = new SymTypeNormalizeVisitor();
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
    check(createUnion(_personSymType, _carSymType), "Car | Person");
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
        "short -> Person"
    );
    assertNoFindings();
  }

  @Test
  public void normalizeSimpleIntersections() {
    // Tests normalization that is at max changing one level
    check(createIntersection(_personSymType, _carSymType), "Car & Person");
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
        // while the function does map every short to a Person,
        // there is the information that every int gets mapped to a Student;
        // Since every short is an int,
        // the shorts need to be mapped to Students as well.
        "int -> Student"
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
  public void moveTuplesBelowSetOperations() {
    check(
        createTuple(
            createUnion(
                createTuple(_personSymType, _intSymType),
                createTuple(_carSymType, _intSymType)
            ),
            _intSymType
        ),
        "((Car, int), int) | ((Person, int), int)"
    );
    check(
        createTuple(
            createIntersection(
                createTuple(_personSymType, _intSymType),
                createTuple(_carSymType, _intSymType)
            ),
            _intSymType
        ),
        createTuple(createBottomType(), _intSymType)
    );
    check(
        createTuple(
            createIntersection(
                createTuple(
                    createUnion(
                        createTuple(_personSymType, _carSymType),
                        createTuple(_carSymType, _carSymType)
                    ),
                    _intSymType
                ),
                createTuple(
                    createTuple(_personSymType, _carSymType),
                    _intSymType
                )
            ),
            _intSymType
        ),
        "(((Person, Car), int), int)"
    );
    assertNoFindings();
  }

  @Test
  public void normalizeFunctionsWithIntersectionsAndUnions() {
    SymTypeOfUnion cup = createUnion(_carSymType, _personSymType);
    SymTypeOfIntersection cip = createIntersection(_carSymType, _personSymType);

  }

  @Test
  public void normalizeComplexTypes() {
    SymTypeOfUnion union1 = createUnion(_personSymType, _carSymType);
    SymTypeOfUnion union2 = createUnion(_personSymType, _intSymType);
    SymTypeOfIntersection inter1 = createIntersection(_personSymType, _carSymType);
    SymTypeOfIntersection inter2 = createIntersection(_personSymType, _intSymType);

    // unions
    check(createUnion(union1, _personSymType), "Car | Person");
    check(createUnion(union1, _intSymType), "Car | Person | int");
    check(createUnion(union1, union2), "Car | Person | int");

    // intersections
    check(createIntersection(inter1, _personSymType), "Car & Person");
    // "Car & Person & int"
    check(createIntersection(inter1, inter2), createBottomType());

    // combine unions and intersections
    check(createUnion(inter1, _personSymType), "Person");
    check(createUnion(inter1, _intSymType), "(Car & Person) | int");
    check(createUnion(inter1, union2), "Person | int");
    check(createUnion(inter1, union1), "Car | Person");
    // "(Car & Person) | (Person & int)"
    check(createUnion(inter1, inter2), "Car & Person");

    check(createIntersection(union1, _personSymType), "Person");
    // "(Car & int) | (Person & int)"
    check(createIntersection(union1, _intSymType), createBottomType());
    check(createIntersection(union1, union2), "Person");
    check(createIntersection(union1, inter1), "Car & Person");
    check(createIntersection(union1, inter2), createBottomType());

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

  @Test
  public void normalizeSIUnitsSimple() {
    check(_m_SISymType, "[m]");
    check(_g_SISymType, "[kg]");
    check(_s_SISymType, "[s]");
    check(_A_SISymType, "[A]");
    check(_K_SISymType, "[K]");
    check(_mol_SISymType, "[mol]");
    check(_cd_SISymType, "[cd]");
    check(_kg_SISymType, "[kg]");

    check(_Hz_SISymType, "[1/s]");
    check(_N_SISymType, "[m^1kg/s^2]");
    check(_Pa_SISymType, "[kg/s^2m]");
    check(_J_SISymType, "[m^2kg/s^2]");
    check(_W_SISymType, "[m^2kg/s^3]");
    check(_C_SISymType, "[s^1A]");
    check(_V_SISymType, "[m^2kg/s^3A]");
    check(_F_SISymType, "[s^4A^2/m^2kg]");
    check(_Ohm_SISymType, "[m^2kg/s^3A^2]");
    check(_ohm_SISymType, "[m^2kg/s^3A^2]");
    check(_S_SISymType, "[s^3A^2/m^2kg]");
    check(_Wb_SISymType, "[m^2kg/s^2A]");
    check(_T_SISymType, "[kg/s^2A]");
    check(_H_SISymType, "[m^2kg/s^2A^2]");
    check(_lm_SISymType, "[cd]");
    check(_lx_SISymType, "[cd/m^2]");
    check(_Bq_SISymType, "[1/s]");
    check(_Gy_SISymType, "[m^2/s^2]");
    check(_Sv_SISymType, "[m^2/s^2]");
    check(_kat_SISymType, "[mol/s]");
    check(_l_SISymType, "[m^3]");
    check(_L_SISymType, "[m^3]");
    check(_min_SISymType, "[s]");
    check(_h_SISymType, "[s]");
    check(_d_SISymType, "[s]");
    check(_ha_SISymType, "[m^2]");
    check(_t_SISymType, "[kg]");
    check(_au_SISymType, "[m]");
    check(_eV_SISymType, "[m^2kg/s^2]");
    check(_Da_SISymType, "[kg]");
    check(_u_SISymType, "[kg]");
    check(_celsius_SISymType, "[K]");
    check(_fahrenheit_SISymType, "[K]");
    check(_Np_SISymType, "[1]");
    check(_B_SISymType, "[1]");
    check(_dB_SISymType, "[1]");
    check(_degSym_SISymType, "[1]");
    check(_deg_SISymType, "[1]");
    check(_rad_SISymType, "[1]");
    check(_sr_SISymType, "[1]");
    assertNoFindings();
  }

  @Test
  public void normalizeSIUnitsComplex() {
    check(
        createSIUnit(List.of(createSIUnitBasic("s", 2)), List.of()),
        "[s^2]"
    );
    check(
        createSIUnit(List.of(createSIUnitBasic("s", -1)), List.of()),
        "[1/s]"
    );
    check(
        createSIUnit(List.of(createSIUnitBasic("s", -2)), List.of()),
        "[1/s^2]"
    );
    check(
        createSIUnit(List.of(), List.of(_s_SIUnitBasic)),
        "[1/s]"
    );
    check(
        createSIUnit(List.of(createSIUnitBasic("s", 0)), List.of()),
        "[1]"
    );

    check(createSIUnit(List.of(createSIUnitBasic("N", 3)), List.of()),
        "[m^3kg^3/s^6]"
    );
    check(createSIUnit(List.of(createSIUnitBasic("N", -2)), List.of()),
        "[s^4/m^2kg^2]"
    );
    check(createSIUnit(List.of(createSIUnitBasic("N", 0)), List.of()),
        "[1]"
    );

    check(createSIUnit(
            List.of(createSIUnitBasic("s", 2)),
            List.of(_g_SIUnitBasic)
        ),
        "[s^2/kg]"
    );
    check(createSIUnit(
            List.of(createSIUnitBasic("s", 2)),
            List.of(_min_SIUnitBasic)
        ),
        "[s]"
    );
    check(createSIUnit(
            List.of(_kg_SIUnitBasic, createSIUnitBasic("s", 2)),
            List.of(_min_SIUnitBasic, _m_SIUnitBasic)
        ),
        "[s^1kg/m]"
    );
    assertNoFindings();
  }

  @Test
  public void normalizeNumericWithSIUnits() {
    check(createNumericWithSIUnit(
            List.of(_s_SIUnitBasic),
            List.of(),
            _intSymType
        ),
        "[s]<int>"
    );
    check(createNumericWithSIUnit(
            List.of(createSIUnitBasic("s", 2)),
            List.of(),
            _intSymType
        ),
        "[s^2]<int>"
    );
    check(createNumericWithSIUnit(
            List.of(createSIUnitBasic("s", -2)),
            List.of(),
            _intSymType
        ),
        "[1/s^2]<int>"
    );
    check(createNumericWithSIUnit(
            List.of(_s_SIUnitBasic),
            List.of(_s_SIUnitBasic),
            _intSymType
        ),
        "int"
    );
  }

  public void check(SymTypeExpression type, String expectedPrint) {
    SymTypeExpression normalized = visitor.calculate(type);
    assertNoFindings();
    Assertions.assertEquals(expectedPrint, normalized.printFullName());
  }

  public void check(SymTypeExpression type, SymTypeExpression expectedType) {
    check(type, expectedType.printFullName());
  }

}
