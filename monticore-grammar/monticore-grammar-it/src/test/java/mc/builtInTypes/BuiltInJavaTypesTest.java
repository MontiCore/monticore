/* (c) https://github.com/MontiCore/monticore */
package mc.builtInTypes;

import de.monticore.io.paths.ModelPath;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.*;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class BuiltInJavaTypesTest {

  private static IOOSymbolsGlobalScope gs;

  @BeforeClass
  public static void setup(){
    LogStub.init();
    gs = OOSymbolsMill
        .oOSymbolsGlobalScopeBuilder()
        .setModelPath(new ModelPath())
        .setModelFileExtension("bijt")
        .build();
    gs.addAdaptedOOTypeSymbolResolvingDelegate(new BuiltInJavaSymbolResolvingDelegate());


     //other way to get globalscope: gs = BuiltInJavaTypeSymbolResolvingDelegate.getScope();

  }

  @Test
  public void testBuiltInPrimitiveJavaTypes(){
    //assert that the primitive types can be resolved in the scope
    Optional<OOTypeSymbol> intsymtype = gs.resolveOOType("int");
    Optional<OOTypeSymbol> doublesymtype = gs.resolveOOType("double");
    Optional<OOTypeSymbol> floatsymtype = gs.resolveOOType("float");
    Optional<OOTypeSymbol> longsymtype = gs.resolveOOType("long");
    Optional<OOTypeSymbol> charsymtype = gs.resolveOOType("char");
    Optional<OOTypeSymbol> shortsymtype = gs.resolveOOType("short");
    Optional<OOTypeSymbol> bytesymtype = gs.resolveOOType("byte");
    Optional<OOTypeSymbol> booleansymtype = gs.resolveOOType("boolean");

    assertTrue(intsymtype.isPresent());
    assertTrue(doublesymtype.isPresent());
    assertTrue(floatsymtype.isPresent());
    assertTrue(longsymtype.isPresent());
    assertTrue(charsymtype.isPresent());
    assertTrue(shortsymtype.isPresent());
    assertTrue(bytesymtype.isPresent());
    assertTrue(booleansymtype.isPresent());

    //assert that the primitives have no fields, methods, type variables and super types
    assertTrue(intsymtype.get().getMethodList().isEmpty());
    assertTrue(intsymtype.get().getFieldList().isEmpty());
    assertTrue(intsymtype.get().getSuperTypesList().isEmpty());
    assertTrue(intsymtype.get().getTypeParameterList().isEmpty());

    assertTrue(doublesymtype.get().getMethodList().isEmpty());
    assertTrue(doublesymtype.get().getFieldList().isEmpty());
    assertTrue(doublesymtype.get().getSuperTypesList().isEmpty());
    assertTrue(doublesymtype.get().getTypeParameterList().isEmpty());

    assertTrue(floatsymtype.get().getMethodList().isEmpty());
    assertTrue(floatsymtype.get().getFieldList().isEmpty());
    assertTrue(floatsymtype.get().getSuperTypesList().isEmpty());
    assertTrue(floatsymtype.get().getTypeParameterList().isEmpty());

    assertTrue(longsymtype.get().getMethodList().isEmpty());
    assertTrue(longsymtype.get().getFieldList().isEmpty());
    assertTrue(longsymtype.get().getSuperTypesList().isEmpty());
    assertTrue(longsymtype.get().getTypeParameterList().isEmpty());

    assertTrue(charsymtype.get().getMethodList().isEmpty());
    assertTrue(charsymtype.get().getFieldList().isEmpty());
    assertTrue(charsymtype.get().getSuperTypesList().isEmpty());
    assertTrue(charsymtype.get().getTypeParameterList().isEmpty());

    assertTrue(shortsymtype.get().getMethodList().isEmpty());
    assertTrue(shortsymtype.get().getFieldList().isEmpty());
    assertTrue(shortsymtype.get().getSuperTypesList().isEmpty());
    assertTrue(shortsymtype.get().getTypeParameterList().isEmpty());

    assertTrue(bytesymtype.get().getMethodList().isEmpty());
    assertTrue(bytesymtype.get().getFieldList().isEmpty());
    assertTrue(bytesymtype.get().getSuperTypesList().isEmpty());
    assertTrue(bytesymtype.get().getTypeParameterList().isEmpty());

    assertTrue(booleansymtype.get().getMethodList().isEmpty());
    assertTrue(booleansymtype.get().getFieldList().isEmpty());
    assertTrue(booleansymtype.get().getSuperTypesList().isEmpty());
    assertTrue(booleansymtype.get().getTypeParameterList().isEmpty());
  }

  @Test
  public void testBuiltInIntWrapper(){
    Optional<OOTypeSymbol> intsymtype = gs.resolveOOType("java.lang.Integer");
    assertTrue(intsymtype.isPresent());

    //java.lang.Integer extends java.lang.Number and has no type parameters
    assertEquals(1,intsymtype.get().getSuperTypesList().size());
    assertEquals("Number",intsymtype.get().getSuperTypesList().get(0).print());
    assertTrue(intsymtype.get().getTypeParameterList().isEmpty());

    //test some methods
    assertFalse(intsymtype.get().getMethodList().isEmpty());
    IOOSymbolsScope intspannedscope = intsymtype.get().getSpannedScope();
    Optional<MethodSymbol> parseInt = intspannedscope.resolveMethod("parseInt");
    Optional<MethodSymbol> sum = intspannedscope.resolveMethod("sum");

    assertTrue(parseInt.isPresent());
    assertTrue(sum.isPresent());

    //test one method and its parameters specifically
    IOOSymbolsScope parseIntSpannedScope = parseInt.get().getSpannedScope();
    Optional<FieldSymbol> s = parseIntSpannedScope.resolveField("s");

    assertTrue(s.isPresent());
    assertEquals("String",s.get().getType().print());
    assertEquals("int",parseInt.get().getReturnType().print());
  }

  @Test
  public void testBuiltInDoubleWrapper(){
    Optional<OOTypeSymbol> doublesymtype = gs.resolveOOType("java.lang.Double");
    assertTrue(doublesymtype.isPresent());

    //java.lang.Double extends java.lang.Number and has no type parameters
    assertEquals(1,doublesymtype.get().getSuperTypesList().size());
    assertEquals("Number",doublesymtype.get().getSuperTypesList().get(0).print());
    assertTrue(doublesymtype.get().getTypeParameterList().isEmpty());

    //test some methods
    assertFalse(doublesymtype.get().getMethodList().isEmpty());
    IOOSymbolsScope doubleSpannedScope = doublesymtype.get().getSpannedScope();
    Optional<MethodSymbol> parseDouble = doubleSpannedScope.resolveMethod("parseDouble");
    Optional<MethodSymbol> sum = doubleSpannedScope.resolveMethod("sum");

    assertTrue(parseDouble.isPresent());
    assertTrue(sum.isPresent());

    //test one method and its parameters specifically
    IOOSymbolsScope parseDoubleSpannedScope = parseDouble.get().getSpannedScope();
    Optional<FieldSymbol> s = parseDoubleSpannedScope.resolveField("s");

    assertTrue(s.isPresent());
    assertEquals("String",s.get().getType().print());
    assertEquals("double",parseDouble.get().getReturnType().print());
  }

  @Test
  public void testBuiltInFloatWrapper(){
    Optional<OOTypeSymbol> floatsymtype = gs.resolveOOType("java.lang.Float");
    assertTrue(floatsymtype.isPresent());

    //java.lang.Float extends java.lang.Number and has no type parameters
    assertEquals(1,floatsymtype.get().getSuperTypesList().size());
    assertEquals("Number",floatsymtype.get().getSuperTypesList().get(0).print());
    assertTrue(floatsymtype.get().getTypeParameterList().isEmpty());

    //test some methods
    assertFalse(floatsymtype.get().getMethodList().isEmpty());
    IOOSymbolsScope floatspannedscope = floatsymtype.get().getSpannedScope();
    Optional<MethodSymbol> parseFloat = floatspannedscope.resolveMethod("parseFloat");
    Optional<MethodSymbol> sum = floatspannedscope.resolveMethod("sum");

    assertTrue(parseFloat.isPresent());
    assertTrue(sum.isPresent());

    //test one method and its parameters specifically
    IOOSymbolsScope parseFloatSpannedScope = parseFloat.get().getSpannedScope();
    Optional<FieldSymbol> s = parseFloatSpannedScope.resolveField("s");

    assertTrue(s.isPresent());
    assertEquals("String",s.get().getType().print());
    assertEquals("float",parseFloat.get().getReturnType().print());
  }

  @Test
  public void testBuiltInLongWrapper(){
    Optional<OOTypeSymbol> longsymtype = gs.resolveOOType("java.lang.Long");
    assertTrue(longsymtype.isPresent());

    //java.lang.Long extends java.lang.Number and has no type parameters
    assertEquals(1,longsymtype.get().getSuperTypesList().size());
    assertEquals("Number",longsymtype.get().getSuperTypesList().get(0).print());
    assertTrue(longsymtype.get().getTypeParameterList().isEmpty());

    //test some methods
    assertFalse(longsymtype.get().getMethodList().isEmpty());
    IOOSymbolsScope longspannedscope = longsymtype.get().getSpannedScope();
    Optional<MethodSymbol> parseLong = longspannedscope.resolveMethod("parseLong");
    Optional<MethodSymbol> sum = longspannedscope.resolveMethod("sum");

    assertTrue(parseLong.isPresent());
    assertTrue(sum.isPresent());

    //test one method and its parameters specifically
    IOOSymbolsScope parseLongSpannedScope = parseLong.get().getSpannedScope();
    Optional<FieldSymbol> s = parseLongSpannedScope.resolveField("s");

    assertTrue(s.isPresent());
    assertEquals("String",s.get().getType().print());
    assertEquals("long",parseLong.get().getReturnType().print());
  }

  @Test
  public void testBuiltInCharWrapper(){
    Optional<OOTypeSymbol> charsymtype = gs.resolveOOType("java.lang.Character");
    assertTrue(charsymtype.isPresent());

    //java.lang.Character directly extends java.lang.Object and has no type parameters
    assertEquals(1,charsymtype.get().getSuperTypesList().size());
    assertEquals("Object",charsymtype.get().getSuperTypesList().get(0).print());
    assertTrue(charsymtype.get().getTypeParameterList().isEmpty());

    //test some methods
    assertFalse(charsymtype.get().getMethodList().isEmpty());
    IOOSymbolsScope charspannedscope = charsymtype.get().getSpannedScope();
    Optional<MethodSymbol> valueOf = charspannedscope.resolveMethod("valueOf");
    Optional<MethodSymbol> isTitleCase = charspannedscope.resolveMethod("isTitleCase");

    assertTrue(valueOf.isPresent());
    assertTrue(isTitleCase.isPresent());

    IOOSymbolsScope valueOfSpannedScope = valueOf.get().getSpannedScope();
    Optional<FieldSymbol> c = valueOfSpannedScope.resolveField("c");
    assertTrue(c.isPresent());
    assertEquals("char",c.get().getType().print());
    assertEquals("Character",valueOf.get().getReturnType().print());
  }

  @Test
  public void testBuiltInShortWrapper(){
    Optional<OOTypeSymbol> shortsymtype = gs.resolveOOType("java.lang.Short");
    assertTrue(shortsymtype.isPresent());

    //java.lang.Short extends java.lang.Number and has no type parameters
    assertEquals(1,shortsymtype.get().getSuperTypesList().size());
    assertEquals("Number",shortsymtype.get().getSuperTypesList().get(0).print());
    assertTrue(shortsymtype.get().getTypeParameterList().isEmpty());

    //test some methods
    assertFalse(shortsymtype.get().getMethodList().isEmpty());
    IOOSymbolsScope shortspannedscope = shortsymtype.get().getSpannedScope();
    Optional<MethodSymbol> parseShort = shortspannedscope.resolveMethod("parseShort");
    Optional<MethodSymbol> valueOf = shortspannedscope.resolveMethod("valueOf");

    assertTrue(parseShort.isPresent());
    assertTrue(valueOf.isPresent());

    //test one method and its parameters specifically
    IOOSymbolsScope parseShortSpannedScope = parseShort.get().getSpannedScope();
    Optional<FieldSymbol> s = parseShortSpannedScope.resolveField("s");

    assertTrue(s.isPresent());
    assertEquals("String",s.get().getType().print());
    assertEquals("short",parseShort.get().getReturnType().print());
  }

  @Test
  public void testBuiltInByteWrapper(){
    Optional<OOTypeSymbol> bytesymtype = gs.resolveOOType("java.lang.Byte");
    assertTrue(bytesymtype.isPresent());

    //java.lang.Byte extends java.lang.Number and has no type parameters
    assertEquals(1,bytesymtype.get().getSuperTypesList().size());
    assertEquals("Number",bytesymtype.get().getSuperTypesList().get(0).print());
    assertTrue(bytesymtype.get().getTypeParameterList().isEmpty());

    //test some methods
    assertFalse(bytesymtype.get().getMethodList().isEmpty());
    IOOSymbolsScope bytespannedscope = bytesymtype.get().getSpannedScope();
    Optional<MethodSymbol> parseByte = bytespannedscope.resolveMethod("parseByte");
    Optional<MethodSymbol> valueOf = bytespannedscope.resolveMethod("valueOf");

    assertTrue(parseByte.isPresent());
    assertTrue(valueOf.isPresent());

    //test one method and its parameters specifically
    IOOSymbolsScope parseByteSpannedScope = parseByte.get().getSpannedScope();
    Optional<FieldSymbol> s = parseByteSpannedScope.resolveField("s");

    assertTrue(s.isPresent());
    assertEquals("String",s.get().getType().print());
    assertEquals("byte",parseByte.get().getReturnType().print());
  }

  @Test
  public void testBuiltInBooleanWrapper(){
    Optional<OOTypeSymbol> booleansymtype = gs.resolveOOType("java.lang.Boolean");
    assertTrue(booleansymtype.isPresent());

    //java.lang.Character directly extends java.lang.Object
    assertEquals(1,booleansymtype.get().getSuperTypesList().size());
    assertEquals("Object",booleansymtype.get().getSuperTypesList().get(0).print());
    assertTrue(booleansymtype.get().getTypeParameterList().isEmpty());

    //test some methods
    assertFalse(booleansymtype.get().getMethodList().isEmpty());
    IOOSymbolsScope booleanspannedscope = booleansymtype.get().getSpannedScope();
    Optional<MethodSymbol> booleanValue = booleanspannedscope.resolveMethod("booleanValue");
    Optional<MethodSymbol> compare = booleanspannedscope.resolveMethod("compare");

    assertTrue(booleanValue.isPresent());
    assertTrue(compare.isPresent());

    //test one method and its parameters specifically
    IOOSymbolsScope compareSpannedScope = compare.get().getSpannedScope();
    Optional<FieldSymbol> x = compareSpannedScope.resolveField("x");
    Optional<FieldSymbol> y = compareSpannedScope.resolveField("y");

    assertTrue(x.isPresent());
    assertTrue(y.isPresent());
    assertEquals("boolean",x.get().getType().print());
    assertEquals("boolean",y.get().getType().print());
    assertEquals("int",compare.get().getReturnType().print());
  }

  @Test
  public void testBuiltInListType(){
    Optional<OOTypeSymbol> listsymtype = gs.resolveOOType("java.util.List");

    assertTrue(listsymtype.isPresent());

    //List extends Collection
    assertEquals(1,listsymtype.get().getSuperTypesList().size());
    assertEquals("Collection<E>",listsymtype.get().getSuperTypesList().get(0).print());
    assertEquals("E",listsymtype.get().getTypeParameterList().get(0).getName());

    //test some methods
    IOOSymbolsScope listspannedscope = listsymtype.get().getSpannedScope();
    Optional<MethodSymbol> get = listspannedscope.resolveMethod("get");
    Optional<MethodSymbol> indexOf = listspannedscope.resolveMethod("indexOf");

    assertTrue(get.isPresent());
    assertTrue(indexOf.isPresent());

    //test one method and its parameters and return type
    IOOSymbolsScope getSpannedScope = get.get().getSpannedScope();
    Optional<FieldSymbol> index = getSpannedScope.resolveField("index");
    assertTrue(index.isPresent());
    assertEquals("int",index.get().getType().print());
    assertEquals("E",get.get().getReturnType().print());
  }

  @Test
  public void testBuiltInSetType(){
    Optional<OOTypeSymbol> setsymtype = gs.resolveOOType("java.util.Set");

    assertTrue(setsymtype.isPresent());

    //Set extends Collection
    assertEquals(1,setsymtype.get().getSuperTypesList().size());
    assertEquals("Collection<E>",setsymtype.get().getSuperTypes((0)).print());
    assertEquals("E",setsymtype.get().getTypeParameterList().get(0).getName());

    //test some methods
    IOOSymbolsScope setSpannedScope = setsymtype.get().getSpannedScope();
    //add is a method of the set type and a method of its super type collection
    List<MethodSymbol> addMethods = setSpannedScope.resolveMethodMany("add");
    //hashCode is a method of the set type and a method of its transitive super type object
    List<MethodSymbol> hashCodeMethods = setSpannedScope.resolveMethodMany("hashCode");

    assertEquals(2, addMethods.size());
    assertEquals(2, hashCodeMethods.size());

    //test for one method
    for(MethodSymbol hashCode: hashCodeMethods) {
      assertEquals("int", hashCode.getReturnType().print());
      assertTrue(hashCode.getParameterList().isEmpty());
    }
  }

  @Test
  public void testBuiltInMapType(){
    Optional<OOTypeSymbol> mapsymtype = gs.resolveOOType("java.util.Map");

    assertTrue(mapsymtype.isPresent());

    assertEquals(1,mapsymtype.get().getSuperTypesList().size());
    assertEquals("Object",mapsymtype.get().getSuperTypes((0)).print());
    assertEquals("K",mapsymtype.get().getTypeParameterList().get(0).getName());
    assertEquals("V",mapsymtype.get().getTypeParameterList().get(1).getName());

    //test some methods
    IOOSymbolsScope mapSpannedScope = mapsymtype.get().getSpannedScope();
    Optional<MethodSymbol> keySet = mapSpannedScope.resolveMethod("keySet");
    Optional<MethodSymbol> values = mapSpannedScope.resolveMethod("values");

    assertTrue(keySet.isPresent());
    assertTrue(values.isPresent());

    //test for both methods parameters and return type
    assertEquals("Collection<V>",values.get().getReturnType().print());
    assertEquals("Set<K>",keySet.get().getReturnType().print());
  }

  @Test
  public void testBuiltInOptionalType(){
    Optional<OOTypeSymbol> optionalsymtype = gs.resolveOOType("java.util.Optional");

    assertTrue(optionalsymtype.isPresent());

    assertEquals(1,optionalsymtype.get().getSuperTypesList().size());
    assertEquals("Object",optionalsymtype.get().getSuperTypes((0)).print());
    assertEquals("T",optionalsymtype.get().getTypeParameterList().get(0).getName());

    //test some methods
    IOOSymbolsScope optionalSpannedScope = optionalsymtype.get().getSpannedScope();
    Optional<MethodSymbol> isPresent = optionalSpannedScope.resolveMethod("isPresent");
    Optional<MethodSymbol> ofNullable = optionalSpannedScope.resolveMethod("ofNullable");

    assertTrue(isPresent.isPresent());
    assertTrue(ofNullable.isPresent());

    //test one method and its parameters and return type
    IOOSymbolsScope ofNullableSpannedScope = ofNullable.get().getSpannedScope();
    Optional<FieldSymbol> t = ofNullableSpannedScope.resolveField("t");
    assertTrue(t.isPresent());
    assertEquals("T",t.get().getType().print());
    assertEquals("Optional<T>",ofNullable.get().getReturnType().print());
  }

  @Test
  public void testBuiltInCollectionTypes(){
    Optional<OOTypeSymbol> collectionsymtype = gs.resolveOOType("java.util.Collection");

    assertTrue(collectionsymtype.isPresent());

    //test some methods
    IOOSymbolsScope collectionSpannedScope = collectionsymtype.get().getSpannedScope();
    Optional<MethodSymbol> size = collectionSpannedScope.resolveMethod("size");
    Optional<MethodSymbol> addAll = collectionSpannedScope.resolveMethod("addAll");
    Optional<MethodSymbol> clear = collectionSpannedScope.resolveMethod("clear");

    //collection is a generic type
    assertEquals(1,collectionsymtype.get().getSuperTypesList().size());
    assertEquals("Object",collectionsymtype.get().getSuperTypes((0)).print());
    assertEquals("E",collectionsymtype.get().getTypeParameterList().get(0).getName());

    assertTrue(size.isPresent());
    assertTrue(addAll.isPresent());
    assertTrue(clear.isPresent());

    //test the generic parameter of the method addAll
    IOOSymbolsScope addAllSpannedScope = addAll.get().getSpannedScope();
    Optional<FieldSymbol> c = addAllSpannedScope.resolveField("c");
    assertTrue(c.isPresent());

    assertEquals("Collection<E>",c.get().getType().print());
  }

  @Test
  public void testBuiltInObjectType(){
    //test that there is an "Object" type
    Optional<OOTypeSymbol> objectType = gs.resolveOOType("java.lang.Object");

    assertTrue(objectType.isPresent());

    assertFalse(objectType.get().getMethodList().isEmpty());

    IOOSymbolsScope objectSpannedScope = objectType.get().getSpannedScope();

    assertNotNull(objectSpannedScope);

    //methods
    Optional<MethodSymbol> equals = objectSpannedScope.resolveMethod("equals");
    Optional<MethodSymbol> toString = objectSpannedScope.resolveMethod("toString");
    Optional<MethodSymbol> hashCode = objectSpannedScope.resolveMethod("hashCode");

    assertTrue(equals.isPresent());
    assertTrue(toString.isPresent());
    assertTrue(hashCode.isPresent());

    //parameters in methods
    Optional<FieldSymbol> obj = equals.get().getSpannedScope().resolveField("obj");

    assertTrue(obj.isPresent());
    assertEquals("Object", obj.get().getType().print());
  }

  @Test
  public void testBuiltInStringType(){
    Optional<OOTypeSymbol> stringType = gs.resolveOOType("java.lang.String");

    assertTrue(stringType.isPresent());

    //super type is Object
    assertEquals("Object",stringType.get().getSuperTypesList().get(0).print());

    IOOSymbolsScope stringSpannedScope = stringType.get().getSpannedScope();

    //test if some methods are present, some are methods from object
    List<MethodSymbol> equalsMethods = stringSpannedScope.resolveMethodMany("equals");
    List<MethodSymbol> hashCodeMethods = stringSpannedScope.resolveMethodMany("hashCode");
    Optional<MethodSymbol> length = stringSpannedScope.resolveMethod("length");
    Optional<MethodSymbol> isEmpty = stringSpannedScope.resolveMethod("isEmpty");
    Optional<MethodSymbol> charAt = stringSpannedScope.resolveMethod("charAt");
    Optional<MethodSymbol> compareTo = stringSpannedScope.resolveMethod("compareTo");
    Optional<MethodSymbol> startsWith = stringSpannedScope.resolveMethod("startsWith");
    Optional<MethodSymbol> endsWith = stringSpannedScope.resolveMethod("endsWith");
    Optional<MethodSymbol> indexOf = stringSpannedScope.resolveMethod("indexOf");
    Optional<MethodSymbol> substring = stringSpannedScope.resolveMethod("substring");
    Optional<MethodSymbol> concat = stringSpannedScope.resolveMethod("concat");
    Optional<MethodSymbol> replace = stringSpannedScope.resolveMethod("replace");
    Optional<MethodSymbol> contains = stringSpannedScope.resolveMethod("contains");
    Optional<MethodSymbol> toLowerCase = stringSpannedScope.resolveMethod("toLowerCase");
    Optional<MethodSymbol> toUpperCase = stringSpannedScope.resolveMethod("toUpperCase");
    Optional<MethodSymbol> valueOf = stringSpannedScope.resolveMethod("valueOf");
    Optional<MethodSymbol> matches = stringSpannedScope.resolveMethod("matches");

    assertEquals(2, equalsMethods.size());
    assertEquals(2, hashCodeMethods.size());
    assertTrue(length.isPresent());
    assertTrue(isEmpty.isPresent());
    assertTrue(charAt.isPresent());
    assertTrue(compareTo.isPresent());
    assertTrue(startsWith.isPresent());
    assertTrue(endsWith.isPresent());
    assertTrue(indexOf.isPresent());
    assertTrue(substring.isPresent());
    assertTrue(concat.isPresent());
    assertTrue(replace.isPresent());
    assertTrue(contains.isPresent());
    assertTrue(toLowerCase.isPresent());
    assertTrue(toUpperCase.isPresent());
    assertTrue(valueOf.isPresent());
    assertTrue(matches.isPresent());
  }

  @Test
  public void testBuiltInNullAndVoidType(){
    //assert that the types null and void can be resolved
    Optional<OOTypeSymbol> nulltype = gs.resolveOOType("nullType");
    Optional<OOTypeSymbol> voidtype = gs.resolveOOType("voidType");

    assertTrue(nulltype.isPresent());
    assertTrue(voidtype.isPresent());
  }


}
