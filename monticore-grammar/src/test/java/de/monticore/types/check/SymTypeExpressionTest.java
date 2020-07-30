/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.symbols.oosymbols._symboltable.BuiltInJavaSymbolResolvingDelegate;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOSymbolsScope;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.monticore.types.check.DefsTypeBasic.*;
import static de.monticore.types.check.SymTypeExpressionFactory.*;
import static org.junit.Assert.*;

public class SymTypeExpressionTest {

  private static IOOSymbolsScope scope = BuiltInJavaSymbolResolvingDelegate.getScope();

  // setup of objects (unchanged during tests)
  SymTypeExpression teDouble = createTypeConstant("double");

  SymTypeExpression teInt = createTypeConstant("int");

  SymTypeExpression teVarA = createTypeVariable("A", scope);

  SymTypeExpression teIntA = createTypeObject("java.lang.Integer",scope);

  SymTypeExpression teVarB = createTypeVariable("B", scope);

  SymTypeExpression teP = createTypeObject("de.x.Person", scope);

  SymTypeExpression teH = createTypeObject("Human",
      scope);  // on purpose: package missing

  SymTypeExpression teVoid = createTypeVoid();

  SymTypeExpression teNull = createTypeOfNull();

  SymTypeExpression teArr1 = createTypeArray(teH.print(), scope, 1, teH);

  SymTypeExpression teArr3 = createTypeArray(teInt.print(), scope, 3, teInt);

  SymTypeExpression teSet = createGenerics("java.util.Set", scope, Lists.newArrayList(teP));

  SymTypeExpression teSetA = createGenerics("java.util.Set", scope, Lists.newArrayList(teVarA));

  SymTypeExpression teSetC = createGenerics("Set",scope,Lists.newArrayList(teInt));

  SymTypeExpression teMap = createGenerics("Map", scope, Lists.newArrayList(teInt, teP)); // no package!

  SymTypeExpression teFoo = createGenerics("x.Foo", scope,  Lists.newArrayList(teP, teDouble, teInt, teH));

  SymTypeExpression teMap2 = createGenerics("Map",scope,Lists.newArrayList(teSetC,teFoo));

  SymTypeExpression teMapA = createGenerics("java.util.Map",scope,Lists.newArrayList(teIntA,teP));

  SymTypeExpression teSetB = createGenerics("java.util.Set",scope,Lists.newArrayList(teMapA));

  SymTypeExpression teDeep1 = createGenerics("java.util.Set", scope, Lists.newArrayList(teMap));

  SymTypeExpression teDeep2 = createGenerics("java.util.Map2", scope, Lists.newArrayList(teInt, teDeep1));

  SymTypeExpression teUpperBound = createWildcard(true, teInt);

  SymTypeExpression teLowerBound = createWildcard(false, teH);

  SymTypeExpression teWildcard = createWildcard();

  SymTypeExpression teMap3 = createGenerics("java.util.Map", scope, Lists.newArrayList(teUpperBound, teWildcard));

  @BeforeClass
  public static void setUpScope(){
    LogStub.init();
    scope.add(new OOTypeSymbol("long"));
    scope.add(new OOTypeSymbol("Human"));
  }

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
    assertEquals("? super int", teUpperBound.print());
    assertEquals("? extends Human", teLowerBound.print());
    assertEquals("?",teWildcard.print());
    assertEquals("java.util.Map<? super int,?>", teMap3.print());
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
    List<JsonElement> teDeep1ArgsJson = teDeep1Json.getArrayMember("arguments");
    assertEquals(1, teDeep1ArgsJson.size(), 0.01);
    assertEquals("de.monticore.types.check.SymTypeOfGenerics", teDeep1ArgsJson.get(0).getAsJsonObject().getStringMember( "kind"));
    assertEquals("Map", teDeep1ArgsJson.get(0).getAsJsonObject().getStringMember( "typeConstructorFullName"));
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
    List<JsonElement> teDeep2ArgsJson = teDeep2Json.getArrayMember("arguments");
    assertEquals(2, teDeep2ArgsJson.size(), 0.01);
    assertEquals("de.monticore.types.check.SymTypeConstant", teDeep2ArgsJson.get(0).getAsJsonObject().getStringMember( "kind"));
    assertEquals("int", teDeep2ArgsJson.get(0).getAsJsonObject().getStringMember( "constName"));
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
    assertEquals("de.monticore.types.check.SymTypeConstant", teDeep2SetMapArgsJson.get(0).getAsJsonObject().getStringMember( "kind"));
    assertEquals("int", teDeep2SetMapArgsJson.get(0).getAsJsonObject().getStringMember( "constName"));
    assertEquals("de.monticore.types.check.SymTypeOfObject", teDeep2SetMapArgsJson.get(1).getAsJsonObject().getStringMember( "kind"));
    assertEquals("de.x.Person", teDeep2SetMapArgsJson.get(1).getAsJsonObject().getStringMember( "objName"));

    result = JsonParser.parse(teUpperBound.printAsJson());
    assertTrue(result.isJsonObject());
    JsonObject teUpperBound2Json = result.getAsJsonObject();
    assertEquals("de.monticore.types.check.SymTypeOfWildcard",teUpperBound2Json.getStringMember("kind"));
    assertTrue(teUpperBound2Json.getBooleanMember("isUpper"));
    JsonObject bound = teUpperBound2Json.getObjectMember("bound");
    assertEquals("de.monticore.types.check.SymTypeConstant",bound.getStringMember("kind"));
    assertEquals("int",bound.getStringMember("constName"));
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
    assertEquals(teVoid.getTypeInfo(),teVoid.deepClone().getTypeInfo());
    assertEquals(teVoid.print(),teVoid.deepClone().print());

    //SymTypeOfNull
    assertTrue(teNull.deepClone() instanceof SymTypeOfNull);
    assertEquals(teNull.getTypeInfo(),teNull.deepClone().getTypeInfo());
    assertEquals(teNull.print(),teNull.deepClone().print());

    //SymTypeVariable
    assertTrue(teVarA.deepClone() instanceof SymTypeVariable);
    assertFalse(teVarA.deepClone().isTypeConstant());
    assertTrue(teVarA.deepClone().isTypeVariable());
    assertEquals(teVarA.print(),teVarA.deepClone().print());

    //SymTypeConstant
    assertTrue(teInt.deepClone() instanceof SymTypeConstant);
    assertEquals(teInt.getTypeInfo(), teInt.deepClone().getTypeInfo());
    assertTrue(teInt.deepClone().isTypeConstant());
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
  }

  @Test
  public void testSymTypeExpressionFactory(){
    SymTypeVoid tVoid = SymTypeExpressionFactory.createTypeVoid();
    assertEquals("void",tVoid.print());

    SymTypeOfNull tNull = SymTypeExpressionFactory.createTypeOfNull();
    assertEquals("nullType",tNull.print());

    SymTypeConstant tInt = SymTypeExpressionFactory.createTypeConstant("int");
    assertEquals("int",tInt.print());
    assertTrue(tInt.isIntegralType());

    SymTypeOfGenerics tA = SymTypeExpressionFactory.createGenerics("A",scope);
    assertEquals("A<>",tA.print());
    assertTrue(tA.isEmptyArguments());

    SymTypeOfGenerics tB = SymTypeExpressionFactory.createGenerics("B",scope,Lists.newArrayList(teArr1,teIntA));
    assertEquals("B<Human[],java.lang.Integer>",tB.print());
    assertEquals(2,tB.sizeArguments());

    SymTypeOfGenerics tC = SymTypeExpressionFactory.createGenerics("C",scope,teDeep1,teDeep2);
    assertEquals("C<java.util.Set<Map<int,de.x.Person>>,java.util.Map2<int,java.util.Set<Map<int,de.x.Person>>>>",tC.print());
    assertEquals(2,tC.sizeArguments());

    SymTypeOfGenerics tD = SymTypeExpressionFactory.createGenerics("D",scope);
    assertEquals("D<>",tD.print());
    assertTrue(tD.isEmptyArguments());

    SymTypeOfGenerics tE = SymTypeExpressionFactory.createGenerics("E",scope,Lists.newArrayList(teDouble,teMap));
    assertEquals("E<double,Map<int,de.x.Person>>",tE.print());
    assertEquals(2,tE.sizeArguments());

    SymTypeOfGenerics tF = SymTypeExpressionFactory.createGenerics("F",scope,teH,teP);
    assertEquals("F<Human,de.x.Person>",tF.print());
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
    List<SymTypeExpression> list = stream.filter(SymTypeExpression::isTypeConstant)
          .collect(Collectors.toList());
    assertEquals(2,list.size());
    assertEquals(teDouble,list.get(0));
    assertEquals(teInt,list.get(1));

    //parallelStreamArguments
    Stream<SymTypeExpression> parStream = teFoo2.parallelStreamArguments();
    List<SymTypeExpression> parList = parStream.filter(SymTypeExpression::isTypeConstant)
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
    teFoo2.removeIfArgument(SymTypeExpression::isTypeConstant);
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
  public void symTypeConstantTest(){
    SymTypeConstant intType = SymTypeExpressionFactory.createTypeConstant("int");
    assertEquals("int",intType.print());
    intType.setConstName("double");
    assertEquals("double",intType.print());
    intType.setConstName("int");

    assertEquals("java.lang.Integer",intType.getBoxedConstName());
    assertEquals("Integer",intType.getBaseOfBoxedName());
    assertTrue(intType.isIntegralType());
    assertTrue(intType.isNumericType());
  }

  @Test
  public void symTypeOfWildcardTest(){
    SymTypeOfWildcard upperBoundInt = (SymTypeOfWildcard) teUpperBound;
    assertEquals("? super int", upperBoundInt.print());
    assertEquals("int", upperBoundInt.getBound().print());
    assertTrue(upperBoundInt.isUpper());
  }

}
