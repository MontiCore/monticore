// (c) https://github.com/MontiCore/monticore
package de.monticore.types2;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsGlobalScope;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.check.SymTypeExpression;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.types.check.SymTypeExpressionFactory.*;
import static de.monticore.types2.DefsTypeBasic.*;
import static org.junit.Assert.assertEquals;

public class SymTypeBoxingVisitorTest extends AbstractTypeTest {

  SymTypeBoxingVisitor visitor = new SymTypeBoxingVisitor();

  @Before
  public void setup() {
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    DefsTypeBasic.setup();
  }

  @Test
  public void boxPrimitives() {
    check(_intSymType, "java.lang.Integer");
    check(_doubleSymType, "java.lang.Double");
    check(_floatSymType, "java.lang.Float");
    check(_shortSymType, "java.lang.Short");
    check(_longSymType, "java.lang.Long");
    check(_booleanSymType, "java.lang.Boolean");
    check(_byteSymType, "java.lang.Byte");
    check(_charSymType, "java.lang.Character");
    assertNoFindings();
  }

  @Test
  public void doNotBoxToUnknownTypes() {
    //remove Double
    _DoubleSymType.getTypeInfo().getEnclosingScope()
        .remove(_DoubleSymType.getTypeInfo());
    // as Double does not exists, double remains double
    check(createPrimitive(BasicSymbolsMill.DOUBLE), "double");
    // same with Collections
    _boxedMapSymType.getTypeInfo().getEnclosingScope()
        .remove(_boxedMapSymType.getTypeInfo());
    check(createGenerics(_unboxedMapSymType.getTypeInfo(), _intSymType, _doubleSymType),
        "Map<java.lang.Integer,double>");
  }

  @Test
  public void boxObjects() {
    check(createTypeObject(_unboxedString.getTypeInfo()), "java.lang.String");
  }

  @Test
  public void boxCollections() {
    check(createGenerics(_unboxedOptionalSymType.getTypeInfo(), _intSymType),
        "java.util.Optional<java.lang.Integer>");
    check(createGenerics(_unboxedSetSymType.getTypeInfo(), _intSymType),
        "java.util.Set<java.lang.Integer>");
    check(createGenerics(_unboxedListSymType.getTypeInfo(), _intSymType),
        "java.util.List<java.lang.Integer>");
    check(createGenerics(_unboxedMapSymType.getTypeInfo(), _intSymType, _doubleSymType),
        "java.util.Map<java.lang.Integer,java.lang.Double>");
    assertNoFindings();
  }

  @Test
  public void boxComplexTypes() {
    ICombineExpressionsWithLiteralsGlobalScope gs =
        CombineExpressionsWithLiteralsMill.globalScope();
    TypeSymbol person = type("Person");
    TypeVarSymbol tVar = typeVariable("T");
    check(
        createTypeArray(
            createGenerics(_unboxedMapSymType.getTypeInfo(),
                createUnion(
                    createTypeObject(person),
                    _IntegerSymType,
                    _doubleSymType
                ),
                createTypeArray(
                    createIntersection(
                        createTypeObject(person),
                        createTypeVariable(tVar),
                        createGenerics(_unboxedOptionalSymType.getTypeInfo(),
                            createTypeArray(_intSymType, 1)
                        )
                    ), 2
                )
            ), 1
        ),
        "java.util.Map<(Person | java.lang.Double | java.lang.Integer),"
            + "(Person & T & java.util.Optional<java.lang.Integer[]>)[][]>[]"
    );
  }

  public void check(SymTypeExpression unboxed, String expectedBoxedName) {
    SymTypeExpression boxed = visitor.calculate(unboxed);
    assertNoFindings();
    assertEquals(expectedBoxedName, boxed.printFullName());
  }

}
