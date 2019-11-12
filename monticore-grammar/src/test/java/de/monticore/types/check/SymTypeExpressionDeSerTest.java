/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.monticore.types.check.SymTypeExpressionFactory.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SymTypeExpressionDeSerTest {
  //TODO AB: Replace TypSymbol constructors with real values and mill

  // setup of objects (unchanged during tests) 
  // these should be the same as those of SymTypeExpressionText
  SymTypeExpression teDouble = createTypeConstant("double");
  SymTypeExpression teInt = createTypeConstant("int");
  SymTypeExpression teVarA = createTypeVariable("A", null);
  SymTypeExpression teVarB = createTypeVariable("B", null);
  SymTypeExpression teP = createTypeObject("de.x.Person", new TypeSymbol("long"));
  SymTypeExpression teH = createTypeObject("Human", new TypeSymbol("long"));  // on purpose: package missing
  SymTypeExpression teVoid = createTypeVoid();
  SymTypeExpression teNull = createTypeOfNull();
  SymTypeExpression teArr1 = createTypeArray(1, teH, new TypeSymbol("long"));
  SymTypeExpression teArr3 = createTypeArray(3, teInt, new TypeSymbol("long"));
  SymTypeExpression teSet = createGenerics("java.util.Set", Lists.newArrayList(teP), new TypeSymbol("long"));
  SymTypeExpression teSetA = createGenerics("java.util.Set", Lists.newArrayList(teVarA), new TypeSymbol("long"));
  SymTypeExpression teMap = createGenerics("Map", Lists.newArrayList(teInt,teP), (TypeSymbol) new TypeSymbol("long")); // no package!
  SymTypeExpression teFoo = createGenerics("x.Foo", Lists.newArrayList(teP,teDouble,teInt,teH), new TypeSymbol("long"));
  SymTypeExpression teDeep1 = createGenerics("java.util.Set", Lists.newArrayList(teMap), new TypeSymbol("long"));
  SymTypeExpression teDeep2 = createGenerics("java.util.Map2", Lists.newArrayList(teInt,teDeep1), new TypeSymbol("long"));

  @BeforeClass
  public static void init() {
//    LogStub.init();
    Log.enableFailQuick(false);
  }

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
    SymTypeExpressionDeSer deser = SymTypeExpressionDeSer.getInstance();
    //first serialize the expression using the deser
    String serialized = deser.serialize(expr);
    // then deserialize it
    SymTypeExpression deserialized = deser.deserialize(serialized);
    assertNotNull(deserialized);
    // and assert that the serialized and deserialized symtype expression equals the one before
    assertEquals(expr.print(), deserialized.print());
    assertEquals(expr.printAsJson(), deserialized.printAsJson());
  }
  
}
