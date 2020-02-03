/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.symboltable.serialization.IDeSer;
import de.monticore.types.typesymbols._symboltable.BuiltInJavaTypeSymbolResolvingDelegate;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbolsArtifactScope;
import de.monticore.types.typesymbols._symboltable.TypeSymbolsScope;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;

import static de.monticore.types.check.SymTypeExpressionFactory.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SymTypeExpressionDeSerTest {
  private static TypeSymbolsScope scope = BuiltInJavaTypeSymbolResolvingDelegate.getScope();

  protected static final String TEST_SYMBOL_STORE_LOCATION = "target/generated-test-sources/monticore/symbols";

  // setup of objects (unchanged during tests)
  // these should be the same as those of SymTypeExpressionText
  SymTypeConstant teDouble = createTypeConstant("double");

  SymTypeConstant teInt = createTypeConstant("int");

  SymTypeVariable teVarA = createTypeVariable("A", scope);

  SymTypeVariable teVarB = createTypeVariable("B", scope);

  SymTypeOfObject teP = createTypeObject("de.x.Person", scope);

  SymTypeOfObject teH = createTypeObject("Human", scope);  // on purpose: package missing

  SymTypeVoid teVoid = createTypeVoid();

  SymTypeOfNull teNull = createTypeOfNull();

  SymTypeArray teArr1 = createTypeArray(teH.print(), scope, 1, teH);

  SymTypeArray teArr3 = createTypeArray(teInt.print(), scope, 3, teInt);

  SymTypeOfGenerics teSet = createGenerics("java.util.Set", scope, Lists.newArrayList(teP));

  SymTypeOfGenerics teSetA = createGenerics("java.util.Set", scope, Lists.newArrayList(teVarA));

  SymTypeOfGenerics teMap = createGenerics("Map", scope,
      Lists.newArrayList(teInt, teP)); // no package!

  SymTypeOfGenerics teFoo = createGenerics("x.Foo", scope,
      Lists.newArrayList(teP, teDouble, teInt, teH));

  SymTypeOfGenerics teDeep1 = createGenerics("java.util.Set", scope, Lists.newArrayList(teMap));

  SymTypeOfGenerics teDeep2 = createGenerics("java.util.Map2", scope,
      Lists.newArrayList(teInt, teDeep1));

  @BeforeClass
  public static void init() {
    Log.enableFailQuick(false);
    LogStub.init();

    scope.add(new TypeSymbol("A"));
    scope.add(new TypeSymbol("B"));
    scope.add(new TypeSymbol("Human"));
    scope.add(new TypeSymbol("Map"));

    TypeSymbolsArtifactScope javaUtilAS = new TypeSymbolsArtifactScope("java.util",
        new ArrayList<>());
    javaUtilAS.add(new TypeSymbol("Map2"));
    scope.addSubScope(javaUtilAS);

    TypeSymbolsArtifactScope deXAS = new TypeSymbolsArtifactScope("de.x", new ArrayList<>());
    deXAS.add(new TypeSymbol("Person"));
    scope.addSubScope(deXAS);

    TypeSymbolsArtifactScope xAS = new TypeSymbolsArtifactScope("x", new ArrayList<>());
    xAS.add(new TypeSymbol("Foo"));
    scope.addSubScope(xAS);
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

    performRoundTripSerializationSymTypeConstant(teDouble);
    performRoundTripSerializationSymTypeConstant(teInt);
    performRoundTripSerializationSymTypeVariable(teVarA);
    performRoundTripSerializationSymTypeVariable(teVarB);
    performRoundTripSerializationSymTypeOfObject(teP);
    performRoundTripSerializationSymTypeOfObject(teH);
    performRoundTripSerializationSymTypeArray(teArr1);
    performRoundTripSerializationSymTypeArray(teArr3);
    performRoundTripSerializationSymTypeOfGenerics(teSet);
    performRoundTripSerializationSymTypeOfGenerics(teSetA);
    performRoundTripSerializationSymTypeOfGenerics(teMap);
    performRoundTripSerializationSymTypeOfGenerics(teFoo);
    performRoundTripSerializationSymTypeOfGenerics(teDeep1);
    performRoundTripSerializationSymTypeOfGenerics(teDeep2);
  }

  protected void performRoundTripSerialization(SymTypeExpression expr) {
    SymTypeExpressionDeSer deser = SymTypeExpressionDeSer.getInstance();
    //first serialize the expression using the deser
    String serialized = deser.serialize(expr);
    // then deserialize it
    SymTypeExpression deserialized = deser.deserialize(serialized, scope);
    assertNotNull(deserialized);
    // and assert that the serialized and deserialized symtype expression equals the one before
    assertEquals(expr.print(), deserialized.print());
    assertEquals(expr.printAsJson(), deserialized.printAsJson());
    TypeSymbol expectedTS = deserialized.getTypeInfo();
    TypeSymbol actualTS = expr.getTypeInfo();
    assertEquals(expectedTS.getName(), actualTS.getName());
  }

  protected void performRoundTripSerializationSymTypeOfGenerics(SymTypeOfGenerics expr){
    SymTypeOfGenericsDeSer deser = new SymTypeOfGenericsDeSer();

    String serialized = deser.serialize(expr);

    SymTypeExpression deserialized = deser.deserialize(serialized,scope);
    assertNotNull(deserialized);

    assertEquals(expr.print(),deserialized.print());
    assertEquals(expr.printAsJson(),deserialized.printAsJson());
    TypeSymbol expectedTS = deserialized.getTypeInfo();
    TypeSymbol actualTS = deserialized.getTypeInfo();
    assertEquals(expectedTS.getName(),actualTS.getName());
  }

  protected void performRoundTripSerializationSymTypeOfObject(SymTypeOfObject expr){
    SymTypeOfObjectDeSer deser = new SymTypeOfObjectDeSer();

    String serialized = deser.serialize(expr);

    SymTypeExpression deserialized = deser.deserialize(serialized,scope);
    assertNotNull(deserialized);

    assertEquals(expr.print(),deserialized.print());
    assertEquals(expr.printAsJson(),deserialized.printAsJson());
    TypeSymbol expectedTS = deserialized.getTypeInfo();
    TypeSymbol actualTS = deserialized.getTypeInfo();
    assertEquals(expectedTS.getName(),actualTS.getName());
  }

  protected void performRoundTripSerializationSymTypeVariable(SymTypeVariable expr){
    SymTypeVariableDeSer deser = new SymTypeVariableDeSer();

    String serialized = deser.serialize(expr);

    SymTypeExpression deserialized = deser.deserialize(serialized,scope);
    assertNotNull(deserialized);

    assertEquals(expr.print(),deserialized.print());
    assertEquals(expr.printAsJson(),deserialized.printAsJson());
    TypeSymbol expectedTS = deserialized.getTypeInfo();
    TypeSymbol actualTS = deserialized.getTypeInfo();
    assertEquals(expectedTS.getName(),actualTS.getName());
  }

  protected void performRoundTripSerializationSymTypeArray(SymTypeArray expr){
    SymTypeArrayDeSer deser = new SymTypeArrayDeSer();

    String serialized = deser.serialize(expr);

    SymTypeExpression deserialized = deser.deserialize(serialized,scope);
    assertNotNull(deserialized);

    assertEquals(expr.print(),deserialized.print());
    assertEquals(expr.printAsJson(),deserialized.printAsJson());
    TypeSymbol expectedTS = deserialized.getTypeInfo();
    TypeSymbol actualTS = deserialized.getTypeInfo();
    assertEquals(expectedTS.getName(),actualTS.getName());
  }

  protected void performRoundTripSerializationSymTypeConstant(SymTypeConstant expr){
    SymTypeConstantDeSer deser = new SymTypeConstantDeSer();

    String serialized = deser.serialize(expr);

    SymTypeExpression deserialized = deser.deserialize(serialized,scope);
    assertNotNull(deserialized);

    assertEquals(expr.print(),deserialized.print());
    assertEquals(expr.printAsJson(),deserialized.printAsJson());
    TypeSymbol expectedTS = deserialized.getTypeInfo();
    TypeSymbol actualTS = deserialized.getTypeInfo();
    assertEquals(expectedTS.getName(),actualTS.getName());
  }



  @Test
  public void testRoundtripLoadStore() throws MalformedURLException {
    performRoundtripLoadStore(teDouble);
    performRoundtripLoadStore(teInt);
    performRoundtripLoadStore(teVarA);
    performRoundtripLoadStore(teVarB);
    performRoundtripLoadStore(teP);
    performRoundtripLoadStore(teH);
    performRoundtripLoadStore(teVoid);
    performRoundtripLoadStore(teNull);
    performRoundtripLoadStore(teArr1);
    performRoundtripLoadStore(teArr3);
    performRoundtripLoadStore(teSet);
    performRoundtripLoadStore(teSetA);
    performRoundtripLoadStore(teMap);
    performRoundtripLoadStore(teFoo);
    performRoundtripLoadStore(teDeep1);
    performRoundtripLoadStore(teDeep2);
  }

  protected void performRoundtripLoadStore(SymTypeExpression expr) throws MalformedURLException {
    SymTypeExpressionDeSer deser = SymTypeExpressionDeSer.getInstance();
    //first serialize the expression using the deser
    deser.store(expr, TEST_SYMBOL_STORE_LOCATION);
    URL url = Paths.get(TEST_SYMBOL_STORE_LOCATION, expr.getTypeInfo().getPackageName(),
        expr.getTypeInfo().getName()+".symtype").toUri().toURL();

    // then deserialize it
    SymTypeExpression loaded = deser.load(url, scope);
    assertNotNull(loaded);
    // and assert that the serialized and deserialized symtype expression equals the one before
    assertEquals(expr.print(), loaded.print());
    assertEquals(expr.printAsJson(), loaded.printAsJson());
    TypeSymbol expectedTS = loaded.getTypeInfo();
    TypeSymbol actualTS = expr.getTypeInfo();
    assertEquals(expectedTS.getName(), actualTS.getName());
  }

}
