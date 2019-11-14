/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types.typesymbols._symboltable;

import com.google.common.collect.Lists;
import de.monticore.antlr4.MCConcreteParser;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.DefsTypeBasic;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static de.monticore.types.check.DefsTypeBasic.*;

/**
 * This resolving delegate can be integrated into any global scopes to find built in Java types such as,
 * e.g., "boolean" or commonly used Java types such as "java.lang.Boolean".
 */
public class BuiltInJavaTypeSymbolResolvingDelegate implements ITypeSymbolResolvingDelegate {

  protected static TypeSymbolsGlobalScope gs = initScope();

  protected static TypeSymbolsGlobalScope initScope() {
    gs = new TypeSymbolsGlobalScope(new ModelPath(),
        new TypeSymbolsLanguage("Types Symbols Language", "ts") {
          @Override public MCConcreteParser getParser() {
            Log.error("0xTODO Type Symbols do not have a parser!");
            return null;
          }
        });
    //package java.lang
    TypeSymbolsArtifactScope javalang = new TypeSymbolsArtifactScope("java.lang",
        new ArrayList<>());
    gs.addSubScope(javalang);
    //package java.util
    TypeSymbolsArtifactScope javautil = new TypeSymbolsArtifactScope("java.util",
        new ArrayList<>());
    gs.addSubScope(javautil);

    //class Object
    TypeSymbol object = type("Object");
    SymTypeExpression objectSymType = SymTypeExpressionFactory.createTypeObject(new TypeSymbolLoader("Object",javalang));
    //methods
    MethodSymbol hashCode = method("hashCode", SymTypeExpressionFactory.createTypeConstant("int"));
    MethodSymbol equals = add(method("equals",SymTypeExpressionFactory.createTypeConstant("boolean")),
        field("obj",objectSymType));
    MethodSymbol toString = method("toString",SymTypeExpressionFactory.createTypeObject("String",object.getSpannedScope()));
    object.setMethodList(Lists.newArrayList(hashCode,equals,toString));
    //add to scope
    javalang.add(object);


    //primitive types
    //String
    TypeSymbol string = type("String");
    SymTypeExpression stringSymType = SymTypeExpressionFactory.createTypeObject(new TypeSymbolLoader("String",javalang));
    //methods
    MethodSymbol length = method("length",SymTypeExpressionFactory.createTypeConstant("int"));
    MethodSymbol isEmpty = method("isEmpty", SymTypeExpressionFactory.createTypeConstant("boolean"));
    MethodSymbol charAt = add(method("charAt",SymTypeExpressionFactory.createTypeConstant("char")),
        field("index",SymTypeExpressionFactory.createTypeConstant("int")));
    MethodSymbol compareTo = add(method("compareTo",SymTypeExpressionFactory.createTypeConstant("int")),
        field("anotherString",stringSymType));
    MethodSymbol startsWith = add(method("startsWith",SymTypeExpressionFactory.createTypeConstant("boolean")),
        field("prefix",stringSymType));
    MethodSymbol endsWith = add(method("endsWith",SymTypeExpressionFactory.createTypeConstant("boolean")),
        field("suffix",stringSymType));
    MethodSymbol indexOf = add(method("indexOf",SymTypeExpressionFactory.createTypeConstant("int")),
        field("ch",SymTypeExpressionFactory.createTypeConstant("int")));
    MethodSymbol substring = add(add(method("substring",stringSymType),field("beginIndex",SymTypeExpressionFactory.createTypeConstant("int"))),
        field("endIndex",SymTypeExpressionFactory.createTypeConstant("int")));
    MethodSymbol concat = add(method("concat",stringSymType),field("str",stringSymType));
    MethodSymbol replace = add(add(method("replace",stringSymType),
        field("oldChar",SymTypeExpressionFactory.createTypeConstant("char"))),
        field("newChar",SymTypeExpressionFactory.createTypeConstant("char")));
    MethodSymbol contains = add(method("contains",SymTypeExpressionFactory.createTypeConstant("boolean")),
        field("s",stringSymType));
    MethodSymbol toLowerCase = method("toLowerCase",stringSymType);
    MethodSymbol toUpperCase = method("toUpperCase",stringSymType);
    MethodSymbol valueOf = add(method("valueOf",stringSymType),field("obj",objectSymType));
    MethodSymbol matches = add(method("matches",SymTypeExpressionFactory.createTypeConstant("boolean")),
        field("regex",stringSymType));
    string.setMethodList(Lists.newArrayList(equals,hashCode,length,isEmpty,charAt,compareTo,startsWith,
        endsWith,indexOf,substring,concat,replace,contains,toLowerCase,toUpperCase,concat,valueOf,matches));

    //add to scope
    javalang.add(string);
    gs.add(string);

    //Boolean
    TypeSymbol bool = type("Boolean");
    SymTypeExpression booleanSymType = SymTypeExpressionFactory.createTypeObject(new TypeSymbolLoader("Boolean",javalang));
    MethodSymbol booleanValue = method("booleanValue",SymTypeExpressionFactory.createTypeConstant("boolean"));
    MethodSymbol boolValueOf = add(method("valueOf",booleanSymType),field("b",
        SymTypeExpressionFactory.createTypeConstant("boolean")));
    MethodSymbol compare = add(add(method("compare",SymTypeExpressionFactory.createTypeConstant("int")),
        field("x",SymTypeExpressionFactory.createTypeConstant("boolean"))),
        field("y",SymTypeExpressionFactory.createTypeConstant("boolean")));
    bool.setMethodList(Lists.newArrayList(booleanValue,boolValueOf,compare,toString,hashCode,equals));

    javalang.add(bool);
    gs.add(new TypeSymbol("boolean"));

    //Integer
    TypeSymbol integer = type("Integer");
    SymTypeExpression integerSymType = SymTypeExpressionFactory.createTypeObject(new TypeSymbolLoader("Integer",javalang));
    MethodSymbol parseInt = add(method("parseInt",SymTypeExpressionFactory.createTypeConstant("int")),
        field("s",stringSymType));
    MethodSymbol intValueOf = add(method("valueOf",integerSymType),field("s",stringSymType));
    MethodSymbol sum = add(add(method("sum",SymTypeExpressionFactory.createTypeConstant("int")),
        field("a",SymTypeExpressionFactory.createTypeConstant("int"))),
        field("b",SymTypeExpressionFactory.createTypeConstant("int")));
    integer.setMethodList(Lists.newArrayList(parseInt,intValueOf,sum,equals,hashCode,toString));

    javalang.add(integer);
    gs.add(new TypeSymbol("int"));

    //Float
    TypeSymbol floatType = type("Float");
    SymTypeExpression floatSymType = SymTypeExpressionFactory.createTypeObject(new TypeSymbolLoader("Float",javalang));
    MethodSymbol floatValueOf = add(method("valueOf",floatSymType),field("s",stringSymType));
    MethodSymbol parseFloat = add(method("parseFloat",SymTypeExpressionFactory.createTypeConstant("float")),
        field("s",stringSymType));
    MethodSymbol isInfinite = method("isInfinite",SymTypeExpressionFactory.createTypeConstant("boolean"));
    MethodSymbol sumFloat = add(add(method("sum",SymTypeExpressionFactory.createTypeConstant("float")),
        field("a",SymTypeExpressionFactory.createTypeConstant("float"))),
        field("b",SymTypeExpressionFactory.createTypeConstant("float")));
    floatType.setMethodList(Lists.newArrayList(floatValueOf,parseFloat,isInfinite,sumFloat,equals,hashCode,toString));

    javalang.add(floatType);
    gs.add(new TypeSymbol("float"));

    //Double
    TypeSymbol doubleType = type("Double");
    SymTypeExpression doubleSymType = SymTypeExpressionFactory.createTypeObject(new TypeSymbolLoader("Double",javalang));
    MethodSymbol doubleValueOf = add(method("valueOf",doubleSymType),field("s",stringSymType));
    MethodSymbol parseDouble = add(method("parseDouble",SymTypeExpressionFactory.createTypeConstant("double")),
        field("s",stringSymType));
    MethodSymbol sumDouble = add(add(method("sum",SymTypeExpressionFactory.createTypeConstant("double")),
        field("a",SymTypeExpressionFactory.createTypeConstant("double"))),
        field("b",SymTypeExpressionFactory.createTypeConstant("double")));
    doubleType.setMethodList(Lists.newArrayList(doubleValueOf,parseDouble,isInfinite,sumDouble,equals,hashCode,toString));

    javalang.add(doubleType);
    gs.add(new TypeSymbol("double"));

    //Long
    TypeSymbol longType = type("Long");
    SymTypeExpression longSymType = SymTypeExpressionFactory.createTypeObject(new TypeSymbolLoader("Long",javalang));
    MethodSymbol parseLong = add(method("parseLong",SymTypeExpressionFactory.createTypeConstant("long")),
        field("s",stringSymType));
    MethodSymbol longValueOf = add(method("valueOf",longSymType),field("s",stringSymType));
    MethodSymbol sumLong = add(add(method("sum",SymTypeExpressionFactory.createTypeConstant("long")),
        field("a",SymTypeExpressionFactory.createTypeConstant("long"))),
        field("b",SymTypeExpressionFactory.createTypeConstant("long")));
    longType.setMethodList(Lists.newArrayList(parseLong,longValueOf,sumLong,equals,hashCode,toString));

    javalang.add(longType);
    gs.add(new TypeSymbol("long"));

    //Byte
    TypeSymbol byteType = type("Byte");
    SymTypeExpression byteSymType = SymTypeExpressionFactory.createTypeObject(new TypeSymbolLoader("Byte",javalang));
    MethodSymbol parseByte = add(method("parseByte",SymTypeExpressionFactory.createTypeConstant("byte")),
        field("s",stringSymType));
    MethodSymbol byteValueOf = add(method("valueOf",byteSymType),field("s",stringSymType));
    byteType.setMethodList(Lists.newArrayList(parseByte,byteValueOf,equals,hashCode,toString));

    javalang.add(byteType);
    gs.add(new TypeSymbol("byte"));

    //Short
    TypeSymbol shortType = type("Short");
    SymTypeExpression shortSymType = SymTypeExpressionFactory.createTypeObject(new TypeSymbolLoader("Short",javalang));
    MethodSymbol parseShort = add(method("parseShort",SymTypeExpressionFactory.createTypeConstant("short")),
        field("s",stringSymType));
    MethodSymbol shortValueOf = add(method("valueOf",shortSymType),field("s",stringSymType));
    shortType.setMethodList(Lists.newArrayList(parseShort,shortValueOf,equals,hashCode,toString));

    javalang.add(shortType);
    gs.add(new TypeSymbol("short"));

    TypeSymbol character = type("Character");
    SymTypeExpression characterSymType = SymTypeExpressionFactory.createTypeObject(new TypeSymbolLoader("Character",javalang));
    MethodSymbol characterValueOf = add(method("valueOf",characterSymType),field("s",stringSymType));
    character.setMethodList(Lists.newArrayList(characterValueOf,equals,hashCode,toString));

    javalang.add(character);
    gs.add(new TypeSymbol("char"));

    //Collection types
    javautil.add(new TypeSymbol("List"));
    javautil.add(new TypeSymbol("Optional"));
    javautil.add(new TypeSymbol("Map"));
    javautil.add(new TypeSymbol("Set"));


    //TODO complete me with other built in types
    return gs;
  }

  @Override public List<TypeSymbol> resolveAdaptedTypeSymbol(boolean foundSymbols,
      String symbolName, AccessModifier modifier, Predicate<TypeSymbol> predicate) {
    return gs.resolveTypeMany(foundSymbols, symbolName, modifier, predicate);
  }

  public static TypeSymbolsScope getScope(){
    return gs;
  }

}
