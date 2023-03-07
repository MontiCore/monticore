/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Optional;

import static de.monticore.symbols.basicsymbols.BasicSymbolsMill.PRIMITIVE_LIST;

/**
 * SymTypeExpressionFactory contains static functions that create
 * the various forms of TypeExpressions used for Sym-Types.
 * <p>
 * This factory therefore should be the only source to create SymTypeExpressions.
 * No other source is needed.
 * (That is ok, as the set of SymTypeExpressions is rather fixed and we do not expect
 * many modular extensions that would be needed. Saying this, we know that
 * potentially union types (A|B) might still be added in the future.)
 */
public class SymTypeExpressionFactory {

  /**
   * createTypeVariable vor Variables
   */
  public static SymTypeVariable createTypeVariable(String name, IBasicSymbolsScope scope) {
    TypeSymbol typeSymbol = new TypeSymbolSurrogate(name);
    typeSymbol.setEnclosingScope(scope);
    return new SymTypeVariable(typeSymbol);
  }

  public static SymTypeVariable createTypeVariable(TypeSymbol typeSymbol) {
    return new SymTypeVariable(typeSymbol);
  }

  /**
   * for primitives, such as "int" (and no other kinds).
   * TypeInfo is not needed (as the Objects are predefined singletons)
   */
  public static SymTypePrimitive createPrimitive(String name) {
    Optional<TypeSymbol> type = BasicSymbolsMill.globalScope().resolveTypeLocally(name);
    if (!type.isPresent()) {
      Log.error("0x893F62 Internal Error: Non primitive type " + name + " stored as constant.");
    }
    return createPrimitive(type.get());
  }

  public static SymTypePrimitive createPrimitive(TypeSymbol type) {
    return new SymTypePrimitive(type);
  }

  /**
   * for ObjectTypes, as e.g. "Person"
   */
  public static SymTypeOfObject createTypeObject(TypeSymbol typeSymbol) {
    return new SymTypeOfObject(typeSymbol);
  }

  /**
   * for ObjectTypes, as e.g. "Person"
   */
  public static SymTypeOfObject createTypeObject(String name, IBasicSymbolsScope enclosingScope) {
    TypeSymbol typeSymbol = new TypeSymbolSurrogate(name);
    typeSymbol.setEnclosingScope(enclosingScope);
    return new SymTypeOfObject(typeSymbol);
  }

  /**
   * creates the "Void"-type, i.e. a pseudotype that represents the absence of a real type
   *
   * @return
   */
  public static SymTypeVoid createTypeVoid() {
    return new SymTypeVoid();
  }

  public static SymTypeObscure createObscureType() {
    return new SymTypeObscure();
  }

  /**
   * That is the pseudo-type of "null"
   */
  public static SymTypeOfNull createTypeOfNull() {
    return new SymTypeOfNull();
  }

  /**
   * creates an array-Type Expression
   *
   * @param typeSymbol
   * @param dim        the dimension of the array
   * @param argument   the argument type (of the elements)
   * @return
   */
  public static SymTypeArray createTypeArray(TypeSymbol typeSymbol, int dim,
                                             SymTypeExpression argument) {
    return new SymTypeArray(typeSymbol, dim, argument);
  }

  public static SymTypeArray createTypeArray(String name, IBasicSymbolsScope typeSymbolsScope,
                                             int dim, SymTypeExpression argument) {
    TypeSymbol typeSymbol = new TypeSymbolSurrogate(name);
    typeSymbol.setEnclosingScope(typeSymbolsScope);
    return new SymTypeArray(typeSymbol, dim, argument);
  }

  public static SymTypeExpression createTypeExpression(TypeSymbol typeSymbol) {
    SymTypeExpression o;
    if(PRIMITIVE_LIST.contains(typeSymbol.getName())){
      o = createPrimitive(typeSymbol.getName());
    } else if ("void".equals(typeSymbol.getName())) {
      o = createTypeVoid();
    } else if ("null".equals(typeSymbol.getName())) {
      o = createTypeOfNull();
    } else if (typeSymbol.getTypeParameterList().isEmpty()) {
      o = createTypeObject(typeSymbol);
    } else {
      o = createGenerics(typeSymbol);
    }
    return o;
  }

  /**
   * creates a TypeExpression for primitives, such as "int", for "null", "void" and
   * also for object types, such as "Person" from a given symbol
   * Primitives don't need a type symbol, object types need both.
   *
   * @param name
   * @param scope
   * @return
   */
  public static SymTypeExpression createTypeExpression(String name, IBasicSymbolsScope scope) {
    SymTypeExpression o;
    if ("void".equals(name)) {
      o = createTypeVoid();
    } else if (PRIMITIVE_LIST.contains(name)) {
      o = createPrimitive(name);
    } else if ("null".equals(name)) {
      o = createTypeOfNull();
    }else if(name.endsWith("[]")) {
      o = createArrayFromString(name, scope);
    } else if (name.contains("<")) {
      o = createGenericsFromString(name, scope);
    } else if (scope!=null && scope.resolveTypeVar(name).isPresent()){
      o = createTypeVariable(name, scope);
    }else {
      o = createTypeObject(name, scope);
    }
    return o;
  }

  protected static SymTypeExpression createArrayFromString(String type, IBasicSymbolsScope scope){
    //berechne Typen vom Array per createTypeExpression, berechne Dimension von Array durch Anzahl der Klammerpaare am Ende
    int countDim = 0;
    int start = type.indexOf("[");
    String typeWithoutBraces = type;
    while(start!=-1){
      countDim++;
      typeWithoutBraces = typeWithoutBraces.substring(0, start);
      int lastGeneric = type.lastIndexOf(">");
      start = typeWithoutBraces.indexOf("[");
      if(lastGeneric!=-1 && lastGeneric > start){
        break;
      }
    }
    int generic = typeWithoutBraces.indexOf("<");
    String typeWithoutGenericsAndBraces = generic==-1? typeWithoutBraces:typeWithoutBraces.substring(0, generic);
    return createTypeArray(typeWithoutGenericsAndBraces, scope, countDim, createTypeExpression(typeWithoutBraces, scope));
  }

  protected static SymTypeExpression createGenericsFromString(String type, IBasicSymbolsScope scope) {
    int start = type.indexOf("<");
    if (start == -1) {
      return SymTypeExpressionFactory.createTypeExpression(type, scope);
    }
    assert type.length() - start > 1;
    List<String> betweenBrackets = iterateBrackets(type, start);
    String beforeBrackets = type.substring(0, start);
    return SymTypeExpressionFactory.createGenerics(beforeBrackets, scope, getSubGenerics(betweenBrackets, scope));
  }

  /**
   * recursively calls {@link #createGenericsFromString(String, IBasicSymbolsScope)} (String, IBasicSymbolsScope)}.
   * If that method for example received {@code Map<String, List<String>>} this Method should get {@code [String, List<String>]} as parameter
   * @param inBrackets list of generics nested one level
   */
  protected static List<SymTypeExpression> getSubGenerics(List<String> inBrackets, IBasicSymbolsScope scope){
    return inBrackets.stream()
        .map(String::trim)
        .map(name -> createGenericsFromString(name, scope))
        .collect(Collectors.toList());
  }

  /**
   * splits the type-string along commas, but only on such that are on the first depth of generics
   * @param type type-string, like {@code Map<Double, HashMap<String, Integer>>}
   * @param start first occurrence of an opening generic
   * @return all first sub-generics as a list, like {@code [Double; HashMap<String, Integer>]}
   */
  protected static List<String> iterateBrackets(String type, int start){
    List<String> list = new ArrayList<>();
    int depth = 0;
    for(int i = 0; i < type.toCharArray().length; i++) {
      char c = type.toCharArray()[i];
      if(c == '<'){
        depth++;
      }
      if(depth == 1 && (c == ',' || c == '>')){
        list.add(type.substring(start+1, i));
        start = i;
      }
      if(c == '>'){
        depth--;
      }
    }
    return list;
  }



  /**
   * createGenerics: for a generic Type
   *
   * @return
   */
  public static SymTypeOfGenerics createGenerics(TypeSymbol typeSymbol) {
    return createGenerics(typeSymbol, Lists.newArrayList());
  }

  public static SymTypeOfGenerics createGenerics(TypeSymbol typeSymbol,
                                                 List<SymTypeExpression> arguments) {
    return new SymTypeOfGenerics(typeSymbol, arguments);
  }

  public static SymTypeOfGenerics createGenerics(TypeSymbol typeSymbol,
                                                 SymTypeExpression... arguments) {
    return createGenerics(typeSymbol, Arrays.asList(arguments));
  }

  /**
   * createGenerics: is created using the enclosing Scope to ask for the appropriate symbol.
   */
  public static SymTypeOfGenerics createGenerics(String name, IBasicSymbolsScope enclosingScope) {
    return createGenerics(name, enclosingScope, Lists.newArrayList());
  }

  public static SymTypeOfGenerics createGenerics(String name, IBasicSymbolsScope enclosingScope,
                                                 List<SymTypeExpression> arguments) {
    TypeSymbol typeSymbol = new TypeSymbolSurrogate(name);
    typeSymbol.setEnclosingScope(enclosingScope);
    return new SymTypeOfGenerics(typeSymbol, arguments);
  }

  public static SymTypeOfGenerics createGenerics(String name, IBasicSymbolsScope enclosingScope,
                                                 SymTypeExpression... arguments) {
    return createGenerics(name, enclosingScope, Arrays.asList(arguments));
  }

  public static SymTypeOfWildcard createWildcard(boolean isUpper, SymTypeExpression bound) {
    return new SymTypeOfWildcard(isUpper, bound);
  }

  public static SymTypeOfWildcard createWildcard() {
    return new SymTypeOfWildcard();
  }

  public static SymTypeOfFunction createFunction(SymTypeExpression returnType) {
    return createFunction(returnType, Lists.newArrayList());
  }

  public static SymTypeOfFunction createFunction(SymTypeExpression returnType,
      List<SymTypeExpression> argumentTypes) {
    return createFunction(returnType, argumentTypes, false);
  }

  public static SymTypeOfFunction createFunction(SymTypeExpression returnType,
      SymTypeExpression... argumentTypes) {
    return createFunction(returnType, Arrays.asList(argumentTypes));
  }

  public static SymTypeOfFunction createFunction(SymTypeExpression returnType,
      List<SymTypeExpression> argumentTypes, boolean elliptic) {
    return new SymTypeOfFunction(returnType, argumentTypes, elliptic);
  }

  public static SymTypeOfUnion createUnion(Set<SymTypeExpression> unionizedTypes) {
    return new SymTypeOfUnion(unionizedTypes);
  }

  public static SymTypeOfUnion createUnion(SymTypeExpression... unionizedTypes) {
    return createUnion(Set.of(unionizedTypes));
  }
}
