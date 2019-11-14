/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.types.typesymbols._symboltable.BuiltInJavaTypeSymbolResolvingDelegate;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbolsScope;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

import static de.monticore.types.check.SymTypeExpressionFactory.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SymTypeExpressionDeSerTest {
  private static TypeSymbolsScope scope = BuiltInJavaTypeSymbolResolvingDelegate.getScope();

  // setup of objects (unchanged during tests) 
  // these should be the same as those of SymTypeExpressionText
  SymTypeExpression teDouble = createTypeConstant("double");
  SymTypeExpression teInt = createTypeConstant("int");
  SymTypeExpression teVarA = createTypeVariable("A", scope);
  SymTypeExpression teVarB = createTypeVariable("B", scope);
  SymTypeExpression teP = createTypeObject("de.x.Person", scope);
  SymTypeExpression teH = createTypeObject("Human", scope);  // on purpose: package missing
  SymTypeExpression teVoid = createTypeVoid();
  SymTypeExpression teNull = createTypeOfNull();
  SymTypeExpression teArr1 = createTypeArray(teH.print(), scope,  1, teH);
  SymTypeExpression teArr3 = createTypeArray(teInt.print(), scope, 3, teInt);
  SymTypeExpression teSet = createGenerics("java.util.Set", scope, Lists.newArrayList(teP));
  SymTypeExpression teSetA = createGenerics("java.util.Set", scope, Lists.newArrayList(teVarA));
  SymTypeExpression teMap = createGenerics("Map", scope, Lists.newArrayList(teInt,teP)); // no package!
  SymTypeExpression teFoo = createGenerics("x.Foo", scope, Lists.newArrayList(teP,teDouble,teInt,teH));
  SymTypeExpression teDeep1 = createGenerics("java.util.Set", scope, Lists.newArrayList(teMap));
  SymTypeExpression teDeep2 = createGenerics("java.util.Map2", scope, Lists.newArrayList(teInt,teDeep1));

  @BeforeClass
  public static void init() {
    Log.enableFailQuick(false);
    scope.add(new TypeSymbol("long"));
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
