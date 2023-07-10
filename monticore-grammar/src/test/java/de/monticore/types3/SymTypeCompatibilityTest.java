/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types3;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsScope;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsGlobalScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types3.util.DefsTypesForTests;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static de.monticore.types.check.SymTypeExpressionFactory.createFunction;
import static de.monticore.types.check.SymTypeExpressionFactory.createGenerics;
import static de.monticore.types.check.SymTypeExpressionFactory.createIntersection;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeArray;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeObject;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeOfNull;
import static de.monticore.types.check.SymTypeExpressionFactory.createUnion;
import static de.monticore.types3.util.DefsTypesForTests.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SymTypeCompatibilityTest extends AbstractTypeTest {

  protected ICombineExpressionsWithLiteralsScope scope;

  protected SymTypeRelations tr;

  @Before
  public void init() {
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    DefsTypesForTests.setup();
    tr = new SymTypeRelations();
  }

  @Test
  public void isCompatiblePrimitives() {
    assertFalse(tr.isCompatible(_booleanSymType, _doubleSymType));
    assertFalse(tr.isCompatible(_booleanSymType, _floatSymType));
    assertFalse(tr.isCompatible(_booleanSymType, _longSymType));
    assertFalse(tr.isCompatible(_booleanSymType, _intSymType));
    assertFalse(tr.isCompatible(_booleanSymType, _charSymType));
    assertFalse(tr.isCompatible(_booleanSymType, _shortSymType));
    assertFalse(tr.isCompatible(_booleanSymType, _byteSymType));
    assertTrue(tr.isCompatible(_booleanSymType, _booleanSymType));

    assertTrue(tr.isCompatible(_doubleSymType, _doubleSymType));
    assertTrue(tr.isCompatible(_doubleSymType, _floatSymType));
    assertTrue(tr.isCompatible(_doubleSymType, _longSymType));
    assertTrue(tr.isCompatible(_doubleSymType, _intSymType));
    assertTrue(tr.isCompatible(_doubleSymType, _charSymType));
    assertTrue(tr.isCompatible(_doubleSymType, _shortSymType));
    assertTrue(tr.isCompatible(_doubleSymType, _byteSymType));
    assertFalse(tr.isCompatible(_doubleSymType, _booleanSymType));

    assertFalse(tr.isCompatible(_floatSymType, _doubleSymType));
    assertTrue(tr.isCompatible(_floatSymType, _floatSymType));
    assertTrue(tr.isCompatible(_floatSymType, _longSymType));
    assertTrue(tr.isCompatible(_floatSymType, _intSymType));
    assertTrue(tr.isCompatible(_floatSymType, _charSymType));
    assertTrue(tr.isCompatible(_floatSymType, _shortSymType));
    assertTrue(tr.isCompatible(_floatSymType, _byteSymType));
    assertFalse(tr.isCompatible(_floatSymType, _booleanSymType));

    assertFalse(tr.isCompatible(_longSymType, _doubleSymType));
    assertFalse(tr.isCompatible(_longSymType, _floatSymType));
    assertTrue(tr.isCompatible(_longSymType, _longSymType));
    assertTrue(tr.isCompatible(_longSymType, _intSymType));
    assertTrue(tr.isCompatible(_longSymType, _charSymType));
    assertTrue(tr.isCompatible(_longSymType, _shortSymType));
    assertTrue(tr.isCompatible(_longSymType, _byteSymType));
    assertFalse(tr.isCompatible(_longSymType, _booleanSymType));

    assertFalse(tr.isCompatible(_intSymType, _doubleSymType));
    assertFalse(tr.isCompatible(_intSymType, _floatSymType));
    assertFalse(tr.isCompatible(_intSymType, _longSymType));
    assertTrue(tr.isCompatible(_intSymType, _intSymType));
    assertTrue(tr.isCompatible(_intSymType, _charSymType));
    assertTrue(tr.isCompatible(_intSymType, _shortSymType));
    assertTrue(tr.isCompatible(_intSymType, _byteSymType));
    assertFalse(tr.isCompatible(_intSymType, _booleanSymType));

    assertFalse(tr.isCompatible(_charSymType, _doubleSymType));
    assertFalse(tr.isCompatible(_charSymType, _floatSymType));
    assertFalse(tr.isCompatible(_charSymType, _longSymType));
    assertFalse(tr.isCompatible(_charSymType, _intSymType));
    assertTrue(tr.isCompatible(_charSymType, _charSymType));
    assertFalse(tr.isCompatible(_charSymType, _shortSymType));
    assertFalse(tr.isCompatible(_charSymType, _byteSymType));
    assertFalse(tr.isCompatible(_charSymType, _booleanSymType));

    assertFalse(tr.isCompatible(_shortSymType, _doubleSymType));
    assertFalse(tr.isCompatible(_shortSymType, _floatSymType));
    assertFalse(tr.isCompatible(_shortSymType, _longSymType));
    assertFalse(tr.isCompatible(_shortSymType, _intSymType));
    assertFalse(tr.isCompatible(_shortSymType, _charSymType));
    assertTrue(tr.isCompatible(_shortSymType, _shortSymType));
    assertTrue(tr.isCompatible(_shortSymType, _byteSymType));
    assertFalse(tr.isCompatible(_shortSymType, _booleanSymType));

    assertFalse(tr.isCompatible(_byteSymType, _doubleSymType));
    assertFalse(tr.isCompatible(_byteSymType, _floatSymType));
    assertFalse(tr.isCompatible(_byteSymType, _longSymType));
    assertFalse(tr.isCompatible(_byteSymType, _intSymType));
    assertFalse(tr.isCompatible(_byteSymType, _charSymType));
    assertFalse(tr.isCompatible(_byteSymType, _shortSymType));
    assertTrue(tr.isCompatible(_byteSymType, _byteSymType));
    assertFalse(tr.isCompatible(_byteSymType, _booleanSymType));

    assertFalse(tr.isCompatible(_booleanSymType, _personSymType));
    assertTrue(tr.isCompatible(_booleanSymType, _BooleanSymType));
    assertNoFindings();
  }

  @Test
  public void isSubTypePrimitives() {
    assertFalse(tr.isSubTypeOf(_booleanSymType, _doubleSymType));
    assertFalse(tr.isSubTypeOf(_booleanSymType, _floatSymType));
    assertFalse(tr.isSubTypeOf(_booleanSymType, _longSymType));
    assertFalse(tr.isSubTypeOf(_booleanSymType, _intSymType));
    assertFalse(tr.isSubTypeOf(_booleanSymType, _charSymType));
    assertFalse(tr.isSubTypeOf(_booleanSymType, _shortSymType));
    assertFalse(tr.isSubTypeOf(_booleanSymType, _byteSymType));
    assertTrue(tr.isSubTypeOf(_booleanSymType, _booleanSymType));

    assertTrue(tr.isSubTypeOf(_doubleSymType, _doubleSymType));
    assertFalse(tr.isSubTypeOf(_doubleSymType, _floatSymType));
    assertFalse(tr.isSubTypeOf(_doubleSymType, _longSymType));
    assertFalse(tr.isSubTypeOf(_doubleSymType, _intSymType));
    assertFalse(tr.isSubTypeOf(_doubleSymType, _charSymType));
    assertFalse(tr.isSubTypeOf(_doubleSymType, _shortSymType));
    assertFalse(tr.isSubTypeOf(_doubleSymType, _byteSymType));
    assertFalse(tr.isSubTypeOf(_doubleSymType, _booleanSymType));

    assertTrue(tr.isSubTypeOf(_floatSymType, _doubleSymType));
    assertTrue(tr.isSubTypeOf(_floatSymType, _floatSymType));
    assertFalse(tr.isSubTypeOf(_floatSymType, _longSymType));
    assertFalse(tr.isSubTypeOf(_floatSymType, _intSymType));
    assertFalse(tr.isSubTypeOf(_floatSymType, _charSymType));
    assertFalse(tr.isSubTypeOf(_floatSymType, _shortSymType));
    assertFalse(tr.isSubTypeOf(_floatSymType, _byteSymType));
    assertFalse(tr.isSubTypeOf(_floatSymType, _booleanSymType));

    assertTrue(tr.isSubTypeOf(_longSymType, _doubleSymType));
    assertTrue(tr.isSubTypeOf(_longSymType, _floatSymType));
    assertTrue(tr.isSubTypeOf(_longSymType, _longSymType));
    assertFalse(tr.isSubTypeOf(_longSymType, _intSymType));
    assertFalse(tr.isSubTypeOf(_longSymType, _charSymType));
    assertFalse(tr.isSubTypeOf(_longSymType, _shortSymType));
    assertFalse(tr.isSubTypeOf(_longSymType, _byteSymType));
    assertFalse(tr.isSubTypeOf(_longSymType, _booleanSymType));

    assertTrue(tr.isSubTypeOf(_intSymType, _doubleSymType));
    assertTrue(tr.isSubTypeOf(_intSymType, _floatSymType));
    assertTrue(tr.isSubTypeOf(_intSymType, _longSymType));
    assertTrue(tr.isSubTypeOf(_intSymType, _intSymType));
    assertFalse(tr.isSubTypeOf(_intSymType, _charSymType));
    assertFalse(tr.isSubTypeOf(_intSymType, _shortSymType));
    assertFalse(tr.isSubTypeOf(_intSymType, _byteSymType));
    assertFalse(tr.isSubTypeOf(_intSymType, _booleanSymType));

    assertTrue(tr.isSubTypeOf(_charSymType, _doubleSymType));
    assertTrue(tr.isSubTypeOf(_charSymType, _floatSymType));
    assertTrue(tr.isSubTypeOf(_charSymType, _longSymType));
    assertTrue(tr.isSubTypeOf(_charSymType, _intSymType));
    assertTrue(tr.isSubTypeOf(_charSymType, _charSymType));
    assertFalse(tr.isSubTypeOf(_charSymType, _shortSymType));
    assertFalse(tr.isSubTypeOf(_charSymType, _byteSymType));
    assertFalse(tr.isSubTypeOf(_charSymType, _booleanSymType));

    assertTrue(tr.isSubTypeOf(_shortSymType, _doubleSymType));
    assertTrue(tr.isSubTypeOf(_shortSymType, _floatSymType));
    assertTrue(tr.isSubTypeOf(_shortSymType, _longSymType));
    assertTrue(tr.isSubTypeOf(_shortSymType, _intSymType));
    assertFalse(tr.isSubTypeOf(_shortSymType, _charSymType));
    assertTrue(tr.isSubTypeOf(_shortSymType, _shortSymType));
    assertFalse(tr.isSubTypeOf(_shortSymType, _byteSymType));
    assertFalse(tr.isSubTypeOf(_shortSymType, _booleanSymType));

    assertTrue(tr.isSubTypeOf(_byteSymType, _doubleSymType));
    assertTrue(tr.isSubTypeOf(_byteSymType, _floatSymType));
    assertTrue(tr.isSubTypeOf(_byteSymType, _longSymType));
    assertTrue(tr.isSubTypeOf(_byteSymType, _intSymType));
    assertFalse(tr.isSubTypeOf(_byteSymType, _charSymType));
    assertTrue(tr.isSubTypeOf(_byteSymType, _shortSymType));
    assertTrue(tr.isSubTypeOf(_byteSymType, _byteSymType));
    assertFalse(tr.isSubTypeOf(_byteSymType, _booleanSymType));

    assertFalse(tr.isSubTypeOf(_booleanSymType, _personSymType));
    assertFalse(tr.isSubTypeOf(_booleanSymType, _BooleanSymType));
    assertNoFindings();
  }

  @Test
  public void isCompatibleObjects() {
    assertTrue(tr.isCompatible(_personSymType, _personSymType));
    assertTrue(tr.isCompatible(_personSymType, _studentSymType));
    assertTrue(tr.isCompatible(_personSymType, _csStudentSymType));
    assertTrue(tr.isCompatible(_personSymType, _teacherSymType));

    assertFalse(tr.isCompatible(_studentSymType, _personSymType));
    assertTrue(tr.isCompatible(_studentSymType, _studentSymType));
    assertTrue(tr.isCompatible(_studentSymType, _csStudentSymType));
    assertFalse(tr.isCompatible(_studentSymType, _teacherSymType));

    assertFalse(tr.isCompatible(_csStudentSymType, _personSymType));
    assertFalse(tr.isCompatible(_csStudentSymType, _studentSymType));
    assertTrue(tr.isCompatible(_csStudentSymType, _csStudentSymType));
    assertFalse(tr.isCompatible(_csStudentSymType, _teacherSymType));

    assertFalse(tr.isCompatible(_teacherSymType, _personSymType));
    assertFalse(tr.isCompatible(_teacherSymType, _studentSymType));
    assertFalse(tr.isCompatible(_teacherSymType, _csStudentSymType));
    assertTrue(tr.isCompatible(_teacherSymType, _teacherSymType));

    // String
    assertTrue(tr.isCompatible(_boxedString, _boxedString));
    assertTrue(tr.isCompatible(_boxedString, _unboxedString));
    assertTrue(tr.isCompatible(_unboxedString, _boxedString));
    assertTrue(tr.isCompatible(_unboxedString, _unboxedString));

    // diverse types
    assertFalse(tr.isCompatible(_personSymType, _intSymType));
    assertFalse(tr.isCompatible(
        _personSymType,
        createTypeArray(_personSymType, 1))
    );
    assertTrue(tr.isCompatible(
        _personSymType,
        createTypeArray(_personSymType, 0))
    );
    assertFalse(tr.isCompatible(_personSymType, _unboxedMapSymType));
    assertFalse(tr.isCompatible(_personSymType, _BooleanSymType));
    assertNoFindings();
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

    assertTrue(tr.isCompatible(listOfPerson, listOfPerson));
    assertTrue(tr.isCompatible(listOfPerson, subPersonList));
    assertTrue(tr.isCompatible(listOfPerson, linkedListOfPerson));

    assertFalse(tr.isCompatible(listOfInt, _intSymType));
    assertFalse(tr.isCompatible(listOfInt, listOfBoolean));
    assertFalse(tr.isCompatible(listOfBoolean, listOfInt));
    assertFalse(tr.isCompatible(listOfBoolean, listOfPerson));
    assertFalse(tr.isCompatible(listOfPerson, listOfBoolean));
    assertFalse(tr.isCompatible(listOfBoolean, subPersonList));
    assertFalse(tr.isCompatible(subPersonList, listOfPerson));
    assertNoFindings();
  }

  @Test
  public void isCompatibleUnions() {
    assertTrue(tr.isCompatible(
        createUnion(_personSymType, _carSymType), _personSymType
    ));
    assertTrue(tr.isCompatible(
        createUnion(_personSymType, _carSymType),
        createUnion(_personSymType, _carSymType)
    ));
    assertTrue(tr.isCompatible(
        _personSymType, createUnion(_studentSymType, _teacherSymType)
    ));
    assertFalse(tr.isCompatible(
        _personSymType, createUnion(_studentSymType, _carSymType)
    ));
    assertFalse(tr.isCompatible(
        createUnion(_studentSymType, _teacherSymType), _personSymType
    ));
    assertNoFindings();
  }

  @Test
  public void IsCompatibleIntersections() {
    assertTrue(tr.isCompatible(
        createIntersection(_personSymType, _carSymType),
        createIntersection(_personSymType, _carSymType)
    ));
    assertTrue(tr.isCompatible(
        _personSymType, createIntersection(_studentSymType, _teacherSymType)
    ));
    assertTrue(tr.isCompatible(
        _personSymType, createIntersection(_personSymType, _carSymType)
    ));
    assertTrue(tr.isCompatible(
        createIntersection(_teachableSymType, _personSymType),
        createUnion(_childSymType, _studentSymType)
    ));
    assertFalse(tr.isCompatible(
        createIntersection(_personSymType, _carSymType), _personSymType
    ));
    assertNoFindings();
  }

  @Test
  public void isCompatibleFunctions() {
    assertTrue(tr.isCompatible(
        createFunction(_personSymType), createFunction(_personSymType)
    ));
    assertTrue(tr.isCompatible(
        createFunction(_personSymType, _intSymType),
        createFunction(_personSymType, _intSymType)
    ));
    assertTrue(tr.isCompatible(
        createFunction(_personSymType, List.of(_intSymType), true),
        createFunction(_personSymType, List.of(_intSymType), true)
    ));
    assertTrue(tr.isCompatible(
        createFunction(_studentSymType, _intSymType),
        createFunction(_personSymType, _intSymType)
    ));
    assertTrue(tr.isCompatible(
        createFunction(_personSymType, _longSymType),
        createFunction(_personSymType, _intSymType)
    ));
    assertTrue(tr.isCompatible(
        createFunction(_personSymType, _longSymType),
        createFunction(_personSymType, List.of(_intSymType), true)
    ));
    assertTrue(tr.isCompatible(
        createFunction(_personSymType, _intSymType, _intSymType),
        createFunction(_personSymType, List.of(_intSymType), true)
    ));
    assertTrue(tr.isCompatible(
        createFunction(_personSymType),
        createFunction(_personSymType, List.of(_intSymType), true)
    ));
    assertFalse(tr.isCompatible(
        createFunction(_personSymType), createFunction(_carSymType)
    ));
    assertFalse(tr.isCompatible(
        createFunction(_personSymType, _intSymType),
        createFunction(_studentSymType, _intSymType)
    ));
    assertFalse(tr.isCompatible(
        createFunction(_personSymType, _intSymType),
        createFunction(_personSymType, _longSymType)
    ));
    assertFalse(tr.isCompatible(
        createFunction(_personSymType, List.of(_intSymType), true),
        createFunction(_personSymType, _intSymType)
    ));

    assertNoFindings();
  }

  @Test
  public void isSubTypeBottom() {
    // bottom is subType of EVERY other type
    assertTrue(tr.isSubTypeOf(_bottomType, _bottomType));
    assertTrue(tr.isSubTypeOf(_bottomType, _topType));
    assertTrue(tr.isSubTypeOf(_bottomType, _intSymType));
    assertTrue(tr.isSubTypeOf(_bottomType, _booleanSymType));
    assertTrue(tr.isSubTypeOf(_bottomType, _IntegerSymType));
    assertTrue(tr.isSubTypeOf(_bottomType, _personSymType));
    assertTrue(tr.isSubTypeOf(_bottomType, _nullSymType));
    assertTrue(tr.isSubTypeOf(_bottomType, _unboxedListSymType));
    assertTrue(tr.isSubTypeOf(_bottomType, _unboxedListSymType));
    assertTrue(tr.isSubTypeOf(_bottomType, _linkedListSymType));
    // bottom is never the superType except for bottom itself
    assertFalse(tr.isSubTypeOf(_topType, _bottomType));
    assertFalse(tr.isSubTypeOf(_intSymType, _bottomType));
    assertFalse(tr.isSubTypeOf(_booleanSymType, _bottomType));
    assertFalse(tr.isSubTypeOf(_IntegerSymType, _bottomType));
    assertFalse(tr.isSubTypeOf(_personSymType, _bottomType));
    assertFalse(tr.isSubTypeOf(_nullSymType, _bottomType));
    assertFalse(tr.isSubTypeOf(_unboxedListSymType, _bottomType));
    assertFalse(tr.isSubTypeOf(_unboxedListSymType, _bottomType));
    assertFalse(tr.isSubTypeOf(_linkedListSymType, _bottomType));
  }

  @Test
  public void isSubTypeTop() {
    // top is superType of EVERY other type
    assertTrue(tr.isSubTypeOf(_bottomType, _topType));
    assertTrue(tr.isSubTypeOf(_topType, _topType));
    assertTrue(tr.isSubTypeOf(_intSymType, _topType));
    assertTrue(tr.isSubTypeOf(_booleanSymType, _topType));
    assertTrue(tr.isSubTypeOf(_IntegerSymType, _topType));
    assertTrue(tr.isSubTypeOf(_personSymType, _topType));
    assertTrue(tr.isSubTypeOf(_nullSymType, _topType));
    assertTrue(tr.isSubTypeOf(_unboxedListSymType, _topType));
    assertTrue(tr.isSubTypeOf(_unboxedListSymType, _topType));
    assertTrue(tr.isSubTypeOf(_linkedListSymType, _topType));
    // top is never the subType except for top itself
    assertFalse(tr.isSubTypeOf(_topType, _bottomType));
    assertFalse(tr.isSubTypeOf(_topType, _intSymType));
    assertFalse(tr.isSubTypeOf(_topType, _booleanSymType));
    assertFalse(tr.isSubTypeOf(_topType, _IntegerSymType));
    assertFalse(tr.isSubTypeOf(_topType, _personSymType));
    assertFalse(tr.isSubTypeOf(_topType, _nullSymType));
    assertFalse(tr.isSubTypeOf(_topType, _unboxedListSymType));
    assertFalse(tr.isSubTypeOf(_topType, _unboxedListSymType));
    assertFalse(tr.isSubTypeOf(_topType, _linkedListSymType));
  }

  @Test
  public void nullCompatibilityAndSubTyping() {
    assertFalse(tr.isCompatible(_intSymType, createTypeOfNull()));
    assertTrue(tr.isCompatible(_IntegerSymType, createTypeOfNull()));
    assertFalse(tr.isSubTypeOf(createTypeOfNull(), _personSymType));
    assertFalse(tr.isSubTypeOf(_personSymType, createTypeOfNull()));
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

    assertTrue(tr.isCompatible(comparableInteger, _IntegerSymType));
    assertTrue(tr.isCompatible(
        createGenerics(iterableSym, _IntegerSymType),
        createGenerics(_unboxedListSymType.getTypeInfo(), _intSymType)
    ));
  }
}
