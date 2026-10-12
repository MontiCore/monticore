// (c) https://github.com/MontiCore/monticore
package de.monticore.types3;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.runtime.junit.AbstractMCTest;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfIntersection;
import de.monticore.types.check.SymTypeOfSIUnit;
import de.monticore.types.check.SymTypeOfUnion;
import de.monticore.types3.util.DefsTypesForTests;
import de.monticore.types3.util.SymTypeNormalizeVisitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.monticore.runtime.junit.MCAssertions.assertNoFindings;
import static de.monticore.types.check.SymTypeExpressionFactory.*;
import static de.monticore.types3.util.DefsTypesForTests.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SymTypeNormalizeVisitorTest extends AbstractMCTest {

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
  public void normalizeSIUnitsKeepsUnits() {
    // only the representation is normalized, the units are kept,
    // s. SIUnitTypeRelationsTest for normalizing to the SI base units
    for (SymTypeOfSIUnit siUnit : List.of(
        _m_SISymType, _g_SISymType, _s_SISymType, _A_SISymType,
        _K_SISymType, _mol_SISymType, _cd_SISymType, _kg_SISymType,
        _Hz_SISymType, _N_SISymType, _Pa_SISymType, _J_SISymType,
        _W_SISymType, _C_SISymType, _V_SISymType, _F_SISymType,
        _Ohm_SISymType, _ohm_SISymType, _S_SISymType, _Wb_SISymType,
        _T_SISymType, _H_SISymType, _lm_SISymType, _lx_SISymType,
        _Bq_SISymType, _Gy_SISymType, _Sv_SISymType, _kat_SISymType,
        _l_SISymType, _L_SISymType, _min_SISymType, _h_SISymType,
        _d_SISymType, _ha_SISymType, _t_SISymType, _au_SISymType,
        _eV_SISymType, _Da_SISymType, _u_SISymType, _celsius_SISymType,
        _fahrenheit_SISymType, _Np_SISymType, _B_SISymType, _dB_SISymType,
        _degSym_SISymType, _deg_SISymType, _rad_SISymType, _sr_SISymType
    )) {
      check(siUnit, siUnit.printFullName());
    }
    check(createSIUnit(List.of(createSIUnitBasic("m", "k", 1)), List.of()),
        "[km]"
    );
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
        "[N^3]"
    );
    check(createSIUnit(List.of(createSIUnitBasic("N", -2)), List.of()),
        "[1/N^2]"
    );
    check(createSIUnit(List.of(createSIUnitBasic("N", 0)), List.of()),
        "[1]"
    );

    check(createSIUnit(
            List.of(createSIUnitBasic("s", 2)),
            List.of(_g_SIUnitBasic)
        ),
        "[s^2/g]"
    );
    // only identical units cancel out
    check(createSIUnit(
            List.of(createSIUnitBasic("s", 2)),
            List.of(_s_SIUnitBasic)
        ),
        "[s]"
    );
    check(createSIUnit(
            List.of(createSIUnitBasic("s", 2)),
            List.of(_min_SIUnitBasic)
        ),
        "[s^2/min]"
    );
    check(createSIUnit(
            List.of(_m_SIUnitBasic),
            List.of(createSIUnitBasic("m", "k", 1))
        ),
        "[m/km]"
    );
    check(createSIUnit(
            List.of(createSIUnitBasic("m", "k", 1), _m_SIUnitBasic),
            List.of(_m_SIUnitBasic)
        ),
        "[km]"
    );
    // deterministic order: SI base units first, larger prefixes first
    check(createSIUnit(
            List.of(_kg_SIUnitBasic, createSIUnitBasic("s", 2)),
            List.of(_min_SIUnitBasic, _m_SIUnitBasic)
        ),
        "[s^2kg/m^1min]"
    );
    check(createSIUnit(
            List.of(_m_SIUnitBasic, createSIUnitBasic("m", "k", 1)),
            List.of()
        ),
        "[km^1m]"
    );
    check(createSIUnit(
            List.of(_J_SIUnitBasic, _m_SIUnitBasic, _N_SIUnitBasic),
            List.of()
        ),
        "[m^1N^1J]"
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
    // dimension one, but not without a unit
    check(createNumericWithSIUnit(
            List.of(_m_SIUnitBasic),
            List.of(createSIUnitBasic("m", "k", 1)),
            _intSymType
        ),
        "[m/km]<int>"
    );
    check(createNumericWithSIUnit(_deg_SISymType, _intSymType),
        "[deg]<int>"
    );
  }

  @Test
  public void normalizeUnionsOfSIUnits() {
    check(createUnion(
            createNumericWithSIUnit(_m_SISymType, _intSymType),
            createNumericWithSIUnit(_m_SISymType, _intSymType)
        ),
        "[m]<int>"
    );
    check(createUnion(
            createNumericWithSIUnit(_m_SISymType, _intSymType),
            createNumericWithSIUnit(
                List.of(createSIUnitBasic("m", "k", 1)), List.of(), _intSymType)
        ),
        "[km]<int> | [m]<int>"
    );
  }

  public void check(SymTypeExpression type, String expectedPrint) {
    SymTypeExpression normalized = visitor.calculate(type);
    assertNoFindings();
    assertEquals(expectedPrint, normalized.printFullName());
  }

  public void check(SymTypeExpression type, SymTypeExpression expectedType) {
    check(type, expectedType.printFullName());
  }

}
