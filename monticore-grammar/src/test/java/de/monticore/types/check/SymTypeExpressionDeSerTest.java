/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.symbols.oosymbols._symboltable.*;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.monticore.types.check.SymTypeExpressionFactory.*;
import static org.junit.Assert.*;

public class SymTypeExpressionDeSerTest {
  private static IOOSymbolsScope scope = BuiltInJavaSymbolResolvingDelegate.getScope();

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

    scope.add(new OOTypeSymbol("A"));
    scope.add(new OOTypeSymbol("B"));
    scope.add(new OOTypeSymbol("Human"));
    scope.add(new OOTypeSymbol("Map"));

    OOSymbolsArtifactScope javaUtilAS = new OOSymbolsArtifactScope("java.util",
        new ArrayList<>());
    javaUtilAS.add(new OOTypeSymbol("Map2"));
    scope.addSubScope(javaUtilAS);

    OOSymbolsArtifactScope deXAS = new OOSymbolsArtifactScope("de.x", new ArrayList<>());
    deXAS.add(new OOTypeSymbol("Person"));
    scope.addSubScope(deXAS);

    OOSymbolsArtifactScope xAS = new OOSymbolsArtifactScope("x", new ArrayList<>());
    xAS.add(new OOTypeSymbol("Foo"));
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
      OOTypeSymbol expectedTS = deserialized.getTypeInfo();
      OOTypeSymbol actualTS = expr.getTypeInfo();
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
    OOTypeSymbol expectedTS = deserialized.getTypeInfo();
    OOTypeSymbol actualTS = deserialized.getTypeInfo();
    assertEquals(expectedTS.getName(),actualTS.getName());
  }

  protected void performRoundTripSerializationSymTypeOfObject(SymTypeOfObject expr){
    SymTypeOfObjectDeSer deser = new SymTypeOfObjectDeSer();

    String serialized = deser.serialize(expr);

    SymTypeExpression deserialized = deser.deserialize(serialized,scope);
    assertNotNull(deserialized);

    assertEquals(expr.print(),deserialized.print());
    assertEquals(expr.printAsJson(),deserialized.printAsJson());
    OOTypeSymbol expectedTS = deserialized.getTypeInfo();
    OOTypeSymbol actualTS = deserialized.getTypeInfo();
    assertEquals(expectedTS.getName(),actualTS.getName());
  }

  protected void performRoundTripSerializationSymTypeVariable(SymTypeVariable expr){
    SymTypeVariableDeSer deser = new SymTypeVariableDeSer();

    String serialized = deser.serialize(expr);

    SymTypeExpression deserialized = deser.deserialize(serialized,scope);
    assertNotNull(deserialized);

    assertEquals(expr.print(),deserialized.print());
    assertEquals(expr.printAsJson(),deserialized.printAsJson());
    OOTypeSymbol expectedTS = deserialized.getTypeInfo();
    OOTypeSymbol actualTS = deserialized.getTypeInfo();
    assertEquals(expectedTS.getName(),actualTS.getName());
  }

  protected void performRoundTripSerializationSymTypeArray(SymTypeArray expr){
    SymTypeArrayDeSer deser = new SymTypeArrayDeSer();

    String serialized = deser.serialize(expr);

    SymTypeExpression deserialized = deser.deserialize(serialized,scope);
    assertNotNull(deserialized);

    assertEquals(expr.print(),deserialized.print());
    assertEquals(expr.printAsJson(),deserialized.printAsJson());
    OOTypeSymbol expectedTS = deserialized.getTypeInfo();
    OOTypeSymbol actualTS = deserialized.getTypeInfo();
    assertEquals(expectedTS.getName(),actualTS.getName());
  }

  protected void performRoundTripSerializationSymTypeConstant(SymTypeConstant expr){
    SymTypeConstantDeSer deser = new SymTypeConstantDeSer();

    String serialized = deser.serialize(expr);

    SymTypeExpression deserialized = deser.deserialize(serialized);
    assertNotNull(deserialized);

    assertEquals(expr.print(),deserialized.print());
    assertEquals(expr.printAsJson(),deserialized.printAsJson());
    OOTypeSymbol expectedTS = deserialized.getTypeInfo();
    OOTypeSymbol actualTS = deserialized.getTypeInfo();
    assertEquals(expectedTS.getName(),actualTS.getName());
  }

  @Test
  public void testRoundtrip2() {
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

  protected void performRoundtrip2(SymTypeExpression expr) {
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
      OOTypeSymbol expectedTS = loaded.getTypeInfo();
      OOTypeSymbol actualTS = expr.getTypeInfo();
      assertEquals(expectedTS.getName(), actualTS.getName());
    }

    // usual member
    JsonPrinter printer = new JsonPrinter();
    SymTypeExpressionDeSer.serializeMember(printer, "foo", expr);
    //produce a fake JSON object from the serialized member and parse this
    JsonObject json = JsonParser.parseJsonObject("{"+printer.getContent()+"}");
    SymTypeExpression deserialized = SymTypeExpressionDeSer.deserializeMember("foo", json, scope);
    assertEquals(expr.print(), deserialized.print());

    // optional member that is present
    printer = new JsonPrinter();
    SymTypeExpressionDeSer.serializeMember(printer, "foo", Optional.ofNullable(expr));
    json = JsonParser.parseJsonObject("{"+printer.getContent()+"}");
    Optional<SymTypeExpression> deserializedOpt = SymTypeExpressionDeSer.deserializeOptionalMember("foo", json, scope);
    assertTrue(deserializedOpt.isPresent());
    assertEquals(expr.print(), deserializedOpt.get().print());

    // optional member that is empty
    printer = new JsonPrinter();
    SymTypeExpressionDeSer.serializeMember(printer, "foo", Optional.empty());
    json = JsonParser.parseJsonObject("{"+printer.getContent()+"}");
    deserializedOpt = SymTypeExpressionDeSer.deserializeOptionalMember("foo", json, scope);
    assertTrue(!deserializedOpt.isPresent());

    // list member that is empty
    printer = new JsonPrinter();
    SymTypeExpressionDeSer.serializeMember(printer, "foo", new ArrayList<>());
    json = JsonParser.parseJsonObject("{"+printer.getContent()+"}");
    List<SymTypeExpression> deserializedList = SymTypeExpressionDeSer.deserializeListMember("foo", json, scope);
    assertEquals(0, deserializedList.size());

    // list member with single element
    printer = new JsonPrinter();
    SymTypeExpressionDeSer.serializeMember(printer, "foo", Lists.newArrayList(expr));
    json = JsonParser.parseJsonObject("{"+printer.getContent()+"}");
    deserializedList = SymTypeExpressionDeSer.deserializeListMember("foo", json, scope);
    assertEquals(1, deserializedList.size());
    assertEquals(expr.print(), deserializedList.get(0).print());

    // list member with two elements
    printer = new JsonPrinter();
    SymTypeExpressionDeSer.serializeMember(printer, "foo", Lists.newArrayList(expr, expr));
    json = JsonParser.parseJsonObject("{"+printer.getContent()+"}");
    deserializedList = SymTypeExpressionDeSer.deserializeListMember("foo", json, scope);
    assertEquals(2, deserializedList.size());
    assertEquals(expr.print(), deserializedList.get(0).print());
    assertEquals(expr.print(), deserializedList.get(1).print());
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
