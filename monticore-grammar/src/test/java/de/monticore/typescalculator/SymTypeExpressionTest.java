/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import com.google.common.collect.Lists;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Optional;

import static de.monticore.typescalculator.SymTypeExpressionFactory.*;
import static org.junit.Assert.*;

public class SymTypeExpressionTest {
  
  // setup of objects (unchanged during tests)
  SymTypeExpression teDouble = createTypeConstant("double");
  SymTypeExpression teInt = createTypeConstant("int");
  SymTypeExpression teVarA = createTypeVariable("A");
  SymTypeExpression teVarB = createTypeVariable("B");
  SymTypeExpression teP = createObjectType("de.x.Person");
  SymTypeExpression teH = createObjectType("Human");  // on purpose: package missing
  SymTypeExpression teVoid = createTypeVoid();
  SymTypeExpression teArr1 = createArrayType(1, teH);
  SymTypeExpression teArr3 = createArrayType(3, teInt);
  SymTypeExpression teSet = createGenericTypeExpression("java.util.Set", Lists.newArrayList(teP));
  SymTypeExpression teSetA = createGenericTypeExpression("java.util.Set", Lists.newArrayList(teVarA));
  SymTypeExpression teMap = createGenericTypeExpression("Map", Lists.newArrayList(teInt,teP)); // no package!
  SymTypeExpression teFoo = createGenericTypeExpression("x.Foo", Lists.newArrayList(teP,teDouble,teInt,teH));
  SymTypeExpression teDeep1 = createGenericTypeExpression("java.util.Set", Lists.newArrayList(teMap));
  SymTypeExpression teDeep2 = createGenericTypeExpression("java.util.Map2", Lists.newArrayList(teInt,teDeep1));
  
  
  @Test
  public void printTest() {
    assertEquals("double", teDouble.print());
    assertEquals("int", teInt.print());
    assertEquals("A", teVarA.print());
    assertEquals("de.x.Person", teP.print());
    assertEquals("void", teVoid.print());
    assertEquals("Human[]", teArr1.print());
    assertEquals("int[][][]", teArr3.print());
    assertEquals("java.util.Set<de.x.Person>", teSet.print());
    assertEquals("java.util.Set<A>", teSetA.print());
    assertEquals("Map<int,de.x.Person>", teMap.print());
    assertEquals("x.Foo<de.x.Person,double,int,Human>", teFoo.print());
    assertEquals("java.util.Set<Map<int,de.x.Person>>", teDeep1.print());
    assertEquals("java.util.Map2<int,java.util.Set<Map<int,de.x.Person>>>", teDeep2.print());
  }
  
  @Test
  public void baseNameTest() {
    assertEquals("Person", teP.getBaseName());
    assertEquals("Human", teH.getBaseName());
    assertEquals("Map", teMap.getBaseName());
    assertEquals("Set", teSetA.getBaseName());
  }
  
  
  // --------------------------------------------------------------------------
  
  @Deprecated
  private void assertSame(SymTypeExpression e, SymTypeExpression eClone) {
    assertFalse(e.equals(eClone));
    assertTrue(e.getName().equals(eClone.getName()));
    assertEquals(e.typeSymbol, eClone.typeSymbol);
  }

  @Deprecated
  private void assertDeepEquals(SymTypeExpression a, SymTypeExpression b){
    assertTrue(a.deepEquals(b));
  }

  @Test @Ignore @Deprecated
  public void deepCloneTypeConstantTest() {
    SymTypeExpression e = createTypeConstant("double");
    TypeSymbol s = new TypeSymbol("double");
    e.typeSymbol = java.util.Optional.of(s);
    SymTypeExpression eClone = e.deepClone();
    assertSame(e,eClone);

    assertDeepEquals(e, eClone);
  }

  @Test @Ignore @Deprecated
  public void deepCloneObjectTypeTest() {
    SymTypeExpression e = createObjectType("String");
    TypeSymbol s = new TypeSymbol("String");
    e.typeSymbol = java.util.Optional.of(s);
    SymTypeExpression eClone = e.deepClone();
    assertSame(e,eClone);
    assertDeepEquals(e,eClone);

    //example with super-/subclasses
    SymTypeExpression exp = createObjectType("List");
    TypeSymbol b = new TypeSymbol("List");
    exp.typeSymbol = Optional.of(b);

    SymTypeExpression ex = createObjectType("ArrayList");
    TypeSymbol a = new TypeSymbol("ArrayList");
    ex.typeSymbol = Optional.of(a);

    SymTypeExpression exClone = ex.deepClone();
    assertSame(ex,exClone);
    assertDeepEquals(ex,exClone);
    assertSame(ex.getSuperTypes().get(0),exClone.getSuperTypes().get(0));
  }

  @Test @Ignore @Deprecated
  public void deepCloneGenericTypeTest() {
    SymTypeExpression expre = createObjectType("Bar");
    TypeSymbol d = new TypeSymbol("Bar");
    expre.typeSymbol = Optional.of(d);
  
  
    SymTypeExpression exp = createObjectType("Bar");
    TypeSymbol b = new TypeSymbol("Bar");
    exp.typeSymbol = Optional.of(b);

    SymGenericTypeExpression ex = createGenericTypeExpression("Foo",Lists.newArrayList(exp));
    TypeSymbol a = new TypeSymbol("Foo");
    ex.typeSymbol = Optional.of(a);

    SymGenericTypeExpression exClone = (SymGenericTypeExpression) ex.deepClone();
    assertSame(ex,exClone);
    assertDeepEquals(ex,exClone);
    assertSame(ex.getArguments().get(0),exClone.getArguments().get(0));
    
    SymTypeExpression e = new SymGenericTypeExpression();
    e.setName("List");
    TypeSymbol s = new TypeSymbol("List");
    e.typeSymbol = java.util.Optional.of(s);
    SymTypeExpression eClone = e.deepClone();
    assertSame(e,eClone);
    assertDeepEquals(e,eClone);
  }
}
