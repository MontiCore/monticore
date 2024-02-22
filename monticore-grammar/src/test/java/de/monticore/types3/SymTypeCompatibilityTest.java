/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types3;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsGlobalScope;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsScope;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsGlobalScope;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.SymTypeOfNumericWithSIUnit;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types.check.SymTypeOfRegEx;
import de.monticore.types.check.SymTypeVariable;
import de.monticore.types3.util.DefsTypesForTests;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static de.monticore.types.check.SymTypeExpressionFactory.createFunction;
import static de.monticore.types.check.SymTypeExpressionFactory.createGenerics;
import static de.monticore.types.check.SymTypeExpressionFactory.createIntersection;
import static de.monticore.types.check.SymTypeExpressionFactory.createNumericWithSIUnit;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeArray;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeObject;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeOfNull;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeRegEx;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeVariable;
import static de.monticore.types.check.SymTypeExpressionFactory.createUnion;
import static de.monticore.types3.util.DefsTypesForTests.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SymTypeCompatibilityTest extends AbstractTypeTest {

  protected ICombineExpressionsWithLiteralsScope scope;

  @Before
  public void init() {
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    DefsTypesForTests.setup();
    SymTypeRelations.init();
  }

  @Test
  public void isCompatiblePrimitives() {
    assertFalse(SymTypeRelations.isCompatible(_booleanSymType, _doubleSymType));
    assertFalse(SymTypeRelations.isCompatible(_booleanSymType, _floatSymType));
    assertFalse(SymTypeRelations.isCompatible(_booleanSymType, _longSymType));
    assertFalse(SymTypeRelations.isCompatible(_booleanSymType, _intSymType));
    assertFalse(SymTypeRelations.isCompatible(_booleanSymType, _charSymType));
    assertFalse(SymTypeRelations.isCompatible(_booleanSymType, _shortSymType));
    assertFalse(SymTypeRelations.isCompatible(_booleanSymType, _byteSymType));
    assertTrue(SymTypeRelations.isCompatible(_booleanSymType, _booleanSymType));

    assertTrue(SymTypeRelations.isCompatible(_doubleSymType, _doubleSymType));
    assertTrue(SymTypeRelations.isCompatible(_doubleSymType, _floatSymType));
    assertTrue(SymTypeRelations.isCompatible(_doubleSymType, _longSymType));
    assertTrue(SymTypeRelations.isCompatible(_doubleSymType, _intSymType));
    assertTrue(SymTypeRelations.isCompatible(_doubleSymType, _charSymType));
    assertTrue(SymTypeRelations.isCompatible(_doubleSymType, _shortSymType));
    assertTrue(SymTypeRelations.isCompatible(_doubleSymType, _byteSymType));
    assertFalse(SymTypeRelations.isCompatible(_doubleSymType, _booleanSymType));

    assertFalse(SymTypeRelations.isCompatible(_floatSymType, _doubleSymType));
    assertTrue(SymTypeRelations.isCompatible(_floatSymType, _floatSymType));
    assertTrue(SymTypeRelations.isCompatible(_floatSymType, _longSymType));
    assertTrue(SymTypeRelations.isCompatible(_floatSymType, _intSymType));
    assertTrue(SymTypeRelations.isCompatible(_floatSymType, _charSymType));
    assertTrue(SymTypeRelations.isCompatible(_floatSymType, _shortSymType));
    assertTrue(SymTypeRelations.isCompatible(_floatSymType, _byteSymType));
    assertFalse(SymTypeRelations.isCompatible(_floatSymType, _booleanSymType));

    assertFalse(SymTypeRelations.isCompatible(_longSymType, _doubleSymType));
    assertFalse(SymTypeRelations.isCompatible(_longSymType, _floatSymType));
    assertTrue(SymTypeRelations.isCompatible(_longSymType, _longSymType));
    assertTrue(SymTypeRelations.isCompatible(_longSymType, _intSymType));
    assertTrue(SymTypeRelations.isCompatible(_longSymType, _charSymType));
    assertTrue(SymTypeRelations.isCompatible(_longSymType, _shortSymType));
    assertTrue(SymTypeRelations.isCompatible(_longSymType, _byteSymType));
    assertFalse(SymTypeRelations.isCompatible(_longSymType, _booleanSymType));

    assertFalse(SymTypeRelations.isCompatible(_intSymType, _doubleSymType));
    assertFalse(SymTypeRelations.isCompatible(_intSymType, _floatSymType));
    assertFalse(SymTypeRelations.isCompatible(_intSymType, _longSymType));
    assertTrue(SymTypeRelations.isCompatible(_intSymType, _intSymType));
    assertTrue(SymTypeRelations.isCompatible(_intSymType, _charSymType));
    assertTrue(SymTypeRelations.isCompatible(_intSymType, _shortSymType));
    assertTrue(SymTypeRelations.isCompatible(_intSymType, _byteSymType));
    assertFalse(SymTypeRelations.isCompatible(_intSymType, _booleanSymType));

    assertFalse(SymTypeRelations.isCompatible(_charSymType, _doubleSymType));
    assertFalse(SymTypeRelations.isCompatible(_charSymType, _floatSymType));
    assertFalse(SymTypeRelations.isCompatible(_charSymType, _longSymType));
    assertFalse(SymTypeRelations.isCompatible(_charSymType, _intSymType));
    assertTrue(SymTypeRelations.isCompatible(_charSymType, _charSymType));
    assertFalse(SymTypeRelations.isCompatible(_charSymType, _shortSymType));
    assertFalse(SymTypeRelations.isCompatible(_charSymType, _byteSymType));
    assertFalse(SymTypeRelations.isCompatible(_charSymType, _booleanSymType));

    assertFalse(SymTypeRelations.isCompatible(_shortSymType, _doubleSymType));
    assertFalse(SymTypeRelations.isCompatible(_shortSymType, _floatSymType));
    assertFalse(SymTypeRelations.isCompatible(_shortSymType, _longSymType));
    assertFalse(SymTypeRelations.isCompatible(_shortSymType, _intSymType));
    assertFalse(SymTypeRelations.isCompatible(_shortSymType, _charSymType));
    assertTrue(SymTypeRelations.isCompatible(_shortSymType, _shortSymType));
    assertTrue(SymTypeRelations.isCompatible(_shortSymType, _byteSymType));
    assertFalse(SymTypeRelations.isCompatible(_shortSymType, _booleanSymType));

    assertFalse(SymTypeRelations.isCompatible(_byteSymType, _doubleSymType));
    assertFalse(SymTypeRelations.isCompatible(_byteSymType, _floatSymType));
    assertFalse(SymTypeRelations.isCompatible(_byteSymType, _longSymType));
    assertFalse(SymTypeRelations.isCompatible(_byteSymType, _intSymType));
    assertFalse(SymTypeRelations.isCompatible(_byteSymType, _charSymType));
    assertFalse(SymTypeRelations.isCompatible(_byteSymType, _shortSymType));
    assertTrue(SymTypeRelations.isCompatible(_byteSymType, _byteSymType));
    assertFalse(SymTypeRelations.isCompatible(_byteSymType, _booleanSymType));

    assertFalse(SymTypeRelations.isCompatible(_booleanSymType, _personSymType));
    assertTrue(SymTypeRelations.isCompatible(_booleanSymType, _BooleanSymType));
    assertNoFindings();
  }

  @Test
  public void isSubTypePrimitives() {
    assertFalse(SymTypeRelations.isSubTypeOf(_booleanSymType, _doubleSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_booleanSymType, _floatSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_booleanSymType, _longSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_booleanSymType, _intSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_booleanSymType, _charSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_booleanSymType, _shortSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_booleanSymType, _byteSymType));
    assertTrue(SymTypeRelations.isSubTypeOf(_booleanSymType, _booleanSymType));

    assertTrue(SymTypeRelations.isSubTypeOf(_doubleSymType, _doubleSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_doubleSymType, _floatSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_doubleSymType, _longSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_doubleSymType, _intSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_doubleSymType, _charSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_doubleSymType, _shortSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_doubleSymType, _byteSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_doubleSymType, _booleanSymType));

    assertTrue(SymTypeRelations.isSubTypeOf(_floatSymType, _doubleSymType));
    assertTrue(SymTypeRelations.isSubTypeOf(_floatSymType, _floatSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_floatSymType, _longSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_floatSymType, _intSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_floatSymType, _charSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_floatSymType, _shortSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_floatSymType, _byteSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_floatSymType, _booleanSymType));

    assertTrue(SymTypeRelations.isSubTypeOf(_longSymType, _doubleSymType));
    assertTrue(SymTypeRelations.isSubTypeOf(_longSymType, _floatSymType));
    assertTrue(SymTypeRelations.isSubTypeOf(_longSymType, _longSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_longSymType, _intSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_longSymType, _charSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_longSymType, _shortSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_longSymType, _byteSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_longSymType, _booleanSymType));

    assertTrue(SymTypeRelations.isSubTypeOf(_intSymType, _doubleSymType));
    assertTrue(SymTypeRelations.isSubTypeOf(_intSymType, _floatSymType));
    assertTrue(SymTypeRelations.isSubTypeOf(_intSymType, _longSymType));
    assertTrue(SymTypeRelations.isSubTypeOf(_intSymType, _intSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_intSymType, _charSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_intSymType, _shortSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_intSymType, _byteSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_intSymType, _booleanSymType));

    assertTrue(SymTypeRelations.isSubTypeOf(_charSymType, _doubleSymType));
    assertTrue(SymTypeRelations.isSubTypeOf(_charSymType, _floatSymType));
    assertTrue(SymTypeRelations.isSubTypeOf(_charSymType, _longSymType));
    assertTrue(SymTypeRelations.isSubTypeOf(_charSymType, _intSymType));
    assertTrue(SymTypeRelations.isSubTypeOf(_charSymType, _charSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_charSymType, _shortSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_charSymType, _byteSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_charSymType, _booleanSymType));

    assertTrue(SymTypeRelations.isSubTypeOf(_shortSymType, _doubleSymType));
    assertTrue(SymTypeRelations.isSubTypeOf(_shortSymType, _floatSymType));
    assertTrue(SymTypeRelations.isSubTypeOf(_shortSymType, _longSymType));
    assertTrue(SymTypeRelations.isSubTypeOf(_shortSymType, _intSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_shortSymType, _charSymType));
    assertTrue(SymTypeRelations.isSubTypeOf(_shortSymType, _shortSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_shortSymType, _byteSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_shortSymType, _booleanSymType));

    assertTrue(SymTypeRelations.isSubTypeOf(_byteSymType, _doubleSymType));
    assertTrue(SymTypeRelations.isSubTypeOf(_byteSymType, _floatSymType));
    assertTrue(SymTypeRelations.isSubTypeOf(_byteSymType, _longSymType));
    assertTrue(SymTypeRelations.isSubTypeOf(_byteSymType, _intSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_byteSymType, _charSymType));
    assertTrue(SymTypeRelations.isSubTypeOf(_byteSymType, _shortSymType));
    assertTrue(SymTypeRelations.isSubTypeOf(_byteSymType, _byteSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_byteSymType, _booleanSymType));

    assertFalse(SymTypeRelations.isSubTypeOf(_booleanSymType, _personSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_booleanSymType, _BooleanSymType));
    assertNoFindings();
  }

  @Test
  public void isCompatibleObjects() {
    assertTrue(SymTypeRelations.isCompatible(_personSymType, _personSymType));
    assertTrue(SymTypeRelations.isCompatible(_personSymType, _studentSymType));
    assertTrue(SymTypeRelations.isCompatible(_personSymType, _csStudentSymType));
    assertTrue(SymTypeRelations.isCompatible(_personSymType, _teacherSymType));

    assertFalse(SymTypeRelations.isCompatible(_studentSymType, _personSymType));
    assertTrue(SymTypeRelations.isCompatible(_studentSymType, _studentSymType));
    assertTrue(SymTypeRelations.isCompatible(_studentSymType, _csStudentSymType));
    assertFalse(SymTypeRelations.isCompatible(_studentSymType, _teacherSymType));

    assertFalse(SymTypeRelations.isCompatible(_csStudentSymType, _personSymType));
    assertFalse(SymTypeRelations.isCompatible(_csStudentSymType, _studentSymType));
    assertTrue(SymTypeRelations.isCompatible(_csStudentSymType, _csStudentSymType));
    assertFalse(SymTypeRelations.isCompatible(_csStudentSymType, _teacherSymType));

    assertFalse(SymTypeRelations.isCompatible(_teacherSymType, _personSymType));
    assertFalse(SymTypeRelations.isCompatible(_teacherSymType, _studentSymType));
    assertFalse(SymTypeRelations.isCompatible(_teacherSymType, _csStudentSymType));
    assertTrue(SymTypeRelations.isCompatible(_teacherSymType, _teacherSymType));

    // String
    assertTrue(SymTypeRelations.isCompatible(_boxedString, _boxedString));
    assertTrue(SymTypeRelations.isCompatible(_boxedString, _unboxedString));
    assertTrue(SymTypeRelations.isCompatible(_unboxedString, _boxedString));
    assertTrue(SymTypeRelations.isCompatible(_unboxedString, _unboxedString));

    // diverse types
    assertFalse(SymTypeRelations.isCompatible(_personSymType, _intSymType));
    assertFalse(SymTypeRelations.isCompatible(
        _personSymType,
        createTypeArray(_personSymType, 1))
    );
    assertTrue(SymTypeRelations.isCompatible(
        _personSymType,
        createTypeArray(_personSymType, 0))
    );
    assertFalse(SymTypeRelations.isCompatible(_personSymType, _unboxedMapSymType));
    assertFalse(SymTypeRelations.isCompatible(_personSymType, _BooleanSymType));
    assertNoFindings();
  }

  @Test
  public void isCompatibleRegEx() {
    SymTypeOfRegEx regEx1 = createTypeRegEx("gr(a|e)y");
    SymTypeOfRegEx regEx2 = createTypeRegEx("gr(e|a)y");
    assertTrue(SymTypeRelations.isCompatible(regEx1, regEx1));
    assertTrue(SymTypeRelations.isCompatible(regEx2, regEx1));
    assertTrue(SymTypeRelations.isCompatible(_unboxedString, regEx1));
    assertTrue(SymTypeRelations.isCompatible(regEx1, _unboxedString));
    assertTrue(SymTypeRelations.isCompatible(_boxedString, regEx1));
    assertTrue(SymTypeRelations.isCompatible(regEx1, _boxedString));
  }

  @Test
  public void isCompatibleGenerics() {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    // List<Person>, List<int>, List<boolean>
    SymTypeOfGenerics listOfPerson
        = createGenerics(_unboxedListSymType.getTypeInfo(), _personSymType);
    SymTypeOfGenerics listOfInt
        = createGenerics(_unboxedListSymType.getTypeInfo(), _intSymType);
    SymTypeOfGenerics listOfBoolean
        = createGenerics(_unboxedListSymType.getTypeInfo(), _booleanSymType);
    // SubPersonList extends List<Person>
    TypeSymbol subPersonSym =
        inScope(gs, type("SubPersonList", List.of(listOfPerson)));
    SymTypeOfObject subPersonList = createTypeObject(subPersonSym);
    // LinkedList<Person>
    SymTypeOfGenerics linkedListOfPerson =
        createGenerics(_linkedListSymType.getTypeInfo(), _personSymType);

    assertTrue(SymTypeRelations.isCompatible(listOfPerson, listOfPerson));
    assertTrue(SymTypeRelations.isCompatible(listOfPerson, subPersonList));
    assertTrue(SymTypeRelations.isCompatible(listOfPerson, linkedListOfPerson));

    assertFalse(SymTypeRelations.isCompatible(listOfInt, _intSymType));
    assertFalse(SymTypeRelations.isCompatible(listOfInt, listOfBoolean));
    assertFalse(SymTypeRelations.isCompatible(listOfBoolean, listOfInt));
    assertFalse(SymTypeRelations.isCompatible(listOfBoolean, listOfPerson));
    assertFalse(SymTypeRelations.isCompatible(listOfPerson, listOfBoolean));
    assertFalse(SymTypeRelations.isCompatible(listOfBoolean, subPersonList));
    assertFalse(SymTypeRelations.isCompatible(subPersonList, listOfPerson));
    assertNoFindings();
  }

  @Test
  public void isSubTypeGenericsDoNotIgnoreTypeArguments() {
    //s. https://git.rwth-aachen.de/monticore/monticore/-/issues/2977
    // Test if we do not ignore TypeVariables in the description of the supertypes.
    // e.g., given HashMap<K, V> extends Map<K, V>
    // do not ignore the identity of the variables, e.g., do not allow
    // Map<Integer, String> x = new HashMap<String, Integer>();
    SymTypeOfGenerics iSMap = createGenerics(
        _boxedMapSymType.getTypeInfo(), _IntegerSymType, _boxedString);
    SymTypeOfGenerics iSHashMap = createGenerics(
        _hashMapSymType.getTypeInfo(), _IntegerSymType, _boxedString);
    SymTypeOfGenerics sIHashMap = createGenerics(
        _hashMapSymType.getTypeInfo(), _boxedString, _IntegerSymType);

    assertTrue(SymTypeRelations.isCompatible(iSMap, iSHashMap));
    assertFalse(SymTypeRelations.isCompatible(iSMap, sIHashMap));
    assertNoFindings();
  }

  @Test
  public void isCompatibleUnions() {
    assertTrue(SymTypeRelations.isCompatible(
        createUnion(_personSymType, _carSymType), _personSymType
    ));
    assertTrue(SymTypeRelations.isCompatible(
        createUnion(_personSymType, _carSymType),
        createUnion(_personSymType, _carSymType)
    ));
    assertTrue(SymTypeRelations.isCompatible(
        _personSymType, createUnion(_studentSymType, _teacherSymType)
    ));
    assertFalse(SymTypeRelations.isCompatible(
        _personSymType, createUnion(_studentSymType, _carSymType)
    ));
    assertFalse(SymTypeRelations.isCompatible(
        createUnion(_studentSymType, _teacherSymType), _personSymType
    ));
    assertNoFindings();
  }

  @Test
  public void IsCompatibleIntersections() {
    assertTrue(SymTypeRelations.isCompatible(
        createIntersection(_personSymType, _carSymType),
        createIntersection(_personSymType, _carSymType)
    ));
    assertTrue(SymTypeRelations.isCompatible(
        _personSymType, createIntersection(_studentSymType, _teacherSymType)
    ));
    assertTrue(SymTypeRelations.isCompatible(
        _personSymType, createIntersection(_personSymType, _carSymType)
    ));
    assertTrue(SymTypeRelations.isCompatible(
        createIntersection(_teachableSymType, _personSymType),
        createUnion(_childSymType, _studentSymType)
    ));
    assertFalse(SymTypeRelations.isCompatible(
        createIntersection(_personSymType, _carSymType), _personSymType
    ));
    assertNoFindings();
  }

  @Test
  public void isCompatibleFunctions() {
    assertTrue(SymTypeRelations.isCompatible(
        createFunction(_personSymType), createFunction(_personSymType)
    ));
    assertTrue(SymTypeRelations.isCompatible(
        createFunction(_personSymType, _intSymType),
        createFunction(_personSymType, _intSymType)
    ));
    assertTrue(SymTypeRelations.isCompatible(
        createFunction(_personSymType, List.of(_intSymType), true),
        createFunction(_personSymType, List.of(_intSymType), true)
    ));
    assertTrue(SymTypeRelations.isCompatible(
        createFunction(_studentSymType, _intSymType),
        createFunction(_personSymType, _intSymType)
    ));
    assertTrue(SymTypeRelations.isCompatible(
        createFunction(_personSymType, _longSymType),
        createFunction(_personSymType, _intSymType)
    ));
    assertTrue(SymTypeRelations.isCompatible(
        createFunction(_personSymType, _longSymType),
        createFunction(_personSymType, List.of(_intSymType), true)
    ));
    assertTrue(SymTypeRelations.isCompatible(
        createFunction(_personSymType, _intSymType, _intSymType),
        createFunction(_personSymType, List.of(_intSymType), true)
    ));
    assertTrue(SymTypeRelations.isCompatible(
        createFunction(_personSymType),
        createFunction(_personSymType, List.of(_intSymType), true)
    ));
    assertFalse(SymTypeRelations.isCompatible(
        createFunction(_personSymType), createFunction(_carSymType)
    ));
    assertFalse(SymTypeRelations.isCompatible(
        createFunction(_personSymType, _intSymType),
        createFunction(_studentSymType, _intSymType)
    ));
    assertFalse(SymTypeRelations.isCompatible(
        createFunction(_personSymType, _intSymType),
        createFunction(_personSymType, _longSymType)
    ));
    assertFalse(SymTypeRelations.isCompatible(
        createFunction(_personSymType, List.of(_intSymType), true),
        createFunction(_personSymType, _intSymType)
    ));

    assertNoFindings();
  }

  @Test
  public void isCompatibleNumericsAndSIUnits() {
    assertFalse(SymTypeRelations.isCompatible(_s_SISymType, _intSymType));
    assertFalse(SymTypeRelations.isCompatible(_intSymType, _s_SISymType));
    // TBD: SIUnit of dimension one
  }

  @Test
  public void isCompatibleNumericsAndNumericsWithSIUnits() {
    SymTypeOfNumericWithSIUnit deg_float_SISymType =
        createNumericWithSIUnit(_deg_SISymType, _floatSymType);
    assertFalse(SymTypeRelations.isCompatible(_s_int_SISymType, _intSymType));
    assertFalse(SymTypeRelations.isCompatible(_intSymType, _s_int_SISymType));
    assertTrue(SymTypeRelations.isCompatible(_deg_int_SISymType, _intSymType));
    assertTrue(SymTypeRelations.isCompatible(_intSymType, _deg_int_SISymType));
    assertTrue(SymTypeRelations.isCompatible(deg_float_SISymType, _intSymType));
    assertFalse(SymTypeRelations.isCompatible(_intSymType, deg_float_SISymType));
  }

  @Test
  public void isCompatibleNumericWithSIUnits() {
    SymTypeOfNumericWithSIUnit s_float_SISymType =
        createNumericWithSIUnit(_s_SISymType, _floatSymType);
    SymTypeOfNumericWithSIUnit deg_float_SISymType =
        createNumericWithSIUnit(_deg_SISymType, _floatSymType);

    assertTrue(SymTypeRelations.isCompatible(_s_int_SISymType, _s_int_SISymType));
    assertTrue(SymTypeRelations.isCompatible(_Ohm_int_SISymType, _Ohm_int_SISymType));
    assertFalse(SymTypeRelations.isCompatible(_s_int_SISymType, _A_int_SISymType));
    assertFalse(SymTypeRelations.isCompatible(_A_int_SISymType, _s_int_SISymType));
    assertTrue(SymTypeRelations.isCompatible(_deg_int_SISymType, _rad_int_SISymType));
    assertTrue(SymTypeRelations.isCompatible(_rad_int_SISymType, _deg_int_SISymType));

    assertTrue(SymTypeRelations.isCompatible(s_float_SISymType, _s_int_SISymType));
    assertFalse(SymTypeRelations.isCompatible(_s_int_SISymType, s_float_SISymType));
    assertTrue(SymTypeRelations.isCompatible(deg_float_SISymType, _rad_int_SISymType));
    assertFalse(SymTypeRelations.isCompatible(_rad_int_SISymType, deg_float_SISymType));
  }

  @Test
  public void isCompatibleSIUnits() {
    assertTrue(SymTypeRelations.isCompatible(_s_SISymType, _s_SISymType));
    assertTrue(SymTypeRelations.isCompatible(_Ohm_SISymType, _Ohm_SISymType));
    assertFalse(SymTypeRelations.isCompatible(_s_SISymType, _A_SISymType));
    assertFalse(SymTypeRelations.isCompatible(_A_SISymType, _s_SISymType));
    assertTrue(SymTypeRelations.isCompatible(_deg_SISymType, _rad_SISymType));
    assertTrue(SymTypeRelations.isCompatible(_rad_SISymType, _deg_SISymType));
    // cannot combine SIUnits with NumericWithSIUnits
    assertFalse(SymTypeRelations.isCompatible(_s_SISymType, _s_int_SISymType));
    assertFalse(SymTypeRelations.isCompatible(_s_int_SISymType, _s_SISymType));
  }

  @Test
  public void isSubTypeBottom() {
    // bottom is subType of EVERY other type
    assertTrue(SymTypeRelations.isSubTypeOf(_bottomType, _bottomType));
    assertTrue(SymTypeRelations.isSubTypeOf(_bottomType, _topType));
    assertTrue(SymTypeRelations.isSubTypeOf(_bottomType, _intSymType));
    assertTrue(SymTypeRelations.isSubTypeOf(_bottomType, _booleanSymType));
    assertTrue(SymTypeRelations.isSubTypeOf(_bottomType, _IntegerSymType));
    assertTrue(SymTypeRelations.isSubTypeOf(_bottomType, _personSymType));
    assertTrue(SymTypeRelations.isSubTypeOf(_bottomType, _nullSymType));
    assertTrue(SymTypeRelations.isSubTypeOf(_bottomType, _unboxedListSymType));
    assertTrue(SymTypeRelations.isSubTypeOf(_bottomType, _unboxedListSymType));
    assertTrue(SymTypeRelations.isSubTypeOf(_bottomType, _linkedListSymType));
    // bottom is never the superType except for bottom itself
    assertFalse(SymTypeRelations.isSubTypeOf(_topType, _bottomType));
    assertFalse(SymTypeRelations.isSubTypeOf(_intSymType, _bottomType));
    assertFalse(SymTypeRelations.isSubTypeOf(_booleanSymType, _bottomType));
    assertFalse(SymTypeRelations.isSubTypeOf(_IntegerSymType, _bottomType));
    assertFalse(SymTypeRelations.isSubTypeOf(_personSymType, _bottomType));
    assertFalse(SymTypeRelations.isSubTypeOf(_nullSymType, _bottomType));
    assertFalse(SymTypeRelations.isSubTypeOf(_unboxedListSymType, _bottomType));
    assertFalse(SymTypeRelations.isSubTypeOf(_unboxedListSymType, _bottomType));
    assertFalse(SymTypeRelations.isSubTypeOf(_linkedListSymType, _bottomType));
  }

  @Test
  public void isSubTypeTop() {
    // top is superType of EVERY other type
    assertTrue(SymTypeRelations.isSubTypeOf(_bottomType, _topType));
    assertTrue(SymTypeRelations.isSubTypeOf(_topType, _topType));
    assertTrue(SymTypeRelations.isSubTypeOf(_intSymType, _topType));
    assertTrue(SymTypeRelations.isSubTypeOf(_booleanSymType, _topType));
    assertTrue(SymTypeRelations.isSubTypeOf(_IntegerSymType, _topType));
    assertTrue(SymTypeRelations.isSubTypeOf(_personSymType, _topType));
    assertTrue(SymTypeRelations.isSubTypeOf(_nullSymType, _topType));
    assertTrue(SymTypeRelations.isSubTypeOf(_unboxedListSymType, _topType));
    assertTrue(SymTypeRelations.isSubTypeOf(_unboxedListSymType, _topType));
    assertTrue(SymTypeRelations.isSubTypeOf(_linkedListSymType, _topType));
    // top is never the subType except for top itself
    assertFalse(SymTypeRelations.isSubTypeOf(_topType, _bottomType));
    assertFalse(SymTypeRelations.isSubTypeOf(_topType, _intSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_topType, _booleanSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_topType, _IntegerSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_topType, _personSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_topType, _nullSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_topType, _unboxedListSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_topType, _unboxedListSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_topType, _linkedListSymType));
  }

  @Test
  public void isCompatibleTypeVariable() {
    // Setup type variables
    IBasicSymbolsScope someScope = BasicSymbolsMill.scope();
    // unbounded, e.g., E in List<E>
    SymTypeVariable unboundedTVar = createTypeVariable(
        inScope(someScope, typeVariable("unbounded"))
    );
    SymTypeVariable unbounded2TVar = createTypeVariable(
        inScope(someScope, typeVariable("unbounded2"))
    );
    // upper bound Student, e.g., E in List<E extends Student>
    SymTypeVariable subStudentTVar =
        createTypeVariable(_bottomType, _studentSymType);
    // upper bound Person
    SymTypeVariable subPersonTVar =
        createTypeVariable(_bottomType, _personSymType);
    // lower bound Student, no direct Java representation available
    SymTypeVariable superStudentTVar =
        createTypeVariable(_studentSymType, _topType);
    // lower bound Person
    SymTypeVariable superPersonTVar =
        createTypeVariable(_personSymType, _topType);
    // lower and upper Bound
    SymTypeVariable superSSubCsSTVar =
        createTypeVariable(_csStudentSymType, _studentSymType);

    // unbounded type variable are like existential types:
    // we don't know enough to do pretty much anything with it
    assertTrue(SymTypeRelations.isCompatible(unboundedTVar, unboundedTVar));
    assertFalse(SymTypeRelations.isCompatible(unboundedTVar, unbounded2TVar));
    assertFalse(SymTypeRelations.isCompatible(unboundedTVar, _personSymType));
    assertFalse(SymTypeRelations.isCompatible(_personSymType, unboundedTVar));

    // we can assign variables if we know their upper bound
    assertTrue(SymTypeRelations.isCompatible(_personSymType, subStudentTVar));
    assertTrue(SymTypeRelations.isCompatible(_studentSymType, subStudentTVar));
    assertFalse(SymTypeRelations.isCompatible(_csStudentSymType, subStudentTVar));
    // we cannot really assign to variable if we only know their upper bounds
    assertFalse(SymTypeRelations.isCompatible(subStudentTVar, _personSymType));
    assertFalse(SymTypeRelations.isCompatible(subStudentTVar, _studentSymType));
    assertFalse(SymTypeRelations.isCompatible(subStudentTVar, _csStudentSymType));

    // we can assign to variables if we know their lower bound
    assertFalse(SymTypeRelations.isCompatible(superStudentTVar, _personSymType));
    assertTrue(SymTypeRelations.isCompatible(superStudentTVar, _studentSymType));
    assertTrue(SymTypeRelations.isCompatible(superStudentTVar, _csStudentSymType));
    // we cannot really assign variables if we only know their lower bounds
    assertFalse(SymTypeRelations.isCompatible(_personSymType, superStudentTVar));
    assertFalse(SymTypeRelations.isCompatible(_studentSymType, superStudentTVar));
    assertFalse(SymTypeRelations.isCompatible(_csStudentSymType, superStudentTVar));

    // two single bounded variables
    assertTrue(SymTypeRelations.isCompatible(superStudentTVar, subStudentTVar));
    assertTrue(SymTypeRelations.isCompatible(superPersonTVar, subStudentTVar));
    assertFalse(SymTypeRelations.isCompatible(superStudentTVar, subPersonTVar));
    assertFalse(SymTypeRelations.isCompatible(subPersonTVar, subStudentTVar));
    assertFalse(SymTypeRelations.isCompatible(subStudentTVar, subPersonTVar));

    // in case of upper AND lower bound set,
    // we can assign and assign to the type variable
    // assign to:
    assertFalse(SymTypeRelations.isCompatible(superSSubCsSTVar, _personSymType));
    assertFalse(SymTypeRelations.isCompatible(superSSubCsSTVar, _studentSymType));
    assertTrue(SymTypeRelations.isCompatible(superSSubCsSTVar, _csStudentSymType));
    assertTrue(SymTypeRelations.isCompatible(superSSubCsSTVar, _firstSemesterCsStudentSymType));
    // assign:
    assertTrue(SymTypeRelations.isCompatible(_personSymType, superSSubCsSTVar));
    assertTrue(SymTypeRelations.isCompatible(_studentSymType, superSSubCsSTVar));
    assertFalse(SymTypeRelations.isCompatible(_csStudentSymType, superSSubCsSTVar));
    assertFalse(SymTypeRelations.isCompatible(_firstSemesterCsStudentSymType, superSSubCsSTVar));

    assertNoFindings();
  }

  @Test
  public void isCompatibleTypeVariableRecursive() {
    // check that we can handle recursively defined generics,
    // e.g., A<T extends A<T>>
    assertTrue(SymTypeRelations.isCompatible(_simpleCrtSymType, _simpleCrtSymType));
    assertTrue(SymTypeRelations.isCompatible(_graphSymType, _graphSymType));
    assertTrue(SymTypeRelations.isCompatible(_graphNodeSymType, _graphNodeSymType));
    assertTrue(SymTypeRelations.isCompatible(_graphEdgeSymType, _graphEdgeSymType));
  }

  @Test
  public void isCompatibleUpperBoundedGenerics() {
    ICombineExpressionsWithLiteralsGlobalScope gs =
        CombineExpressionsWithLiteralsMill.globalScope();
    // List<Person>
    SymTypeOfGenerics pList = createGenerics(
        _boxedListSymType.getTypeInfo(), _personSymType);
    // List<Student>
    SymTypeOfGenerics sList = createGenerics(
        _boxedListSymType.getTypeInfo(), _studentSymType);
    // List<? extends Student>
    SymTypeOfGenerics sSubList = createGenerics(
        _boxedListSymType.getTypeInfo(),
        createTypeVariable(_bottomType, _studentSymType)
    );
    // List<? extends Person>
    SymTypeOfGenerics pSubList = createGenerics(
        _boxedListSymType.getTypeInfo(),
        createTypeVariable(_bottomType, _personSymType)
    );
    // LinkedList<? extends Student>
    SymTypeOfGenerics sSubLinkedList = createGenerics(
        _linkedListSymType.getTypeInfo(),
        createTypeVariable(_bottomType, _studentSymType)
    );
    // LinkedList<? extends Person>
    SymTypeOfGenerics pSubLinkedList = createGenerics(
        _linkedListSymType.getTypeInfo(),
        createTypeVariable(_bottomType, _personSymType)
    );

    assertFalse(SymTypeRelations.isCompatible(pList, sList));
    assertFalse(SymTypeRelations.isCompatible(sList, pList));
    assertFalse(SymTypeRelations.isCompatible(pList, sSubList));
    assertFalse(SymTypeRelations.isCompatible(sList, sSubList));

    assertTrue(SymTypeRelations.isCompatible(pSubList, pList));
    assertTrue(SymTypeRelations.isCompatible(pSubList, sList));
    assertTrue(SymTypeRelations.isCompatible(pSubList, pSubList));
    assertTrue(SymTypeRelations.isCompatible(pSubList, sSubList));
    assertFalse(SymTypeRelations.isCompatible(sSubList, pList));
    assertTrue(SymTypeRelations.isCompatible(sSubList, sList));
    assertFalse(SymTypeRelations.isCompatible(sSubList, pSubList));
    assertTrue(SymTypeRelations.isCompatible(sSubList, sSubList));

    assertTrue(SymTypeRelations.isCompatible(pSubList, pSubLinkedList));
    assertTrue(SymTypeRelations.isCompatible(pSubList, sSubLinkedList));
    assertFalse(SymTypeRelations.isCompatible(sSubList, pSubLinkedList));
    assertTrue(SymTypeRelations.isCompatible(sSubList, sSubLinkedList));

    assertNoFindings();
  }

  @Test
  public void isCompatibleLowerBoundedGenerics() {
    ICombineExpressionsWithLiteralsGlobalScope gs =
        CombineExpressionsWithLiteralsMill.globalScope();
    // List<Person>
    SymTypeOfGenerics pList = createGenerics(
        _boxedListSymType.getTypeInfo(), _personSymType);
    // List<Student>
    SymTypeOfGenerics sList = createGenerics(
        _boxedListSymType.getTypeInfo(), _studentSymType);
    // List<? super Student>
    SymTypeOfGenerics sSuperList = createGenerics(
        _boxedListSymType.getTypeInfo(),
        createTypeVariable(_studentSymType, _topType)
    );
    // List<? super Person>
    SymTypeOfGenerics pSuperList = createGenerics(
        _boxedListSymType.getTypeInfo(),
        createTypeVariable(_personSymType, _topType)
    );
    // LinkedList<? super Student>
    SymTypeOfGenerics sSuperLinkedList = createGenerics(
        _linkedListSymType.getTypeInfo(),
        createTypeVariable(_studentSymType, _topType)
    );
    // LinkedList<? super Person>
    SymTypeOfGenerics pSuperLinkedList = createGenerics(
        _linkedListSymType.getTypeInfo(),
        createTypeVariable(_personSymType, _topType)
    );

    assertFalse(SymTypeRelations.isCompatible(pList, sList));
    assertFalse(SymTypeRelations.isCompatible(sList, pList));
    assertFalse(SymTypeRelations.isCompatible(pList, sSuperList));
    assertFalse(SymTypeRelations.isCompatible(sList, sSuperList));

    assertTrue(SymTypeRelations.isCompatible(pSuperList, pList));
    assertFalse(SymTypeRelations.isCompatible(pSuperList, sList));
    assertTrue(SymTypeRelations.isCompatible(pSuperList, pSuperList));
    assertFalse(SymTypeRelations.isCompatible(pSuperList, sSuperList));
    assertTrue(SymTypeRelations.isCompatible(sSuperList, pList));
    assertTrue(SymTypeRelations.isCompatible(sSuperList, sList));
    assertTrue(SymTypeRelations.isCompatible(sSuperList, pSuperList));
    assertTrue(SymTypeRelations.isCompatible(sSuperList, sSuperList));

    assertTrue(SymTypeRelations.isCompatible(pSuperList, pSuperLinkedList));
    assertFalse(SymTypeRelations.isCompatible(pSuperList, sSuperLinkedList));
    assertTrue(SymTypeRelations.isCompatible(sSuperList, pSuperLinkedList));
    assertTrue(SymTypeRelations.isCompatible(sSuperList, sSuperLinkedList));

    assertNoFindings();
  }

  @Test
  public void nullCompatibilityAndSubTyping() {
    assertFalse(SymTypeRelations.isCompatible(_intSymType, createTypeOfNull()));
    assertTrue(SymTypeRelations.isCompatible(_IntegerSymType, createTypeOfNull()));
    assertFalse(SymTypeRelations.isSubTypeOf(createTypeOfNull(), _personSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_personSymType, createTypeOfNull()));
    assertFalse(SymTypeRelations.isSubTypeOf(_s_SISymType, _nullSymType));
    assertFalse(SymTypeRelations.isSubTypeOf(_s_int_SISymType, _nullSymType));
  }

  /**
   * tests wether the unboxed type
   * is compatible to a superclass of the boxed type, e.g.,
   * Comparable<Integer> ci = 2;
   * Iterable<Integer> ii = List<int>;
   */
  @Test
  public void isCompatibleSuperTypeOfBoxed() {
    // add superclasses, these would exist for Class2MC
    // Integer implements Comparable<Integer>
    TypeSymbol integerSym = _IntegerSymType.getTypeInfo();
    TypeVarSymbol comparableSymVar = typeVariable("T");
    TypeSymbol comparableSym =
        type("Comparable", Collections.emptyList(), List.of(comparableSymVar));
    SymTypeExpression comparableInteger = createGenerics(comparableSym, _IntegerSymType);
    integerSym.addSuperTypes(comparableInteger);
    // java.util.List<T> implements Iterable<Integer>
    TypeSymbol listSym = _boxedListSymType.getTypeInfo();
    TypeVarSymbol iterableSymVar = typeVariable("T");
    TypeSymbol iterableSym =
        type("Iterable", Collections.emptyList(), List.of(iterableSymVar));
    listSym.setSuperTypesList(List.of(
        createGenerics(iterableSym, _boxedListSymType.getArgument(0))
    ));

    assertTrue(SymTypeRelations.isCompatible(comparableInteger, _IntegerSymType));
    assertTrue(SymTypeRelations.isCompatible(
        createGenerics(iterableSym, _IntegerSymType),
        createGenerics(_unboxedListSymType.getTypeInfo(), _intSymType)
    ));
  }
}
