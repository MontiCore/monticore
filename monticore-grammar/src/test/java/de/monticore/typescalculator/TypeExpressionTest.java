package de.monticore.typescalculator;

import com.google.common.collect.Lists;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import org.junit.Test;

import java.util.Optional;

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
    TypeExpression e = new TypeConstant();
    e.setName("double");
    TypeSymbol s = new TypeSymbol("double");
    e.typeSymbol = java.util.Optional.of(s);
    TypeExpression eClone = e.deepClone();
    assertSame(e,eClone);

    assertDeepEquals(e, eClone);
  }

  @Test
  public void deepCloneObjectTypeTest() {
    TypeExpression e = new ObjectType();
    e.setName("String");
    TypeSymbol s = new TypeSymbol("String");
    e.typeSymbol = java.util.Optional.of(s);
    TypeExpression eClone = e.deepClone();
    assertSame(e,eClone);
    assertDeepEquals(e,eClone);

    //example with super-/subclasses

    TypeExpression ex = new ObjectType();
    ex.setName("ArrayList");
    TypeSymbol a = new TypeSymbol("ArrayList");
    TypeExpression exp = new ObjectType();
    exp.setName("List");
    TypeSymbol b = new TypeSymbol("List");
    ex.typeSymbol = Optional.of(a);
    exp.typeSymbol = Optional.of(b);
    ex.setSuperTypes(Lists.newArrayList(exp));

    TypeExpression exClone = ex.deepClone();
    assertSame(ex,exClone);
    assertDeepEquals(ex,exClone);
    assertSame(ex.getSuperTypes().get(0),exClone.getSuperTypes().get(0));
  }

  @Test
  public void deepCloneGenericTypeTest() {
    TypeExpression e = new GenericTypeExpression();
    e.setName("List");
    TypeSymbol s = new TypeSymbol("List");
    e.typeSymbol = java.util.Optional.of(s);
    TypeExpression eClone = e.deepClone();
    assertSame(e,eClone);
    assertDeepEquals(e,eClone);

    //example with generics

    GenericTypeExpression ex = new GenericTypeExpression();
    ex.setName("Foo");
    TypeSymbol a = new TypeSymbol("Foo");
    ex.typeSymbol = Optional.of(a);

    TypeExpression exp = new ObjectType();
    exp.setName("Bar");
    TypeSymbol b = new TypeSymbol("Bar");
    exp.typeSymbol = Optional.of(b);

    ex.setArguments(Lists.newArrayList(exp));

    GenericTypeExpression exClone = (GenericTypeExpression) ex.deepClone();
    assertSame(ex,exClone);
    assertDeepEquals(ex,exClone);
    assertSame(ex.getArguments().get(0),exClone.getArguments().get(0));

    //example with generics and super-/subclasses

    GenericTypeExpression expr = new GenericTypeExpression();
    expr.setName("SuperFoo");
    TypeSymbol c = new TypeSymbol("SuperFoo");
    expr.typeSymbol = Optional.of(c);

    TypeExpression expre = new ObjectType();
    expre.setName("Bar");
    TypeSymbol d = new TypeSymbol("Bar");
    expre.typeSymbol = Optional.of(d);

    expr.setArguments(Lists.newArrayList(expre));

    ex.setSuperTypes(Lists.newArrayList(expr));

    exClone = (GenericTypeExpression) ex.deepClone();
    assertSame(ex,exClone);
    assertDeepEquals(ex,exClone);
    assertSame(ex.getArguments().get(0),exClone.getArguments().get(0));
    assertSame(ex.getSuperTypes().get(0),exClone.getSuperTypes().get(0));
    assertSame(((GenericTypeExpression)(ex.getSuperTypes().get(0))).getArguments().get(0),((GenericTypeExpression)exClone.getSuperTypes().get(0)).getArguments().get(0));
  }
}
