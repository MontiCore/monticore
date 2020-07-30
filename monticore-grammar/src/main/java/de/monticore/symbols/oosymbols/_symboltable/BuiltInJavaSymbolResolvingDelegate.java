// (c) https://github.com/MontiCore/monticore

/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symbols.oosymbols._symboltable;

import com.google.common.collect.Lists;
import de.monticore.io.paths.ModelPath;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;

import java.util.List;
import java.util.function.Predicate;

import static de.monticore.types.check.DefsTypeBasic.*;

/**
 * This resolving delegate can be integrated into any global scopes to find built in Java types such as,
 * e.g., "boolean" or commonly used Java types such as "java.lang.Boolean".
 */
public class BuiltInJavaSymbolResolvingDelegate implements IOOTypeSymbolResolvingDelegate {

  protected static IOOSymbolsGlobalScope gs = initScope();

  protected static IOOSymbolsGlobalScope initScope() {
    gs = OOSymbolsMill
        .oOSymbolsGlobalScopeBuilder()
        .setModelPath(new ModelPath())
        .setModelFileExtension("ts")
        .build();
    //package java.lang
    IOOSymbolsArtifactScope javalang = OOSymbolsMill
        .oOSymbolsArtifactScopeBuilder()
        .setPackageName("java.lang")
        .build();
    gs.addSubScope(javalang);
    //package java.util
    IOOSymbolsArtifactScope javautil = OOSymbolsMill
        .oOSymbolsArtifactScopeBuilder()
        .setPackageName("java.util")
        .build();
    gs.addSubScope(javautil);

    //some SymTypeExpressions to use for methods and fields

    //java.lang
    OOTypeSymbolSurrogate loader = new OOTypeSymbolSurrogate("Object");
    loader.setEnclosingScope(javalang);
    final SymTypeExpression objectSymType = SymTypeExpressionFactory.createTypeObject(loader);
    loader = new OOTypeSymbolSurrogate("Integer");
    loader.setEnclosingScope(javalang);
    final SymTypeExpression intWrapperSymType = SymTypeExpressionFactory.createTypeObject(loader);
    loader = new OOTypeSymbolSurrogate("Double");
    loader.setEnclosingScope(javalang);
    final SymTypeExpression doubleWrapperSymType = SymTypeExpressionFactory.createTypeObject(loader);
    loader = new OOTypeSymbolSurrogate("Float");
    loader.setEnclosingScope(javalang);
    final SymTypeExpression floatWrapperSymType = SymTypeExpressionFactory.createTypeObject(loader);
    loader = new OOTypeSymbolSurrogate("Long");
    loader.setEnclosingScope(javalang);
    final SymTypeExpression longWrapperSymType = SymTypeExpressionFactory.createTypeObject(loader);
    loader = new OOTypeSymbolSurrogate("Character");
    loader.setEnclosingScope(javalang);
    final SymTypeExpression charWrapperSymType = SymTypeExpressionFactory.createTypeObject(loader);
    loader = new OOTypeSymbolSurrogate("Byte");
    loader.setEnclosingScope(javalang);
    final SymTypeExpression byteWrapperSymType = SymTypeExpressionFactory.createTypeObject(loader);
    loader = new OOTypeSymbolSurrogate("Short");
    loader.setEnclosingScope(javalang);
    final SymTypeExpression shortWrapperSymType = SymTypeExpressionFactory.createTypeObject(loader);
    loader = new OOTypeSymbolSurrogate("Boolean");
    loader.setEnclosingScope(javalang);
    final SymTypeExpression booleanWrapperSymType = SymTypeExpressionFactory.createTypeObject(loader);
    loader = new OOTypeSymbolSurrogate("String");
    loader.setEnclosingScope(javalang);
    final SymTypeExpression stringSymType = SymTypeExpressionFactory.createTypeObject(loader);
    loader = new OOTypeSymbolSurrogate("Number");
    loader.setEnclosingScope(javalang);
    final SymTypeExpression numberSymType = SymTypeExpressionFactory.createTypeObject(loader);


    //java.util
    //TypeSymbolSurrogate for the Generics -> enclosingScopes have to be set later on when the type symbol is created
    loader = new OOTypeSymbolSurrogate("E");
    loader.setEnclosingScope(gs);
    OOTypeSymbolSurrogate eVarSymbolCollectionLoader = loader;
    OOTypeSymbolSurrogate eVarSymbolListLoader = loader;
    OOTypeSymbolSurrogate eVarSymbolSetLoader = loader;
    loader = new OOTypeSymbolSurrogate("T");
    loader.setEnclosingScope(gs);
    OOTypeSymbolSurrogate tVarSymbolOptionalLoader = loader;
    loader = new OOTypeSymbolSurrogate("K");
    loader.setEnclosingScope(gs);
    OOTypeSymbolSurrogate kVarSymbolMapLoader = loader;
    loader = new OOTypeSymbolSurrogate("V");
    loader.setEnclosingScope(gs);
    OOTypeSymbolSurrogate vVarSymbolMapLoader = loader;

    loader = new OOTypeSymbolSurrogate("Optional");
    loader.setEnclosingScope(javautil);
    SymTypeExpression optionalSymType = SymTypeExpressionFactory.createGenerics(loader,SymTypeExpressionFactory.createTypeVariable(tVarSymbolOptionalLoader));
    loader = new OOTypeSymbolSurrogate("Collection");
    loader.setEnclosingScope(javautil);
    SymTypeExpression collectionSymType = SymTypeExpressionFactory.createGenerics(loader,SymTypeExpressionFactory.createTypeVariable(eVarSymbolCollectionLoader));

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

  public static IOOSymbolsScope getScope(){
    return gs;
  }

  public static MethodSymbol methodSymbol(String name, SymTypeExpression returnType){
    MethodSymbol m = OOSymbolsMill.methodSymbolBuilder()
        .setSpannedScope(OOSymbolsMill.oOSymbolsScopeBuilder().build())
        .setName(name)
        .setFullName(name)  // can later be adapted, when fullname of Type is known
        .setAccessModifier(AccessModifier.ALL_INCLUSION)
        .setReturnType(returnType)
        .build();
    m.setSpannedScope(OOSymbolsMill.oOSymbolsScopeBuilder().build());
    return m;
  }

  public static OOTypeSymbol typeSymbol(String name, List<MethodSymbol> methodList, List<FieldSymbol> fieldList, List<SymTypeExpression> superTypeList, List<TypeVarSymbol> typeVariableList, IOOSymbolsScope enclosingScope){
    OOTypeSymbol t = OOSymbolsMill.oOTypeSymbolBuilder()
        .setEnclosingScope(enclosingScope)
        .setSpannedScope(OOSymbolsMill.oOSymbolsScopeBuilder().build())
        .setName(name)
        .setFullName(name)
        .setSuperTypesList(superTypeList).build();
    typeVariableList.forEach(v -> t.addTypeVarSymbol(v));
    methodList.forEach(m -> t.addMethodSymbol(m));
    fieldList.forEach((f -> t.addFieldSymbol(f)));
    t.getSpannedScope().setEnclosingScope(enclosingScope);

    for(MethodSymbol method: t.getMethodList()){
      method.getSpannedScope().setEnclosingScope(t.getSpannedScope());
    }
    return t;
  }

  public static MethodSymbol addFieldToMethod(MethodSymbol m, FieldSymbol f){
    m.getSpannedScope().add(f);
    f.setEnclosingScope(m.getSpannedScope());
    return m;
  }

}
