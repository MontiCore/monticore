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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static de.monticore.types.check.SymTypeExpressionFactory.createFunction;
import static de.monticore.types.check.SymTypeExpressionFactory.createGenerics;
import static de.monticore.types.check.SymTypeExpressionFactory.createIntersection;
import static de.monticore.types.check.SymTypeExpressionFactory.createNumericWithSIUnit;
import static de.monticore.types.check.SymTypeExpressionFactory.createTuple;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeArray;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeObject;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeOfNull;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeRegEx;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeVariable;
import static de.monticore.types.check.SymTypeExpressionFactory.createUnion;
import static de.monticore.types.check.SymTypeExpressionFactory.createWildcard;
import static de.monticore.types3.util.DefsTypesForTests.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SymTypeCompatibilityTest extends AbstractTypeTest {

  protected ICombineExpressionsWithLiteralsScope scope;

  @BeforeEach
  public void init() {
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    DefsTypesForTests.setup();
    SymTypeRelations.init();
  }

  @Test
  public void isCompatiblePrimitives() {
    Assertions.assertFalse(SymTypeRelations.isCompatible(_booleanSymType, _doubleSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_booleanSymType, _floatSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_booleanSymType, _longSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_booleanSymType, _intSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_booleanSymType, _charSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_booleanSymType, _shortSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_booleanSymType, _byteSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_booleanSymType, _booleanSymType));

    Assertions.assertTrue(SymTypeRelations.isCompatible(_doubleSymType, _doubleSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_doubleSymType, _floatSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_doubleSymType, _longSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_doubleSymType, _intSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_doubleSymType, _charSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_doubleSymType, _shortSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_doubleSymType, _byteSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_doubleSymType, _booleanSymType));

    Assertions.assertFalse(SymTypeRelations.isCompatible(_floatSymType, _doubleSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_floatSymType, _floatSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_floatSymType, _longSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_floatSymType, _intSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_floatSymType, _charSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_floatSymType, _shortSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_floatSymType, _byteSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_floatSymType, _booleanSymType));

    Assertions.assertFalse(SymTypeRelations.isCompatible(_longSymType, _doubleSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_longSymType, _floatSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_longSymType, _longSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_longSymType, _intSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_longSymType, _charSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_longSymType, _shortSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_longSymType, _byteSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_longSymType, _booleanSymType));

    Assertions.assertFalse(SymTypeRelations.isCompatible(_intSymType, _doubleSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_intSymType, _floatSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_intSymType, _longSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_intSymType, _intSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_intSymType, _charSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_intSymType, _shortSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_intSymType, _byteSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_intSymType, _booleanSymType));

    Assertions.assertFalse(SymTypeRelations.isCompatible(_charSymType, _doubleSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_charSymType, _floatSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_charSymType, _longSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_charSymType, _intSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_charSymType, _charSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_charSymType, _shortSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_charSymType, _byteSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_charSymType, _booleanSymType));

    Assertions.assertFalse(SymTypeRelations.isCompatible(_shortSymType, _doubleSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_shortSymType, _floatSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_shortSymType, _longSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_shortSymType, _intSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_shortSymType, _charSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_shortSymType, _shortSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_shortSymType, _byteSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_shortSymType, _booleanSymType));

    Assertions.assertFalse(SymTypeRelations.isCompatible(_byteSymType, _doubleSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_byteSymType, _floatSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_byteSymType, _longSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_byteSymType, _intSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_byteSymType, _charSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_byteSymType, _shortSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_byteSymType, _byteSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_byteSymType, _booleanSymType));

    Assertions.assertFalse(SymTypeRelations.isCompatible(_booleanSymType, _personSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_booleanSymType, _BooleanSymType));
    assertNoFindings();
  }

  @Test
  public void isSubTypePrimitives() {
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_booleanSymType, _doubleSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_booleanSymType, _floatSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_booleanSymType, _longSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_booleanSymType, _intSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_booleanSymType, _charSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_booleanSymType, _shortSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_booleanSymType, _byteSymType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_booleanSymType, _booleanSymType));

    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_doubleSymType, _doubleSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_doubleSymType, _floatSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_doubleSymType, _longSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_doubleSymType, _intSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_doubleSymType, _charSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_doubleSymType, _shortSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_doubleSymType, _byteSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_doubleSymType, _booleanSymType));

    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_floatSymType, _doubleSymType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_floatSymType, _floatSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_floatSymType, _longSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_floatSymType, _intSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_floatSymType, _charSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_floatSymType, _shortSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_floatSymType, _byteSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_floatSymType, _booleanSymType));

    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_longSymType, _doubleSymType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_longSymType, _floatSymType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_longSymType, _longSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_longSymType, _intSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_longSymType, _charSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_longSymType, _shortSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_longSymType, _byteSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_longSymType, _booleanSymType));

    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_intSymType, _doubleSymType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_intSymType, _floatSymType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_intSymType, _longSymType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_intSymType, _intSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_intSymType, _charSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_intSymType, _shortSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_intSymType, _byteSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_intSymType, _booleanSymType));

    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_charSymType, _doubleSymType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_charSymType, _floatSymType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_charSymType, _longSymType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_charSymType, _intSymType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_charSymType, _charSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_charSymType, _shortSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_charSymType, _byteSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_charSymType, _booleanSymType));

    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_shortSymType, _doubleSymType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_shortSymType, _floatSymType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_shortSymType, _longSymType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_shortSymType, _intSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_shortSymType, _charSymType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_shortSymType, _shortSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_shortSymType, _byteSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_shortSymType, _booleanSymType));

    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_byteSymType, _doubleSymType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_byteSymType, _floatSymType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_byteSymType, _longSymType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_byteSymType, _intSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_byteSymType, _charSymType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_byteSymType, _shortSymType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_byteSymType, _byteSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_byteSymType, _booleanSymType));

    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_booleanSymType, _personSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_booleanSymType, _BooleanSymType));
    assertNoFindings();
  }

  @Test
  public void isCompatibleObjects() {
    Assertions.assertTrue(SymTypeRelations.isCompatible(_personSymType, _personSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_personSymType, _studentSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_personSymType, _csStudentSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_personSymType, _teacherSymType));

    Assertions.assertFalse(SymTypeRelations.isCompatible(_studentSymType, _personSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_studentSymType, _studentSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_studentSymType, _csStudentSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_studentSymType, _teacherSymType));

    Assertions.assertFalse(SymTypeRelations.isCompatible(_csStudentSymType, _personSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_csStudentSymType, _studentSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_csStudentSymType, _csStudentSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_csStudentSymType, _teacherSymType));

    Assertions.assertFalse(SymTypeRelations.isCompatible(_teacherSymType, _personSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_teacherSymType, _studentSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_teacherSymType, _csStudentSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_teacherSymType, _teacherSymType));

    // String
    Assertions.assertTrue(SymTypeRelations.isCompatible(_boxedString, _boxedString));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_boxedString, _unboxedString));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_unboxedString, _boxedString));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_unboxedString, _unboxedString));

    // diverse types
    Assertions.assertFalse(SymTypeRelations.isCompatible(_personSymType, _intSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(
        _personSymType,
        createTypeArray(_personSymType, 1)));
    Assertions.assertTrue(SymTypeRelations.isCompatible(
        _personSymType,
        createTypeArray(_personSymType, 0)));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_personSymType, _unboxedMapSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_personSymType, _BooleanSymType));
    assertNoFindings();
  }

  @Test
  public void isCompatibleRegEx() {
    SymTypeOfRegEx regEx1 = createTypeRegEx("gr(a|e)y");
    SymTypeOfRegEx regEx2 = createTypeRegEx("gr(e|a)y");
    Assertions.assertTrue(SymTypeRelations.isCompatible(regEx1, regEx1));
    Assertions.assertTrue(SymTypeRelations.isCompatible(regEx2, regEx1));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_unboxedString, regEx1));
    Assertions.assertTrue(SymTypeRelations.isCompatible(regEx1, _unboxedString));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_boxedString, regEx1));
    Assertions.assertTrue(SymTypeRelations.isCompatible(regEx1, _boxedString));
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

    Assertions.assertTrue(SymTypeRelations.isCompatible(listOfPerson, listOfPerson));
    Assertions.assertTrue(SymTypeRelations.isCompatible(listOfPerson, subPersonList));
    Assertions.assertTrue(SymTypeRelations.isCompatible(listOfPerson, linkedListOfPerson));

    Assertions.assertFalse(SymTypeRelations.isCompatible(listOfInt, _intSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(listOfInt, listOfBoolean));
    Assertions.assertFalse(SymTypeRelations.isCompatible(listOfBoolean, listOfInt));
    Assertions.assertFalse(SymTypeRelations.isCompatible(listOfBoolean, listOfPerson));
    Assertions.assertFalse(SymTypeRelations.isCompatible(listOfPerson, listOfBoolean));
    Assertions.assertFalse(SymTypeRelations.isCompatible(listOfBoolean, subPersonList));
    Assertions.assertFalse(SymTypeRelations.isCompatible(subPersonList, listOfPerson));
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

    Assertions.assertTrue(SymTypeRelations.isCompatible(iSMap, iSHashMap));
    Assertions.assertFalse(SymTypeRelations.isCompatible(iSMap, sIHashMap));
    assertNoFindings();
  }

  @Test
  public void isCompatibleUnions() {
    Assertions.assertTrue(SymTypeRelations.isCompatible(
        createUnion(_personSymType, _carSymType), _personSymType
    ));
    Assertions.assertTrue(SymTypeRelations.isCompatible(
        createUnion(_personSymType, _carSymType),
        createUnion(_personSymType, _carSymType)
    ));
    Assertions.assertTrue(SymTypeRelations.isCompatible(
        _personSymType, createUnion(_studentSymType, _teacherSymType)
    ));
    Assertions.assertFalse(SymTypeRelations.isCompatible(
        _personSymType, createUnion(_studentSymType, _carSymType)
    ));
    Assertions.assertFalse(SymTypeRelations.isCompatible(
        createUnion(_studentSymType, _teacherSymType), _personSymType
    ));
    assertNoFindings();
  }

  @Test
  public void IsCompatibleIntersections() {
    Assertions.assertTrue(SymTypeRelations.isCompatible(
        createIntersection(_personSymType, _carSymType),
        createIntersection(_personSymType, _carSymType)
    ));
    Assertions.assertTrue(SymTypeRelations.isCompatible(
        _personSymType, createIntersection(_studentSymType, _teacherSymType)
    ));
    Assertions.assertTrue(SymTypeRelations.isCompatible(
        _personSymType, createIntersection(_personSymType, _carSymType)
    ));
    Assertions.assertTrue(SymTypeRelations.isCompatible(
        createIntersection(_teachableSymType, _personSymType),
        createUnion(_childSymType, _studentSymType)
    ));
    Assertions.assertFalse(SymTypeRelations.isCompatible(
        createIntersection(_personSymType, _carSymType), _personSymType
    ));
    assertNoFindings();
  }

  @Test
  public void isCompatibleTuples() {
    Assertions.assertTrue(SymTypeRelations.isCompatible(
        createTuple(_personSymType, _intSymType),
        createTuple(_personSymType, _intSymType)
    ));
    Assertions.assertTrue(SymTypeRelations.isCompatible(
        createTuple(_personSymType, _intSymType),
        createTuple(_studentSymType, _intSymType)
    ));
    Assertions.assertFalse(SymTypeRelations.isCompatible(
        createTuple(_studentSymType, _intSymType),
        createTuple(_personSymType, _intSymType)
    ));
    Assertions.assertFalse(SymTypeRelations.isCompatible(
        createTuple(_carSymType, _intSymType),
        createTuple(_personSymType, _intSymType)
    ));

    Assertions.assertFalse(SymTypeRelations.isCompatible(
        createTuple(_personSymType, _intSymType),
        createTuple(_personSymType, _intSymType, _intSymType)
    ));
    Assertions.assertFalse(SymTypeRelations.isCompatible(
        createTuple(_personSymType, _intSymType, _intSymType),
        createTuple(_personSymType, _intSymType)
    ));
  }

  @Test
  public void isCompatibleFunctions() {
    Assertions.assertTrue(SymTypeRelations.isCompatible(
        createFunction(_personSymType), createFunction(_personSymType)
    ));
    Assertions.assertTrue(SymTypeRelations.isCompatible(
        createFunction(_personSymType, _intSymType),
        createFunction(_personSymType, _intSymType)
    ));
    Assertions.assertTrue(SymTypeRelations.isCompatible(
        createFunction(_personSymType, List.of(_intSymType), true),
        createFunction(_personSymType, List.of(_intSymType), true)
    ));
    Assertions.assertTrue(SymTypeRelations.isCompatible(
        createFunction(_personSymType, _intSymType),
        createFunction(_studentSymType, _intSymType)
    ));
    Assertions.assertTrue(SymTypeRelations.isCompatible(
        createFunction(_personSymType, _intSymType),
        createFunction(_personSymType, _longSymType)
    ));
    Assertions.assertTrue(SymTypeRelations.isCompatible(
        createFunction(_personSymType, _intSymType),
        createFunction(_personSymType, List.of(_longSymType), true)
    ));
    Assertions.assertTrue(SymTypeRelations.isCompatible(
        createFunction(_personSymType, _intSymType, _intSymType),
        createFunction(_personSymType, List.of(_intSymType), true)
    ));
    Assertions.assertTrue(SymTypeRelations.isCompatible(
        createFunction(_personSymType),
        createFunction(_personSymType, List.of(_intSymType), true)
    ));
    Assertions.assertFalse(SymTypeRelations.isCompatible(
        createFunction(_personSymType), createFunction(_carSymType)
    ));
    Assertions.assertFalse(SymTypeRelations.isCompatible(
        createFunction(_studentSymType, _intSymType),
        createFunction(_personSymType, _intSymType)
    ));
    Assertions.assertFalse(SymTypeRelations.isCompatible(
        createFunction(_personSymType, _longSymType),
        createFunction(_personSymType, _intSymType)
    ));
    Assertions.assertFalse(SymTypeRelations.isCompatible(
        createFunction(_personSymType, List.of(_intSymType), true),
        createFunction(_personSymType, _intSymType)
    ));

    assertNoFindings();
  }

  @Test
  public void isCompatibleNumericsAndSIUnits() {
    Assertions.assertFalse(SymTypeRelations.isCompatible(_s_SISymType, _intSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_intSymType, _s_SISymType));
    // TBD: SIUnit of dimension one
  }

  @Test
  public void isCompatibleNumericsAndNumericsWithSIUnits() {
    SymTypeOfNumericWithSIUnit deg_float_SISymType =
        createNumericWithSIUnit(_deg_SISymType, _floatSymType);
    Assertions.assertFalse(SymTypeRelations.isCompatible(_s_int_SISymType, _intSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_intSymType, _s_int_SISymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_deg_int_SISymType, _intSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_intSymType, _deg_int_SISymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(deg_float_SISymType, _intSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_intSymType, deg_float_SISymType));
  }

  @Test
  public void isCompatibleNumericWithSIUnits() {
    SymTypeOfNumericWithSIUnit s_float_SISymType =
        createNumericWithSIUnit(_s_SISymType, _floatSymType);
    SymTypeOfNumericWithSIUnit deg_float_SISymType =
        createNumericWithSIUnit(_deg_SISymType, _floatSymType);

    Assertions.assertTrue(SymTypeRelations.isCompatible(_s_int_SISymType, _s_int_SISymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_Ohm_int_SISymType, _Ohm_int_SISymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_s_int_SISymType, _A_int_SISymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_A_int_SISymType, _s_int_SISymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_deg_int_SISymType, _rad_int_SISymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_rad_int_SISymType, _deg_int_SISymType));

    Assertions.assertTrue(SymTypeRelations.isCompatible(s_float_SISymType, _s_int_SISymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_s_int_SISymType, s_float_SISymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(deg_float_SISymType, _rad_int_SISymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_rad_int_SISymType, deg_float_SISymType));
  }

  @Test
  public void isCompatibleSIUnits() {
    Assertions.assertTrue(SymTypeRelations.isCompatible(_s_SISymType, _s_SISymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_Ohm_SISymType, _Ohm_SISymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_s_SISymType, _A_SISymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_A_SISymType, _s_SISymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_deg_SISymType, _rad_SISymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_rad_SISymType, _deg_SISymType));
    // cannot combine SIUnits with NumericWithSIUnits
    Assertions.assertFalse(SymTypeRelations.isCompatible(_s_SISymType, _s_int_SISymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_s_int_SISymType, _s_SISymType));
  }

  @Test
  public void isSubTypeBottom() {
    // bottom is subType of EVERY other type
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_bottomType, _bottomType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_bottomType, _topType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_bottomType, _intSymType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_bottomType, _booleanSymType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_bottomType, _IntegerSymType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_bottomType, _personSymType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_bottomType, _nullSymType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_bottomType, _unboxedListSymType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_bottomType, _unboxedListSymType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_bottomType, _linkedListSymType));
    // bottom is never the superType except for bottom itself
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_topType, _bottomType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_intSymType, _bottomType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_booleanSymType, _bottomType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_IntegerSymType, _bottomType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_personSymType, _bottomType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_nullSymType, _bottomType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_unboxedListSymType, _bottomType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_unboxedListSymType, _bottomType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_linkedListSymType, _bottomType));
  }

  @Test
  public void isSubTypeTop() {
    // top is superType of EVERY other type
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_bottomType, _topType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_topType, _topType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_intSymType, _topType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_booleanSymType, _topType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_IntegerSymType, _topType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_personSymType, _topType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_nullSymType, _topType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_unboxedListSymType, _topType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_unboxedListSymType, _topType));
    Assertions.assertTrue(SymTypeRelations.isSubTypeOf(_linkedListSymType, _topType));
    // top is never the subType except for top itself
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_topType, _bottomType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_topType, _intSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_topType, _booleanSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_topType, _IntegerSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_topType, _personSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_topType, _nullSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_topType, _unboxedListSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_topType, _unboxedListSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_topType, _linkedListSymType));
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
    Assertions.assertTrue(SymTypeRelations.isCompatible(unboundedTVar, unboundedTVar));
    Assertions.assertFalse(SymTypeRelations.isCompatible(unboundedTVar, unbounded2TVar));
    Assertions.assertFalse(SymTypeRelations.isCompatible(unboundedTVar, _personSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_personSymType, unboundedTVar));

    // we can assign variables if we know their upper bound
    Assertions.assertTrue(SymTypeRelations.isCompatible(_personSymType, subStudentTVar));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_studentSymType, subStudentTVar));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_csStudentSymType, subStudentTVar));
    // we cannot really assign to variable if we only know their upper bounds
    Assertions.assertFalse(SymTypeRelations.isCompatible(subStudentTVar, _personSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(subStudentTVar, _studentSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(subStudentTVar, _csStudentSymType));

    // we can assign to variables if we know their lower bound
    Assertions.assertFalse(SymTypeRelations.isCompatible(superStudentTVar, _personSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(superStudentTVar, _studentSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(superStudentTVar, _csStudentSymType));
    // we cannot really assign variables if we only know their lower bounds
    Assertions.assertFalse(SymTypeRelations.isCompatible(_personSymType, superStudentTVar));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_studentSymType, superStudentTVar));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_csStudentSymType, superStudentTVar));

    // two single bounded variables
    Assertions.assertTrue(SymTypeRelations.isCompatible(superStudentTVar, subStudentTVar));
    Assertions.assertTrue(SymTypeRelations.isCompatible(superPersonTVar, subStudentTVar));
    Assertions.assertFalse(SymTypeRelations.isCompatible(superStudentTVar, subPersonTVar));
    Assertions.assertFalse(SymTypeRelations.isCompatible(subPersonTVar, subStudentTVar));
    Assertions.assertFalse(SymTypeRelations.isCompatible(subStudentTVar, subPersonTVar));

    // in case of upper AND lower bound set,
    // we can assign and assign to the type variable
    // assign to:
    Assertions.assertFalse(SymTypeRelations.isCompatible(superSSubCsSTVar, _personSymType));
    Assertions.assertFalse(SymTypeRelations.isCompatible(superSSubCsSTVar, _studentSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(superSSubCsSTVar, _csStudentSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(superSSubCsSTVar, _firstSemesterCsStudentSymType));
    // assign:
    Assertions.assertTrue(SymTypeRelations.isCompatible(_personSymType, superSSubCsSTVar));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_studentSymType, superSSubCsSTVar));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_csStudentSymType, superSSubCsSTVar));
    Assertions.assertFalse(SymTypeRelations.isCompatible(_firstSemesterCsStudentSymType, superSSubCsSTVar));

    assertNoFindings();
  }

  @Test
  public void isCompatibleTypeVariableRecursive() {
    // check that we can handle recursively defined generics,
    // e.g., A<T extends A<T>>
    Assertions.assertTrue(SymTypeRelations.isCompatible(_simpleCrtSymType, _simpleCrtSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_graphSymType, _graphSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_graphNodeSymType, _graphNodeSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_graphEdgeSymType, _graphEdgeSymType));
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
        createWildcard(true, _studentSymType)
    );
    // List<? extends Person>
    SymTypeOfGenerics pSubList = createGenerics(
        _boxedListSymType.getTypeInfo(),
        createWildcard(true, _personSymType)
    );
    // LinkedList<? extends Student>
    SymTypeOfGenerics sSubLinkedList = createGenerics(
        _linkedListSymType.getTypeInfo(),
        createWildcard(true, _studentSymType)
    );
    // LinkedList<? extends Person>
    SymTypeOfGenerics pSubLinkedList = createGenerics(
        _linkedListSymType.getTypeInfo(),
        createWildcard(true, _personSymType)
    );

    Assertions.assertFalse(SymTypeRelations.isCompatible(pList, sList));
    Assertions.assertFalse(SymTypeRelations.isCompatible(sList, pList));
    Assertions.assertFalse(SymTypeRelations.isCompatible(pList, sSubList));
    Assertions.assertFalse(SymTypeRelations.isCompatible(sList, sSubList));

    Assertions.assertTrue(SymTypeRelations.isCompatible(pSubList, pList));
    Assertions.assertTrue(SymTypeRelations.isCompatible(pSubList, sList));
    Assertions.assertTrue(SymTypeRelations.isCompatible(pSubList, pSubList));
    Assertions.assertTrue(SymTypeRelations.isCompatible(pSubList, sSubList));
    Assertions.assertFalse(SymTypeRelations.isCompatible(sSubList, pList));
    Assertions.assertTrue(SymTypeRelations.isCompatible(sSubList, sList));
    Assertions.assertFalse(SymTypeRelations.isCompatible(sSubList, pSubList));
    Assertions.assertTrue(SymTypeRelations.isCompatible(sSubList, sSubList));

    Assertions.assertTrue(SymTypeRelations.isCompatible(pSubList, pSubLinkedList));
    Assertions.assertTrue(SymTypeRelations.isCompatible(pSubList, sSubLinkedList));
    Assertions.assertFalse(SymTypeRelations.isCompatible(sSubList, pSubLinkedList));
    Assertions.assertTrue(SymTypeRelations.isCompatible(sSubList, sSubLinkedList));

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
        createWildcard(false, _studentSymType)
    );
    // List<? super Person>
    SymTypeOfGenerics pSuperList = createGenerics(
        _boxedListSymType.getTypeInfo(),
        createWildcard(false, _personSymType)
    );
    // LinkedList<? super Student>
    SymTypeOfGenerics sSuperLinkedList = createGenerics(
        _linkedListSymType.getTypeInfo(),
        createWildcard(false, _studentSymType)
    );
    // LinkedList<? super Person>
    SymTypeOfGenerics pSuperLinkedList = createGenerics(
        _linkedListSymType.getTypeInfo(),
        createWildcard(false, _personSymType)
    );

    Assertions.assertFalse(SymTypeRelations.isCompatible(pList, sList));
    Assertions.assertFalse(SymTypeRelations.isCompatible(sList, pList));
    Assertions.assertFalse(SymTypeRelations.isCompatible(pList, sSuperList));
    Assertions.assertFalse(SymTypeRelations.isCompatible(sList, sSuperList));

    Assertions.assertTrue(SymTypeRelations.isCompatible(pSuperList, pList));
    Assertions.assertFalse(SymTypeRelations.isCompatible(pSuperList, sList));
    Assertions.assertTrue(SymTypeRelations.isCompatible(pSuperList, pSuperList));
    Assertions.assertFalse(SymTypeRelations.isCompatible(pSuperList, sSuperList));
    Assertions.assertTrue(SymTypeRelations.isCompatible(sSuperList, pList));
    Assertions.assertTrue(SymTypeRelations.isCompatible(sSuperList, sList));
    Assertions.assertTrue(SymTypeRelations.isCompatible(sSuperList, pSuperList));
    Assertions.assertTrue(SymTypeRelations.isCompatible(sSuperList, sSuperList));

    Assertions.assertTrue(SymTypeRelations.isCompatible(pSuperList, pSuperLinkedList));
    Assertions.assertFalse(SymTypeRelations.isCompatible(pSuperList, sSuperLinkedList));
    Assertions.assertTrue(SymTypeRelations.isCompatible(sSuperList, pSuperLinkedList));
    Assertions.assertTrue(SymTypeRelations.isCompatible(sSuperList, sSuperLinkedList));

    assertNoFindings();
  }

  @Test
  public void nullCompatibilityAndSubTyping() {
    Assertions.assertFalse(SymTypeRelations.isCompatible(_intSymType, createTypeOfNull()));
    Assertions.assertTrue(SymTypeRelations.isCompatible(_IntegerSymType, createTypeOfNull()));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(createTypeOfNull(), _personSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_personSymType, createTypeOfNull()));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_s_SISymType, _nullSymType));
    Assertions.assertFalse(SymTypeRelations.isSubTypeOf(_s_int_SISymType, _nullSymType));
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

    Assertions.assertTrue(SymTypeRelations.isCompatible(comparableInteger, _IntegerSymType));
    Assertions.assertTrue(SymTypeRelations.isCompatible(
        createGenerics(iterableSym, _IntegerSymType),
        createGenerics(_unboxedListSymType.getTypeInfo(), _intSymType)
    ));
  }
}
