/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.types.typesymbols._symboltable.BuiltInJavaTypeSymbolResolvingDelegate;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbolsArtifactScope;
import de.monticore.types.typesymbols._symboltable.TypeSymbolsScope;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.ArrayList;

import static de.monticore.types.check.SymTypeExpressionFactory.*;
import static org.junit.Assert.*;

public class SymTypeExpressionDeSerTest {
  private static TypeSymbolsScope scope = BuiltInJavaTypeSymbolResolvingDelegate.getScope();

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

  SymTypeOfWildcard teLowerBound = createWildcard(false,teInt);

  SymTypeOfWildcard teUpperBound = createWildcard(true, teH);

  SymTypeOfWildcard teWildcard = createWildcard();

  SymTypeOfGenerics teMap2 = createGenerics("Map",scope,Lists.newArrayList(teUpperBound,teWildcard));

  @BeforeClass
  public static void init() {
    Log.enableFailQuick(false);

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
    performRoundTripSerialization(teLowerBound);
    performRoundTripSerialization(teUpperBound);
    performRoundTripSerialization(teWildcard);
    performRoundTripSerialization(teMap2);

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
    performRoundTripSerializationSymTypeOfGenerics(teMap2);
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
    if(!(deserialized instanceof SymTypeOfWildcard)) {
      TypeSymbol expectedTS = deserialized.getTypeInfo();
      TypeSymbol actualTS = expr.getTypeInfo();
      assertEquals(expectedTS.getName(), actualTS.getName());
    }
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

    SymTypeExpression deserialized = deser.deserialize(serialized);
    assertNotNull(deserialized);

    assertEquals(expr.print(),deserialized.print());
    assertEquals(expr.printAsJson(),deserialized.printAsJson());
    TypeSymbol expectedTS = deserialized.getTypeInfo();
    TypeSymbol actualTS = deserialized.getTypeInfo();
    assertEquals(expectedTS.getName(),actualTS.getName());
  }



  @Test
  public void testRoundtrip2() throws MalformedURLException {
    performRoundtrip2(teDouble);
    performRoundtrip2(teInt);
    performRoundtrip2(teVarA);
    performRoundtrip2(teVarB);
    performRoundtrip2(teP);
    performRoundtrip2(teH);
    performRoundtrip2(teVoid);
    performRoundtrip2(teNull);
    performRoundtrip2(teArr1);
    performRoundtrip2(teArr3);
    performRoundtrip2(teSet);
    performRoundtrip2(teSetA);
    performRoundtrip2(teMap);
    performRoundtrip2(teFoo);
    performRoundtrip2(teDeep1);
    performRoundtrip2(teDeep2);
    performRoundtrip2(teUpperBound);
    performRoundtrip2(teLowerBound);
    performRoundtrip2(teWildcard);
    performRoundtrip2(teMap2);
  }

  protected void performRoundtrip2(SymTypeExpression expr) throws MalformedURLException {
    SymTypeExpressionDeSer deser = SymTypeExpressionDeSer.getInstance();
    //first serialize the expression using the deser
    String serialized = deser.serialize(expr);

    // then deserialize it
    SymTypeExpression loaded = deser.deserialize(serialized, scope);
    assertNotNull(loaded);
    // and assert that the serialized and deserialized symtype expression equals the one before
    assertEquals(expr.print(), loaded.print());
    assertEquals(expr.printAsJson(), loaded.printAsJson());
    if(!(loaded instanceof SymTypeOfWildcard)) {
      TypeSymbol expectedTS = loaded.getTypeInfo();
      TypeSymbol actualTS = expr.getTypeInfo();
      assertEquals(expectedTS.getName(), actualTS.getName());
    }
  }

  @Test
  public void testInvalidJsonForSerializingReturnsError(){
    String invalidJsonForSerializing = "\"Foo\":\"bar\"";
    String invalidJsonForSerializing2 = "{\n\t\"symTypeExpression\": {\n\t\t\"foo\":\"bar\", \n\t\t\"foo2\":\"bar2\"\n\t}\n}";

    SymTypeExpressionDeSer.getInstance().deserialize(invalidJsonForSerializing, scope);
    assertTrue(Log.getFindings().get(Log.getFindings().size()-1).getMsg().startsWith("0x823F3"));

    SymTypeExpressionDeSer.getInstance().deserialize(invalidJsonForSerializing2,scope);
    assertTrue(Log.getFindings().get(Log.getFindings().size()-1).getMsg().startsWith("0x823FE"));

    SymTypeOfGenericsDeSer symTypeOfGenericsDeSer = new SymTypeOfGenericsDeSer();
    symTypeOfGenericsDeSer.deserialize(invalidJsonForSerializing, scope);
    assertTrue(Log.getFindings().get(Log.getFindings().size()-1).getMsg().startsWith("0x823F6"));

    SymTypeArrayDeSer symTypeArrayDeSer = new SymTypeArrayDeSer();
    symTypeArrayDeSer.deserialize(invalidJsonForSerializing,scope);
    assertTrue(Log.getFindings().get(Log.getFindings().size()-1).getMsg().startsWith("0x823F2"));

    SymTypeOfObjectDeSer symTypeOfObjectDeSer = new SymTypeOfObjectDeSer();
    symTypeOfObjectDeSer.deserialize(invalidJsonForSerializing,scope);
    assertTrue(Log.getFindings().get(Log.getFindings().size()-1).getMsg().startsWith("0x823F4"));

    SymTypeVariableDeSer symTypeVariableDeSer = new SymTypeVariableDeSer();
    symTypeVariableDeSer.deserialize(invalidJsonForSerializing,scope);
    assertTrue(Log.getFindings().get(Log.getFindings().size()-1).getMsg().startsWith("0x823F5"));

    SymTypeConstantDeSer symTypeConstantDeser = new SymTypeConstantDeSer();
    symTypeConstantDeser.deserialize(invalidJsonForSerializing);
    assertTrue(Log.getFindings().get(Log.getFindings().size()-1).getMsg().startsWith("0x823F1"));

    SymTypeOfWildcardDeSer symTypeOfWildcardDeSer = new SymTypeOfWildcardDeSer();
    symTypeOfWildcardDeSer.deserialize(invalidJsonForSerializing,scope);
    assertTrue(Log.getFindings().get(Log.getFindings().size()-1).getMsg().startsWith("0x823F7"));
  }

}
