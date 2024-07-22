/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.monticore.types.check.DefsTypeBasic._intSymType;
import static de.monticore.types.check.SymTypeExpressionFactory.*;

public class SymTypeExpressionTest {

  private static IOOSymbolsScope scope = OOSymbolsMill.scope();

  // setup of objects (unchanged during tests)
  static SymTypeExpression teDouble;

  static SymTypeExpression teInt;

  static SymTypeExpression teVarA;

  static SymTypeExpression teIntA;

  static SymTypeExpression teVarB;

  static SymTypeExpression teVarUpper;

  static SymTypeExpression teVarLower;

  static SymTypeExpression teP;

  static SymTypeExpression teH;  // on purpose: package missing

  static SymTypeExpression teVoid;

  static SymTypeExpression teNull;

  static SymTypeExpression teArr1;

  static SymTypeExpression teArr3;

  static SymTypeExpression teSet;

  static SymTypeExpression teSetA;

  static SymTypeExpression teSetC;

  static SymTypeExpression teMap; // no package!

  static SymTypeExpression teFoo;

  static SymTypeExpression teMap2;

  static SymTypeExpression teMapA;

  static SymTypeExpression teSetB;

  static SymTypeExpression teDeep1;

  static SymTypeExpression teDeep2;

  static SymTypeExpression teUpperBound;

  static SymTypeExpression teLowerBound;

  static SymTypeExpression teWildcard;

  static SymTypeExpression teMap3;

  static SymTypeExpression teFunc1;

  static SymTypeExpression teFunc2;

  static SymTypeExpression teFunc3;

  static SymTypeExpression teFunc4;

  static SymTypeExpression teSIUnit1;

  static SymTypeExpression teSIUnit2;

  static SymTypeExpression teNumWithSIUnit1;

  static SymTypeExpression teNumWithSIUnit2;

  static SymTypeExpression teUnion1;

  static SymTypeExpression teInter1;

  static SymTypeExpression teTuple1;

  static SymTypeExpression teRegEx1;

  static SymTypeExpression teRegEx2;

  static SymTypeExpression teObscure;

  @BeforeEach
  public void init(){
    LogStub.init();
    Log.enableFailQuick(false);
    OOSymbolsMill.reset();
    OOSymbolsMill.init();
    BasicSymbolsMill.initializePrimitives();
    scope.add(new OOTypeSymbol("long"));
    scope.add(new OOTypeSymbol("Human"));
    scope = OOSymbolsMill.scope();

    // setup of objects (unchanged during tests)
    teDouble = createPrimitive("double");

    teInt = createPrimitive("int");

    teVarA = createTypeVariable("A", scope);

    teIntA = createTypeObject("java.lang.Integer",scope);

    teVarB = createTypeVariable("B", scope);

    teVarUpper = createTypeVariable(teIntA, createBottomType());

    teVarLower = createTypeVariable(createTopType(), teIntA);

    teP = createTypeObject("de.x.Person", scope);

    teH = createTypeObject("Human",
            scope);  // on purpose: package missing

    teVoid = createTypeVoid();

    teNull = createTypeOfNull();

    teArr1 = createTypeArray(teH.print(), scope, 1, teH);

    teArr3 = createTypeArray(teInt.print(), scope, 3, teInt);

    teSet = createGenerics("java.util.Set", scope, Lists.newArrayList(teP));

    teSetA = createGenerics("java.util.Set", scope, Lists.newArrayList(teVarA));

    teSetC = createGenerics("Set",scope,Lists.newArrayList(teInt));

    teMap = createGenerics("Map", scope, Lists.newArrayList(teInt, teP)); // no package!

    teFoo = createGenerics("x.Foo", scope,  Lists.newArrayList(teP, teDouble, teInt, teH));

    teMap2 = createGenerics("Map",scope,Lists.newArrayList(teSetC,teFoo));

    teMapA = createGenerics("java.util.Map",scope,Lists.newArrayList(teIntA,teP));

    teSetB = createGenerics("java.util.Set",scope,Lists.newArrayList(teMapA));

    teDeep1 = createGenerics("java.util.Set", scope, Lists.newArrayList(teMap));

    teDeep2 = createGenerics("java.util.Map2", scope, Lists.newArrayList(teInt, teDeep1));

    teUpperBound = createWildcard(true, teInt);

    teLowerBound = createWildcard(false, teH);

    teWildcard = createWildcard();

    teMap3 = createGenerics("java.util.Map", scope, Lists.newArrayList(teUpperBound, teWildcard));

    teFunc1 = createFunction(teVoid);

    teFunc2 = createFunction(teInt, Lists.newArrayList(teDouble, teInt));

    teFunc3 = createFunction(teFunc1, Lists.newArrayList(teFunc2));

    teFunc4 = createFunction(teVoid, Lists.newArrayList(teDouble, teInt), true);

    teSIUnit1 = createSIUnit(
        List.of(createSIUnitBasic("m")),
        List.of(createSIUnitBasic("s", "m", 2))
    );

    teSIUnit2 = createSIUnit(List.of(), List.of());

    teNumWithSIUnit1 = createNumericWithSIUnit((SymTypeOfSIUnit) teSIUnit1, teInt);

    teNumWithSIUnit2 = createNumericWithSIUnit((SymTypeOfSIUnit) teSIUnit2, teInt);

    teUnion1 = createUnion(teInt, teDouble);

    teInter1 = createIntersection(teInt, teDouble);

    teTuple1 = createTuple(teInt, teDouble);

    teRegEx1 = createTypeRegEx("gr(a|e)y");

    teObscure = createObscureType();

  }

  @Test
  public void subTypeTest() {
    Assertions.assertTrue(teInt.isPrimitive());
    Assertions.assertTrue(teInt.isValidType());
    Assertions.assertFalse(teInt.isGenericType());
    Assertions.assertFalse(teInt.isTypeVariable());
    Assertions.assertFalse(teInt.isArrayType());
    Assertions.assertFalse(teInt.isVoidType());
    Assertions.assertFalse(teInt.isNullType());
    Assertions.assertFalse(teInt.isObjectType());
    Assertions.assertFalse(teInt.isFunctionType());
    Assertions.assertFalse(teInt.isObscureType());
    Assertions.assertFalse(teInt.isWildcard());
    Assertions.assertFalse(teInt.isUnionType());

    Assertions.assertTrue(teVarA.isTypeVariable());
    Assertions.assertFalse(teVarA.isValidType());
    Assertions.assertTrue(teP.isObjectType());
    Assertions.assertTrue(teP.isValidType());
    Assertions.assertTrue(teVoid.isVoidType());
    Assertions.assertTrue(teVoid.isValidType());
    Assertions.assertTrue(teNull.isNullType());
    Assertions.assertTrue(teNull.isValidType());
    Assertions.assertTrue(teArr1.isArrayType());
    Assertions.assertTrue(teArr1.isValidType());
    Assertions.assertTrue(teSet.isGenericType());
    Assertions.assertTrue(teSet.isValidType());
    Assertions.assertTrue(teUpperBound.isWildcard());
    Assertions.assertFalse(teUpperBound.isValidType());
    Assertions.assertTrue(teFunc1.isFunctionType());
    Assertions.assertTrue(teFunc1.isValidType());
    Assertions.assertTrue(teSIUnit1.isSIUnitType());
    Assertions.assertTrue(teNumWithSIUnit1.isNumericWithSIUnitType());
    Assertions.assertTrue(teUnion1.isUnionType());
    Assertions.assertTrue(teUnion1.isValidType());
    Assertions.assertTrue(teInter1.isIntersectionType());
    Assertions.assertTrue(teInter1.isValidType());
    Assertions.assertTrue(teTuple1.isTupleType());
    Assertions.assertTrue(teRegEx1.isRegExType());
    Assertions.assertTrue(teObscure.isObscureType());
    Assertions.assertFalse(teObscure.isValidType());
  }
  
  @Test
  public void printTest() {
    Assertions.assertEquals("double", teDouble.print());
    Assertions.assertEquals("int", teInt.print());
    Assertions.assertEquals("A", teVarA.print());
    Assertions.assertEquals("de.x.Person", teP.print());
    Assertions.assertEquals("void", teVoid.print());
    Assertions.assertEquals("null", teNull.print());
    Assertions.assertEquals("Human[]", teArr1.print());
    Assertions.assertEquals("int[][][]", teArr3.print());
    Assertions.assertEquals("java.util.Set<de.x.Person>", teSet.printFullName());
    Assertions.assertEquals("java.util.Set<A>", teSetA.printFullName());
    Assertions.assertEquals("Map<int,de.x.Person>", teMap.printFullName());
    Assertions.assertEquals("x.Foo<de.x.Person,double,int,Human>", teFoo.printFullName());
    Assertions.assertEquals("java.util.Set<Map<int,de.x.Person>>", teDeep1.printFullName());
    Assertions.assertEquals("java.util.Map2<int,java.util.Set<Map<int,de.x.Person>>>", teDeep2.printFullName());
    Assertions.assertEquals("? extends int", teUpperBound.print());
    Assertions.assertEquals("? super Human", teLowerBound.print());
    Assertions.assertEquals("?", teWildcard.print());
    Assertions.assertEquals("java.util.Map<? extends int,?>", teMap3.printFullName());
    Assertions.assertEquals("() -> void", teFunc1.print());
    Assertions.assertEquals("(double, int) -> int", teFunc2.print());
    Assertions.assertEquals("((double, int) -> int) -> () -> void", teFunc3.print());
    Assertions.assertEquals("(double, int...) -> void", teFunc4.print());
    Assertions.assertEquals("[m/ms^2]", teSIUnit1.print());
    Assertions.assertEquals("[1]", teSIUnit2.print());
    Assertions.assertEquals("[m/ms^2]<int>", teNumWithSIUnit1.print());
    Assertions.assertEquals("double | int", teUnion1.print());
    Assertions.assertEquals("double & int", teInter1.print());
    Assertions.assertEquals("(int, double)", teTuple1.print());
    Assertions.assertEquals("R\"gr(a|e)y\"", teRegEx1.print());
  }

  @Test
  public void printAsJsonTest() {
    JsonElement result = JsonParser.parse(teDouble.printAsJson());
    Assertions.assertTrue(result.isJsonObject());
    JsonObject teDoubleJson = result.getAsJsonObject();
    Assertions.assertEquals("de.monticore.types.check.SymTypePrimitive", teDoubleJson.getStringMember("kind"));
    Assertions.assertEquals("double", teDoubleJson.getStringMember("primitiveName"));

    result = JsonParser.parse(teInt.printAsJson());
    Assertions.assertTrue(result.isJsonObject());
    JsonObject teIntJson = result.getAsJsonObject();
    Assertions.assertEquals("de.monticore.types.check.SymTypePrimitive", teIntJson.getStringMember("kind"));
    Assertions.assertEquals("int", teIntJson.getStringMember("primitiveName"));

    result = JsonParser.parse(teVarA.printAsJson());
    Assertions.assertTrue(result.isJsonObject());
    JsonObject teVarAJson = result.getAsJsonObject();
    Assertions.assertEquals("de.monticore.types.check.SymTypeVariable", teVarAJson.getStringMember("kind"));
    Assertions.assertEquals("A", teVarAJson.getStringMember("varName"));

    result = JsonParser.parse(teP.printAsJson());
    Assertions.assertTrue(result.isJsonObject());
    JsonObject tePJson = result.getAsJsonObject();
    Assertions.assertEquals("de.monticore.types.check.SymTypeOfObject", tePJson.getStringMember("kind"));
    Assertions.assertEquals("de.x.Person", tePJson.getStringMember("objName"));

    result = JsonParser.parse(teVoid.printAsJson());
    Assertions.assertTrue(result.isJsonString());
    Assertions.assertEquals("void", result.getAsJsonString().getValue());

    result = JsonParser.parse(teNull.printAsJson());
    Assertions.assertTrue(result.isJsonString());
    Assertions.assertEquals("null", result.getAsJsonString().getValue());

    result = JsonParser.parse(teArr1.printAsJson());
    Assertions.assertTrue(result.isJsonObject());
    JsonObject teArr1Json = result.getAsJsonObject();
    Assertions.assertEquals("de.monticore.types.check.SymTypeArray", teArr1Json.getStringMember("kind"));
    Assertions.assertEquals(1, teArr1Json.getIntegerMember("dim"), 0.01);
    JsonObject teArr1ArgJson = teArr1Json.getObjectMember("argument");
    Assertions.assertEquals("de.monticore.types.check.SymTypeOfObject", teArr1ArgJson.getStringMember("kind"));
    Assertions.assertEquals("Human", teArr1ArgJson.getStringMember("objName"));

    result = JsonParser.parse(teArr3.printAsJson());
    Assertions.assertTrue(result.isJsonObject());
    JsonObject teArr3Json = result.getAsJsonObject();
    Assertions.assertEquals("de.monticore.types.check.SymTypeArray", teArr3Json.getStringMember("kind"));
    Assertions.assertEquals(3, teArr3Json.getIntegerMember("dim"), 0.01);
    JsonObject teArr3ArgJson = teArr3Json.getObjectMember("argument");
    Assertions.assertEquals("de.monticore.types.check.SymTypePrimitive", teArr3ArgJson.getStringMember("kind"));
    Assertions.assertEquals("int", teArr3ArgJson.getStringMember("primitiveName"));

    result = JsonParser.parse(teSet.printAsJson());
    Assertions.assertTrue(result.isJsonObject());
    JsonObject teSetJson = result.getAsJsonObject();
    Assertions.assertEquals("de.monticore.types.check.SymTypeOfGenerics", teSetJson.getStringMember("kind"));
    Assertions.assertEquals("java.util.Set", teSetJson.getStringMember("typeConstructorFullName"));
    List<JsonElement> teSetArgsJson = teSetJson.getArrayMember("arguments");
    Assertions.assertEquals(1, teSetArgsJson.size(), 0.01);
    Assertions.assertEquals("de.monticore.types.check.SymTypeOfObject", teSetArgsJson.get(0).getAsJsonObject().getStringMember("kind"));
    Assertions.assertEquals("de.x.Person", teSetArgsJson.get(0).getAsJsonObject().getStringMember("objName"));

    result = JsonParser.parse(teSetA.printAsJson());
    Assertions.assertTrue(result.isJsonObject());
    JsonObject teSetAJson = result.getAsJsonObject();
    Assertions.assertEquals("de.monticore.types.check.SymTypeOfGenerics", teSetAJson.getStringMember("kind"));
    Assertions.assertEquals("java.util.Set", teSetAJson.getStringMember("typeConstructorFullName"));
    List<JsonElement> teSetAArgsJson = teSetAJson.getArrayMember("arguments");
    Assertions.assertEquals(1, teSetAArgsJson.size(), 0.01);
    Assertions.assertEquals("de.monticore.types.check.SymTypeVariable", teSetAArgsJson.get(0).getAsJsonObject().getStringMember("kind"));
    Assertions.assertEquals("A", teSetAArgsJson.get(0).getAsJsonObject().getStringMember("varName"));

    result = JsonParser.parse(teMap.printAsJson());
    Assertions.assertTrue(result.isJsonObject());
    JsonObject teMapJson = result.getAsJsonObject();
    Assertions.assertEquals("de.monticore.types.check.SymTypeOfGenerics", teMapJson.getStringMember("kind"));
    Assertions.assertEquals("Map", teMapJson.getStringMember("typeConstructorFullName"));
    List<JsonElement> teMapArgsJson = teMapJson.getArrayMember("arguments");
    Assertions.assertEquals(2, teMapArgsJson.size(), 0.01);
    Assertions.assertEquals("de.monticore.types.check.SymTypePrimitive", teMapArgsJson.get(0).getAsJsonObject().getStringMember("kind"));
    Assertions.assertEquals("int", teMapArgsJson.get(0).getAsJsonObject().getStringMember("primitiveName"));
    Assertions.assertEquals("de.monticore.types.check.SymTypeOfObject", teMapArgsJson.get(1).getAsJsonObject().getStringMember("kind"));
    Assertions.assertEquals("de.x.Person", teMapArgsJson.get(1).getAsJsonObject().getStringMember("objName"));

    result = JsonParser.parse(teFoo.printAsJson());
    Assertions.assertTrue(result.isJsonObject());
    JsonObject teFooJson = result.getAsJsonObject();
    Assertions.assertEquals("de.monticore.types.check.SymTypeOfGenerics", teFooJson.getStringMember("kind"));
    Assertions.assertEquals("x.Foo", teFooJson.getStringMember("typeConstructorFullName"));
    List<JsonElement> teFooArgsJson = teFooJson.getArrayMember("arguments");
    Assertions.assertEquals(4, teFooArgsJson.size(), 0.01);
    Assertions.assertEquals("de.monticore.types.check.SymTypeOfObject", teFooArgsJson.get(0).getAsJsonObject().getStringMember("kind"));
    Assertions.assertEquals("de.x.Person", teFooArgsJson.get(0).getAsJsonObject().getStringMember("objName"));
    Assertions.assertEquals("de.monticore.types.check.SymTypePrimitive", teFooArgsJson.get(1).getAsJsonObject().getStringMember("kind"));
    Assertions.assertEquals("double", teFooArgsJson.get(1).getAsJsonObject().getStringMember("primitiveName"));
    Assertions.assertEquals("de.monticore.types.check.SymTypePrimitive", teFooArgsJson.get(2).getAsJsonObject().getStringMember("kind"));
    Assertions.assertEquals("int", teFooArgsJson.get(2).getAsJsonObject().getStringMember("primitiveName"));
    Assertions.assertEquals("de.monticore.types.check.SymTypeOfObject", teFooArgsJson.get(3).getAsJsonObject().getStringMember("kind"));
    Assertions.assertEquals("Human", teFooArgsJson.get(3).getAsJsonObject().getStringMember("objName"));

    result = JsonParser.parse(teDeep1.printAsJson());
    Assertions.assertTrue(result.isJsonObject());
    JsonObject teDeep1Json = result.getAsJsonObject();
    Assertions.assertEquals("de.monticore.types.check.SymTypeOfGenerics", teDeep1Json.getStringMember("kind"));
    Assertions.assertEquals("java.util.Set", teDeep1Json.getStringMember("typeConstructorFullName"));
    List<JsonElement> teDeep1ArgsJson = teDeep1Json.getArrayMember("arguments");
    Assertions.assertEquals(1, teDeep1ArgsJson.size(), 0.01);
    Assertions.assertEquals("de.monticore.types.check.SymTypeOfGenerics", teDeep1ArgsJson.get(0).getAsJsonObject().getStringMember( "kind"));
    Assertions.assertEquals("Map", teDeep1ArgsJson.get(0).getAsJsonObject().getStringMember( "typeConstructorFullName"));
    List<JsonElement> teDeep1teMapArgsJson = teDeep1ArgsJson.get(0).getAsJsonObject()
        .getArrayMember("arguments");
    Assertions.assertEquals(2, teDeep1teMapArgsJson.size(), 0.01);
    Assertions.assertEquals("de.monticore.types.check.SymTypePrimitive", teDeep1teMapArgsJson.get(0).getAsJsonObject().getStringMember( "kind"));
    Assertions.assertEquals("int", teDeep1teMapArgsJson.get(0).getAsJsonObject().getStringMember("primitiveName"));
    Assertions.assertEquals("de.monticore.types.check.SymTypeOfObject", teDeep1teMapArgsJson.get(1).getAsJsonObject().getStringMember( "kind"));
    Assertions.assertEquals("de.x.Person", teDeep1teMapArgsJson.get(1).getAsJsonObject().getStringMember( "objName"));

    result = JsonParser.parse(teDeep2.printAsJson());
    Assertions.assertTrue(result.isJsonObject());
    JsonObject teDeep2Json = result.getAsJsonObject();
    Assertions.assertEquals("de.monticore.types.check.SymTypeOfGenerics", teDeep2Json.getStringMember("kind"));
    Assertions.assertEquals("java.util.Map2", teDeep2Json.getStringMember("typeConstructorFullName"));
    List<JsonElement> teDeep2ArgsJson = teDeep2Json.getArrayMember("arguments");
    Assertions.assertEquals(2, teDeep2ArgsJson.size(), 0.01);
    Assertions.assertEquals("de.monticore.types.check.SymTypePrimitive", teDeep2ArgsJson.get(0).getAsJsonObject().getStringMember( "kind"));
    Assertions.assertEquals("int", teDeep2ArgsJson.get(0).getAsJsonObject().getStringMember( "primitiveName"));
    Assertions.assertEquals("de.monticore.types.check.SymTypeOfGenerics", teDeep2ArgsJson.get(1).getAsJsonObject().getStringMember( "kind"));
    Assertions.assertEquals("java.util.Set", teDeep2ArgsJson.get(1).getAsJsonObject().getStringMember( "typeConstructorFullName"));
    List<JsonElement> teDeep2SetArgsJson = teDeep2ArgsJson.get(1).getAsJsonObject()
        .getArrayMember("arguments");
    Assertions.assertEquals(1, teDeep2SetArgsJson.size(), 0.01);
    Assertions.assertEquals("de.monticore.types.check.SymTypeOfGenerics", teDeep2SetArgsJson.get(0).getAsJsonObject().getStringMember( "kind"));
    Assertions.assertEquals("Map", teDeep2SetArgsJson.get(0).getAsJsonObject().getStringMember( "typeConstructorFullName"));
    List<JsonElement> teDeep2SetMapArgsJson = teDeep2SetArgsJson.get(0).getAsJsonObject()
        .getArrayMember("arguments");
    Assertions.assertEquals(2, teDeep2SetMapArgsJson.size(), 0.01);
    Assertions.assertEquals("de.monticore.types.check.SymTypePrimitive", teDeep2SetMapArgsJson.get(0).getAsJsonObject().getStringMember( "kind"));
    Assertions.assertEquals("int", teDeep2SetMapArgsJson.get(0).getAsJsonObject().getStringMember( "primitiveName"));
    Assertions.assertEquals("de.monticore.types.check.SymTypeOfObject", teDeep2SetMapArgsJson.get(1).getAsJsonObject().getStringMember( "kind"));
    Assertions.assertEquals("de.x.Person", teDeep2SetMapArgsJson.get(1).getAsJsonObject().getStringMember( "objName"));

    result = JsonParser.parse(teUpperBound.printAsJson());
    Assertions.assertTrue(result.isJsonObject());
    JsonObject teUpperBound2Json = result.getAsJsonObject();
    Assertions.assertEquals("de.monticore.types.check.SymTypeOfWildcard", teUpperBound2Json.getStringMember("kind"));
    Assertions.assertTrue(teUpperBound2Json.getBooleanMember("isUpper"));
    JsonObject bound = teUpperBound2Json.getObjectMember("bound");
    Assertions.assertEquals("de.monticore.types.check.SymTypePrimitive", bound.getStringMember("kind"));
    Assertions.assertEquals("int", bound.getStringMember("primitiveName"));

    result = JsonParser.parse(teFunc2.printAsJson());
    Assertions.assertTrue(result.isJsonObject());
    JsonObject teFunc2Json = result.getAsJsonObject();
    Assertions.assertEquals("de.monticore.types.check.SymTypeOfFunction", teFunc2Json.getStringMember("kind"));
    JsonObject func2returnType = teFunc2Json.getObjectMember("returnType");
    Assertions.assertEquals("de.monticore.types.check.SymTypePrimitive", func2returnType.getStringMember( "kind"));
    Assertions.assertEquals("int", func2returnType.getStringMember( "primitiveName"));
    List<JsonElement> func2Arguments = teFunc2Json.getArrayMember("argumentTypes");
    Assertions.assertEquals(2, func2Arguments.size());
    Assertions.assertEquals("de.monticore.types.check.SymTypePrimitive", func2Arguments.get(0).getAsJsonObject().getStringMember( "kind"));
    Assertions.assertEquals("double", func2Arguments.get(0).getAsJsonObject().getStringMember( "primitiveName"));
    Assertions.assertEquals("de.monticore.types.check.SymTypePrimitive", func2Arguments.get(1).getAsJsonObject().getStringMember( "kind"));
    Assertions.assertEquals("int", func2Arguments.get(1).getAsJsonObject().getStringMember( "primitiveName"));
    Assertions.assertFalse(teFunc2Json.getBooleanMember("elliptic"));

    result = JsonParser.parse(teFunc4.printAsJson());
    Assertions.assertTrue(result.isJsonObject());
    JsonObject teFunc4Json = result.getAsJsonObject();
    Assertions.assertTrue(teFunc4Json.getBooleanMember("elliptic"));

    result = JsonParser.parse(teUnion1.printAsJson());
    Assertions.assertTrue(result.isJsonObject());
    JsonObject teUnion1Json = result.getAsJsonObject();
    Assertions.assertEquals("de.monticore.types.check.SymTypeOfUnion", teUnion1Json.getStringMember("kind"));
    Assertions.assertEquals(2, teUnion1Json.getArrayMember("unionizedTypes").size());
    List<JsonElement> union1Types = teUnion1Json.getArrayMember("unionizedTypes");
    Assertions.assertEquals("de.monticore.types.check.SymTypePrimitive", union1Types.get(0).getAsJsonObject().getStringMember( "kind"));
    Assertions.assertEquals("double", union1Types.get(0).getAsJsonObject().getStringMember( "primitiveName"));
    Assertions.assertEquals("de.monticore.types.check.SymTypePrimitive", union1Types.get(1).getAsJsonObject().getStringMember( "kind"));
    Assertions.assertEquals("int", union1Types.get(1).getAsJsonObject().getStringMember( "primitiveName"));

    result = JsonParser.parse(teInter1.printAsJson());
    Assertions.assertTrue(result.isJsonObject());
    JsonObject teInter1Json = result.getAsJsonObject();
    Assertions.assertEquals("de.monticore.types.check.SymTypeOfIntersection", teInter1Json.getStringMember("kind"));
    Assertions.assertEquals(2, teInter1Json.getArrayMember("intersectedTypes").size());
    List<JsonElement> intersected1Types = teInter1Json.getArrayMember("intersectedTypes");
    Assertions.assertEquals("de.monticore.types.check.SymTypePrimitive", intersected1Types.get(0).getAsJsonObject().getStringMember( "kind"));
    Assertions.assertEquals("double", intersected1Types.get(0).getAsJsonObject().getStringMember( "primitiveName"));
    Assertions.assertEquals("de.monticore.types.check.SymTypePrimitive", intersected1Types.get(1).getAsJsonObject().getStringMember( "kind"));
    Assertions.assertEquals("int", intersected1Types.get(1).getAsJsonObject().getStringMember( "primitiveName"));

    result = JsonParser.parse(teTuple1.printAsJson());
    Assertions.assertTrue(result.isJsonObject());
    JsonObject teTuple1Json = result.getAsJsonObject();
    Assertions.assertEquals("de.monticore.types.check.SymTypeOfTuple", teTuple1Json.getStringMember("kind"));
    Assertions.assertEquals(2, teTuple1Json.getArrayMember("listedTypes").size());
    List<JsonElement> tuple1types = teTuple1Json.getArrayMember("listedTypes");
    Assertions.assertEquals("de.monticore.types.check.SymTypePrimitive", tuple1types.get(0).getAsJsonObject().getStringMember( "kind"));
    Assertions.assertEquals("int", tuple1types.get(0).getAsJsonObject().getStringMember( "primitiveName"));
    Assertions.assertEquals("de.monticore.types.check.SymTypePrimitive", tuple1types.get(1).getAsJsonObject().getStringMember( "kind"));
    Assertions.assertEquals("double", tuple1types.get(1).getAsJsonObject().getStringMember( "primitiveName"));

    result = JsonParser.parse(teRegEx1.printAsJson());
    Assertions.assertTrue(result.isJsonObject());
    JsonObject teRegEx1Json = result.getAsJsonObject();
    Assertions.assertEquals("de.monticore.types.check.SymTypeOfRegEx", teRegEx1Json.getStringMember("kind"));
    Assertions.assertEquals("gr(a|e)y", teRegEx1Json.getStringMember("regex"));
  }

  @Test
  public void baseNameTest() {
    Assertions.assertEquals("Person", ((SymTypeOfObject) (teP)).getBaseName());
    Assertions.assertEquals("Human", ((SymTypeOfObject) (teH)).getBaseName());
    Assertions.assertEquals("Map", ((SymTypeOfGenerics) (teMap)).getBaseName());
    Assertions.assertEquals("Set", ((SymTypeOfGenerics) (teSet)).getBaseName());
  }

  @Test
  public void unboxTest(){
    Assertions.assertEquals("Set<Map<int,de.x.Person>>", SymTypeOfGenerics.unbox((SymTypeOfGenerics)teSetB));
    Assertions.assertEquals("Set<de.x.Person>", SymTypeOfGenerics.unbox((SymTypeOfGenerics)teSet));
    Assertions.assertEquals("Set<A>", SymTypeOfGenerics.unbox((SymTypeOfGenerics)teSetA));
    Assertions.assertEquals("Map<int,de.x.Person>", SymTypeOfGenerics.unbox((SymTypeOfGenerics)teMap));
    Assertions.assertEquals("Map<Set<int>,x.Foo<de.x.Person,double,int,Human>>", SymTypeOfGenerics.unbox((SymTypeOfGenerics)teMap2));
  }

  @Test
  public void boxTest() {
    Assertions.assertEquals("java.util.Set<java.util.Map<java.lang.Integer,de.x.Person>>", SymTypeOfGenerics.box((SymTypeOfGenerics) teSetB));
    Assertions.assertEquals("java.util.Set<de.x.Person>", SymTypeOfGenerics.box((SymTypeOfGenerics) teSet));
    Assertions.assertEquals("java.util.Set<A>", SymTypeOfGenerics.box((SymTypeOfGenerics) teSetA));
    Assertions.assertEquals("java.util.Map<java.lang.Integer,de.x.Person>", SymTypeOfGenerics.box((SymTypeOfGenerics)teMap));
    Assertions.assertEquals("java.util.Map<java.util.Set<java.lang.Integer>,x.Foo<de.x.Person,java.lang.Double,java.lang.Integer,Human>>", SymTypeOfGenerics.box((SymTypeOfGenerics)teMap2));
  }

  @Test
  public void testHasTypeInfo() {
    Assertions.assertTrue(teInt.hasTypeInfo());
    Assertions.assertFalse(createPrimitive((TypeSymbol) null).hasTypeInfo());
    Assertions.assertTrue(teVarA.hasTypeInfo());
    Assertions.assertTrue(teVarB.hasTypeInfo());
    Assertions.assertFalse(teVarUpper.hasTypeInfo());
    Assertions.assertFalse(teVarLower.hasTypeInfo());
    Assertions.assertTrue(teIntA.hasTypeInfo());
    Assertions.assertFalse(createTypeObject(null).hasTypeInfo());
    Assertions.assertTrue(teP.hasTypeInfo());
    Assertions.assertTrue(teH.hasTypeInfo());
    Assertions.assertFalse(teVoid.hasTypeInfo());
    Assertions.assertFalse(teNull.hasTypeInfo());
    Assertions.assertFalse(teArr1.hasTypeInfo());
    Assertions.assertFalse(teArr3.hasTypeInfo());
    Assertions.assertTrue(teSetA.hasTypeInfo());
    Assertions.assertTrue(teSetB.hasTypeInfo());
    Assertions.assertTrue(teSetC.hasTypeInfo());
    Assertions.assertTrue(teMap.hasTypeInfo());
    Assertions.assertTrue(teMapA.hasTypeInfo());
    Assertions.assertTrue(teMap3.hasTypeInfo());
    Assertions.assertTrue(teFoo.hasTypeInfo());
    Assertions.assertTrue(teDeep1.hasTypeInfo());
    Assertions.assertTrue(teDeep2.hasTypeInfo());
    Assertions.assertFalse(teUpperBound.hasTypeInfo());
    Assertions.assertFalse(teLowerBound.hasTypeInfo());
    Assertions.assertFalse(teWildcard.hasTypeInfo());
    Assertions.assertFalse(teFunc1.hasTypeInfo());
    Assertions.assertFalse(teFunc2.hasTypeInfo());
    Assertions.assertFalse(teFunc3.hasTypeInfo());
    Assertions.assertFalse(teFunc4.hasTypeInfo());
    Assertions.assertFalse(teSIUnit1.hasTypeInfo());
    Assertions.assertFalse(teNumWithSIUnit1.hasTypeInfo());
    Assertions.assertFalse(teUnion1.hasTypeInfo());
    Assertions.assertFalse(teTuple1.hasTypeInfo());
    Assertions.assertFalse(teRegEx1.hasTypeInfo());
    Assertions.assertFalse(teObscure.hasTypeInfo());
  }

  @Test
  public void deepCloneTest(){
    //SymTypeVoid
    Assertions.assertTrue(teVoid.deepClone() instanceof SymTypeVoid);
    Assertions.assertEquals(teVoid.getTypeInfo().getName(), teVoid.deepClone().getTypeInfo().getName());
    Assertions.assertEquals(teVoid.print(), teVoid.deepClone().print());

    //SymTypeOfNull
    Assertions.assertTrue(teNull.deepClone() instanceof SymTypeOfNull);
    Assertions.assertEquals(teNull.getTypeInfo().getName(), teNull.deepClone().getTypeInfo().getName());
    Assertions.assertEquals(teNull.print(), teNull.deepClone().print());

    //SymTypeVariable
    Assertions.assertTrue(teVarA.deepClone() instanceof SymTypeVariable);
    Assertions.assertFalse(teVarA.deepClone().isPrimitive());
    Assertions.assertTrue(teVarA.deepClone().isTypeVariable());
    Assertions.assertEquals(teVarA.print(), teVarA.deepClone().print());

    Assertions.assertTrue(teVarUpper.deepClone() instanceof SymTypeVariable);
    Assertions.assertFalse(teVarUpper.deepClone().isPrimitive());
    Assertions.assertTrue(teVarUpper.deepClone().isTypeVariable());
    Assertions.assertEquals(teVarUpper.print(), teVarUpper.deepClone().print());

    Assertions.assertTrue(teVarLower.deepClone() instanceof SymTypeVariable);
    Assertions.assertFalse(teVarLower.deepClone().isPrimitive());
    Assertions.assertTrue(teVarLower.deepClone().isTypeVariable());
    Assertions.assertEquals(teVarLower.print(), teVarLower.deepClone().print());

    //SymTypePrimitive
    Assertions.assertTrue(teInt.deepClone() instanceof SymTypePrimitive);
    Assertions.assertEquals(teInt.getTypeInfo().getName(), teInt.deepClone().getTypeInfo().getName());
    Assertions.assertTrue(teInt.deepClone().isPrimitive());
    Assertions.assertEquals(teInt.print(), teInt.deepClone().print());

    //SymTypeOfObject
    Assertions.assertTrue(teH.deepClone() instanceof SymTypeOfObject);
    Assertions.assertEquals(teH.print(), teH.deepClone().print());

    //SymTypeArray
    Assertions.assertTrue(teArr1.deepClone() instanceof SymTypeArray);
    Assertions.assertEquals(teArr1.print(), teArr1.deepClone().print());
    Assertions.assertEquals(((SymTypeArray)teArr1).getDim(), ((SymTypeArray)teArr1.deepClone()).getDim());
    Assertions.assertEquals(((SymTypeArray)teArr1).getArgument().print(), ((SymTypeArray)teArr1.deepClone()).getArgument().print());

    //SymTypeOfGenerics
    Assertions.assertTrue(teDeep1.deepClone() instanceof SymTypeOfGenerics);
    Assertions.assertTrue(teDeep1.deepClone().isGenericType());
    Assertions.assertEquals(teDeep1.print(), teDeep1.deepClone().print());

    //SymTypeOfWildcard
    Assertions.assertTrue(teUpperBound.deepClone() instanceof SymTypeOfWildcard);
    Assertions.assertEquals(((SymTypeOfWildcard) teUpperBound).getBound().print(), ((SymTypeOfWildcard) teUpperBound.deepClone()).getBound().print());
    Assertions.assertEquals(teUpperBound.print(), teUpperBound.deepClone().print());

    //SymTypeOfFunction
    Assertions.assertTrue(teFunc3.deepClone() instanceof SymTypeOfFunction);
    Assertions.assertTrue(teFunc3.deepClone().isFunctionType());
    Assertions.assertEquals(teFunc3.print(), teFunc3.deepClone().print());

    // SymTypeOfSIUnit
    Assertions.assertTrue(teSIUnit1.deepClone() instanceof SymTypeOfSIUnit);
    Assertions.assertTrue(teSIUnit1.deepClone().isSIUnitType());
    Assertions.assertEquals(teSIUnit1.print(), teSIUnit1.deepClone().print());

    // SymTypeOfNumericWithSIUnit
    Assertions.assertTrue(teNumWithSIUnit1.deepClone() instanceof SymTypeOfNumericWithSIUnit);
    Assertions.assertTrue(teNumWithSIUnit1.deepClone().isNumericWithSIUnitType());
    Assertions.assertEquals(teNumWithSIUnit1.print(), teNumWithSIUnit1.deepClone().print());

    //SymTypeOfUnion
    Assertions.assertTrue(teUnion1.deepClone() instanceof SymTypeOfUnion);
    Assertions.assertTrue(teUnion1.deepClone().isUnionType());
    Assertions.assertEquals(teUnion1.print(), teUnion1.deepClone().print());

    //SymTypeOfIntersection
    Assertions.assertTrue(teInter1.deepClone() instanceof SymTypeOfIntersection);
    Assertions.assertTrue(teInter1.deepClone().isIntersectionType());
    Assertions.assertEquals(teInter1.print(), teInter1.deepClone().print());

    //SymTypeOfTuple
    Assertions.assertTrue(teTuple1.deepClone() instanceof SymTypeOfTuple);
    Assertions.assertTrue(teTuple1.deepClone().isTupleType());
    Assertions.assertEquals(teTuple1.print(), teTuple1.deepClone().print());

    Assertions.assertTrue(teRegEx1.deepClone() instanceof SymTypeOfRegEx);
    Assertions.assertTrue(teRegEx1.deepClone().isRegExType());
    Assertions.assertEquals(teRegEx1.print(), teRegEx1.deepClone().print());
  }

  @Test
  public void testSymTypeExpressionFactory(){
    SymTypeVoid tVoid = SymTypeExpressionFactory.createTypeVoid();
    Assertions.assertEquals("void", tVoid.print());

    SymTypeOfNull tNull = SymTypeExpressionFactory.createTypeOfNull();
    Assertions.assertEquals("null", tNull.print());

    SymTypePrimitive tInt = SymTypeExpressionFactory.createPrimitive("int");
    Assertions.assertEquals("int", tInt.print());
    Assertions.assertTrue(tInt.isIntegralType());

    SymTypeOfGenerics tA = SymTypeExpressionFactory.createGenerics("A",scope);
    Assertions.assertEquals("A<>", tA.print());
    Assertions.assertTrue(tA.isEmptyArguments());

    SymTypeOfGenerics tB = SymTypeExpressionFactory.createGenerics("B",scope,Lists.newArrayList(teArr1,teIntA));
    Assertions.assertEquals("B<Human[],java.lang.Integer>", tB.printFullName());
    Assertions.assertEquals(2, tB.sizeArguments());

    SymTypeOfGenerics tC = SymTypeExpressionFactory.createGenerics("C",scope,teDeep1,teDeep2);
    Assertions.assertEquals("C<java.util.Set<Map<int,de.x.Person>>,java.util.Map2<int,java.util.Set<Map<int,de.x.Person>>>>", tC.printFullName());
    Assertions.assertEquals(2, tC.sizeArguments());

    SymTypeOfGenerics tD = SymTypeExpressionFactory.createGenerics("D",scope);
    Assertions.assertEquals("D<>", tD.printFullName());
    Assertions.assertTrue(tD.isEmptyArguments());

    SymTypeOfGenerics tE = SymTypeExpressionFactory.createGenerics("E",scope,Lists.newArrayList(teDouble,teMap));
    Assertions.assertEquals("E<double,Map<int,de.x.Person>>", tE.printFullName());
    Assertions.assertEquals(2, tE.sizeArguments());

    SymTypeOfGenerics tF = SymTypeExpressionFactory.createGenerics("F",scope,teH,teP);
    Assertions.assertEquals("F<Human,de.x.Person>", tF.printFullName());
    Assertions.assertEquals(2, tF.sizeArguments());

    SymTypeArray tHuman = SymTypeExpressionFactory.createTypeArray("Human",scope,1,teH);
    Assertions.assertEquals("Human[]", tHuman.print());
    Assertions.assertEquals(1, tHuman.getDim());
    Assertions.assertEquals("Human", tHuman.getArgument().print());

    SymTypeArray tPerson = SymTypeExpressionFactory.createTypeArray("de.x.Person",scope,2,teP);
    Assertions.assertEquals("de.x.Person[][]", tPerson.print());
    Assertions.assertEquals(2, tPerson.getDim());
    Assertions.assertEquals("de.x.Person", tPerson.getArgument().print());

    SymTypeOfObject tG = SymTypeExpressionFactory.createTypeObject("G",scope);
    Assertions.assertEquals("G", tG.print());

    SymTypeOfObject tH = SymTypeExpressionFactory.createTypeObject("H",scope);
    Assertions.assertEquals("H", tH.print());

    SymTypeVariable tT = SymTypeExpressionFactory.createTypeVariable("T",scope);
    Assertions.assertEquals("T", tT.print());

    SymTypeVariable tS = SymTypeExpressionFactory.createTypeVariable("S",scope);
    Assertions.assertEquals("S", tS.print());

    SymTypeExpression tExpr = SymTypeExpressionFactory.createTypeExpression("void",scope);
    Assertions.assertTrue(tExpr instanceof SymTypeVoid);
    Assertions.assertEquals("void", tExpr.print());

    SymTypeOfFunction tFunc1 = SymTypeExpressionFactory.createFunction(tVoid);
    Assertions.assertEquals("() -> void", tFunc1.print());

    SymTypeOfFunction tFunc2 = SymTypeExpressionFactory.createFunction(tVoid, Lists.newArrayList(tFunc1, tFunc1));
    Assertions.assertEquals("((() -> void), (() -> void)) -> void", tFunc2.print());

    SymTypeOfFunction tFunc3 = SymTypeExpressionFactory.createFunction(tVoid, tFunc1, tFunc1);
    Assertions.assertEquals("((() -> void), (() -> void)) -> void", tFunc3.print());

    SymTypeOfFunction tFunc4 = SymTypeExpressionFactory.createFunction(tVoid, Lists.newArrayList(teDouble, teInt), true);
    Assertions.assertEquals("(double, int...) -> void", tFunc4.print());

    SIUnitBasic tSIUnitBasic1 = createSIUnitBasic("m");
    Assertions.assertEquals("m", tSIUnitBasic1.print());

    SIUnitBasic tSIUnitBasic2 = createSIUnitBasic("s", 2);
    Assertions.assertEquals("s^2", tSIUnitBasic2.print());

    SIUnitBasic tSIUnitBasic3 = createSIUnitBasic("g", "m", -2);
    Assertions.assertEquals("mg^-2", tSIUnitBasic3.print());

    SymTypeOfSIUnit tSIUnit1 = createSIUnit(List.of(tSIUnitBasic1), List.of());
    Assertions.assertEquals("[m]", tSIUnit1.print());

    SymTypeOfSIUnit tSIUnit2 = createSIUnit(List.of(), List.of(tSIUnitBasic2));
    Assertions.assertEquals("[1/s^2]", tSIUnit2.print());

    SymTypeOfSIUnit tSIUnit3 = createSIUnit(
        List.of(tSIUnitBasic1),
        List.of(tSIUnitBasic2, tSIUnitBasic3)
    );
    Assertions.assertEquals("[m/s^2mg^-2]", tSIUnit3.print());

    SymTypeOfNumericWithSIUnit tNumSIUnit1 =
        createNumericWithSIUnit(tSIUnit1, teInt);
    Assertions.assertEquals("[m]<int>", tNumSIUnit1.print());

    SymTypeOfNumericWithSIUnit tNumSIUnit2 = createNumericWithSIUnit(
        List.of(tSIUnitBasic1), List.of(tSIUnitBasic2, tSIUnitBasic3), teInt
    );
    Assertions.assertEquals("[m/s^2mg^-2]<int>", tNumSIUnit2.print());

    SymTypeOfUnion tUnion1 = createUnion(teInt, teDouble);
    Assertions.assertEquals("double | int", tUnion1.print());

    SymTypeOfUnion tUnion2 = createUnion(Set.of(teInt, teDouble, teArr1));
    Assertions.assertEquals("Human[] | double | int", tUnion2.print());

    SymTypeOfIntersection tInter1 = createIntersection(teInt, teDouble);
    Assertions.assertEquals("double & int", tInter1.print());

    SymTypeOfIntersection tInter2 = createIntersection(Set.of(teInt, teDouble, teArr1));
    Assertions.assertEquals("Human[] & double & int", tInter2.print());

    SymTypeOfTuple tTuple1 = createTuple(teInt, teDouble);
    Assertions.assertEquals("(int, double)", tTuple1.print());

    SymTypeOfTuple tTuple2 = createTuple(List.of(teInt, teDouble));
    Assertions.assertEquals("(int, double)", tTuple2.print());

    SymTypeOfRegEx tRegEx1 = createTypeRegEx("rege(x(es)?|xps?)");
    Assertions.assertEquals("R\"rege(x(es)?|xps?)\"", tRegEx1.print());
  }

  @Test
  public void testGenericArguments(){
    SymTypeExpression teFoo = createGenerics("x.Foo", scope,  Lists.newArrayList(teP, teDouble, teInt, teH));
    Assertions.assertTrue(teFoo.isGenericType());
    SymTypeOfGenerics teFoo2 = (SymTypeOfGenerics) teFoo;
    //getArgumentList & getArgument
    Assertions.assertEquals(4, teFoo2.getArgumentList().size());
    Assertions.assertEquals(teP, teFoo2.getArgument(0));
    Assertions.assertEquals(teDouble, teFoo2.getArgument(1));
    Assertions.assertEquals(teInt, teFoo2.getArgument(2));
    Assertions.assertEquals(teH, teFoo2.getArgument(3));
    List<SymTypeExpression> arguments = teFoo2.getArgumentList();

    //toArrayArguments
    Object[] args = teFoo2.toArrayArguments();
    Assertions.assertEquals(teP, args[0]);
    Assertions.assertEquals(teDouble, args[1]);
    Assertions.assertEquals(teInt, args[2]);
    Assertions.assertEquals(teH, args[3]);

    //toArrayArguments2
    SymTypeExpression[] symArgs = teFoo2.toArrayArguments(new SymTypeExpression[4]);
    Assertions.assertEquals(teP, symArgs[0]);
    Assertions.assertEquals(teDouble, symArgs[1]);
    Assertions.assertEquals(teInt, symArgs[2]);
    Assertions.assertEquals(teH, symArgs[3]);

    //subListArguments
    List<SymTypeExpression> subList = teFoo2.subListArguments(1,3);
    Assertions.assertEquals(2, subList.size());
    Assertions.assertEquals(teDouble, subList.get(0));
    Assertions.assertEquals(teInt, subList.get(1));

    //containsArgument
    Assertions.assertTrue(teFoo2.containsArgument(teDouble));
    Assertions.assertFalse(teFoo2.containsArgument(teDeep1));

    //containsAllArguments
    Assertions.assertTrue(teFoo2.containsAllArguments(subList));

    //indexOfArgument
    Assertions.assertEquals(0, teFoo2.indexOfArgument(teP));

    //lastIndexOfArgument
    Assertions.assertEquals(0, teFoo2.lastIndexOfArgument(teP));

    //equalsArguments
    Assertions.assertTrue(teFoo2.equalsArguments(teFoo2.getArgumentList()));
    Assertions.assertFalse(teFoo2.equalsArguments(subList));

    //listIteratorArguments
    Iterator<SymTypeExpression> it = teFoo2.listIteratorArguments();
    int i = 0;
    while(it.hasNext()){
      Assertions.assertEquals(symArgs[i], it.next());
      ++i;
    }
    Assertions.assertEquals(4, i);

    //listIteratorArguments
    Iterator<SymTypeExpression> it3 = teFoo2.listIteratorArguments(1);
    i=0;
    while(it3.hasNext()){
      Assertions.assertEquals(symArgs[i+1], it3.next());
      ++i;
    }
    Assertions.assertEquals(3, i);

    //iteratorArguments
    Iterator<SymTypeExpression> it2 = teFoo2.iteratorArguments();
    i = 0;
    while(it2.hasNext()){
      Assertions.assertEquals(symArgs[i], it2.next());
      ++i;
    }
    Assertions.assertEquals(4, i);

    //spliteratorArguments
    Spliterator<SymTypeExpression> split = teFoo2.spliteratorArguments();
    Assertions.assertEquals(4, split.getExactSizeIfKnown());
    split.forEachRemaining(SymTypeExpression::print);

    //sizeArguments
    Assertions.assertEquals(4, teFoo2.sizeArguments());

    //streamArguments
    Stream<SymTypeExpression> stream =teFoo2.streamArguments();
    List<SymTypeExpression> list = stream.filter(SymTypeExpression::isPrimitive)
          .collect(Collectors.toList());
    Assertions.assertEquals(2, list.size());
    Assertions.assertEquals(teDouble, list.get(0));
    Assertions.assertEquals(teInt, list.get(1));

    //parallelStreamArguments
    Stream<SymTypeExpression> parStream = teFoo2.parallelStreamArguments();
    List<SymTypeExpression> parList = parStream.filter(SymTypeExpression::isPrimitive)
        .collect(Collectors.toList());
    Assertions.assertEquals(2, parList.size());
    Assertions.assertEquals(teDouble, parList.get(0));
    Assertions.assertEquals(teInt, parList.get(1));

    //hashCodeArguments
    Assertions.assertEquals(teFoo2.getArgumentList().hashCode(), teFoo2.hashCodeArguments());

    //forEachArguments
    teFoo2.forEachArguments(SymTypeExpression::deepClone);
    Assertions.assertEquals(teP, teFoo2.getArgument(0));

    //setArgument
    teFoo2.setArgument(2,teDeep2);
    Assertions.assertEquals(teDeep2, teFoo2.getArgument(2));

    //addArgument
    teFoo2.addArgument(teSetA);
    Assertions.assertEquals(5, teFoo2.sizeArguments());
    Assertions.assertEquals(teSetA, teFoo2.getArgument(4));
    teFoo2.addArgument(3,teArr3);
    Assertions.assertEquals(6, teFoo2.sizeArguments());
    Assertions.assertEquals(teArr3, teFoo2.getArgument(3));

    //removeArgument
    teFoo2.removeArgument(teArr3);
    Assertions.assertFalse(teFoo2.containsArgument(teArr3));
    Assertions.assertEquals(5, teFoo2.sizeArguments());
    teFoo2.removeArgument(4);
    Assertions.assertFalse(teFoo2.containsArgument(teSetA));
    Assertions.assertEquals(4, teFoo2.sizeArguments());

    //clearArguments, isEmptyArguments
    Assertions.assertFalse(teFoo2.isEmptyArguments());
    teFoo2.clearArguments();
    Assertions.assertEquals(0, teFoo2.sizeArguments());
    Assertions.assertTrue(teFoo2.isEmptyArguments());

    //setArgumentList
    arguments = Lists.newArrayList(teP,teDouble,teInt,teH);
    teFoo2.setArgumentList(arguments);
    Assertions.assertEquals(4, teFoo2.sizeArguments());
    Assertions.assertEquals(teP, teFoo2.getArgument(0));
    Assertions.assertEquals(teDouble, teFoo2.getArgument(1));
    Assertions.assertEquals(teInt, teFoo2.getArgument(2));
    Assertions.assertEquals(teH, teFoo2.getArgument(3));

    //sortArguments
    teFoo2.sortArguments((arg1,arg2) -> arg1.hashCode()+arg2.hashCode());
    Assertions.assertEquals(4, teFoo2.sizeArguments());

    //addAllArguments
    teFoo2.setArgumentList(Lists.newArrayList());
    Assertions.assertTrue(teFoo2.isEmptyArguments());
    arguments = Lists.newArrayList(teP,teDouble,teInt,teH);
    teFoo2.addAllArguments(arguments);
    Assertions.assertEquals(4, teFoo2.getArgumentList().size());
    Assertions.assertEquals(teP, teFoo2.getArgument(0));
    Assertions.assertEquals(teDouble, teFoo2.getArgument(1));
    Assertions.assertEquals(teInt, teFoo2.getArgument(2));
    Assertions.assertEquals(teH, teFoo2.getArgument(3));

    //retainAllArguments
    subList = Lists.newArrayList(teP,teH);
    teFoo2.retainAllArguments(subList);
    Assertions.assertEquals(2, teFoo2.sizeArguments());
    Assertions.assertEquals(teP, teFoo2.getArgument(0));
    Assertions.assertEquals(teH, teFoo2.getArgument(1));

    //removeAllArguments
    teFoo2.removeAllArguments(subList);
    Assertions.assertTrue(teFoo2.isEmptyArguments());

    //replaceAllArguments
    arguments = Lists.newArrayList(teP,teDouble,teInt,teH);
    teFoo2.setArgumentList(arguments);
    teFoo2.replaceAllArguments(SymTypeExpression::deepClone);
    Assertions.assertEquals(4, teFoo2.sizeArguments());
    Assertions.assertTrue(teFoo2.equalsArguments(arguments));

    //removeIfArgument
    teFoo2.removeIfArgument(SymTypeExpression::isPrimitive);
    Assertions.assertEquals(2, teFoo2.sizeArguments());
  }

  @Test
  public void symTypeArrayTest(){
    SymTypeArray array = SymTypeExpressionFactory.createTypeArray("int",scope,1,_intSymType);
    Assertions.assertEquals("int[]", array.print());
    array.setDim(2);
    Assertions.assertEquals("int[][]", array.print());
  }

  @Test
  public void symTypeArrayCloneWithLessDimTest() {
    SymTypeArray arr3 = (SymTypeArray) teArr3;
    Assertions.assertEquals("int[][][][]", arr3.cloneWithLessDim(-1).print());
    Assertions.assertEquals("int[][][]", arr3.cloneWithLessDim(0).print());
    Assertions.assertEquals("int[][]", arr3.cloneWithLessDim(1).print());
    Assertions.assertEquals("int[]", arr3.cloneWithLessDim(2).print());
    Assertions.assertEquals("int", arr3.cloneWithLessDim(3).print());
    Assertions.assertFalse(arr3.cloneWithLessDim(3).isArrayType());
  }

  @Test
  public void symTypePrimitiveTest(){
    SymTypePrimitive intType = SymTypeExpressionFactory.createPrimitive("int");
    Assertions.assertEquals("int", intType.print());
    intType.setPrimitiveName("double");
    Assertions.assertEquals("double", intType.print());
    intType.setPrimitiveName("int");

    Assertions.assertEquals("java.lang.Integer", intType.getBoxedPrimitiveName());
    Assertions.assertEquals("Integer", intType.getBaseOfBoxedName());
    Assertions.assertTrue(intType.isIntegralType());
    Assertions.assertTrue(intType.isNumericType());
  }

  @Test
  public void symTypeOfWildcardTest(){
    SymTypeOfWildcard upperBoundInt = (SymTypeOfWildcard) teUpperBound;
    Assertions.assertEquals("? extends int", upperBoundInt.print());
    Assertions.assertEquals("int", upperBoundInt.getBound().print());
    Assertions.assertTrue(upperBoundInt.isUpper());
  }

  @Test
  public void testFunctionArguments() {
    SymTypeExpression teFunExp = createFunction(teVoid,
        Lists.newArrayList(teP, teDouble, teInt, teH));
    Assertions.assertTrue(teFunExp.isFunctionType());
    SymTypeOfFunction teFun = (SymTypeOfFunction) teFunExp;

    //getArgumentTypeList & getArgumentType
    Assertions.assertEquals(4, teFun.getArgumentTypeList().size());
    Assertions.assertEquals(teP, teFun.getArgumentType(0));
    Assertions.assertEquals(teDouble, teFun.getArgumentType(1));
    Assertions.assertEquals(teInt, teFun.getArgumentType(2));
    Assertions.assertEquals(teH, teFun.getArgumentType(3));
    List<SymTypeExpression> arguments = teFun.getArgumentTypeList();

    //toArrayArguments
    Object[] args = teFun.toArrayArgumentTypes();
    Assertions.assertEquals(teP, args[0]);
    Assertions.assertEquals(teDouble, args[1]);
    Assertions.assertEquals(teInt, args[2]);
    Assertions.assertEquals(teH, args[3]);

    //toArrayArguments2
    SymTypeExpression[] symArgs = teFun.toArrayArgumentTypes(new SymTypeExpression[4]);
    Assertions.assertEquals(teP, symArgs[0]);
    Assertions.assertEquals(teDouble, symArgs[1]);
    Assertions.assertEquals(teInt, symArgs[2]);
    Assertions.assertEquals(teH, symArgs[3]);

    //subListArguments
    List<SymTypeExpression> subList = teFun.subListArgumentTypes(1, 3);
    Assertions.assertEquals(2, subList.size());
    Assertions.assertEquals(teDouble, subList.get(0));
    Assertions.assertEquals(teInt, subList.get(1));

    //containsArgument
    Assertions.assertTrue(teFun.containsArgumentType(teDouble));
    Assertions.assertFalse(teFun.containsArgumentType(teDeep1));

    //containsAllArgumentTypes
    Assertions.assertTrue(teFun.containsAllArgumentTypes(subList));

    //indexOfArgument
    Assertions.assertEquals(0, teFun.indexOfArgumentType(teP));

    //lastIndexOfArgument
    Assertions.assertEquals(0, teFun.lastIndexOfArgumentType(teP));

    //equalsArgumentTypes
    Assertions.assertTrue(teFun.equalsArgumentTypeTypes(teFun.getArgumentTypeList()));
    Assertions.assertFalse(teFun.equalsArgumentTypeTypes(subList));

    //listIteratorArgumentTypes
    Iterator<SymTypeExpression> it = teFun.listIteratorArgumentTypes();
    int i = 0;
    while (it.hasNext()) {
      Assertions.assertEquals(symArgs[i], it.next());
      ++i;
    }
    Assertions.assertEquals(4, i);

    //listIteratorArgumentTypes
    Iterator<SymTypeExpression> it3 = teFun.listIteratorArgumentTypes(1);
    i = 0;
    while (it3.hasNext()) {
      Assertions.assertEquals(symArgs[i + 1], it3.next());
      ++i;
    }
    Assertions.assertEquals(3, i);

    //iteratorArgumentTypes
    Iterator<SymTypeExpression> it2 = teFun.iteratorArgumentTypes();
    i = 0;
    while (it2.hasNext()) {
      Assertions.assertEquals(symArgs[i], it2.next());
      ++i;
    }
    Assertions.assertEquals(4, i);

    //spliteratorArgumentTypes
    Spliterator<SymTypeExpression> split = teFun.spliteratorArgumentTypes();
    Assertions.assertEquals(4, split.getExactSizeIfKnown());
    split.forEachRemaining(SymTypeExpression::print);

    //sizeArgumentTypes
    Assertions.assertEquals(4, teFun.sizeArgumentTypes());

    //streamArgumentTypes
    Stream<SymTypeExpression> stream = teFun.streamArgumentTypes();
    List<SymTypeExpression> list = stream.filter(SymTypeExpression::isPrimitive)
        .collect(Collectors.toList());
    Assertions.assertEquals(2, list.size());
    Assertions.assertEquals(teDouble, list.get(0));
    Assertions.assertEquals(teInt, list.get(1));

    //parallelStreamArgumentTypes
    Stream<SymTypeExpression> parStream = teFun.parallelStreamArgumentTypes();
    List<SymTypeExpression> parList = parStream.filter(SymTypeExpression::isPrimitive)
        .collect(Collectors.toList());
    Assertions.assertEquals(2, parList.size());
    Assertions.assertEquals(teDouble, parList.get(0));
    Assertions.assertEquals(teInt, parList.get(1));

    //hashCodeArgumentTypes
    Assertions.assertEquals(teFun.getArgumentTypeList().hashCode(), teFun.hashCodeArgumentTypes());

    //forEachArgumentTypes
    teFun.forEachArgumentTypes(SymTypeExpression::deepClone);
    Assertions.assertEquals(teP, teFun.getArgumentType(0));

    //setArgument
    teFun.setArgumentType(2, teDeep2);
    Assertions.assertEquals(teDeep2, teFun.getArgumentType(2));

    //addArgument
    teFun.addArgumentType(teSetA);
    Assertions.assertEquals(5, teFun.sizeArgumentTypes());
    Assertions.assertEquals(teSetA, teFun.getArgumentType(4));
    teFun.addArgumentType(3, teArr3);
    Assertions.assertEquals(6, teFun.sizeArgumentTypes());
    Assertions.assertEquals(teArr3, teFun.getArgumentType(3));

    //removeArgument
    teFun.removeArgumentType(teArr3);
    Assertions.assertFalse(teFun.containsArgumentType(teArr3));
    Assertions.assertEquals(5, teFun.sizeArgumentTypes());
    teFun.removeArgumentType(4);
    Assertions.assertFalse(teFun.containsArgumentType(teSetA));
    Assertions.assertEquals(4, teFun.sizeArgumentTypes());

    //clearArgumentTypes, isEmptyArgumentTypes
    Assertions.assertFalse(teFun.isEmptyArgumentTypes());
    teFun.clearArgumentTypes();
    Assertions.assertEquals(0, teFun.sizeArgumentTypes());
    Assertions.assertTrue(teFun.isEmptyArgumentTypes());

    //setArgumentList
    arguments = Lists.newArrayList(teP, teDouble, teInt, teH);
    teFun.setArgumentTypeList(arguments);
    Assertions.assertEquals(4, teFun.sizeArgumentTypes());
    Assertions.assertEquals(teP, teFun.getArgumentType(0));
    Assertions.assertEquals(teDouble, teFun.getArgumentType(1));
    Assertions.assertEquals(teInt, teFun.getArgumentType(2));
    Assertions.assertEquals(teH, teFun.getArgumentType(3));

    //sortArgumentTypes
    teFun.sortArgumentTypes((arg1, arg2) -> arg1.hashCode() + arg2.hashCode());
    Assertions.assertEquals(4, teFun.sizeArgumentTypes());

    //addAllArgumentTypes
    teFun.setArgumentTypeList(Lists.newArrayList());
    Assertions.assertTrue(teFun.isEmptyArgumentTypes());
    arguments = Lists.newArrayList(teP, teDouble, teInt, teH);
    teFun.addAllArgumentTypes(arguments);
    Assertions.assertEquals(4, teFun.getArgumentTypeList().size());
    Assertions.assertEquals(teP, teFun.getArgumentType(0));
    Assertions.assertEquals(teDouble, teFun.getArgumentType(1));
    Assertions.assertEquals(teInt, teFun.getArgumentType(2));
    Assertions.assertEquals(teH, teFun.getArgumentType(3));

    //retainAllArgumentTypes
    subList = Lists.newArrayList(teP, teH);
    teFun.retainAllArgumentTypes(subList);
    Assertions.assertEquals(2, teFun.sizeArgumentTypes());
    Assertions.assertEquals(teP, teFun.getArgumentType(0));
    Assertions.assertEquals(teH, teFun.getArgumentType(1));

    //removeAllArgumentTypes
    teFun.removeAllArgumentTypes(subList);
    Assertions.assertTrue(teFun.isEmptyArgumentTypes());

    //replaceAllArgumentTypes
    arguments = Lists.newArrayList(teP, teDouble, teInt, teH);
    teFun.setArgumentTypeList(arguments);
    teFun.replaceAllArgumentTypes(SymTypeExpression::deepClone);
    Assertions.assertEquals(4, teFun.sizeArgumentTypes());
    Assertions.assertTrue(teFun.equalsArgumentTypeTypes(arguments));

    //removeIfArgument
    teFun.removeIfArgumentType(SymTypeExpression::isPrimitive);
    Assertions.assertEquals(2, teFun.sizeArgumentTypes());
  }

}
