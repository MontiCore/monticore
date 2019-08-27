package de.monticore.typescalculator;

import com.google.common.collect.Lists;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import org.junit.Test;

import java.util.Optional;

import static de.monticore.typescalculator.TypeExpressionBuilder.*;
import static org.junit.Assert.*;

public class TypeExpressionTest {

  private void assertSame(TypeExpression e, TypeExpression eClone) {
    assertFalse(e.equals(eClone));
    assertTrue(e.getName().equals(eClone.getName()));
    assertEquals(e.typeSymbol, eClone.typeSymbol);
  }

  private void assertDeepEquals(TypeExpression a, TypeExpression b){
    assertTrue(a.deepEquals(b));
  }

  @Test
  public void deepCloneTypeConstantTest() {
    TypeExpression e = buildTypeConstant("double");
    TypeSymbol s = new TypeSymbol("double");
    e.typeSymbol = java.util.Optional.of(s);
    TypeExpression eClone = e.deepClone();
    assertSame(e,eClone);

    assertDeepEquals(e, eClone);
  }

  @Test
  public void deepCloneObjectTypeTest() {
    TypeExpression e = buildObjectType("String", Lists.newArrayList());
    TypeSymbol s = new TypeSymbol("String");
    e.typeSymbol = java.util.Optional.of(s);
    TypeExpression eClone = e.deepClone();
    assertSame(e,eClone);
    assertDeepEquals(e,eClone);

    //example with super-/subclasses
    TypeExpression exp = buildObjectType("List", Lists.newArrayList());
    TypeSymbol b = new TypeSymbol("List");
    exp.typeSymbol = Optional.of(b);

    TypeExpression ex = buildObjectType("ArrayList", Lists.newArrayList(exp));
    TypeSymbol a = new TypeSymbol("ArrayList");
    ex.typeSymbol = Optional.of(a);

    TypeExpression exClone = ex.deepClone();
    assertSame(ex,exClone);
    assertDeepEquals(ex,exClone);
    assertSame(ex.getSuperTypes().get(0),exClone.getSuperTypes().get(0));
  }

  @Test
  public void deepCloneGenericTypeTest() {
    TypeExpression expre = buildObjectType("Bar", Lists.newArrayList());
    TypeSymbol d = new TypeSymbol("Bar");
    expre.typeSymbol = Optional.of(d);


    GenericTypeExpression expr = buildGenericTypeExpression("SuperFoo",Lists.newArrayList(),Lists.newArrayList(expre));
    TypeSymbol c = new TypeSymbol("SuperFoo");
    expr.typeSymbol = Optional.of(c);

    TypeExpression exp = buildObjectType("Bar",Lists.newArrayList());
    TypeSymbol b = new TypeSymbol("Bar");
    exp.typeSymbol = Optional.of(b);

    GenericTypeExpression ex = buildGenericTypeExpression("Foo",Lists.newArrayList(expr),Lists.newArrayList(exp));
    TypeSymbol a = new TypeSymbol("Foo");
    ex.typeSymbol = Optional.of(a);

    GenericTypeExpression exClone = (GenericTypeExpression) ex.deepClone();
    assertSame(ex,exClone);
    assertDeepEquals(ex,exClone);
    assertSame(ex.getArguments().get(0),exClone.getArguments().get(0));
    assertSame(ex.getSuperTypes().get(0),exClone.getSuperTypes().get(0));
    assertSame(((GenericTypeExpression)(ex.getSuperTypes().get(0))).getArguments().get(0),((GenericTypeExpression)exClone.getSuperTypes().get(0)).getArguments().get(0));


    TypeExpression e = new GenericTypeExpression();
    e.setName("List");
    TypeSymbol s = new TypeSymbol("List");
    e.typeSymbol = java.util.Optional.of(s);
    TypeExpression eClone = e.deepClone();
    assertSame(e,eClone);
    assertDeepEquals(e,eClone);
  }
}
