package mc.builtInTypes;

import de.monticore.antlr4.MCConcreteParser;
import de.monticore.io.paths.ModelPath;
import de.monticore.types.typesymbols._symboltable.*;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class BuiltInJavaTypesTest {

  private static TypeSymbolsGlobalScope gs;

  @BeforeClass
  public static void setup(){
    LogStub.init();
    gs = TypeSymbolsSymTabMill
        .typeSymbolsGlobalScopeBuilder()
        .setTypeSymbolsLanguage(new TypeSymbolsLanguage("TypeSymbols","ts") {
          @Override
          public MCConcreteParser getParser() {
            return null;
          }
        })
        .setModelPath(new ModelPath())
        .build();
    gs.addAdaptedTypeSymbolResolvingDelegate(new BuiltInJavaTypeSymbolResolvingDelegate());


     //other way to get globalscope: gs = BuiltInJavaTypeSymbolResolvingDelegate.getScope();

  }

  @Test
  public void testBuiltInPrimitiveJavaTypes(){
    //assert that the primitive types can be resolved in the scope
    Optional<TypeSymbol> intsymtype = gs.resolveType("int");
    Optional<TypeSymbol> doublesymtype = gs.resolveType("double");
    Optional<TypeSymbol> floatsymtype = gs.resolveType("float");
    Optional<TypeSymbol> longsymtype = gs.resolveType("long");
    Optional<TypeSymbol> charsymtype = gs.resolveType("char");
    Optional<TypeSymbol> shortsymtype = gs.resolveType("short");
    Optional<TypeSymbol> bytesymtype = gs.resolveType("byte");
    Optional<TypeSymbol> booleansymtype = gs.resolveType("boolean");

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
    assertTrue(intsymtype.get().getSuperTypeList().isEmpty());
    assertTrue(intsymtype.get().getTypeParameterList().isEmpty());

    assertTrue(doublesymtype.get().getMethodList().isEmpty());
    assertTrue(doublesymtype.get().getFieldList().isEmpty());
    assertTrue(doublesymtype.get().getSuperTypeList().isEmpty());
    assertTrue(doublesymtype.get().getTypeParameterList().isEmpty());

    assertTrue(floatsymtype.get().getMethodList().isEmpty());
    assertTrue(floatsymtype.get().getFieldList().isEmpty());
    assertTrue(floatsymtype.get().getSuperTypeList().isEmpty());
    assertTrue(floatsymtype.get().getTypeParameterList().isEmpty());

    assertTrue(longsymtype.get().getMethodList().isEmpty());
    assertTrue(longsymtype.get().getFieldList().isEmpty());
    assertTrue(longsymtype.get().getSuperTypeList().isEmpty());
    assertTrue(longsymtype.get().getTypeParameterList().isEmpty());

    assertTrue(charsymtype.get().getMethodList().isEmpty());
    assertTrue(charsymtype.get().getFieldList().isEmpty());
    assertTrue(charsymtype.get().getSuperTypeList().isEmpty());
    assertTrue(charsymtype.get().getTypeParameterList().isEmpty());

    assertTrue(shortsymtype.get().getMethodList().isEmpty());
    assertTrue(shortsymtype.get().getFieldList().isEmpty());
    assertTrue(shortsymtype.get().getSuperTypeList().isEmpty());
    assertTrue(shortsymtype.get().getTypeParameterList().isEmpty());

    assertTrue(bytesymtype.get().getMethodList().isEmpty());
    assertTrue(bytesymtype.get().getFieldList().isEmpty());
    assertTrue(bytesymtype.get().getSuperTypeList().isEmpty());
    assertTrue(bytesymtype.get().getTypeParameterList().isEmpty());

    assertTrue(booleansymtype.get().getMethodList().isEmpty());
    assertTrue(booleansymtype.get().getFieldList().isEmpty());
    assertTrue(booleansymtype.get().getSuperTypeList().isEmpty());
    assertTrue(booleansymtype.get().getTypeParameterList().isEmpty());
  }

  @Test
  public void BuiltInWrapperTypes(){
    Optional<TypeSymbol> intsymtype = gs.resolveType("java.lang.Integer");
    Optional<TypeSymbol> doublesymtype = gs.resolveType("java.lang.Double");
    Optional<TypeSymbol> floatsymtype = gs.resolveType("java.lang.Float");
    Optional<TypeSymbol> longsymtype = gs.resolveType("java.lang.Long");
    Optional<TypeSymbol> charsymtype = gs.resolveType("java.lang.Character");
    Optional<TypeSymbol> shortsymtype = gs.resolveType("java.lang.Short");
    Optional<TypeSymbol> bytesymtype = gs.resolveType("java.lang.Byte");
    Optional<TypeSymbol> booleansymtype = gs.resolveType("java.lang.Boolean");

    assertTrue(intsymtype.isPresent());
    assertTrue(doublesymtype.isPresent());
    assertTrue(floatsymtype.isPresent());
    assertTrue(longsymtype.isPresent());
    assertTrue(charsymtype.isPresent());
    assertTrue(shortsymtype.isPresent());
    assertTrue(bytesymtype.isPresent());
    assertTrue(booleansymtype.isPresent());

    //check that the wrapper classes do have methods and super types
    assertFalse(intsymtype.get().getMethodList().isEmpty());
    assertEquals("Number",intsymtype.get().getSuperTypeList().get(0).print());
    assertFalse(doublesymtype.get().getMethodList().isEmpty());
    assertEquals("Number",doublesymtype.get().getSuperTypeList().get(0).print());
    assertFalse(floatsymtype.get().getMethodList().isEmpty());
    assertEquals("Number",floatsymtype.get().getSuperTypeList().get(0).print());
    assertFalse(longsymtype.get().getMethodList().isEmpty());
    assertEquals("Number",longsymtype.get().getSuperTypeList().get(0).print());
    assertFalse(charsymtype.get().getMethodList().isEmpty());
    assertEquals("Object",charsymtype.get().getSuperTypeList().get(0).print());
    assertFalse(shortsymtype.get().getMethodList().isEmpty());
    assertEquals("Number",shortsymtype.get().getSuperTypeList().get(0).print());
    assertFalse(bytesymtype.get().getMethodList().isEmpty());
    assertEquals("Number",bytesymtype.get().getSuperTypeList().get(0).print());
    assertFalse(booleansymtype.get().getMethodList().isEmpty());
    assertEquals("Object",booleansymtype.get().getSuperTypeList().get(0).print());
  }

  @Test
  public void testBuiltInListType(){
    Optional<TypeSymbol> listsymtype = gs.resolveType("java.util.List");

    assertTrue(listsymtype.isPresent());

    //List extends Collection
    assertEquals(1,listsymtype.get().getSuperTypeList().size());
    assertEquals("Collection<E>",listsymtype.get().getSuperTypeList().get(0).print());
    assertEquals("E",listsymtype.get().getTypeParameterList().get(0).getName());

    //test some methods
    ITypeSymbolsScope listspannedscope = listsymtype.get().getSpannedScope();
    Optional<MethodSymbol> get = listspannedscope.resolveMethod("get");
    Optional<MethodSymbol> indexOf = listspannedscope.resolveMethod("indexOf");

    assertTrue(get.isPresent());
    assertTrue(indexOf.isPresent());

    //test one method and its parameters and return type
    ITypeSymbolsScope getSpannedScope = get.get().getSpannedScope();
    Optional<FieldSymbol> index = getSpannedScope.resolveField("index");
    assertTrue(index.isPresent());
    assertEquals("int",index.get().getType().print());
    assertEquals("E",get.get().getReturnType().print());
  }

  @Test
  public void testBuiltInSetType(){
    Optional<TypeSymbol> setsymtype = gs.resolveType("java.util.Set");

    assertTrue(setsymtype.isPresent());

    //Set extends Collection
    assertEquals(1,setsymtype.get().getSuperTypeList().size());
    assertEquals("Collection<E>",setsymtype.get().getSuperType((0)).print());
    assertEquals("E",setsymtype.get().getTypeParameterList().get(0).getName());

    //test some methods
    ITypeSymbolsScope setSpannedScope = setsymtype.get().getSpannedScope();
    Optional<MethodSymbol> add = setSpannedScope.resolveMethod("add");
    Optional<MethodSymbol> hashCode = setSpannedScope.resolveMethod("hashCode");

    assertTrue(add.isPresent());
    assertTrue(hashCode.isPresent());

    //test for one method
    assertEquals("int",hashCode.get().getReturnType().print());
    assertTrue(hashCode.get().getParameterList().isEmpty());
  }

  @Test
  public void testBuiltInMapType(){
    Optional<TypeSymbol> mapsymtype = gs.resolveType("java.util.Map");

    assertTrue(mapsymtype.isPresent());

    assertEquals(1,mapsymtype.get().getSuperTypeList().size());
    assertEquals("Object",mapsymtype.get().getSuperType((0)).print());
    assertEquals("K",mapsymtype.get().getTypeParameterList().get(0).getName());
    assertEquals("V",mapsymtype.get().getTypeParameterList().get(1).getName());

    //test some methods
    ITypeSymbolsScope mapSpannedScope = mapsymtype.get().getSpannedScope();
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
    Optional<TypeSymbol> optionalsymtype = gs.resolveType("java.util.Optional");

    assertTrue(optionalsymtype.isPresent());

    assertEquals(1,optionalsymtype.get().getSuperTypeList().size());
    assertEquals("Object",optionalsymtype.get().getSuperType((0)).print());
    assertEquals("T",optionalsymtype.get().getTypeParameterList().get(0).getName());

    //test some methods
    ITypeSymbolsScope optionalSpannedScope = optionalsymtype.get().getSpannedScope();
    Optional<MethodSymbol> isPresent = optionalSpannedScope.resolveMethod("isPresent");
    Optional<MethodSymbol> ofNullable = optionalSpannedScope.resolveMethod("ofNullable");

    assertTrue(isPresent.isPresent());
    assertTrue(ofNullable.isPresent());

    //test one method and its parameters and return type
    ITypeSymbolsScope ofNullableSpannedScope = ofNullable.get().getSpannedScope();
    Optional<FieldSymbol> t = ofNullableSpannedScope.resolveField("t");
    assertTrue(t.isPresent());
    assertEquals("T",t.get().getType().print());
    assertEquals("Optional<T>",ofNullable.get().getReturnType().print());
  }

  @Test
  public void testBuiltInCollectionTypes(){
    Optional<TypeSymbol> collectionsymtype = gs.resolveType("java.util.Collection");

    assertTrue(collectionsymtype.isPresent());

    //test some methods
    ITypeSymbolsScope collectionSpannedScope = collectionsymtype.get().getSpannedScope();
    Optional<MethodSymbol> size = collectionSpannedScope.resolveMethod("size");
    Optional<MethodSymbol> addAll = collectionSpannedScope.resolveMethod("addAll");
    Optional<MethodSymbol> clear = collectionSpannedScope.resolveMethod("clear");

    //collection is a generic type
    assertEquals(1,collectionsymtype.get().getSuperTypeList().size());
    assertEquals("Object",collectionsymtype.get().getSuperType((0)).print());
    assertEquals("E",collectionsymtype.get().getTypeParameterList().get(0).getName());

    assertTrue(size.isPresent());
    assertTrue(addAll.isPresent());
    assertTrue(clear.isPresent());

    //test the generic parameter of the method addAll
    ITypeSymbolsScope addAllSpannedScope = addAll.get().getSpannedScope();
    Optional<FieldSymbol> c = addAllSpannedScope.resolveField("c");
    assertTrue(c.isPresent());

    assertEquals("Collection<E>",c.get().getType().print());
  }

  @Test
  public void testBuiltInObjectType(){
    //test that there is an "Object" type
    Optional<TypeSymbol> objectType = gs.resolveType("java.lang.Object");

    assertTrue(objectType.isPresent());

    assertFalse(objectType.get().getMethodList().isEmpty());

    ITypeSymbolsScope objectSpannedScope = objectType.get().getSpannedScope();

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
    Optional<TypeSymbol> stringType = gs.resolveType("java.lang.String");

    assertTrue(stringType.isPresent());

    //super type is Object
    assertEquals("Object",stringType.get().getSuperTypeList().get(0).print());

    ITypeSymbolsScope stringSpannedScope = stringType.get().getSpannedScope();

    //test if some methods are present
    Optional<MethodSymbol> equals = stringSpannedScope.resolveMethod("equals");
    Optional<MethodSymbol> hashCode = stringSpannedScope.resolveMethod("hashCode");
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

    assertTrue(equals.isPresent());
    assertTrue(hashCode.isPresent());
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
    Optional<TypeSymbol> nulltype = gs.resolveType("nullType");
    Optional<TypeSymbol> voidtype = gs.resolveType("voidType");

    assertTrue(nulltype.isPresent());
    assertTrue(voidtype.isPresent());
  }


}
