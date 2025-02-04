// (c) https://github.com/MontiCore/monticore
package de.monticore.types3;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsGlobalScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.util.DefsTypesForTests;
import de.monticore.types3.util.SymTypeUnboxingVisitor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static de.monticore.types.check.SymTypeExpressionFactory.createFunction;
import static de.monticore.types.check.SymTypeExpressionFactory.createGenerics;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeArray;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeObject;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeVariable;
import static de.monticore.types.check.SymTypeExpressionFactory.createUnion;
import static de.monticore.types3.util.DefsTypesForTests.*;
import static org.junit.Assert.assertEquals;

public class SymTypeUnboxingVisitorTest extends AbstractTypeTest {

  SymTypeUnboxingVisitor visitor = new SymTypeUnboxingVisitor();

  @BeforeEach
  public void init() {
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    DefsTypesForTests.setup();
  }

  @Test
  public void unboxPrimitives() {
    check(_IntegerSymType, "int");
    check(_DoubleSymType, "double");
    check(_FloatSymType, "float");
    check(_ShortSymType, "short");
    check(_LongSymType, "long");
    check(_BooleanSymType, "boolean");
    check(_ByteSymType, "byte");
    check(_CharacterSymType, "char");
    assertNoFindings();
  }

  @Test
  public void doNotUnboxToUnknownTypes() {
    //remove double
    _doubleSymType.getTypeInfo().getEnclosingScope()
        .remove(_doubleSymType.getTypeInfo());
    // as double does not exists, java.lang.Double remains java.Lang.Double
    check(_DoubleSymType, "java.lang.Double");
    // same with Collections
    _unboxedMapSymType.getTypeInfo().getEnclosingScope()
        .remove(_unboxedMapSymType.getTypeInfo());
    check(createGenerics(_boxedMapSymType.getTypeInfo(), _IntegerSymType, _DoubleSymType),
        "java.util.Map<int,java.lang.Double>");
  }

  @Test
  public void unboxObjects() {
    check(createTypeObject(_boxedString.getTypeInfo()), "String");
  }

  @Test
  public void unboxCollections() {
    check(createGenerics(_boxedOptionalSymType.getTypeInfo(), _IntegerSymType),
        "Optional<int>");
    check(createGenerics(_boxedSetSymType.getTypeInfo(), _IntegerSymType),
        "Set<int>");
    check(createGenerics(_boxedListSymType.getTypeInfo(), _IntegerSymType),
        "List<int>");
    check(createGenerics(_boxedMapSymType.getTypeInfo(), _IntegerSymType, _DoubleSymType),
        "Map<int,double>");
    assertNoFindings();
  }

  @Test
  public void unboxComplexTypes() {
    ICombineExpressionsWithLiteralsGlobalScope gs =
        CombineExpressionsWithLiteralsMill.globalScope();
    TypeSymbol person = type("Person");
    TypeVarSymbol tVar = typeVariable("T");
    check(
        createTypeArray(
            createGenerics(_boxedMapSymType.getTypeInfo(),
                createUnion(
                    createTypeObject(person),
                    _intSymType,
                    _DoubleSymType
                ),
                createTypeArray(
                    createFunction(
                        createTypeObject(person),
                        createTypeVariable(tVar),
                        createGenerics(_boxedOptionalSymType.getTypeInfo(),
                            createTypeArray(_IntegerSymType, 1)
                        )
                    ), 2
                )
            ), 1
        ),
        "Map<(Person | double | int),"
            + "((T, Optional<int[]>) -> Person)[][]>[]"
    );
  }

  public void check(SymTypeExpression boxed, String expectedUnboxedName) {
    SymTypeExpression unboxed = visitor.calculate(boxed);
    assertNoFindings();
    Assertions.assertEquals(expectedUnboxedName, unboxed.printFullName());
  }

}
