/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._util.BasicSymbolsTypeDispatcher;
import de.monticore.symbols.basicsymbols._util.IBasicSymbolsTypeDispatcher;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
   * @deprecated -> create a symbol and use it to create a SymTypeVariable
   */
  @Deprecated
  public static SymTypeVariable createTypeVariable(String name, IBasicSymbolsScope scope) {
    TypeVarSymbol typeSymbol = new TypeVarSymbol(name);
    typeSymbol.setEnclosingScope(scope);
    return createTypeVariable(typeSymbol);
  }

  /**
   * used to create a (optionally bounded) FREE type variable.
   * These are created internally while inferring types.
   *
   * @param lowerBound is required to be a sub-type.
   *                   For no bounds use {@link #createBottomType()}.
   * @param upperBound is required to be a super-type,
   *                   e.g., {@code T extends Person & Iterable<Integer>}.
   *                   For no bounds use {@link #createTopType()}.
   */
  public static SymTypeVariable createTypeVariable(
      SymTypeExpression lowerBound, SymTypeExpression upperBound) {
    // free type variables need an identifier
    return createTypeVariable(
        getUniqueFreeTypeVarName(),
        lowerBound, upperBound
    );
  }

  /**
   * Creates FREE type variable, BUT:
   * You most likely do not want this method,
   * this is (nearly) only used for deepCloning.
   * Use {@link #createTypeVariable(SymTypeExpression, SymTypeExpression)}.
   */
  public static SymTypeVariable createTypeVariable(
      String name,
      SymTypeExpression lowerBound,
      SymTypeExpression upperBound
  ) {
    return new SymTypeVariable(name, lowerBound, upperBound);
  }

  public static SymTypeVariable createTypeVariable(TypeVarSymbol typeVarSymbol) {
    // the SymTypeVariable extracts the upper bound from the type itself,
    // as such we do not set it here
    SymTypeExpression upperBound = createTopType();
    // our Symbols have no notion of lower bound,
    // as such we use the bottom type
    SymTypeExpression lowerBound = createBottomType();
    return createTypeVariable(typeVarSymbol, lowerBound, upperBound);
  }

  public static SymTypeVariable createTypeVariable(
      TypeVarSymbol typeVarSymbol,
      SymTypeExpression lowerBound,
      SymTypeExpression upperBound
  ) {
    return new SymTypeVariable(typeVarSymbol, lowerBound, upperBound);
  }

  @Deprecated
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
   * for RegEx-types, e.g. 'R"gr(a|e)y"'
   */
  public static SymTypeOfRegEx createTypeRegEx(String regex) {
    return new SymTypeOfRegEx(regex);
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
   * @deprecated arrays do not have an type symbol
   * @param typeSymbol
   * @param dim        the dimension of the array
   * @param argument   the argument type (of the elements)
   * @return
   */
  @Deprecated
  public static SymTypeArray createTypeArray(TypeSymbol typeSymbol, int dim,
                                             SymTypeExpression argument) {
    return new SymTypeArray(typeSymbol, dim, argument);
  }

  /**
   * @deprecated arrays do not have a name
   */
  @Deprecated
  public static SymTypeArray createTypeArray(String name, IBasicSymbolsScope typeSymbolsScope,
                                             int dim, SymTypeExpression argument) {
    TypeSymbol typeSymbol = new TypeSymbolSurrogate(name);
    typeSymbol.setEnclosingScope(typeSymbolsScope);
    return new SymTypeArray(typeSymbol, dim, argument);
  }

  public static SymTypeArray createTypeArray(SymTypeExpression argument, int dim) {
    return new SymTypeArray(argument, dim);
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
    List<SymTypeExpression> parameters =
        typeSymbol.getTypeParameterList().stream()
            .map(tp -> createFromSymbol(tp))
            .collect(Collectors.toList());
    return createGenerics(typeSymbol, parameters);
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
    return createGenerics(typeSymbol, arguments);
  }

  public static SymTypeOfGenerics createGenerics(String name, IBasicSymbolsScope enclosingScope,
                                                 SymTypeExpression... arguments) {
    return createGenerics(name, enclosingScope, Arrays.asList(arguments));
  }

  public static SymTypeExpression createFromSymbol(TypeSymbol typeSymbol) {
    IBasicSymbolsTypeDispatcher typeDispatcher =
        BasicSymbolsMill.typeDispatcher();
    if(typeDispatcher.isBasicSymbolsTypeVar(typeSymbol)) {
      return createTypeVariable((TypeVarSymbol) typeSymbol);
    }
    if(typeSymbol.getSpannedScope().getLocalTypeVarSymbols().isEmpty()) {
      return createTypeObject(typeSymbol);
    }
    else {
      return createGenerics(typeSymbol);
    }
  }

  public static SymTypeOfWildcard createWildcard(boolean isUpper, SymTypeExpression bound) {
    return new SymTypeOfWildcard(bound, isUpper);
  }

  public static SymTypeOfWildcard createWildcard() {
    return createWildcard(false, null);
  }

  public static SymTypeOfFunction createFunction(FunctionSymbol symbol) {
    return symbol.getFunctionType();
  }

  public static SymTypeOfFunction createFunction(SymTypeExpression returnType) {
    return createFunction(returnType, Lists.newArrayList());
  }

  public static SymTypeOfFunction createFunction(SymTypeExpression returnType,
      List<? extends SymTypeExpression> argumentTypes) {
    return createFunction(returnType, argumentTypes, false);
  }

  public static SymTypeOfFunction createFunction(SymTypeExpression returnType,
      SymTypeExpression... argumentTypes) {
    return createFunction(returnType, Arrays.asList(argumentTypes));
  }

  public static SymTypeOfFunction createFunction(SymTypeExpression returnType,
      List<? extends SymTypeExpression> argumentTypes, boolean elliptic) {
    return createFunction(null, returnType, argumentTypes, elliptic);
  }

  public static SymTypeOfFunction createFunction(
      FunctionSymbol symbol,
      SymTypeExpression returnType,
      List<? extends SymTypeExpression> argumentTypes,
      boolean elliptic) {
    return new SymTypeOfFunction(symbol, returnType, argumentTypes, elliptic);
  }

  public static SIUnitBasic createSIUnitBasic(String dimension) {
    return createSIUnitBasic(dimension, 1);
  }

  public static SIUnitBasic createSIUnitBasic(String dimension, int exponent) {
    return createSIUnitBasic(dimension, "", exponent);
  }

  public static SIUnitBasic createSIUnitBasic(
      String dimension, String prefix, int exponent) {
    return new SIUnitBasic(dimension, prefix, exponent);
  }

  public static SymTypeOfSIUnit createSIUnit(
      List<SIUnitBasic> numerator,
      List<SIUnitBasic> denominator
  ) {
    return new SymTypeOfSIUnit(numerator, denominator);
  }

  public static SymTypeOfNumericWithSIUnit createNumericWithSIUnit(
      List<SIUnitBasic> numerator,
      List<SIUnitBasic> denominator,
      SymTypeExpression numericType
  ) {
    return createNumericWithSIUnit(
        createSIUnit(numerator, denominator),
        numericType
    );
  }

  public static SymTypeOfNumericWithSIUnit createNumericWithSIUnit(
      SymTypeOfSIUnit siUnitType,
      SymTypeExpression numericType
  ) {
    return new SymTypeOfNumericWithSIUnit(siUnitType, numericType);
  }

  public static SymTypeOfTuple createTuple(List<? extends SymTypeExpression> types) {
    return new SymTypeOfTuple(new ArrayList<>(types));
  }

  public static SymTypeOfTuple createTuple(SymTypeExpression... types) {
    return createTuple(List.of(types));
  }

  public static SymTypeOfUnion createUnion(Collection<? extends SymTypeExpression> unionizedTypes) {
    return new SymTypeOfUnion(unionizedTypes);
  }

  public static SymTypeOfUnion createUnion(SymTypeExpression... unionizedTypes) {
    return createUnion(Arrays.stream(unionizedTypes).collect(Collectors.toSet()));
  }

  /**
   * a slightly normalizing {@link #createUnion(Collection)}
   */
  public static SymTypeExpression createUnionOrDefault(
      SymTypeExpression defaultType,
      Collection<? extends SymTypeExpression> unionizedTypes
  ) {
    // an empty union is usually the bottom type,
    // but there are exceptions
    if (unionizedTypes.isEmpty()) {
      return defaultType;
    }
    // union of one "is not" a union
    // (A) = A
    else if (unionizedTypes.size() == 1) {
      return unionizedTypes.stream().findAny().get();
    }
    // still a union
    else {
      return createUnion(unionizedTypes);
    }
  }

  public static SymTypeOfIntersection createIntersection(Collection<? extends SymTypeExpression> intersectedTypes) {
    return new SymTypeOfIntersection(intersectedTypes);
  }

  public static SymTypeOfIntersection createIntersection(SymTypeExpression... intersectedTypes) {
    return createIntersection(Set.of(intersectedTypes));
  }

  /**
   * a slightly normalizing {@link #createIntersection(Collection)}
   */
  public static SymTypeExpression createIntersectionOrDefault(
      SymTypeExpression defaultType,
      Collection<? extends SymTypeExpression> intersectedTypes
  ) {
    // an empty intersection is usually the top type,
    // but there are exceptions
    if (intersectedTypes.isEmpty()) {
      return defaultType;
    }
    // intersection of one "is not" an intersection
    // (A) = A
    else if (intersectedTypes.size() == 1) {
      return intersectedTypes.stream().findAny().get();
    }
    // still an intersection
    else {
      return createIntersection(intersectedTypes);
    }
  }

  /**
   * @return a type which is the superType of ALL other types,
   * NO further guarantees are made.
   * Note, this is not the case for,
   * e.g., Java's Object class as it is not a superType of primitives
   * This is used internally in the TypeCheck.
   */
  public static SymTypeExpression createTopType() {
    // This is technically dependent on the type system in use.
    // Here, use the fact that we support
    // intersection types in our implementations
    return createIntersection();
  }

  /**
   * @return a type which is the subType of ALL other types,
   * NO further guarantees are made.
   * Note, this is not the case for,
   * e.g., Java's null type as it is not a subType of primitives
   * This is used internally in the TypeCheck.
   */
  public static SymTypeExpression createBottomType() {
    // This is technically dependent on the type system in use.
    // Here, use the fact that we support
    // union types in our implementations
    return createUnion();
  }

  // Helper

  /**
   * The Only guarantee is that the names are unique.
   * Never test against them.
   */
  protected static String getUniqueFreeTypeVarName() {
    // naming inspired by JDK
    return "FV#" + typeVarIDCounter++;
  }
  protected static int typeVarIDCounter = 0;

}
