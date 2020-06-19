/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types.typesymbols._symboltable;

import com.google.common.collect.Lists;
import de.monticore.antlr4.MCConcreteParser;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.basictypesymbols._symboltable.TypeVarSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.typesymbols.TypeSymbolsMill;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static de.monticore.types.check.DefsTypeBasic.*;

/**
 * This resolving delegate can be integrated into any global scopes to find built in Java types such as,
 * e.g., "boolean" or commonly used Java types such as "java.lang.Boolean".
 */
public class BuiltInJavaTypeSymbolResolvingDelegate implements IOOTypeSymbolResolvingDelegate {

  protected static TypeSymbolsGlobalScope gs = initScope();

  protected static TypeSymbolsGlobalScope initScope() {
    gs = new TypeSymbolsGlobalScope(new ModelPath(), "ts");
    //package java.lang
    TypeSymbolsArtifactScope javalang = new TypeSymbolsArtifactScope("java.lang",
        new ArrayList<>());
    gs.addSubScope(javalang);
    //package java.util
    TypeSymbolsArtifactScope javautil = new TypeSymbolsArtifactScope("java.util",
        new ArrayList<>());
    gs.addSubScope(javautil);

    //some SymTypeExpressions to use for methods and fields

    //java.lang
    final SymTypeExpression objectSymType = SymTypeExpressionFactory.createTypeObject(new OOTypeSymbolLoader("Object",javalang));
    final SymTypeExpression intWrapperSymType = SymTypeExpressionFactory.createTypeObject(new OOTypeSymbolLoader("Integer",javalang));
    final SymTypeExpression doubleWrapperSymType = SymTypeExpressionFactory.createTypeObject(new OOTypeSymbolLoader("Double",javalang));
    final SymTypeExpression floatWrapperSymType = SymTypeExpressionFactory.createTypeObject(new OOTypeSymbolLoader("Float",javalang));
    final SymTypeExpression longWrapperSymType = SymTypeExpressionFactory.createTypeObject(new OOTypeSymbolLoader("Long",javalang));
    final SymTypeExpression charWrapperSymType = SymTypeExpressionFactory.createTypeObject(new OOTypeSymbolLoader("Character",javalang));
    final SymTypeExpression byteWrapperSymType = SymTypeExpressionFactory.createTypeObject(new OOTypeSymbolLoader("Byte",javalang));
    final SymTypeExpression shortWrapperSymType = SymTypeExpressionFactory.createTypeObject(new OOTypeSymbolLoader("Short",javalang));
    final SymTypeExpression booleanWrapperSymType = SymTypeExpressionFactory.createTypeObject(new OOTypeSymbolLoader("Boolean",javalang));
    final SymTypeExpression stringSymType = SymTypeExpressionFactory.createTypeObject(new OOTypeSymbolLoader("String",javalang));
    final SymTypeExpression numberSymType = SymTypeExpressionFactory.createTypeObject(new OOTypeSymbolLoader("Number",javalang));


    //java.util
    //TypeSymbolLoader for the Generics -> enclosingScopes have to be set later on when the type symbol is created
    OOTypeSymbolLoader eVarSymbolCollectionLoader = new OOTypeSymbolLoader("E",gs);
    OOTypeSymbolLoader eVarSymbolListLoader = new OOTypeSymbolLoader("E",gs);
    OOTypeSymbolLoader eVarSymbolSetLoader = new OOTypeSymbolLoader("E",gs);
    OOTypeSymbolLoader tVarSymbolOptionalLoader = new OOTypeSymbolLoader("T",gs);
    OOTypeSymbolLoader kVarSymbolMapLoader = new OOTypeSymbolLoader("K",gs);
    OOTypeSymbolLoader vVarSymbolMapLoader = new OOTypeSymbolLoader("V",gs);

    SymTypeExpression optionalSymType = SymTypeExpressionFactory.createGenerics(new OOTypeSymbolLoader("Optional",javautil),SymTypeExpressionFactory.createTypeVariable(tVarSymbolOptionalLoader));
    SymTypeExpression collectionSymType = SymTypeExpressionFactory.createGenerics(new OOTypeSymbolLoader("Collection",javautil),SymTypeExpressionFactory.createTypeVariable(eVarSymbolCollectionLoader));

    //primitives
    final SymTypeExpression intSymType = SymTypeExpressionFactory.createTypeConstant("int");
    final SymTypeExpression doubleSymType = SymTypeExpressionFactory.createTypeConstant("double");
    final SymTypeExpression floatSymType = SymTypeExpressionFactory.createTypeConstant("float");
    final SymTypeExpression longSymType = SymTypeExpressionFactory.createTypeConstant("long");
    final SymTypeExpression charSymType = SymTypeExpressionFactory.createTypeConstant("char");
    final SymTypeExpression shortSymType = SymTypeExpressionFactory.createTypeConstant("short");
    final SymTypeExpression byteSymType = SymTypeExpressionFactory.createTypeConstant("byte");
    final SymTypeExpression booleanSymType = SymTypeExpressionFactory.createTypeConstant("boolean");

    //other
    final SymTypeExpression voidSymType = SymTypeExpressionFactory.createTypeVoid();



    //class Object
    //methods
    MethodSymbol hashCode = methodSymbol("hashCode", intSymType);
    MethodSymbol equals = addFieldToMethod(methodSymbol("equals",booleanSymType),
        field("obj",objectSymType));
    MethodSymbol toString = methodSymbol("toString",stringSymType);
    OOTypeSymbol object = typeSymbol("Object",Lists.newArrayList(hashCode,equals,toString),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),javalang);
    //add to scope
    javalang.add(object);

    //Number
    //methods
    MethodSymbol intValue = methodSymbol("intValue",intSymType);
    MethodSymbol longValue = methodSymbol("longValue",longSymType);
    MethodSymbol floatValue = methodSymbol("floatValue",floatSymType);
    MethodSymbol doubleValue = methodSymbol("doubleValue",doubleSymType);
    MethodSymbol byteValue = methodSymbol("byteValue",byteSymType);
    MethodSymbol shortValue = methodSymbol("shortValue",shortSymType);
    OOTypeSymbol number = typeSymbol("Number",Lists.newArrayList(intValue,longValue,floatValue,doubleValue,byteValue,shortValue),Lists.newArrayList(),Lists.newArrayList(objectSymType),Lists.newArrayList(),javalang);

    //add to scope
    javalang.add(number);


    //primitive types
    //String
    //methods
    MethodSymbol length = methodSymbol("length",intSymType);
    MethodSymbol isEmpty = methodSymbol("isEmpty", booleanSymType);
    MethodSymbol charAt = addFieldToMethod(methodSymbol("charAt",charSymType),
        field("index",intSymType));
    MethodSymbol compareTo = addFieldToMethod(methodSymbol("compareTo",intSymType),
        field("anotherString",stringSymType));
    MethodSymbol startsWith = addFieldToMethod(methodSymbol("startsWith",booleanSymType),
        field("prefix",stringSymType));
    MethodSymbol endsWith = addFieldToMethod(methodSymbol("endsWith",booleanSymType),
        field("suffix",stringSymType));
    MethodSymbol indexOf = addFieldToMethod(methodSymbol("indexOf",intSymType),
        field("ch",intSymType));
    MethodSymbol substring = addFieldToMethod(addFieldToMethod(methodSymbol("substring",stringSymType),field("beginIndex",intSymType)),
        field("endIndex",intSymType));
    MethodSymbol concat = addFieldToMethod(methodSymbol("concat",stringSymType),field("str",stringSymType));
    MethodSymbol replace = addFieldToMethod(addFieldToMethod(methodSymbol("replace",stringSymType),
        field("oldChar",charSymType)),
        field("newChar",charSymType));
    MethodSymbol contains = addFieldToMethod(methodSymbol("contains",booleanSymType),
        field("s",stringSymType));
    MethodSymbol toLowerCase = methodSymbol("toLowerCase",stringSymType);
    MethodSymbol toUpperCase = methodSymbol("toUpperCase",stringSymType);
    MethodSymbol valueOf = addFieldToMethod(methodSymbol("valueOf",stringSymType),field("obj",objectSymType));
    MethodSymbol matches = addFieldToMethod(methodSymbol("matches",booleanSymType),
        field("regex",stringSymType));

    OOTypeSymbol string = typeSymbol("String",Lists.newArrayList(equals.deepClone(),hashCode.deepClone(),length,isEmpty,charAt,compareTo,startsWith,endsWith,indexOf,substring,concat,replace,contains,toLowerCase,toUpperCase,valueOf,matches),Lists.newArrayList(),Lists.newArrayList(objectSymType),Lists.newArrayList(),javalang);

    //add to scope
    javalang.add(string);

    //Boolean
    MethodSymbol booleanValue = methodSymbol("booleanValue",booleanSymType);
    MethodSymbol boolValueOf = addFieldToMethod(methodSymbol("valueOf",booleanWrapperSymType),field("b",
        booleanSymType));
    MethodSymbol compare = addFieldToMethod(addFieldToMethod(methodSymbol("compare",intSymType),
        field("x",booleanSymType)),
        field("y",booleanSymType));
    OOTypeSymbol bool = typeSymbol("Boolean",Lists.newArrayList(booleanValue,boolValueOf,compare,toString.deepClone(),hashCode.deepClone(),equals.deepClone()),Lists.newArrayList(),Lists.newArrayList(objectSymType),Lists.newArrayList(),javalang);

    javalang.add(bool);
    gs.add(typeSymbol("boolean",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),gs));

    //Integer
    MethodSymbol parseInt = addFieldToMethod(methodSymbol("parseInt",intSymType),
        field("s",stringSymType));
    MethodSymbol intValueOf = addFieldToMethod(methodSymbol("valueOf",intWrapperSymType),field("s",stringSymType));
    MethodSymbol sum = addFieldToMethod(addFieldToMethod(methodSymbol("sum",intSymType),
        field("a",intSymType)),
        field("b",intSymType));
    OOTypeSymbol integer = typeSymbol("Integer",Lists.newArrayList(parseInt,intValueOf,sum,equals.deepClone(),hashCode.deepClone(),toString.deepClone()),Lists.newArrayList(),Lists.newArrayList(numberSymType),Lists.newArrayList(),javalang);

    javalang.add(integer);
    gs.add(typeSymbol("int",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),gs));

    //Float
    MethodSymbol floatValueOf = addFieldToMethod(methodSymbol("valueOf",floatWrapperSymType),field("s",stringSymType));
    MethodSymbol parseFloat = addFieldToMethod(methodSymbol("parseFloat",floatSymType),
        field("s",stringSymType));
    MethodSymbol isInfinite = methodSymbol("isInfinite",booleanSymType);
    MethodSymbol sumFloat = addFieldToMethod(addFieldToMethod(methodSymbol("sum",floatSymType),
        field("a",floatSymType)),
        field("b",floatSymType));
    OOTypeSymbol floatType = typeSymbol("Float",Lists.newArrayList(floatValueOf,parseFloat,isInfinite,sumFloat,equals.deepClone(),hashCode.deepClone(),toString.deepClone()),Lists.newArrayList(),Lists.newArrayList(numberSymType),Lists.newArrayList(),javalang);

    javalang.add(floatType);
    gs.add(typeSymbol("float",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),gs));

    //Double
    MethodSymbol doubleValueOf = addFieldToMethod(methodSymbol("valueOf",doubleWrapperSymType),field("s",stringSymType));
    MethodSymbol parseDouble = addFieldToMethod(methodSymbol("parseDouble",doubleSymType),
        field("s",stringSymType));
    MethodSymbol sumDouble = addFieldToMethod(addFieldToMethod(methodSymbol("sum",doubleSymType),
        field("a",doubleSymType)),
        field("b",doubleSymType));
    OOTypeSymbol doubleType = typeSymbol("Double",Lists.newArrayList(doubleValueOf,parseDouble,sumDouble,isInfinite,equals.deepClone(),hashCode.deepClone(),toString.deepClone()),Lists.newArrayList(),Lists.newArrayList(numberSymType),Lists.newArrayList(),javalang);

    javalang.add(doubleType);
    gs.add(typeSymbol("double",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),gs));

    //Long
    MethodSymbol parseLong = addFieldToMethod(methodSymbol("parseLong",longSymType),
        field("s",stringSymType));
    MethodSymbol longValueOf = addFieldToMethod(methodSymbol("valueOf",longWrapperSymType),field("s",stringSymType));
    MethodSymbol sumLong = addFieldToMethod(addFieldToMethod(methodSymbol("sum",longSymType),
        field("a",longSymType)),
        field("b",longSymType));
    OOTypeSymbol longType = typeSymbol("Long",Lists.newArrayList(parseLong,longValueOf,sumLong,equals.deepClone(),hashCode.deepClone(),toString.deepClone()),Lists.newArrayList(),Lists.newArrayList(numberSymType),Lists.newArrayList(),javalang);

    javalang.add(longType);
    gs.add(typeSymbol("long",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),gs));

    //Byte
    MethodSymbol parseByte = addFieldToMethod(methodSymbol("parseByte",byteSymType),
        field("s",stringSymType));
    MethodSymbol byteValueOf = addFieldToMethod(methodSymbol("valueOf",byteWrapperSymType),field("s",stringSymType));
    OOTypeSymbol byteType = typeSymbol("Byte",Lists.newArrayList(parseByte,byteValueOf,equals.deepClone(),hashCode.deepClone(),toString.deepClone()),Lists.newArrayList(),Lists.newArrayList(numberSymType),Lists.newArrayList(),javalang);

    javalang.add(byteType);
    gs.add(typeSymbol("byte",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),gs));

    //Short
    MethodSymbol parseShort = addFieldToMethod(methodSymbol("parseShort",shortSymType),
        field("s",stringSymType));
    MethodSymbol shortValueOf = addFieldToMethod(methodSymbol("valueOf",shortWrapperSymType),field("s",stringSymType));
    OOTypeSymbol shortType = typeSymbol("Short",Lists.newArrayList(parseShort,shortValueOf,equals.deepClone(),hashCode.deepClone(),toString.deepClone()),Lists.newArrayList(),Lists.newArrayList(numberSymType),Lists.newArrayList(),javalang);

    javalang.add(shortType);
    gs.add(typeSymbol("short",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),gs));

    //char/Character
    MethodSymbol characterValueOf = addFieldToMethod(methodSymbol("valueOf",charWrapperSymType),field("c",charSymType));
    MethodSymbol isTitleCase = addFieldToMethod(methodSymbol("isTitleCase",booleanSymType),field("ch",charSymType));
    OOTypeSymbol character = typeSymbol("Character",Lists.newArrayList(characterValueOf,isTitleCase,equals.deepClone(),hashCode.deepClone(),toString.deepClone()),Lists.newArrayList(),Lists.newArrayList(objectSymType),Lists.newArrayList(),javalang);

    javalang.add(character);
    gs.add(typeSymbol("char",Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),Lists.newArrayList(),gs));

    //Collection types

    //Collection
    MethodSymbol size = methodSymbol("size",intSymType);
    MethodSymbol containsList = addFieldToMethod(methodSymbol("contains",booleanSymType),field("o",objectSymType));
    MethodSymbol add = addFieldToMethod(methodSymbol("add",booleanSymType),field("e",SymTypeExpressionFactory.createTypeVariable(eVarSymbolCollectionLoader)));
    MethodSymbol remove = addFieldToMethod(methodSymbol("remove",booleanSymType),field("o",objectSymType));
    MethodSymbol containsAll = addFieldToMethod(methodSymbol("containsAll",booleanSymType),field("c",collectionSymType));
    MethodSymbol addAll = addFieldToMethod(methodSymbol("addAll",booleanSymType),field("c",collectionSymType));
    MethodSymbol removeAll = addFieldToMethod(methodSymbol("removeAll",booleanSymType),field("c",collectionSymType));
    MethodSymbol retainAll = addFieldToMethod(methodSymbol("retainAll",booleanSymType),field("c",collectionSymType));
    MethodSymbol clear = methodSymbol("clear",voidSymType);

    OOTypeSymbol collection = typeSymbol("Collection",Lists.newArrayList(isEmpty.deepClone(),containsList,size,add,remove,containsAll,addAll,removeAll,retainAll,clear,equals.deepClone(),hashCode.deepClone()),Lists.newArrayList(),Lists.newArrayList(objectSymType),Lists.newArrayList(typeVariable("E")),javautil);
    eVarSymbolCollectionLoader.setEnclosingScope(collection.getSpannedScope());
    javautil.add(collection);

    //List
    SymTypeExpression eListSymType = SymTypeExpressionFactory.createTypeVariable(eVarSymbolListLoader);
    SymTypeExpression collectionSymTypeForList = SymTypeExpressionFactory.createGenerics("Collection",javautil,eListSymType);
    MethodSymbol addList = addFieldToMethod(methodSymbol("add",booleanSymType),field("e",eListSymType));
    MethodSymbol containsAllList = addFieldToMethod(methodSymbol("containsAll",booleanSymType),field("c",collectionSymTypeForList));
    MethodSymbol addAllList = addFieldToMethod(methodSymbol("addAll",booleanSymType),field("c",collectionSymTypeForList));
    MethodSymbol removeAllList = addFieldToMethod(methodSymbol("removeAll",booleanSymType),field("c",collectionSymTypeForList));
    MethodSymbol retainAllList = addFieldToMethod(methodSymbol("retainAll",booleanSymType),field("c",collectionSymTypeForList));
    MethodSymbol get = addFieldToMethod(methodSymbol("get",eListSymType),field("index",intSymType));
    MethodSymbol setList = addFieldToMethod(addFieldToMethod(methodSymbol("set",eListSymType),field("index",intSymType)),field("element",eListSymType));
    MethodSymbol addList2 = addFieldToMethod(addFieldToMethod(methodSymbol("add",voidSymType),field("index",intSymType)),field("element",eListSymType));
    MethodSymbol removeList2 = addFieldToMethod(methodSymbol("remove",eListSymType),field("index",intSymType));
    MethodSymbol indexOfList = addFieldToMethod(methodSymbol("indexOf",intSymType),field("o",objectSymType));

    OOTypeSymbol list = typeSymbol("List",Lists.newArrayList(size.deepClone(),isEmpty.deepClone(),containsList.deepClone(),clear.deepClone(),remove.deepClone(),addList,containsAllList,addAllList,removeAllList,retainAllList,get,setList,removeList2,indexOfList,addList2),Lists.newArrayList(),Lists.newArrayList(collectionSymTypeForList),Lists.newArrayList(typeVariable("E")),javautil);
    eVarSymbolListLoader.setEnclosingScope(list.getSpannedScope());
    javautil.add(list);

    //Optional
    SymTypeExpression tOptionalSymType = SymTypeExpressionFactory.createTypeVariable(tVarSymbolOptionalLoader);
    MethodSymbol empty = methodSymbol("empty",optionalSymType);
    MethodSymbol of = addFieldToMethod(methodSymbol("of",optionalSymType),field("t",tOptionalSymType));
    MethodSymbol ofNullable = addFieldToMethod(methodSymbol("ofNullable",optionalSymType),field("t",tOptionalSymType));
    MethodSymbol getOptional = methodSymbol("get",tOptionalSymType);
    MethodSymbol isPresent = methodSymbol("isPresent",booleanSymType);

    OOTypeSymbol optional = typeSymbol("Optional",Lists.newArrayList(equals.deepClone(),hashCode.deepClone(),toString.deepClone(),empty,of,ofNullable,getOptional,isPresent),Lists.newArrayList(),Lists.newArrayList(objectSymType),Lists.newArrayList(typeVariable("T")),javautil);
    tVarSymbolOptionalLoader.setEnclosingScope(optional.getSpannedScope());
    javautil.add(optional);

    //Map
    SymTypeExpression kMapSymType = SymTypeExpressionFactory.createTypeVariable(kVarSymbolMapLoader);
    SymTypeExpression vMapSymType = SymTypeExpressionFactory.createTypeVariable(vVarSymbolMapLoader);
    MethodSymbol containsKey = addFieldToMethod(methodSymbol("containsKey",booleanSymType),field("key",objectSymType));
    MethodSymbol containsValue = addFieldToMethod(methodSymbol("containsValue",booleanSymType),field("value",objectSymType));
    MethodSymbol getMap = addFieldToMethod(methodSymbol("get",vMapSymType),field("key",objectSymType));
    MethodSymbol put = addFieldToMethod(addFieldToMethod(methodSymbol("put",vMapSymType),field("key",kMapSymType)),field("value",vMapSymType));
    MethodSymbol removeMap = addFieldToMethod(methodSymbol("remove",vMapSymType),field("key",objectSymType));
    MethodSymbol keySet = methodSymbol("keySet",SymTypeExpressionFactory.createGenerics("Set",javautil,kMapSymType));
    MethodSymbol values = methodSymbol("values",SymTypeExpressionFactory.createGenerics("Collection",javautil,vMapSymType));
    MethodSymbol replaceMap = addFieldToMethod(addFieldToMethod(methodSymbol("replace",vMapSymType),field("key",kMapSymType)),field("value",vMapSymType));

    OOTypeSymbol map = typeSymbol("Map",Lists.newArrayList(size.deepClone(),isEmpty.deepClone(),containsKey,containsValue,getMap,put,removeMap,keySet,values,replaceMap),Lists.newArrayList(),Lists.newArrayList(objectSymType),Lists.newArrayList(typeVariable("K"),typeVariable("V")),javautil);
    kVarSymbolMapLoader.setEnclosingScope(map.getSpannedScope());
    vVarSymbolMapLoader.setEnclosingScope(map.getSpannedScope());
    javautil.add(map);

    //Set
    SymTypeExpression eSetSymType = SymTypeExpressionFactory.createTypeVariable(eVarSymbolSetLoader);
    SymTypeExpression collectionForSet = SymTypeExpressionFactory.createGenerics("Collection",javautil,eSetSymType);
    MethodSymbol addSet = addFieldToMethod(methodSymbol("add",booleanSymType),field("element",eSetSymType));
    MethodSymbol addAllSet = addFieldToMethod(methodSymbol("addAll",booleanSymType),field("c",collectionForSet));
    MethodSymbol retainAllSet = addFieldToMethod(methodSymbol("retainAll",booleanSymType),field("c",collectionForSet));
    MethodSymbol removeAllSet = addFieldToMethod(methodSymbol("removeAll",booleanSymType),field("c",collectionForSet));

    OOTypeSymbol set = typeSymbol("Set",Lists.newArrayList(size.deepClone(),isEmpty.deepClone(),containsList.deepClone(),addSet,addAllSet,retainAllSet,removeAllSet,equals.deepClone(),hashCode.deepClone()),Lists.newArrayList(),Lists.newArrayList(collectionForSet),Lists.newArrayList(typeVariable("E")),javautil);
    eVarSymbolSetLoader.setEnclosingScope(set.getSpannedScope());
    javautil.add(set);

    //TODO complete me with other built in types

    gs.add(new OOTypeSymbol(_nullTypeString));
    gs.add(new OOTypeSymbol(_voidTypeString));
    return gs;
  }

  @Override public List<OOTypeSymbol> resolveAdaptedOOTypeSymbol(boolean foundSymbols,
                                                                 String symbolName, AccessModifier modifier, Predicate<OOTypeSymbol> predicate) {
    return gs.resolveOOTypeMany(foundSymbols, symbolName, modifier, predicate);
  }

  public static TypeSymbolsScope getScope(){
    return gs;
  }

  public static MethodSymbol methodSymbol(String name, SymTypeExpression returnType){
    MethodSymbol m = TypeSymbolsMill.methodSymbolBuilder()
        .setSpannedScope(TypeSymbolsMill.typeSymbolsScopeBuilder().build())
        .setName(name)
        .setFullName(name)  // can later be adapted, when fullname of Type is known
        .setAccessModifier(AccessModifier.ALL_INCLUSION)
        .setReturnType(returnType)
        .build();
    m.setSpannedScope(TypeSymbolsMill.typeSymbolsScopeBuilder().build());
    return m;
  }

  public static OOTypeSymbol typeSymbol(String name, List<MethodSymbol> methodList, List<FieldSymbol> fieldList, List<SymTypeExpression> superTypeList, List<TypeVarSymbol> typeVariableList, ITypeSymbolsScope enclosingScope){
    OOTypeSymbol t = TypeSymbolsMill.oOTypeSymbolBuilder()
        .setEnclosingScope(enclosingScope)
        .setSpannedScope(TypeSymbolsMill.typeSymbolsScopeBuilder().build())
        .setName(name)
        .setFullName(name)
        .setTypeParameterList(typeVariableList)
        .setSuperTypeList(superTypeList)
        .setMethodList(methodList)
        .setFieldList(fieldList)
        .build();

    t.getSpannedScope().setEnclosingScope(enclosingScope);

    for(MethodSymbol method: t.getMethodList()){
      method.getSpannedScope().setEnclosingScope(t.getSpannedScope());
    }
    return t;
  }

  public static MethodSymbol addFieldToMethod(MethodSymbol m, FieldSymbol f){
    f.setIsParameter(true);
    m.getSpannedScope().add(f);
    f.setEnclosingScope(m.getSpannedScope());
    return m;
  }

}
