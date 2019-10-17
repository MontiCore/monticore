/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonUtil;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import org.junit.Test;

import java.util.List;

import static de.monticore.types.check.SymTypeExpressionFactory.*;
import static org.junit.Assert.assertEquals;

public class SymTypeExpressionTest {
  
  // setup of objects (unchanged during tests)
  SymTypeExpression teDouble = createTypeConstant("double");
  SymTypeExpression teInt = createTypeConstant("int");
  SymTypeExpression teVarA = createTypeVariable("A", new TypeSymbol("long"));
  SymTypeExpression teVarB = createTypeVariable("B", new TypeSymbol("long"));
  SymTypeExpression teP = createTypeObject("de.x.Person", new TypeSymbol("long"));
  SymTypeExpression teH = createTypeObject("Human", new TypeSymbol("long"));  // on purpose: package missing
  SymTypeExpression teVoid = createTypeVoid();
  SymTypeExpression teNull = createTypeOfNull();
  SymTypeExpression teArr1 = createTypeArray(1, teH, (TypeSymbol) null);
  SymTypeExpression teArr3 = createTypeArray(3, teInt, (TypeSymbol) null);
  SymTypeExpression teSet = createGenerics("java.util.Set", Lists.newArrayList(teP), (TypeSymbol) null);
  SymTypeExpression teSetA = createGenerics("java.util.Set", Lists.newArrayList(teVarA), (TypeSymbol) null);
  SymTypeExpression teMap = createGenerics("Map", Lists.newArrayList(teInt,teP), (TypeSymbol) null); // no package!
  SymTypeExpression teFoo = createGenerics("x.Foo", Lists.newArrayList(teP,teDouble,teInt,teH), (TypeSymbol) null);
  SymTypeExpression teDeep1 = createGenerics("java.util.Set", Lists.newArrayList(teMap), (TypeSymbol) null);
  SymTypeExpression teDeep2 = createGenerics("java.util.Map2", Lists.newArrayList(teInt,teDeep1), (TypeSymbol) null);
  
  
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
    JsonElement teDoubleJson = JsonParser.parseJson(teDouble.printAsJson());
    assertEquals("de.monticore.types.check.SymTypeConstant", JsonUtil.getOptStringMember(teDoubleJson, "kind").get());
    assertEquals("double",                              JsonUtil.getOptStringMember(teDoubleJson, "constName").get());

    JsonElement teIntJson = JsonParser.parseJson(teInt.printAsJson());
    assertEquals("de.monticore.types.check.SymTypeConstant", JsonUtil.getOptStringMember(teIntJson, "kind").get());
    assertEquals("int",                                 JsonUtil.getOptStringMember(teIntJson, "constName").get());
    
    JsonElement teVarAJson = JsonParser.parseJson(teVarA.printAsJson());
    assertEquals("de.monticore.types.check.SymTypeVariable", JsonUtil.getOptStringMember(teVarAJson, "kind").get());
    assertEquals("A",                                   JsonUtil.getOptStringMember(teVarAJson, "varName").get());
    
    JsonElement tePJson = JsonParser.parseJson(teP.printAsJson());
    assertEquals("de.monticore.types.check.SymTypeOfObject", JsonUtil.getOptStringMember(tePJson, "kind").get());
    assertEquals("de.x.Person",                         JsonUtil.getOptStringMember(tePJson, "objName").get());
    
    JsonElement teVoidJson = JsonParser.parseJson(teVoid.printAsJson());
    assertEquals("void", teVoidJson.getAsJsonString().getValue());
    
    JsonElement teNullJson = JsonParser.parseJson(teNull.printAsJson());
    assertEquals("nullType", teNullJson.getAsJsonString().getValue());
    
    JsonElement teArr1Json = JsonParser.parseJson(teArr1.printAsJson());
    assertEquals("de.monticore.types.check.SymTypeArray",    JsonUtil.getOptStringMember(teArr1Json, "kind").get());
    assertEquals(1,                                     JsonUtil.getOptIntMember(teArr1Json, "dim").get(), 0.01);
    JsonElement teArr1ArgJson = teArr1Json.getAsJsonObject().get("argument");
    assertEquals("de.monticore.types.check.SymTypeOfObject", JsonUtil.getOptStringMember(teArr1ArgJson, "kind").get());
    assertEquals("Human",                               JsonUtil.getOptStringMember(teArr1ArgJson, "objName").get());
    
    JsonElement teArr3Json = JsonParser.parseJson(teArr3.printAsJson());
    assertEquals("de.monticore.types.check.SymTypeArray",    JsonUtil.getOptStringMember(teArr3Json, "kind").get());
    assertEquals(3,                                     JsonUtil.getOptIntMember(teArr3Json, "dim").get(), 0.01);
    JsonElement teArr3ArgJson = teArr3Json.getAsJsonObject().get("argument");
    assertEquals("de.monticore.types.check.SymTypeConstant", JsonUtil.getOptStringMember(teArr3ArgJson, "kind").get());
    assertEquals("int",                                 JsonUtil.getOptStringMember(teArr3ArgJson, "constName").get());
    
    JsonElement teSetJson = JsonParser.parseJson(teSet.printAsJson());
    assertEquals("de.monticore.types.check.SymTypeOfGenerics", JsonUtil.getOptStringMember(teSetJson, "kind").get());
    assertEquals("java.util.Set",                         JsonUtil.getOptStringMember(teSetJson, "typeConstructorFullName").get());
    assertEquals("TODO",                                  JsonUtil.getOptStringMember(teSetJson, "objTypeConstructorSymbol").get());
    List<JsonElement> teSetArgsJson = teSetJson.getAsJsonObject().get("arguments").getAsJsonArray().getValues();
    assertEquals(1, teSetArgsJson.size(), 0.01);
    assertEquals("de.monticore.types.check.SymTypeOfObject",   JsonUtil.getOptStringMember(teSetArgsJson.get(0), "kind").get());
    assertEquals("de.x.Person",                           JsonUtil.getOptStringMember(teSetArgsJson.get(0), "objName").get());
    
    JsonElement teSetAJson = JsonParser.parseJson(teSetA.printAsJson());
    assertEquals("de.monticore.types.check.SymTypeOfGenerics", JsonUtil.getOptStringMember(teSetAJson, "kind").get());
    assertEquals("java.util.Set",                         JsonUtil.getOptStringMember(teSetAJson, "typeConstructorFullName").get());
    assertEquals("TODO",                                  JsonUtil.getOptStringMember(teSetAJson, "objTypeConstructorSymbol").get());
    List<JsonElement> teSetAArgsJson = teSetAJson.getAsJsonObject().get("arguments").getAsJsonArray().getValues();
    assertEquals(1, teSetAArgsJson.size(), 0.01);
    assertEquals("de.monticore.types.check.SymTypeVariable",   JsonUtil.getOptStringMember(teSetAArgsJson.get(0), "kind").get());
    assertEquals("A", JsonUtil.getOptStringMember(teSetAArgsJson.get(0), "varName").get());
    
    
    JsonElement teMapJson = JsonParser.parseJson(teMap.printAsJson());
    assertEquals("de.monticore.types.check.SymTypeOfGenerics", JsonUtil.getOptStringMember(teMapJson, "kind").get());
    assertEquals("Map",                                   JsonUtil.getOptStringMember(teMapJson, "typeConstructorFullName").get());
    assertEquals("TODO",                                  JsonUtil.getOptStringMember(teMapJson, "objTypeConstructorSymbol").get());
    List<JsonElement> teMapArgsJson = teMapJson.getAsJsonObject().get("arguments").getAsJsonArray().getValues();
    assertEquals(2, teMapArgsJson.size(), 0.01);
    assertEquals("de.monticore.types.check.SymTypeConstant",   JsonUtil.getOptStringMember(teMapArgsJson.get(0), "kind").get());
    assertEquals("int", JsonUtil.getOptStringMember(teMapArgsJson.get(0), "constName").get());
    assertEquals("de.monticore.types.check.SymTypeOfObject",   JsonUtil.getOptStringMember(teMapArgsJson.get(1), "kind").get());
    assertEquals("de.x.Person",                           JsonUtil.getOptStringMember(teMapArgsJson.get(1), "objName").get());
    
    JsonElement teFooJson = JsonParser.parseJson(teFoo.printAsJson());
    assertEquals("de.monticore.types.check.SymTypeOfGenerics", JsonUtil.getOptStringMember(teFooJson, "kind").get());
    assertEquals("x.Foo",                                 JsonUtil.getOptStringMember(teFooJson, "typeConstructorFullName").get());
    assertEquals("TODO",                                  JsonUtil.getOptStringMember(teFooJson, "objTypeConstructorSymbol").get());
    List<JsonElement> teFooArgsJson = teFooJson.getAsJsonObject().get("arguments").getAsJsonArray().getValues();
    assertEquals(4, teFooArgsJson.size(), 0.01);
    assertEquals("de.monticore.types.check.SymTypeOfObject",   JsonUtil.getOptStringMember(teFooArgsJson.get(0), "kind").get());
    assertEquals("de.x.Person",                           JsonUtil.getOptStringMember(teFooArgsJson.get(0), "objName").get());
    assertEquals("de.monticore.types.check.SymTypeConstant",   JsonUtil.getOptStringMember(teFooArgsJson.get(1), "kind").get());
    assertEquals("double",                                JsonUtil.getOptStringMember(teFooArgsJson.get(1), "constName").get());
    assertEquals("de.monticore.types.check.SymTypeConstant",   JsonUtil.getOptStringMember(teFooArgsJson.get(2), "kind").get());
    assertEquals("int",                                   JsonUtil.getOptStringMember(teFooArgsJson.get(2), "constName").get());
    assertEquals("de.monticore.types.check.SymTypeOfObject",   JsonUtil.getOptStringMember(teFooArgsJson.get(3), "kind").get());
    assertEquals("Human",                                 JsonUtil.getOptStringMember(teFooArgsJson.get(3), "objName").get());
    
    JsonElement teDeep1Json = JsonParser.parseJson(teDeep1.printAsJson());
    assertEquals("de.monticore.types.check.SymTypeOfGenerics", JsonUtil.getOptStringMember(teDeep1Json, "kind").get());
    assertEquals("java.util.Set",                         JsonUtil.getOptStringMember(teDeep1Json, "typeConstructorFullName").get());
    assertEquals("TODO",                                  JsonUtil.getOptStringMember(teDeep1Json, "objTypeConstructorSymbol").get());
    List<JsonElement> teDeep1ArgsJson = teDeep1Json.getAsJsonObject().get("arguments").getAsJsonArray().getValues();
    assertEquals(1, teDeep1ArgsJson.size(), 0.01);
    assertEquals("de.monticore.types.check.SymTypeOfGenerics", JsonUtil.getOptStringMember(teDeep1ArgsJson.get(0), "kind").get());
    assertEquals("Map",                                   JsonUtil.getOptStringMember(teDeep1ArgsJson.get(0), "typeConstructorFullName").get());
    assertEquals("TODO",                                  JsonUtil.getOptStringMember(teDeep1ArgsJson.get(0), "objTypeConstructorSymbol").get());
    List<JsonElement> teDeep1teMapArgsJson = teDeep1ArgsJson.get(0).getAsJsonObject().get("arguments").getAsJsonArray().getValues();
    assertEquals(2, teDeep1teMapArgsJson.size(), 0.01);
    assertEquals("de.monticore.types.check.SymTypeConstant",   JsonUtil.getOptStringMember(teDeep1teMapArgsJson.get(0), "kind").get());
    assertEquals("int",                                   JsonUtil.getOptStringMember(teDeep1teMapArgsJson.get(0), "constName").get());
    assertEquals("de.monticore.types.check.SymTypeOfObject",   JsonUtil.getOptStringMember(teDeep1teMapArgsJson.get(1), "kind").get());
    assertEquals("de.x.Person",                           JsonUtil.getOptStringMember(teDeep1teMapArgsJson.get(1), "objName").get());
    
    JsonElement teDeep2Json = JsonParser.parseJson(teDeep2.printAsJson());
    assertEquals("de.monticore.types.check.SymTypeOfGenerics", JsonUtil.getOptStringMember(teDeep2Json, "kind").get());
    assertEquals("java.util.Map2",                        JsonUtil.getOptStringMember(teDeep2Json, "typeConstructorFullName").get());
    assertEquals("TODO",                                  JsonUtil.getOptStringMember(teDeep2Json, "objTypeConstructorSymbol").get());
    List<JsonElement> teDeep2ArgsJson = teDeep2Json.getAsJsonObject().get("arguments").getAsJsonArray().getValues();
    assertEquals(2, teDeep2ArgsJson.size(), 0.01);
    assertEquals("de.monticore.types.check.SymTypeConstant",   JsonUtil.getOptStringMember(teDeep2ArgsJson.get(0), "kind").get());
    assertEquals("int",                                   JsonUtil.getOptStringMember(teDeep2ArgsJson.get(0), "constName").get());
    assertEquals("de.monticore.types.check.SymTypeOfGenerics", JsonUtil.getOptStringMember(teDeep2ArgsJson.get(1), "kind").get());
    assertEquals("java.util.Set",                         JsonUtil.getOptStringMember(teDeep2ArgsJson.get(1), "typeConstructorFullName").get());
    assertEquals("TODO",                                  JsonUtil.getOptStringMember(teDeep2Json, "objTypeConstructorSymbol").get());
    List<JsonElement> teDeep2SetArgsJson = teDeep2ArgsJson.get(1).getAsJsonObject().get("arguments").getAsJsonArray().getValues();
    assertEquals(1, teDeep2SetArgsJson.size(), 0.01);
    assertEquals("de.monticore.types.check.SymTypeOfGenerics", JsonUtil.getOptStringMember(teDeep2SetArgsJson.get(0), "kind").get());
    assertEquals("Map",                                   JsonUtil.getOptStringMember(teDeep2SetArgsJson.get(0), "typeConstructorFullName").get());
    assertEquals("TODO",                                  JsonUtil.getOptStringMember(teDeep2SetArgsJson.get(0), "objTypeConstructorSymbol").get());
    List<JsonElement> teDeep2SetMapArgsJson = teDeep2SetArgsJson.get(0).getAsJsonObject().get("arguments").getAsJsonArray().getValues();
    assertEquals(2, teDeep2SetMapArgsJson.size(), 0.01);
    assertEquals("de.monticore.types.check.SymTypeConstant",   JsonUtil.getOptStringMember(teDeep2SetMapArgsJson.get(0), "kind").get());
    assertEquals("int",                                   JsonUtil.getOptStringMember(teDeep2SetMapArgsJson.get(0), "constName").get());
    assertEquals("de.monticore.types.check.SymTypeOfObject",   JsonUtil.getOptStringMember(teDeep2SetMapArgsJson.get(1), "kind").get());
    assertEquals("de.x.Person",                           JsonUtil.getOptStringMember(teDeep2SetMapArgsJson.get(1), "objName").get());
  }
  
  @Test
  public void baseNameTest() {
    assertEquals("Person", ((SymTypeOfObject)(teP)).getBaseName());
    assertEquals("Human", ((SymTypeOfObject)(teH)).getBaseName());
    assertEquals("Map", ((SymTypeOfGenerics)(teMap)).getBaseName());
    assertEquals("Set", ((SymTypeOfGenerics)(teSet)).getBaseName());
  }
  
}
