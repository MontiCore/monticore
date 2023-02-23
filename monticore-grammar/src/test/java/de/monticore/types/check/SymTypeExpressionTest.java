/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.monticore.types.check.DefsTypeBasic._intSymType;
import static de.monticore.types.check.SymTypeExpressionFactory.*;
import static org.junit.Assert.*;

public class SymTypeExpressionTest {

  private static IOOSymbolsScope scope = OOSymbolsMill.scope();

  // setup of objects (unchanged during tests)
  static SymTypeExpression teDouble;

  static SymTypeExpression teInt;

  static SymTypeExpression teVarA;

  static SymTypeExpression teIntA;

  static SymTypeExpression teVarB;

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

  static SymTypeExpression teObscure;

  @BeforeClass
  public static void setUpScope(){
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

    teObscure = createObscureType();

  }
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void subTypeTest() {
    assertTrue(teInt.isPrimitive());
    assertTrue(teInt.isValidType());
    assertFalse(teInt.isGenericType());
    assertFalse(teInt.isTypeVariable());
    assertFalse(teInt.isArrayType());
    assertFalse(teInt.isVoidType());
    assertFalse(teInt.isNullType());
    assertFalse(teInt.isObjectType());
    assertFalse(teInt.isFunctionType());
    assertFalse(teInt.isObscureType());
    assertFalse(teInt.isWildcard());

    assertTrue(teVarA.isTypeVariable());
    assertFalse(teVarA.isValidType());
    assertTrue(teP.isObjectType());
    assertTrue(teP.isValidType());
    assertTrue(teVoid.isVoidType());
    assertTrue(teVoid.isValidType());
    assertTrue(teNull.isNullType());
    assertTrue(teNull.isValidType());
    assertTrue(teArr1.isArrayType());
    assertTrue(teArr1.isValidType());
    assertTrue(teSet.isGenericType());
    assertTrue(teSet.isValidType());
    assertTrue(teUpperBound.isWildcard());
    assertFalse(teUpperBound.isValidType());
    assertTrue(teFunc1.isFunctionType());
    assertTrue(teFunc1.isValidType());
    assertTrue(teObscure.isObscureType());
    assertFalse(teObscure.isValidType());
  }
  
  @Test
  public void printTest() {
    assertEquals("double", teDouble.print());
    assertEquals("int", teInt.print());
    assertEquals("A", teVarA.print());
    assertEquals("de.x.Person", teP.print());
    assertEquals("void", teVoid.print());
    assertEquals("null", teNull.print());
    assertEquals("Human[]", teArr1.print());
    assertEquals("int[][][]", teArr3.print());
    assertEquals("java.util.Set<de.x.Person>", teSet.printFullName());
    assertEquals("java.util.Set<A>", teSetA.printFullName());
    assertEquals("Map<int,de.x.Person>", teMap.printFullName());
    assertEquals("x.Foo<de.x.Person,double,int,Human>", teFoo.printFullName());
    assertEquals("java.util.Set<Map<int,de.x.Person>>", teDeep1.printFullName());
    assertEquals("java.util.Map2<int,java.util.Set<Map<int,de.x.Person>>>", teDeep2.printFullName());
    assertEquals("? extends int", teUpperBound.print());
    assertEquals("? super Human", teLowerBound.print());
    assertEquals("?",teWildcard.print());
    assertEquals("java.util.Map<? extends int,?>", teMap3.printFullName());
    assertEquals("() -> void", teFunc1.print());
    assertEquals("(double, int) -> int", teFunc2.print());
    assertEquals("((double, int) -> int) -> () -> void", teFunc3.print());
    assertEquals("(double, int...) -> void", teFunc4.print());
  }

  @Test
  public void printAsJsonTest() {
    JsonElement result = JsonParser.parse(teDouble.printAsJson());
    assertTrue(result.isJsonObject());
    JsonObject teDoubleJson = result.getAsJsonObject();
    assertEquals("de.monticore.types.check.SymTypePrimitive", teDoubleJson.getStringMember("kind"));
    assertEquals("double", teDoubleJson.getStringMember("primitiveName"));

    result = JsonParser.parse(teInt.printAsJson());
    assertTrue(result.isJsonObject());
    JsonObject teIntJson = result.getAsJsonObject();
    assertEquals("de.monticore.types.check.SymTypePrimitive", teIntJson.getStringMember("kind"));
    assertEquals("int", teIntJson.getStringMember("primitiveName"));

    result = JsonParser.parse(teVarA.printAsJson());
    assertTrue(result.isJsonObject());
    JsonObject teVarAJson = result.getAsJsonObject();
    assertEquals("de.monticore.types.check.SymTypeVariable", teVarAJson.getStringMember("kind"));
    assertEquals("A", teVarAJson.getStringMember("varName"));

    result = JsonParser.parse(teP.printAsJson());
    assertTrue(result.isJsonObject());
    JsonObject tePJson = result.getAsJsonObject();
    assertEquals("de.monticore.types.check.SymTypeOfObject", tePJson.getStringMember("kind"));
    assertEquals("de.x.Person", tePJson.getStringMember("objName"));

    result = JsonParser.parse(teVoid.printAsJson());
    assertTrue(result.isJsonString());
    assertEquals("void", result.getAsJsonString().getValue());

    result = JsonParser.parse(teNull.printAsJson());
    assertTrue(result.isJsonString());
    assertEquals("null", result.getAsJsonString().getValue());

    result = JsonParser.parse(teArr1.printAsJson());
    assertTrue(result.isJsonObject());
    JsonObject teArr1Json = result.getAsJsonObject();
    assertEquals("de.monticore.types.check.SymTypeArray", teArr1Json.getStringMember("kind"));
    assertEquals(1, teArr1Json.getIntegerMember("dim"), 0.01);
    JsonObject teArr1ArgJson = teArr1Json.getObjectMember("argument");
    assertEquals("de.monticore.types.check.SymTypeOfObject", teArr1ArgJson.getStringMember("kind"));
    assertEquals("Human", teArr1ArgJson.getStringMember("objName"));

    result = JsonParser.parse(teArr3.printAsJson());
    assertTrue(result.isJsonObject());
    JsonObject teArr3Json = result.getAsJsonObject();
    assertEquals("de.monticore.types.check.SymTypeArray", teArr3Json.getStringMember("kind"));
    assertEquals(3, teArr3Json.getIntegerMember("dim"), 0.01);
    JsonObject teArr3ArgJson = teArr3Json.getObjectMember("argument");
    assertEquals("de.monticore.types.check.SymTypePrimitive", teArr3ArgJson.getStringMember("kind"));
    assertEquals("int", teArr3ArgJson.getStringMember("primitiveName"));

    result = JsonParser.parse(teSet.printAsJson());
    assertTrue(result.isJsonObject());
    JsonObject teSetJson = result.getAsJsonObject();
    assertEquals("de.monticore.types.check.SymTypeOfGenerics", teSetJson.getStringMember("kind"));
    assertEquals("java.util.Set", teSetJson.getStringMember("typeConstructorFullName"));
    List<JsonElement> teSetArgsJson = teSetJson.getArrayMember("arguments");
    assertEquals(1, teSetArgsJson.size(), 0.01);
    assertEquals("de.monticore.types.check.SymTypeOfObject",
        teSetArgsJson.get(0).getAsJsonObject().getStringMember("kind"));
    assertEquals("de.x.Person", teSetArgsJson.get(0).getAsJsonObject().getStringMember("objName"));

    result = JsonParser.parse(teSetA.printAsJson());
    assertTrue(result.isJsonObject());
    JsonObject teSetAJson = result.getAsJsonObject();
    assertEquals("de.monticore.types.check.SymTypeOfGenerics", teSetAJson.getStringMember("kind"));
    assertEquals("java.util.Set", teSetAJson.getStringMember("typeConstructorFullName"));
    List<JsonElement> teSetAArgsJson = teSetAJson.getArrayMember("arguments");
    assertEquals(1, teSetAArgsJson.size(), 0.01);
    assertEquals("de.monticore.types.check.SymTypeVariable",
        teSetAArgsJson.get(0).getAsJsonObject().getStringMember("kind"));
    assertEquals("A", teSetAArgsJson.get(0).getAsJsonObject().getStringMember("varName"));

    result = JsonParser.parse(teMap.printAsJson());
    assertTrue(result.isJsonObject());
    JsonObject teMapJson = result.getAsJsonObject();
    assertEquals("de.monticore.types.check.SymTypeOfGenerics", teMapJson.getStringMember("kind"));
    assertEquals("Map", teMapJson.getStringMember("typeConstructorFullName"));
    List<JsonElement> teMapArgsJson = teMapJson.getArrayMember("arguments");
    assertEquals(2, teMapArgsJson.size(), 0.01);
    assertEquals("de.monticore.types.check.SymTypePrimitive",
        teMapArgsJson.get(0).getAsJsonObject().getStringMember("kind"));
    assertEquals("int", teMapArgsJson.get(0).getAsJsonObject().getStringMember("primitiveName"));
    assertEquals("de.monticore.types.check.SymTypeOfObject",
        teMapArgsJson.get(1).getAsJsonObject().getStringMember("kind"));
    assertEquals("de.x.Person", teMapArgsJson.get(1).getAsJsonObject().getStringMember("objName"));

    result = JsonParser.parse(teFoo.printAsJson());
    assertTrue(result.isJsonObject());
    JsonObject teFooJson = result.getAsJsonObject();
    assertEquals("de.monticore.types.check.SymTypeOfGenerics", teFooJson.getStringMember("kind"));
    assertEquals("x.Foo", teFooJson.getStringMember("typeConstructorFullName"));
    List<JsonElement> teFooArgsJson = teFooJson.getArrayMember("arguments");
    assertEquals(4, teFooArgsJson.size(), 0.01);
    assertEquals("de.monticore.types.check.SymTypeOfObject",
        teFooArgsJson.get(0).getAsJsonObject().getStringMember("kind"));
    assertEquals("de.x.Person", teFooArgsJson.get(0).getAsJsonObject().getStringMember("objName"));
    assertEquals("de.monticore.types.check.SymTypePrimitive",
        teFooArgsJson.get(1).getAsJsonObject().getStringMember("kind"));
    assertEquals("double", teFooArgsJson.get(1).getAsJsonObject().getStringMember("primitiveName"));
    assertEquals("de.monticore.types.check.SymTypePrimitive",
        teFooArgsJson.get(2).getAsJsonObject().getStringMember("kind"));
    assertEquals("int", teFooArgsJson.get(2).getAsJsonObject().getStringMember("primitiveName"));
    assertEquals("de.monticore.types.check.SymTypeOfObject",
        teFooArgsJson.get(3).getAsJsonObject().getStringMember("kind"));
    assertEquals("Human", teFooArgsJson.get(3).getAsJsonObject().getStringMember("objName"));

    result = JsonParser.parse(teDeep1.printAsJson());
    assertTrue(result.isJsonObject());
    JsonObject teDeep1Json = result.getAsJsonObject();
    assertEquals("de.monticore.types.check.SymTypeOfGenerics", teDeep1Json.getStringMember("kind"));
    assertEquals("java.util.Set", teDeep1Json.getStringMember("typeConstructorFullName"));
    List<JsonElement> teDeep1ArgsJson = teDeep1Json.getArrayMember("arguments");
    assertEquals(1, teDeep1ArgsJson.size(), 0.01);
    assertEquals("de.monticore.types.check.SymTypeOfGenerics", teDeep1ArgsJson.get(0).getAsJsonObject().getStringMember( "kind"));
    assertEquals("Map", teDeep1ArgsJson.get(0).getAsJsonObject().getStringMember( "typeConstructorFullName"));
    List<JsonElement> teDeep1teMapArgsJson = teDeep1ArgsJson.get(0).getAsJsonObject()
        .getArrayMember("arguments");
    assertEquals(2, teDeep1teMapArgsJson.size(), 0.01);
    assertEquals("de.monticore.types.check.SymTypePrimitive", teDeep1teMapArgsJson.get(0).getAsJsonObject().getStringMember( "kind"));
    assertEquals("int", teDeep1teMapArgsJson.get(0).getAsJsonObject().getStringMember("primitiveName"));
    assertEquals("de.monticore.types.check.SymTypeOfObject", teDeep1teMapArgsJson.get(1).getAsJsonObject().getStringMember( "kind"));
    assertEquals("de.x.Person", teDeep1teMapArgsJson.get(1).getAsJsonObject().getStringMember( "objName"));

    result = JsonParser.parse(teDeep2.printAsJson());
    assertTrue(result.isJsonObject());
    JsonObject teDeep2Json = result.getAsJsonObject();
    assertEquals("de.monticore.types.check.SymTypeOfGenerics", teDeep2Json.getStringMember("kind"));
    assertEquals("java.util.Map2", teDeep2Json.getStringMember("typeConstructorFullName"));
    List<JsonElement> teDeep2ArgsJson = teDeep2Json.getArrayMember("arguments");
    assertEquals(2, teDeep2ArgsJson.size(), 0.01);
    assertEquals("de.monticore.types.check.SymTypePrimitive", teDeep2ArgsJson.get(0).getAsJsonObject().getStringMember( "kind"));
    assertEquals("int", teDeep2ArgsJson.get(0).getAsJsonObject().getStringMember( "primitiveName"));
    assertEquals("de.monticore.types.check.SymTypeOfGenerics", teDeep2ArgsJson.get(1).getAsJsonObject().getStringMember( "kind"));
    assertEquals("java.util.Set", teDeep2ArgsJson.get(1).getAsJsonObject().getStringMember( "typeConstructorFullName"));
    List<JsonElement> teDeep2SetArgsJson = teDeep2ArgsJson.get(1).getAsJsonObject()
        .getArrayMember("arguments");
    assertEquals(1, teDeep2SetArgsJson.size(), 0.01);
    assertEquals("de.monticore.types.check.SymTypeOfGenerics", teDeep2SetArgsJson.get(0).getAsJsonObject().getStringMember( "kind"));
    assertEquals("Map", teDeep2SetArgsJson.get(0).getAsJsonObject().getStringMember( "typeConstructorFullName"));
    List<JsonElement> teDeep2SetMapArgsJson = teDeep2SetArgsJson.get(0).getAsJsonObject()
        .getArrayMember("arguments");
    assertEquals(2, teDeep2SetMapArgsJson.size(), 0.01);
    assertEquals("de.monticore.types.check.SymTypePrimitive", teDeep2SetMapArgsJson.get(0).getAsJsonObject().getStringMember( "kind"));
    assertEquals("int", teDeep2SetMapArgsJson.get(0).getAsJsonObject().getStringMember( "primitiveName"));
    assertEquals("de.monticore.types.check.SymTypeOfObject", teDeep2SetMapArgsJson.get(1).getAsJsonObject().getStringMember( "kind"));
    assertEquals("de.x.Person", teDeep2SetMapArgsJson.get(1).getAsJsonObject().getStringMember( "objName"));

    result = JsonParser.parse(teUpperBound.printAsJson());
    assertTrue(result.isJsonObject());
    JsonObject teUpperBound2Json = result.getAsJsonObject();
    assertEquals("de.monticore.types.check.SymTypeOfWildcard",teUpperBound2Json.getStringMember("kind"));
    assertTrue(teUpperBound2Json.getBooleanMember("isUpper"));
    JsonObject bound = teUpperBound2Json.getObjectMember("bound");
    assertEquals("de.monticore.types.check.SymTypePrimitive",bound.getStringMember("kind"));
    assertEquals("int",bound.getStringMember("primitiveName"));

    result = JsonParser.parse(teFunc2.printAsJson());
    assertTrue(result.isJsonObject());
    JsonObject teFunc2Json = result.getAsJsonObject();
    assertEquals("de.monticore.types.check.SymTypeOfFunction",teFunc2Json.getStringMember("kind"));
    JsonObject func2returnType = teFunc2Json.getObjectMember("returnType");
    assertEquals("de.monticore.types.check.SymTypePrimitive", func2returnType.getStringMember( "kind"));
    assertEquals("int", func2returnType.getStringMember( "primitiveName"));
    List<JsonElement> func2Arguments = teFunc2Json.getArrayMember("argumentTypes");
    assertEquals(2, func2Arguments.size());
    assertEquals("de.monticore.types.check.SymTypePrimitive", func2Arguments.get(0).getAsJsonObject().getStringMember( "kind"));
    assertEquals("double", func2Arguments.get(0).getAsJsonObject().getStringMember( "primitiveName"));
    assertEquals("de.monticore.types.check.SymTypePrimitive", func2Arguments.get(1).getAsJsonObject().getStringMember( "kind"));
    assertEquals("int", func2Arguments.get(1).getAsJsonObject().getStringMember( "primitiveName"));
    assertFalse(teFunc2Json.getBooleanMember("elliptic"));

    result = JsonParser.parse(teFunc4.printAsJson());
    assertTrue(result.isJsonObject());
    JsonObject teFunc4Json = result.getAsJsonObject();
    assertTrue(teFunc4Json.getBooleanMember("elliptic"));
  }

  @Test
  public void baseNameTest() {
    assertEquals("Person", ((SymTypeOfObject) (teP)).getBaseName());
    assertEquals("Human", ((SymTypeOfObject) (teH)).getBaseName());
    assertEquals("Map", ((SymTypeOfGenerics) (teMap)).getBaseName());
    assertEquals("Set", ((SymTypeOfGenerics) (teSet)).getBaseName());
  }

  @Test
  public void unboxTest(){
    assertEquals("Set<Map<int,de.x.Person>>",SymTypeOfGenerics.unbox((SymTypeOfGenerics)teSetB));
    assertEquals("Set<de.x.Person>",SymTypeOfGenerics.unbox((SymTypeOfGenerics)teSet));
    assertEquals("Set<A>",SymTypeOfGenerics.unbox((SymTypeOfGenerics)teSetA));
    assertEquals("Map<int,de.x.Person>",SymTypeOfGenerics.unbox((SymTypeOfGenerics)teMap));
    assertEquals("Map<Set<int>,x.Foo<de.x.Person,double,int,Human>>",SymTypeOfGenerics.unbox((SymTypeOfGenerics)teMap2));
  }

  @Test
  public void boxTest() {
    assertEquals("java.util.Set<java.util.Map<java.lang.Integer,de.x.Person>>", SymTypeOfGenerics.box((SymTypeOfGenerics) teSetB));
    assertEquals("java.util.Set<de.x.Person>", SymTypeOfGenerics.box((SymTypeOfGenerics) teSet));
    assertEquals("java.util.Set<A>", SymTypeOfGenerics.box((SymTypeOfGenerics) teSetA));
    assertEquals("java.util.Map<java.lang.Integer,de.x.Person>", SymTypeOfGenerics.box((SymTypeOfGenerics)teMap));
    assertEquals("java.util.Map<java.util.Set<java.lang.Integer>,x.Foo<de.x.Person,java.lang.Double,java.lang.Integer,Human>>",SymTypeOfGenerics.box((SymTypeOfGenerics)teMap2));
  }

  @Test
  public void deepCloneTest(){
    //SymTypeVoid
    assertTrue(teVoid.deepClone() instanceof SymTypeVoid);
    assertEquals(teVoid.getTypeInfo().getName(),teVoid.deepClone().getTypeInfo().getName());
    assertEquals(teVoid.print(),teVoid.deepClone().print());

    //SymTypeOfNull
    assertTrue(teNull.deepClone() instanceof SymTypeOfNull);
    assertEquals(teNull.getTypeInfo().getName(),teNull.deepClone().getTypeInfo().getName());
    assertEquals(teNull.print(),teNull.deepClone().print());

    //SymTypeVariable
    assertTrue(teVarA.deepClone() instanceof SymTypeVariable);
    assertFalse(teVarA.deepClone().isPrimitive());
    assertTrue(teVarA.deepClone().isTypeVariable());
    assertEquals(teVarA.print(),teVarA.deepClone().print());

    //SymTypePrimitive
    assertTrue(teInt.deepClone() instanceof SymTypePrimitive);
    assertEquals(teInt.getTypeInfo().getName(), teInt.deepClone().getTypeInfo().getName());
    assertTrue(teInt.deepClone().isPrimitive());
    assertEquals(teInt.print(),teInt.deepClone().print());

    //SymTypeOfObject
    assertTrue(teH.deepClone() instanceof SymTypeOfObject);
    assertEquals(teH.print(),teH.deepClone().print());

    //SymTypeArray
    assertTrue(teArr1.deepClone() instanceof SymTypeArray);
    assertEquals(teArr1.print(),teArr1.deepClone().print());
    assertEquals(((SymTypeArray)teArr1).getDim(),((SymTypeArray)teArr1.deepClone()).getDim());
    assertEquals(((SymTypeArray)teArr1).getArgument().print(),((SymTypeArray)teArr1.deepClone()).getArgument().print());

    //SymTypeOfGenerics
    assertTrue(teDeep1.deepClone() instanceof SymTypeOfGenerics);
    assertTrue(teDeep1.deepClone().isGenericType());
    assertEquals(teDeep1.print(),teDeep1.deepClone().print());

    //SymTypeOfWildcard
    assertTrue(teUpperBound.deepClone() instanceof SymTypeOfWildcard);
    assertEquals(((SymTypeOfWildcard) teUpperBound).getBound().print(), ((SymTypeOfWildcard) teUpperBound.deepClone()).getBound().print());
    assertEquals(teUpperBound.print(), teUpperBound.deepClone().print());

    //SymTypeOfFunction
    assertTrue(teFunc3.deepClone() instanceof SymTypeOfFunction);
    assertTrue(teFunc3.deepClone().isFunctionType());
    assertEquals(teFunc3.print(), teFunc3.deepClone().print());
  }

  @Test
  public void testSymTypeExpressionFactory(){
    SymTypeVoid tVoid = SymTypeExpressionFactory.createTypeVoid();
    assertEquals("void",tVoid.print());

    SymTypeOfNull tNull = SymTypeExpressionFactory.createTypeOfNull();
    assertEquals("null",tNull.print());

    SymTypePrimitive tInt = SymTypeExpressionFactory.createPrimitive("int");
    assertEquals("int",tInt.print());
    assertTrue(tInt.isIntegralType());

    SymTypeOfGenerics tA = SymTypeExpressionFactory.createGenerics("A",scope);
    assertEquals("A<>",tA.print());
    assertTrue(tA.isEmptyArguments());

    SymTypeOfGenerics tB = SymTypeExpressionFactory.createGenerics("B",scope,Lists.newArrayList(teArr1,teIntA));
    assertEquals("B<Human[],java.lang.Integer>",tB.printFullName());
    assertEquals(2,tB.sizeArguments());

    SymTypeOfGenerics tC = SymTypeExpressionFactory.createGenerics("C",scope,teDeep1,teDeep2);
    assertEquals("C<java.util.Set<Map<int,de.x.Person>>,java.util.Map2<int,java.util.Set<Map<int,de.x.Person>>>>",tC.printFullName());
    assertEquals(2,tC.sizeArguments());

    SymTypeOfGenerics tD = SymTypeExpressionFactory.createGenerics("D",scope);
    assertEquals("D<>",tD.printFullName());
    assertTrue(tD.isEmptyArguments());

    SymTypeOfGenerics tE = SymTypeExpressionFactory.createGenerics("E",scope,Lists.newArrayList(teDouble,teMap));
    assertEquals("E<double,Map<int,de.x.Person>>",tE.printFullName());
    assertEquals(2,tE.sizeArguments());

    SymTypeOfGenerics tF = SymTypeExpressionFactory.createGenerics("F",scope,teH,teP);
    assertEquals("F<Human,de.x.Person>",tF.printFullName());
    assertEquals(2,tF.sizeArguments());

    SymTypeArray tHuman = SymTypeExpressionFactory.createTypeArray("Human",scope,1,teH);
    assertEquals("Human[]",tHuman.print());
    assertEquals(1,tHuman.getDim());
    assertEquals("Human",tHuman.getArgument().print());

    SymTypeArray tPerson = SymTypeExpressionFactory.createTypeArray("de.x.Person",scope,2,teP);
    assertEquals("de.x.Person[][]",tPerson.print());
    assertEquals(2,tPerson.getDim());
    assertEquals("de.x.Person",tPerson.getArgument().print());

    SymTypeOfObject tG = SymTypeExpressionFactory.createTypeObject("G",scope);
    assertEquals("G",tG.print());

    SymTypeOfObject tH = SymTypeExpressionFactory.createTypeObject("H",scope);
    assertEquals("H",tH.print());

    SymTypeVariable tT = SymTypeExpressionFactory.createTypeVariable("T",scope);
    assertEquals("T",tT.print());

    SymTypeVariable tS = SymTypeExpressionFactory.createTypeVariable("S",scope);
    assertEquals("S",tS.print());

    SymTypeExpression tExpr = SymTypeExpressionFactory.createTypeExpression("void",scope);
    assertTrue(tExpr instanceof SymTypeVoid);
    assertEquals("void",tExpr.print());

    SymTypeOfFunction tFunc1 = SymTypeExpressionFactory.createFunction(tVoid);
    assertEquals("() -> void", tFunc1.print());

    SymTypeOfFunction tFunc2 = SymTypeExpressionFactory.createFunction(tVoid, Lists.newArrayList(tFunc1, tFunc1));
    assertEquals("(() -> void, () -> void) -> void", tFunc2.print());

    SymTypeOfFunction tFunc3 = SymTypeExpressionFactory.createFunction(tVoid, tFunc1, tFunc1);
    assertEquals("(() -> void, () -> void) -> void", tFunc3.print());

    SymTypeOfFunction tFunc4 = SymTypeExpressionFactory.createFunction(tVoid, Lists.newArrayList(teDouble, teInt), true);
    assertEquals("(double, int...) -> void", tFunc4.print());
  }

  @Test
  public void testGenericArguments(){
    SymTypeExpression teFoo = createGenerics("x.Foo", scope,  Lists.newArrayList(teP, teDouble, teInt, teH));
    assertTrue(teFoo.isGenericType());
    SymTypeOfGenerics teFoo2 = (SymTypeOfGenerics) teFoo;
    //getArgumentList & getArgument
    assertEquals(4, teFoo2.getArgumentList().size());
    assertEquals(teP,teFoo2.getArgument(0));
    assertEquals(teDouble,teFoo2.getArgument(1));
    assertEquals(teInt,teFoo2.getArgument(2));
    assertEquals(teH,teFoo2.getArgument(3));
    List<SymTypeExpression> arguments = teFoo2.getArgumentList();

    //toArrayArguments
    Object[] args = teFoo2.toArrayArguments();
    assertEquals(teP,args[0]);
    assertEquals(teDouble,args[1]);
    assertEquals(teInt,args[2]);
    assertEquals(teH,args[3]);

    //toArrayArguments2
    SymTypeExpression[] symArgs = teFoo2.toArrayArguments(new SymTypeExpression[4]);
    assertEquals(teP,symArgs[0]);
    assertEquals(teDouble,symArgs[1]);
    assertEquals(teInt,symArgs[2]);
    assertEquals(teH,symArgs[3]);

    //subListArguments
    List<SymTypeExpression> subList = teFoo2.subListArguments(1,3);
    assertEquals(2,subList.size());
    assertEquals(teDouble,subList.get(0));
    assertEquals(teInt,subList.get(1));

    //containsArgument
    assertTrue(teFoo2.containsArgument(teDouble));
    assertFalse(teFoo2.containsArgument(teDeep1));

    //containsAllArguments
    assertTrue(teFoo2.containsAllArguments(subList));

    //indexOfArgument
    assertEquals(0,teFoo2.indexOfArgument(teP));

    //lastIndexOfArgument
    assertEquals(0,teFoo2.lastIndexOfArgument(teP));

    //equalsArguments
    assertTrue(teFoo2.equalsArguments(teFoo2.getArgumentList()));
    assertFalse(teFoo2.equalsArguments(subList));

    //listIteratorArguments
    Iterator<SymTypeExpression> it = teFoo2.listIteratorArguments();
    int i = 0;
    while(it.hasNext()){
      assertEquals(symArgs[i],it.next());
      ++i;
    }
    assertEquals(4,i);

    //listIteratorArguments
    Iterator<SymTypeExpression> it3 = teFoo2.listIteratorArguments(1);
    i=0;
    while(it3.hasNext()){
      assertEquals(symArgs[i+1],it3.next());
      ++i;
    }
    assertEquals(3,i);

    //iteratorArguments
    Iterator<SymTypeExpression> it2 = teFoo2.iteratorArguments();
    i = 0;
    while(it2.hasNext()){
      assertEquals(symArgs[i],it2.next());
      ++i;
    }
    assertEquals(4,i);

    //spliteratorArguments
    Spliterator<SymTypeExpression> split = teFoo2.spliteratorArguments();
    assertEquals(4,split.getExactSizeIfKnown());
    split.forEachRemaining(SymTypeExpression::print);

    //sizeArguments
    assertEquals(4,teFoo2.sizeArguments());

    //streamArguments
    Stream<SymTypeExpression> stream =teFoo2.streamArguments();
    List<SymTypeExpression> list = stream.filter(SymTypeExpression::isPrimitive)
          .collect(Collectors.toList());
    assertEquals(2,list.size());
    assertEquals(teDouble,list.get(0));
    assertEquals(teInt,list.get(1));

    //parallelStreamArguments
    Stream<SymTypeExpression> parStream = teFoo2.parallelStreamArguments();
    List<SymTypeExpression> parList = parStream.filter(SymTypeExpression::isPrimitive)
        .collect(Collectors.toList());
    assertEquals(2,parList.size());
    assertEquals(teDouble,parList.get(0));
    assertEquals(teInt,parList.get(1));

    //hashCodeArguments
    assertEquals(teFoo2.getArgumentList().hashCode(),teFoo2.hashCodeArguments());

    //forEachArguments
    teFoo2.forEachArguments(SymTypeExpression::deepClone);
    assertEquals(teP,teFoo2.getArgument(0));

    //setArgument
    teFoo2.setArgument(2,teDeep2);
    assertEquals(teDeep2,teFoo2.getArgument(2));

    //addArgument
    teFoo2.addArgument(teSetA);
    assertEquals(5, teFoo2.sizeArguments());
    assertEquals(teSetA,teFoo2.getArgument(4));
    teFoo2.addArgument(3,teArr3);
    assertEquals(6,teFoo2.sizeArguments());
    assertEquals(teArr3,teFoo2.getArgument(3));

    //removeArgument
    teFoo2.removeArgument(teArr3);
    assertFalse(teFoo2.containsArgument(teArr3));
    assertEquals(5,teFoo2.sizeArguments());
    teFoo2.removeArgument(4);
    assertFalse(teFoo2.containsArgument(teSetA));
    assertEquals(4,teFoo2.sizeArguments());

    //clearArguments, isEmptyArguments
    assertFalse(teFoo2.isEmptyArguments());
    teFoo2.clearArguments();
    assertEquals(0,teFoo2.sizeArguments());
    assertTrue(teFoo2.isEmptyArguments());

    //setArgumentList
    arguments = Lists.newArrayList(teP,teDouble,teInt,teH);
    teFoo2.setArgumentList(arguments);
    assertEquals(4, teFoo2.sizeArguments());
    assertEquals(teP,teFoo2.getArgument(0));
    assertEquals(teDouble,teFoo2.getArgument(1));
    assertEquals(teInt,teFoo2.getArgument(2));
    assertEquals(teH,teFoo2.getArgument(3));

    //sortArguments
    teFoo2.sortArguments((arg1,arg2) -> arg1.hashCode()+arg2.hashCode());
    assertEquals(4,teFoo2.sizeArguments());

    //addAllArguments
    teFoo2.setArgumentList(Lists.newArrayList());
    assertTrue(teFoo2.isEmptyArguments());
    arguments = Lists.newArrayList(teP,teDouble,teInt,teH);
    teFoo2.addAllArguments(arguments);
    assertEquals(4, teFoo2.getArgumentList().size());
    assertEquals(teP,teFoo2.getArgument(0));
    assertEquals(teDouble,teFoo2.getArgument(1));
    assertEquals(teInt,teFoo2.getArgument(2));
    assertEquals(teH,teFoo2.getArgument(3));

    //retainAllArguments
    subList = Lists.newArrayList(teP,teH);
    teFoo2.retainAllArguments(subList);
    assertEquals(2,teFoo2.sizeArguments());
    assertEquals(teP,teFoo2.getArgument(0));
    assertEquals(teH,teFoo2.getArgument(1));

    //removeAllArguments
    teFoo2.removeAllArguments(subList);
    assertTrue(teFoo2.isEmptyArguments());

    //replaceAllArguments
    arguments = Lists.newArrayList(teP,teDouble,teInt,teH);
    teFoo2.setArgumentList(arguments);
    teFoo2.replaceAllArguments(SymTypeExpression::deepClone);
    assertEquals(4,teFoo2.sizeArguments());
    assertTrue(teFoo2.equalsArguments(arguments));

    //removeIfArgument
    teFoo2.removeIfArgument(SymTypeExpression::isPrimitive);
    assertEquals(2,teFoo2.sizeArguments());
  }

  @Test
  public void symTypeArrayTest(){
    SymTypeArray array = SymTypeExpressionFactory.createTypeArray("int",scope,1,_intSymType);
    assertEquals("int[]",array.print());
    array.setDim(2);
    assertEquals("int[][]",array.print());
  }

  @Test
  public void symTypePrimitiveTest(){
    SymTypePrimitive intType = SymTypeExpressionFactory.createPrimitive("int");
    assertEquals("int",intType.print());
    intType.setPrimitiveName("double");
    assertEquals("double",intType.print());
    intType.setPrimitiveName("int");

    assertEquals("java.lang.Integer",intType.getBoxedPrimitiveName());
    assertEquals("Integer",intType.getBaseOfBoxedName());
    assertTrue(intType.isIntegralType());
    assertTrue(intType.isNumericType());
  }

  @Test
  public void symTypeOfWildcardTest(){
    SymTypeOfWildcard upperBoundInt = (SymTypeOfWildcard) teUpperBound;
    assertEquals("? extends int", upperBoundInt.print());
    assertEquals("int", upperBoundInt.getBound().print());
    assertTrue(upperBoundInt.isUpper());
  }

  @Test
  public void testFunctionArguments() {
    SymTypeExpression teFunExp = createFunction(teVoid,
        Lists.newArrayList(teP, teDouble, teInt, teH));
    assertTrue(teFunExp.isFunctionType());
    SymTypeOfFunction teFun = (SymTypeOfFunction) teFunExp;

    //getArgumentTypeList & getArgumentType
    assertEquals(4, teFun.getArgumentTypeList().size());
    assertEquals(teP, teFun.getArgumentType(0));
    assertEquals(teDouble, teFun.getArgumentType(1));
    assertEquals(teInt, teFun.getArgumentType(2));
    assertEquals(teH, teFun.getArgumentType(3));
    List<SymTypeExpression> arguments = teFun.getArgumentTypeList();

    //toArrayArguments
    Object[] args = teFun.toArrayArgumentTypes();
    assertEquals(teP, args[0]);
    assertEquals(teDouble, args[1]);
    assertEquals(teInt, args[2]);
    assertEquals(teH, args[3]);

    //toArrayArguments2
    SymTypeExpression[] symArgs = teFun.toArrayArgumentTypes(new SymTypeExpression[4]);
    assertEquals(teP, symArgs[0]);
    assertEquals(teDouble, symArgs[1]);
    assertEquals(teInt, symArgs[2]);
    assertEquals(teH, symArgs[3]);

    //subListArguments
    List<SymTypeExpression> subList = teFun.subListArgumentTypes(1, 3);
    assertEquals(2, subList.size());
    assertEquals(teDouble, subList.get(0));
    assertEquals(teInt, subList.get(1));

    //containsArgument
    assertTrue(teFun.containsArgumentType(teDouble));
    assertFalse(teFun.containsArgumentType(teDeep1));

    //containsAllArgumentTypes
    assertTrue(teFun.containsAllArgumentTypes(subList));

    //indexOfArgument
    assertEquals(0, teFun.indexOfArgumentType(teP));

    //lastIndexOfArgument
    assertEquals(0, teFun.lastIndexOfArgumentType(teP));

    //equalsArgumentTypes
    assertTrue(teFun.equalsArgumentTypeTypes(teFun.getArgumentTypeList()));
    assertFalse(teFun.equalsArgumentTypeTypes(subList));

    //listIteratorArgumentTypes
    Iterator<SymTypeExpression> it = teFun.listIteratorArgumentTypes();
    int i = 0;
    while (it.hasNext()) {
      assertEquals(symArgs[i], it.next());
      ++i;
    }
    assertEquals(4, i);

    //listIteratorArgumentTypes
    Iterator<SymTypeExpression> it3 = teFun.listIteratorArgumentTypes(1);
    i = 0;
    while (it3.hasNext()) {
      assertEquals(symArgs[i + 1], it3.next());
      ++i;
    }
    assertEquals(3, i);

    //iteratorArgumentTypes
    Iterator<SymTypeExpression> it2 = teFun.iteratorArgumentTypes();
    i = 0;
    while (it2.hasNext()) {
      assertEquals(symArgs[i], it2.next());
      ++i;
    }
    assertEquals(4, i);

    //spliteratorArgumentTypes
    Spliterator<SymTypeExpression> split = teFun.spliteratorArgumentTypes();
    assertEquals(4, split.getExactSizeIfKnown());
    split.forEachRemaining(SymTypeExpression::print);

    //sizeArgumentTypes
    assertEquals(4, teFun.sizeArgumentTypes());

    //streamArgumentTypes
    Stream<SymTypeExpression> stream = teFun.streamArgumentTypes();
    List<SymTypeExpression> list = stream.filter(SymTypeExpression::isPrimitive)
        .collect(Collectors.toList());
    assertEquals(2, list.size());
    assertEquals(teDouble, list.get(0));
    assertEquals(teInt, list.get(1));

    //parallelStreamArgumentTypes
    Stream<SymTypeExpression> parStream = teFun.parallelStreamArgumentTypes();
    List<SymTypeExpression> parList = parStream.filter(SymTypeExpression::isPrimitive)
        .collect(Collectors.toList());
    assertEquals(2, parList.size());
    assertEquals(teDouble, parList.get(0));
    assertEquals(teInt, parList.get(1));

    //hashCodeArgumentTypes
    assertEquals(teFun.getArgumentTypeList().hashCode(), teFun.hashCodeArgumentTypes());

    //forEachArgumentTypes
    teFun.forEachArgumentTypes(SymTypeExpression::deepClone);
    assertEquals(teP, teFun.getArgumentType(0));

    //setArgument
    teFun.setArgumentType(2, teDeep2);
    assertEquals(teDeep2, teFun.getArgumentType(2));

    //addArgument
    teFun.addArgumentType(teSetA);
    assertEquals(5, teFun.sizeArgumentTypes());
    assertEquals(teSetA, teFun.getArgumentType(4));
    teFun.addArgumentType(3, teArr3);
    assertEquals(6, teFun.sizeArgumentTypes());
    assertEquals(teArr3, teFun.getArgumentType(3));

    //removeArgument
    teFun.removeArgumentType(teArr3);
    assertFalse(teFun.containsArgumentType(teArr3));
    assertEquals(5, teFun.sizeArgumentTypes());
    teFun.removeArgumentType(4);
    assertFalse(teFun.containsArgumentType(teSetA));
    assertEquals(4, teFun.sizeArgumentTypes());

    //clearArgumentTypes, isEmptyArgumentTypes
    assertFalse(teFun.isEmptyArgumentTypes());
    teFun.clearArgumentTypes();
    assertEquals(0, teFun.sizeArgumentTypes());
    assertTrue(teFun.isEmptyArgumentTypes());

    //setArgumentList
    arguments = Lists.newArrayList(teP, teDouble, teInt, teH);
    teFun.setArgumentTypeList(arguments);
    assertEquals(4, teFun.sizeArgumentTypes());
    assertEquals(teP, teFun.getArgumentType(0));
    assertEquals(teDouble, teFun.getArgumentType(1));
    assertEquals(teInt, teFun.getArgumentType(2));
    assertEquals(teH, teFun.getArgumentType(3));

    //sortArgumentTypes
    teFun.sortArgumentTypes((arg1, arg2) -> arg1.hashCode() + arg2.hashCode());
    assertEquals(4, teFun.sizeArgumentTypes());

    //addAllArgumentTypes
    teFun.setArgumentTypeList(Lists.newArrayList());
    assertTrue(teFun.isEmptyArgumentTypes());
    arguments = Lists.newArrayList(teP, teDouble, teInt, teH);
    teFun.addAllArgumentTypes(arguments);
    assertEquals(4, teFun.getArgumentTypeList().size());
    assertEquals(teP, teFun.getArgumentType(0));
    assertEquals(teDouble, teFun.getArgumentType(1));
    assertEquals(teInt, teFun.getArgumentType(2));
    assertEquals(teH, teFun.getArgumentType(3));

    //retainAllArgumentTypes
    subList = Lists.newArrayList(teP, teH);
    teFun.retainAllArgumentTypes(subList);
    assertEquals(2, teFun.sizeArgumentTypes());
    assertEquals(teP, teFun.getArgumentType(0));
    assertEquals(teH, teFun.getArgumentType(1));

    //removeAllArgumentTypes
    teFun.removeAllArgumentTypes(subList);
    assertTrue(teFun.isEmptyArgumentTypes());

    //replaceAllArgumentTypes
    arguments = Lists.newArrayList(teP, teDouble, teInt, teH);
    teFun.setArgumentTypeList(arguments);
    teFun.replaceAllArgumentTypes(SymTypeExpression::deepClone);
    assertEquals(4, teFun.sizeArgumentTypes());
    assertTrue(teFun.equalsArgumentTypeTypes(arguments));

    //removeIfArgument
    teFun.removeIfArgumentType(SymTypeExpression::isPrimitive);
    assertEquals(2, teFun.sizeArgumentTypes());
  }

}
