/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types2;

import com.google.common.collect.Lists;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import org.junit.Test;

import static de.monticore.types2.SymTypeExpressionFactory.*;
import static org.junit.Assert.*;

public class SymTypeExpressionTest {
  
  // setup of objects (unchanged during tests)
  SymTypeExpression teDouble = createTypeConstant("double");
  SymTypeExpression teInt = createTypeConstant("int");
  SymTypeExpression teVarA = createTypeVariable("A");
  SymTypeExpression teVarB = createTypeVariable("B");
  SymTypeExpression teP = createTypeObject("de.x.Person", new TypeSymbol("long"));
  SymTypeExpression teH = createTypeObject("Human", new TypeSymbol("long"));  // on purpose: package missing
  SymTypeExpression teVoid = createTypeVoid();
  SymTypeExpression teNull = createTypeOfNull();
  SymTypeExpression teArr1 = createTypeArray(1, teH);
  SymTypeExpression teArr3 = createTypeArray(3, teInt);
  SymTypeExpression teSet = createGenerics("java.util.Set", Lists.newArrayList(teP), (TypeSymbol) null);
  SymTypeExpression teSetA = createGenerics("java.util.Set", Lists.newArrayList(teVarA), (TypeSymbol) null);
  SymTypeExpression teMap = createGenerics("Map", Lists.newArrayList(teInt,teP), (TypeSymbol) null); // no package!
  SymTypeExpression teFoo = createGenerics("x.Foo", Lists.newArrayList(teP,teDouble,teInt,teH), (TypeSymbol) null);
  SymTypeExpression teDeep1 = createGenerics("java.util.Set", Lists.newArrayList(teMap), (TypeSymbol) null);
  SymTypeExpression teDeep2 = createGenerics("java.util.Map2", Lists.newArrayList(teInt,teDeep1), (TypeSymbol) null);
  
  
  @Test
  public void printTest() {
    assertEquals("double", teDouble.print());
    assertEquals("int", teInt.print());
    assertEquals("A", teVarA.print());
    assertEquals("de.x.Person", teP.print());
    assertEquals("void", teVoid.print());
    assertEquals("nullType", teNull.print());
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
  
}
