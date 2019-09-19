/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.types2;

import static de.monticore.types2.SymTypeExpressionFactory.createGenerics;
import static de.monticore.types2.SymTypeExpressionFactory.createTypeArray;
import static de.monticore.types2.SymTypeExpressionFactory.createTypeConstant;
import static de.monticore.types2.SymTypeExpressionFactory.createTypeObject;
import static de.monticore.types2.SymTypeExpressionFactory.createTypeOfNull;
import static de.monticore.types2.SymTypeExpressionFactory.createTypeVariable;
import static de.monticore.types2.SymTypeExpressionFactory.createTypeVoid;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.google.common.collect.Lists;

import de.monticore.types.typesymbols._symboltable.TypeSymbol;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
public class SymTypeExpressionDeSerTest {
  
  // setup of objects (unchanged during tests) 
  // these should be the same as those of SymTypeExpressionText
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
  public void testRoundtripSerialization() {
    performRoundTripSerialization(teDouble);
    performRoundTripSerialization(teInt);
    performRoundTripSerialization(teVarA);
    performRoundTripSerialization(teVarB);
    performRoundTripSerialization(teP);
    performRoundTripSerialization(teH);
    performRoundTripSerialization(teVoid);
    performRoundTripSerialization(teNull);
    performRoundTripSerialization(teArr1);
    performRoundTripSerialization(teArr3);
    performRoundTripSerialization(teSet);
    performRoundTripSerialization(teSetA);
    performRoundTripSerialization(teMap);
    performRoundTripSerialization(teFoo);
    performRoundTripSerialization(teDeep1);
    performRoundTripSerialization(teDeep2);
  }
  
  protected void performRoundTripSerialization(SymTypeExpression expr) {
    SymTypeExpressionDeSer deser = new SymTypeExpressionDeSer();
    //first serialize the expression using the deser
    String serialized = deser.serialize(expr);
    // then deserialize it
    SymTypeExpression deserialized = deser.deserialize(serialized).orElse(null);
    assertNotNull(deserialized);
    // and assert that the serialized and deserialized symtype expression equals the one before
    assertEquals(expr.print(), deserialized.print());
    assertEquals(expr.printAsJson(), deserialized.printAsJson());
  }
  
}
