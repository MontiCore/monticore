/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import com.google.common.collect.Lists;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Optional;

import static de.monticore.typescalculator.TypeExpressionBuilder.*;
import static org.junit.Assert.*;

public class SymTypeExpressionTest {

  private void assertSame(SymTypeExpression e, SymTypeExpression eClone) {
    assertFalse(e.equals(eClone));
    assertTrue(e.getName().equals(eClone.getName()));
    assertEquals(e.typeSymbol, eClone.typeSymbol);
  }

  private void assertDeepEquals(SymTypeExpression a, SymTypeExpression b){
    assertTrue(a.deepEquals(b));
  }

  @Test @Ignore
  public void deepCloneTypeConstantTest() {
    SymTypeExpression e = buildTypeConstant("double");
    TypeSymbol s = new TypeSymbol("double");
    e.typeSymbol = java.util.Optional.of(s);
    SymTypeExpression eClone = e.deepClone();
    assertSame(e,eClone);

    assertDeepEquals(e, eClone);
  }

  @Test @Ignore
  public void deepCloneObjectTypeTest() {
    SymTypeExpression e = buildObjectType("String");
    TypeSymbol s = new TypeSymbol("String");
    e.typeSymbol = java.util.Optional.of(s);
    SymTypeExpression eClone = e.deepClone();
    assertSame(e,eClone);
    assertDeepEquals(e,eClone);

    //example with super-/subclasses
    SymTypeExpression exp = buildObjectType("List");
    TypeSymbol b = new TypeSymbol("List");
    exp.typeSymbol = Optional.of(b);

    SymTypeExpression ex = buildObjectType("ArrayList");
    TypeSymbol a = new TypeSymbol("ArrayList");
    ex.typeSymbol = Optional.of(a);

    SymTypeExpression exClone = ex.deepClone();
    assertSame(ex,exClone);
    assertDeepEquals(ex,exClone);
    assertSame(ex.getSuperTypes().get(0),exClone.getSuperTypes().get(0));
  }

  @Test @Ignore
  public void deepCloneGenericTypeTest() {
    SymTypeExpression expre = buildObjectType("Bar");
    TypeSymbol d = new TypeSymbol("Bar");
    expre.typeSymbol = Optional.of(d);


    SymGenericTypeExpression expr = buildGenericTypeExpression("SuperFoo",Lists.newArrayList(expre));
    TypeSymbol c = new TypeSymbol("SuperFoo");
    expr.typeSymbol = Optional.of(c);

    SymTypeExpression exp = buildObjectType("Bar");
    TypeSymbol b = new TypeSymbol("Bar");
    exp.typeSymbol = Optional.of(b);

    SymGenericTypeExpression ex = buildGenericTypeExpression("Foo",Lists.newArrayList(expr),Lists.newArrayList(exp));
    TypeSymbol a = new TypeSymbol("Foo");
    ex.typeSymbol = Optional.of(a);

    SymGenericTypeExpression exClone = (SymGenericTypeExpression) ex.deepClone();
    assertSame(ex,exClone);
    assertDeepEquals(ex,exClone);
    assertSame(ex.getArguments().get(0),exClone.getArguments().get(0));
    assertSame(ex.getSuperTypes().get(0),exClone.getSuperTypes().get(0));
    assertSame(((SymGenericTypeExpression)(ex.getSuperTypes().get(0))).getArguments().get(0),((SymGenericTypeExpression)exClone.getSuperTypes().get(0)).getArguments().get(0));


    SymTypeExpression e = new SymGenericTypeExpression();
    e.setName("List");
    TypeSymbol s = new TypeSymbol("List");
    e.typeSymbol = java.util.Optional.of(s);
    SymTypeExpression eClone = e.deepClone();
    assertSame(e,eClone);
    assertDeepEquals(e,eClone);
  }
}
