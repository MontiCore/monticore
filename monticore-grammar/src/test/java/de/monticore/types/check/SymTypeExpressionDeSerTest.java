/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsArtifactScope;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.monticore.types.check.SymTypeExpressionFactory.*;

public class SymTypeExpressionDeSerTest {
  // setup of objects (unchanged during tests)
  // these should be the same as those of SymTypeExpressionText
  SymTypePrimitive teDouble;

  SymTypePrimitive teInt;

  SymTypeVariable teVarA;

  SymTypeVariable teVarB;

  SymTypeOfObject teP;

  SymTypeOfObject teH;  // on purpose: package missing

  SymTypeVoid teVoid;

  SymTypeOfNull teNull;

  SymTypeArray teArr1;

  SymTypeArray teArr3;

  SymTypeOfGenerics teSet;

  SymTypeOfGenerics teSetA;

  SymTypeOfGenerics teMap;

  SymTypeOfGenerics teFoo;

  SymTypeOfGenerics teDeep1;

  SymTypeOfGenerics teDeep2;

  SymTypeOfWildcard teLowerBound;

  SymTypeOfWildcard teUpperBound;

  SymTypeOfWildcard teWildcard;

  SymTypeOfGenerics teMap2;

  SymTypeOfFunction teFun1;

  SymTypeOfFunction teFun2;

  SymTypeOfFunction teFun3;

  SymTypeOfFunction teFun4;

  SymTypeOfUnion teUnion1;

  SymTypeOfRegEx teRegEx1;

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    OOSymbolsMill.reset();
    OOSymbolsMill.init();
    BasicSymbolsMill.initializePrimitives();

    IOOSymbolsScope scope = OOSymbolsMill.scope();

    // setup of objects (unchanged during tests)
    // these should be the same as those of SymTypeExpressionText
    teDouble = createPrimitive("double");

    teInt = createPrimitive("int");

    teVarA = createTypeVariable("A", scope);

    teVarB = createTypeVariable("B", scope);

    teP = createTypeObject("de.x.Person", scope);

    teH = createTypeObject("Human", scope);  // on purpose: package missing

    teVoid = createTypeVoid();

    teNull = createTypeOfNull();

    teArr1 = createTypeArray(teH.print(), scope, 1, teH);

    teArr3 = createTypeArray(teInt.print(), scope, 3, teInt);

    teSet = createGenerics("java.util.Set", scope, Lists.newArrayList(teP));

    teSetA = createGenerics("java.util.Set", scope, Lists.newArrayList(teVarA));

    teMap = createGenerics("Map", scope,
            Lists.newArrayList(teInt, teP)); // no package!

    teFoo = createGenerics("x.Foo", scope,
            Lists.newArrayList(teP, teDouble, teInt, teH));

    teDeep1 = createGenerics("java.util.Set", scope, Lists.newArrayList(teMap));

    teDeep2 = createGenerics("java.util.Map2", scope,
            Lists.newArrayList(teInt, teDeep1));

    teLowerBound = createWildcard(false, teInt);

    teUpperBound = createWildcard(true, teH);

    teWildcard = createWildcard();

    teMap2 = createGenerics("Map", scope,
            Lists.newArrayList(teUpperBound, teWildcard));

    teFun1 = createFunction(teInt);

    teFun2 = createFunction(teInt, teInt);

    teFun3 = createFunction(teInt, teInt, teP);

    teFun4 = createFunction(teInt, List.of(teInt), true);

    teUnion1 = createUnion(teInt, teDouble);

    teRegEx1 = createTypeRegEx("gr(a|e)y");

    scope.add(new OOTypeSymbol("A"));
    scope.add(new OOTypeSymbol("B"));
    scope.add(new OOTypeSymbol("Human"));
    scope.add(new OOTypeSymbol("Map"));

    IOOSymbolsArtifactScope javaUtilAS = OOSymbolsMill.artifactScope();
    javaUtilAS.add(new OOTypeSymbol("Map2"));
    scope.addSubScope(javaUtilAS);

    IOOSymbolsArtifactScope deXAS = OOSymbolsMill.artifactScope();
    deXAS.add(new OOTypeSymbol("Person"));
    scope.addSubScope(deXAS);

    IOOSymbolsArtifactScope xAS = OOSymbolsMill.artifactScope();
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
    performRoundTripSerialization(teFun1);
    performRoundTripSerialization(teFun2);
    performRoundTripSerialization(teFun3);
    performRoundTripSerialization(teFun4);
    performRoundTripSerialization(teUnion1);
    performRoundTripSerialization(teRegEx1);

    performRoundTripSerializationSymTypePrimitive(teDouble);
    performRoundTripSerializationSymTypePrimitive(teInt);
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
    performRoundTripSerializationSymTypeOfFunction(teFun1);
    performRoundTripSerializationSymTypeOfFunction(teFun2);
    performRoundTripSerializationSymTypeOfFunction(teFun3);
    performRoundTripSerializationSymTypeOfFunction(teFun4);
    performRoundTripSerializationSymTypeOfUnion(teUnion1);
    performRoundTripSerializationSymTypeOfRegEx(teRegEx1);
  }

  protected void performRoundTripSerialization(SymTypeExpression expr) {
    SymTypeExpressionDeSer deser = SymTypeExpressionDeSer.getInstance();
    //first serialize the expression using the deser
    String serialized = deser.serialize(expr);
    // then deserialize it
    SymTypeExpression deserialized = deser.deserialize(serialized);
    Assertions.assertNotNull(deserialized);
    // and assert that the serialized and deserialized symtype expression equals the one before
    Assertions.assertEquals(expr.print(), deserialized.print());
    Assertions.assertEquals(expr.printAsJson(), deserialized.printAsJson());
    if (!(deserialized instanceof SymTypeOfWildcard)
        && !(deserialized instanceof SymTypeOfRegEx)) {
      TypeSymbol expectedTS = deserialized.getTypeInfo();
      TypeSymbol actualTS = expr.getTypeInfo();
      Assertions.assertEquals(expectedTS.getName(), actualTS.getName());
    }
  }

  protected void performRoundTripSerializationSymTypeOfGenerics(SymTypeOfGenerics expr) {
    SymTypeOfGenericsDeSer deser = new SymTypeOfGenericsDeSer();

    String serialized = deser.serialize(expr);

    SymTypeExpression deserialized = deser.deserialize(serialized);
    Assertions.assertNotNull(deserialized);

    Assertions.assertEquals(expr.print(), deserialized.print());
    Assertions.assertEquals(expr.printAsJson(), deserialized.printAsJson());
    TypeSymbol expectedTS = deserialized.getTypeInfo();
    TypeSymbol actualTS = deserialized.getTypeInfo();
    Assertions.assertEquals(expectedTS.getName(), actualTS.getName());
  }

  protected void performRoundTripSerializationSymTypeOfFunction(SymTypeOfFunction expr) {
    SymTypeOfFunctionDeSer deser = new SymTypeOfFunctionDeSer();

    String serialized = deser.serialize(expr);

    SymTypeExpression deserialized = deser.deserialize(serialized);
    Assertions.assertNotNull(deserialized);

    Assertions.assertEquals(expr.print(), deserialized.print());
    Assertions.assertEquals(expr.printAsJson(), deserialized.printAsJson());
    TypeSymbol expectedTS = deserialized.getTypeInfo();
    TypeSymbol actualTS = deserialized.getTypeInfo();
    Assertions.assertEquals(expectedTS.getName(), actualTS.getName());
  }

  protected void performRoundTripSerializationSymTypeOfObject(SymTypeOfObject expr) {
    SymTypeOfObjectDeSer deser = new SymTypeOfObjectDeSer();

    String serialized = deser.serialize(expr);

    SymTypeExpression deserialized = deser.deserialize(serialized);
    Assertions.assertNotNull(deserialized);

    Assertions.assertEquals(expr.print(), deserialized.print());
    Assertions.assertEquals(expr.printAsJson(), deserialized.printAsJson());
    TypeSymbol expectedTS = deserialized.getTypeInfo();
    TypeSymbol actualTS = deserialized.getTypeInfo();
    Assertions.assertEquals(expectedTS.getName(), actualTS.getName());
  }

  protected void performRoundTripSerializationSymTypeVariable(SymTypeVariable expr) {
    SymTypeVariableDeSer deser = new SymTypeVariableDeSer();

    String serialized = deser.serialize(expr);

    SymTypeExpression deserialized = deser.deserialize(serialized);
    Assertions.assertNotNull(deserialized);

    Assertions.assertEquals(expr.print(), deserialized.print());
    Assertions.assertEquals(expr.printAsJson(), deserialized.printAsJson());
    TypeSymbol expectedTS = deserialized.getTypeInfo();
    TypeSymbol actualTS = deserialized.getTypeInfo();
    Assertions.assertEquals(expectedTS.getName(), actualTS.getName());
  }

  protected void performRoundTripSerializationSymTypeArray(SymTypeArray expr) {
    SymTypeArrayDeSer deser = new SymTypeArrayDeSer();

    String serialized = deser.serialize(expr);

    SymTypeExpression deserialized = deser.deserialize(serialized);
    Assertions.assertNotNull(deserialized);

    Assertions.assertEquals(expr.print(), deserialized.print());
    Assertions.assertEquals(expr.printAsJson(), deserialized.printAsJson());
    TypeSymbol expectedTS = deserialized.getTypeInfo();
    TypeSymbol actualTS = deserialized.getTypeInfo();
    Assertions.assertEquals(expectedTS.getName(), actualTS.getName());

    //assertTrue(Log.getFindings().isEmpty());
  }

  protected void performRoundTripSerializationSymTypePrimitive(SymTypePrimitive expr) {
    SymTypePrimitiveDeSer deser = new SymTypePrimitiveDeSer();

    String serialized = deser.serialize(expr);

    SymTypeExpression deserialized = deser.deserialize(serialized);
    Assertions.assertNotNull(deserialized);

    Assertions.assertEquals(expr.print(), deserialized.print());
    Assertions.assertEquals(expr.printAsJson(), deserialized.printAsJson());
    TypeSymbol expectedTS = deserialized.getTypeInfo();
    TypeSymbol actualTS = deserialized.getTypeInfo();
    Assertions.assertEquals(expectedTS.getName(), actualTS.getName());
  }

  protected void performRoundTripSerializationSymTypeOfUnion(SymTypeOfUnion expr) {
    SymTypeOfUnionDeSer deser = new SymTypeOfUnionDeSer();

    String serialized = deser.serialize(expr);

    SymTypeExpression deserialized = deser.deserialize(serialized);
    Assertions.assertNotNull(deserialized);

    Assertions.assertTrue(expr.deepEquals(deserialized));
    Assertions.assertEquals(deser.serialize(expr), deser.serialize((SymTypeOfUnion) deserialized));
    TypeSymbol expectedTS = deserialized.getTypeInfo();
    TypeSymbol actualTS = deserialized.getTypeInfo();
    Assertions.assertEquals(expectedTS.getName(), actualTS.getName());
  }

  protected void performRoundTripSerializationSymTypeOfRegEx(SymTypeOfRegEx expr) {
    SymTypeOfRegExDeSer deser = new SymTypeOfRegExDeSer();

    String serialized = deser.serialize(expr);

    SymTypeExpression deserialized = deser.deserialize(serialized);
    Assertions.assertNotNull(deserialized);

    Assertions.assertTrue(expr.deepEquals(deserialized));
    Assertions.assertEquals(deser.serialize(expr), deser.serialize((SymTypeOfRegEx) deserialized));
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
    performRoundtrip2(teFun1);
    performRoundtrip2(teFun2);
    performRoundtrip2(teFun3);
    performRoundtrip2(teFun4);
    performRoundtrip2(teUnion1);
    performRoundtrip2(teRegEx1);
  }

  protected void performRoundtrip2(SymTypeExpression expr) {
    SymTypeExpressionDeSer deser = SymTypeExpressionDeSer.getInstance();
    //first serialize the expression using the deser
    String serialized = deser.serialize(expr);

    // then deserialize it
    SymTypeExpression loaded = deser.deserialize(serialized);
    Assertions.assertNotNull(loaded);
    // and assert that the serialized and deserialized symtype expression equals the one before
    Assertions.assertEquals(expr.print(), loaded.print());
    Assertions.assertEquals(expr.printAsJson(), loaded.printAsJson());
    if (!(loaded instanceof SymTypeOfWildcard)
        && !(loaded instanceof SymTypeOfRegEx)) {
      TypeSymbol expectedTS = loaded.getTypeInfo();
      TypeSymbol actualTS = expr.getTypeInfo();
      Assertions.assertEquals(expectedTS.getName(), actualTS.getName());
    }

    // usual member
    JsonPrinter printer = new JsonPrinter();
    printer.beginObject();
    SymTypeExpressionDeSer.serializeMember(printer, "foo", expr);
    printer.endObject();
    //produce a fake JSON object from the serialized member and parse this
    JsonObject json = JsonParser.parseJsonObject(printer.getContent());
    SymTypeExpression deserialized = SymTypeExpressionDeSer.deserializeMember("foo", json);
    Assertions.assertEquals(expr.print(), deserialized.print());

    // optional member that is present
    printer = new JsonPrinter();
    printer.beginObject();
    SymTypeExpressionDeSer.serializeMember(printer, "foo", Optional.ofNullable(expr));
    printer.endObject();
    json = JsonParser.parseJsonObject(printer.getContent());
    Optional<SymTypeExpression> deserializedOpt = SymTypeExpressionDeSer
        .deserializeOptionalMember("foo", json);
    Assertions.assertTrue(deserializedOpt.isPresent());
    Assertions.assertEquals(expr.print(), deserializedOpt.get().print());

    // optional member that is empty
    printer = new JsonPrinter(true);
    printer.beginObject();
    SymTypeExpressionDeSer.serializeMember(printer, "foo", Optional.empty());
    printer.endObject();
    json = JsonParser.parseJsonObject(printer.getContent());
    deserializedOpt = SymTypeExpressionDeSer.deserializeOptionalMember("foo", json);
    Assertions.assertTrue(!deserializedOpt.isPresent());

    // list member that is empty
    printer = new JsonPrinter(true);
    printer.beginObject();
    SymTypeExpressionDeSer.serializeMember(printer, "foo", new ArrayList<>());
    printer.endObject();
    json = JsonParser.parseJsonObject(printer.getContent());
    List<SymTypeExpression> deserializedList = SymTypeExpressionDeSer
        .deserializeListMember("foo", json);
    Assertions.assertEquals(0, deserializedList.size());

    // list member with single element
    printer = new JsonPrinter();
    printer.beginObject();
    SymTypeExpressionDeSer.serializeMember(printer, "foo", Lists.newArrayList(expr));
    printer.endObject();
    json = JsonParser.parseJsonObject(printer.getContent());
    deserializedList = SymTypeExpressionDeSer.deserializeListMember("foo", json);
    Assertions.assertEquals(1, deserializedList.size());
    Assertions.assertEquals(expr.print(), deserializedList.get(0).print());

    // list member with two elements
    printer = new JsonPrinter();
    printer.beginObject();
    SymTypeExpressionDeSer.serializeMember(printer, "foo", Lists.newArrayList(expr, expr));
    printer.endObject();
    json = JsonParser.parseJsonObject(printer.getContent());
    deserializedList = SymTypeExpressionDeSer.deserializeListMember("foo", json);
    Assertions.assertEquals(2, deserializedList.size());
    Assertions.assertEquals(expr.print(), deserializedList.get(0).print());
    Assertions.assertEquals(expr.print(), deserializedList.get(1).print());
  }

  @Test
  public void testInvalidJsonForSerializingReturnsError() {
    String invalidJsonForSerializing = "\"Foo\":\"bar\"";
    String invalidJsonForSerializing2 = "{\n\t\"symTypeExpression\": {\n\t\t\"foo\":\"bar\", \n\t\t\"foo2\":\"bar2\"\n\t}\n}";

    SymTypeExpressionDeSer.getInstance().deserialize(invalidJsonForSerializing);
    Assertions.assertTrue(Log.getFindings().get(Log.getFindings().size() - 1).getMsg().startsWith("0x823FE"));

    SymTypeExpressionDeSer.getInstance().deserialize(invalidJsonForSerializing2);
    Assertions.assertTrue(Log.getFindings().get(Log.getFindings().size() - 1).getMsg().startsWith("0x823FE"));

    SymTypeOfGenericsDeSer symTypeOfGenericsDeSer = new SymTypeOfGenericsDeSer();
    symTypeOfGenericsDeSer.deserialize(invalidJsonForSerializing2);
    Assertions.assertTrue(Log.getFindings().get(Log.getFindings().size() - 1).getMsg().startsWith("0x823F6"));

    SymTypeArrayDeSer symTypeArrayDeSer = new SymTypeArrayDeSer();
    symTypeArrayDeSer.deserialize(invalidJsonForSerializing2);
    Assertions.assertTrue(Log.getFindings().get(Log.getFindings().size() - 1).getMsg().startsWith("0x823F2"));

    SymTypeOfObjectDeSer symTypeOfObjectDeSer = new SymTypeOfObjectDeSer();
    symTypeOfObjectDeSer.deserialize(invalidJsonForSerializing2);
    Assertions.assertTrue(Log.getFindings().get(Log.getFindings().size() - 1).getMsg().startsWith("0x823F4"));

    SymTypeVariableDeSer symTypeVariableDeSer = new SymTypeVariableDeSer();
    symTypeVariableDeSer.deserialize(invalidJsonForSerializing2);
    Assertions.assertTrue(Log.getFindings().get(Log.getFindings().size() - 1).getMsg().startsWith("0x823F5"));

    SymTypePrimitiveDeSer symTypePrimitiveDeser = new SymTypePrimitiveDeSer();
    symTypePrimitiveDeser.deserialize(invalidJsonForSerializing2);
    Assertions.assertTrue(Log.getFindings().get(Log.getFindings().size() - 1).getMsg().startsWith("0x823F1"));

    SymTypeOfUnionDeSer symTypeOfUnionDeser = new SymTypeOfUnionDeSer();
    symTypeOfUnionDeser.deserialize(invalidJsonForSerializing2);
    Assertions.assertTrue(Log.getFindings().get(Log.getFindings().size() - 1).getMsg().startsWith("0x9E2F7"));

    SymTypeOfRegExDeSer symTypeOfRegExDeSer = new SymTypeOfRegExDeSer();
    symTypeOfRegExDeSer.deserialize(invalidJsonForSerializing2);
    Assertions.assertTrue(Log.getFindings().get(Log.getFindings().size() - 1).getMsg().startsWith("0x9E2F9"));
  }

}
