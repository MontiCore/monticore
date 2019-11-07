/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisSymTabMill;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonUtil;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbolsSymTabMill;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.types.check.DefsTypeBasic.add2scope;
import static de.monticore.types.check.SymTypeExpressionFactory.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SymTypeExpressionTest {

  private ExpressionsBasisScope scope;

  // setup of objects (unchanged during tests)
  SymTypeExpression teDouble = createTypeConstant("double");

  SymTypeExpression teInt = createTypeConstant("int");

  SymTypeExpression teVarA = createTypeVariable("A", new TypeSymbol("long"));

  SymTypeExpression teVarB = createTypeVariable("B", new TypeSymbol("long"));

  SymTypeExpression teP = createTypeObject("de.x.Person", new TypeSymbol("long"));

  SymTypeExpression teH = createTypeObject("Human",
      new TypeSymbol("long"));  // on purpose: package missing

  SymTypeExpression teVoid = createTypeVoid();

  SymTypeExpression teNull = createTypeOfNull();

  SymTypeExpression teArr1 = createTypeArray(1, teH, (TypeSymbol) null);

  SymTypeExpression teArr3 = createTypeArray(3, teInt, (TypeSymbol) null);

  SymTypeExpression teSet = createGenerics("java.util.Set", Lists.newArrayList(teP),
      (TypeSymbol) null);

  SymTypeExpression teSetA = createGenerics("java.util.Set", Lists.newArrayList(teVarA),
      (TypeSymbol) null);

  SymTypeExpression teMap = createGenerics("Map", Lists.newArrayList(teInt, teP),
      (TypeSymbol) null); // no package!

  SymTypeExpression teFoo = createGenerics("x.Foo", Lists.newArrayList(teP, teDouble, teInt, teH),
      (TypeSymbol) null);

  SymTypeExpression teDeep1 = createGenerics("java.util.Set", Lists.newArrayList(teMap),
      (TypeSymbol) null);

  SymTypeExpression teDeep2 = createGenerics("java.util.Map2", Lists.newArrayList(teInt, teDeep1),
      (TypeSymbol) null);

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
  public void printAsJsonTest() {
    JsonElement result = JsonParser.parse(teDouble.printAsJson());
    assertTrue(result.isJsonObject());
    JsonObject teDoubleJson = result.getAsJsonObject();
    assertEquals("de.monticore.types.check.SymTypeConstant", teDoubleJson.getStringMember("kind"));
    assertEquals("double", teDoubleJson.getStringMember("constName"));

    result = JsonParser.parse(teInt.printAsJson());
    assertTrue(result.isJsonObject());
    JsonObject teIntJson = result.getAsJsonObject();
    assertEquals("de.monticore.types.check.SymTypeConstant", teIntJson.getStringMember("kind"));
    assertEquals("int", teIntJson.getStringMember("constName"));

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
    assertEquals("voidType", result.getAsJsonString().getValue());

    result = JsonParser.parse(teNull.printAsJson());
    assertTrue(result.isJsonString());
    assertEquals("nullType", result.getAsJsonString().getValue());

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
    assertEquals("de.monticore.types.check.SymTypeConstant", teArr3ArgJson.getStringMember("kind"));
    assertEquals("int", teArr3ArgJson.getStringMember("constName"));

    result = JsonParser.parse(teSet.printAsJson());
    assertTrue(result.isJsonObject());
    JsonObject teSetJson = result.getAsJsonObject();
    assertEquals("de.monticore.types.check.SymTypeOfGenerics", teSetJson.getStringMember("kind"));
    assertEquals("java.util.Set", teSetJson.getStringMember("typeConstructorFullName"));
    assertEquals("TODO", teSetJson.getStringMember("objTypeConstructorSymbol"));
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
    assertEquals("TODO", teSetAJson.getStringMember("objTypeConstructorSymbol"));
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
    assertEquals("TODO", teMapJson.getStringMember("objTypeConstructorSymbol"));
    List<JsonElement> teMapArgsJson = teMapJson.getArrayMember("arguments");
    assertEquals(2, teMapArgsJson.size(), 0.01);
    assertEquals("de.monticore.types.check.SymTypeConstant",
        teMapArgsJson.get(0).getAsJsonObject().getStringMember("kind"));
    assertEquals("int", teMapArgsJson.get(0).getAsJsonObject().getStringMember("constName"));
    assertEquals("de.monticore.types.check.SymTypeOfObject",
        teMapArgsJson.get(1).getAsJsonObject().getStringMember("kind"));
    assertEquals("de.x.Person", teMapArgsJson.get(1).getAsJsonObject().getStringMember("objName"));

    result = JsonParser.parse(teFoo.printAsJson());
    assertTrue(result.isJsonObject());
    JsonObject teFooJson = result.getAsJsonObject();
    assertEquals("de.monticore.types.check.SymTypeOfGenerics", teFooJson.getStringMember("kind"));
    assertEquals("x.Foo", teFooJson.getStringMember("typeConstructorFullName"));
    assertEquals("TODO", teFooJson.getStringMember("objTypeConstructorSymbol"));
    List<JsonElement> teFooArgsJson = teFooJson.getArrayMember("arguments");
    assertEquals(4, teFooArgsJson.size(), 0.01);
    assertEquals("de.monticore.types.check.SymTypeOfObject",
        teFooArgsJson.get(0).getAsJsonObject().getStringMember("kind"));
    assertEquals("de.x.Person", teFooArgsJson.get(0).getAsJsonObject().getStringMember("objName"));
    assertEquals("de.monticore.types.check.SymTypeConstant",
        teFooArgsJson.get(1).getAsJsonObject().getStringMember("kind"));
    assertEquals("double", teFooArgsJson.get(1).getAsJsonObject().getStringMember("constName"));
    assertEquals("de.monticore.types.check.SymTypeConstant",
        teFooArgsJson.get(2).getAsJsonObject().getStringMember("kind"));
    assertEquals("int", teFooArgsJson.get(2).getAsJsonObject().getStringMember("constName"));
    assertEquals("de.monticore.types.check.SymTypeOfObject",
        teFooArgsJson.get(3).getAsJsonObject().getStringMember("kind"));
    assertEquals("Human", teFooArgsJson.get(3).getAsJsonObject().getStringMember("objName"));

    result = JsonParser.parse(teDeep1.printAsJson());
    assertTrue(result.isJsonObject());
    JsonObject teDeep1Json = result.getAsJsonObject();
    assertEquals("de.monticore.types.check.SymTypeOfGenerics", teDeep1Json.getStringMember("kind"));
    assertEquals("java.util.Set", teDeep1Json.getStringMember("typeConstructorFullName"));
    assertEquals("TODO", teDeep1Json.getStringMember("objTypeConstructorSymbol"));
    List<JsonElement> teDeep1ArgsJson = teDeep1Json.getArrayMember("arguments");
    assertEquals(1, teDeep1ArgsJson.size(), 0.01);
    assertEquals("de.monticore.types.check.SymTypeOfGenerics", teDeep1ArgsJson.get(0).getAsJsonObject().getStringMember( "kind"));
    assertEquals("Map", teDeep1ArgsJson.get(0).getAsJsonObject().getStringMember( "typeConstructorFullName"));
    assertEquals("TODO", teDeep1ArgsJson.get(0).getAsJsonObject().getStringMember( "objTypeConstructorSymbol"));
    List<JsonElement> teDeep1teMapArgsJson = teDeep1ArgsJson.get(0).getAsJsonObject()
        .getArrayMember("arguments");
    assertEquals(2, teDeep1teMapArgsJson.size(), 0.01);
    assertEquals("de.monticore.types.check.SymTypeConstant", teDeep1teMapArgsJson.get(0).getAsJsonObject().getStringMember( "kind"));
    assertEquals("int", teDeep1teMapArgsJson.get(0).getAsJsonObject().getStringMember("constName"));
    assertEquals("de.monticore.types.check.SymTypeOfObject", teDeep1teMapArgsJson.get(1).getAsJsonObject().getStringMember( "kind"));
    assertEquals("de.x.Person", teDeep1teMapArgsJson.get(1).getAsJsonObject().getStringMember( "objName"));

    result = JsonParser.parse(teDeep2.printAsJson());
    assertTrue(result.isJsonObject());
    JsonObject teDeep2Json = result.getAsJsonObject();
    assertEquals("de.monticore.types.check.SymTypeOfGenerics", teDeep2Json.getStringMember("kind"));
    assertEquals("java.util.Map2", teDeep2Json.getStringMember("typeConstructorFullName"));
    assertEquals("TODO", teDeep2Json.getStringMember("objTypeConstructorSymbol"));
    List<JsonElement> teDeep2ArgsJson = teDeep2Json.getArrayMember("arguments");
    assertEquals(2, teDeep2ArgsJson.size(), 0.01);
    assertEquals("de.monticore.types.check.SymTypeConstant", teDeep2ArgsJson.get(0).getAsJsonObject().getStringMember( "kind"));
    assertEquals("int", teDeep2ArgsJson.get(0).getAsJsonObject().getStringMember( "constName"));
    assertEquals("de.monticore.types.check.SymTypeOfGenerics", teDeep2ArgsJson.get(1).getAsJsonObject().getStringMember( "kind"));
    assertEquals("java.util.Set", teDeep2ArgsJson.get(1).getAsJsonObject().getStringMember( "typeConstructorFullName"));
    assertEquals("TODO", teDeep2Json.getStringMember("objTypeConstructorSymbol"));
    List<JsonElement> teDeep2SetArgsJson = teDeep2ArgsJson.get(1).getAsJsonObject()
        .getArrayMember("arguments");
    assertEquals(1, teDeep2SetArgsJson.size(), 0.01);
    assertEquals("de.monticore.types.check.SymTypeOfGenerics", teDeep2SetArgsJson.get(0).getAsJsonObject().getStringMember( "kind"));
    assertEquals("Map", teDeep2SetArgsJson.get(0).getAsJsonObject().getStringMember( "typeConstructorFullName"));
    assertEquals("TODO", teDeep2SetArgsJson.get(0).getAsJsonObject().getStringMember( "objTypeConstructorSymbol"));
    List<JsonElement> teDeep2SetMapArgsJson = teDeep2SetArgsJson.get(0).getAsJsonObject()
        .getArrayMember("arguments");
    assertEquals(2, teDeep2SetMapArgsJson.size(), 0.01);
    assertEquals("de.monticore.types.check.SymTypeConstant", teDeep2SetMapArgsJson.get(0).getAsJsonObject().getStringMember( "kind"));
    assertEquals("int", teDeep2SetMapArgsJson.get(0).getAsJsonObject().getStringMember( "constName"));
    assertEquals("de.monticore.types.check.SymTypeOfObject", teDeep2SetMapArgsJson.get(1).getAsJsonObject().getStringMember( "kind"));
    assertEquals("de.x.Person", teDeep2SetMapArgsJson.get(1).getAsJsonObject().getStringMember( "objName"));
  }

  @Test
  public void baseNameTest() {
    assertEquals("Person", ((SymTypeOfObject) (teP)).getBaseName());
    assertEquals("Human", ((SymTypeOfObject) (teH)).getBaseName());
    assertEquals("Map", ((SymTypeOfGenerics) (teMap)).getBaseName());
    assertEquals("Set", ((SymTypeOfGenerics) (teSet)).getBaseName());
  }

}
