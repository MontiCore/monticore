/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.se_rwth.commons.logging.Log;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * SymTypeExpressionFactory contains static functions that create
 * the various forms of TypeExpressions used for Sym-Types.
 * <p>
 * This factory therefore should be the only source to create SymTypeExpressions.
 * No other source is needed.
 * (That is ok, as the set of SymTypeExpressions is rather fixed and we do not expect
 * many modular extensions that would be needed. Saying this, we know that function types and
 * potentially also union types (A|B) might still be added in the future.)
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
   * for constants, such as "int" (and no other kinds).
   * TypeInfo is not needed (as the Objects are predefined singletons)
   */
  public static SymTypeConstant createTypeConstant(String name) {
    Optional<TypeSymbol> type = BasicSymbolsMill.globalScope().resolveType(name);
    if (!type.isPresent()) {
      Log.error("0x893F62 Internal Error: Non primitive type " + name + " stored as constant.");
    }
    return new SymTypeConstant(type.get());
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
   * @param dim              the dimension of the array
   * @param argument         the argument type (of the elements)
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
    }
    else if ("null".equals(name)) {
      o = createTypeOfNull();
    }
    else {
      o = createTypeObject(name, scope);
    }
    return o;
  }

  /**
   * createGenerics: for a generic Type
   *
   * @return
   */
  public static SymTypeOfGenerics createGenerics(TypeSymbol typeSymbol) {
    return new SymTypeOfGenerics(typeSymbol);
  }

  public static SymTypeOfGenerics createGenerics(TypeSymbol typeSymbol,
      List<SymTypeExpression> arguments) {
    return new SymTypeOfGenerics(typeSymbol, arguments);
  }

  public static SymTypeOfGenerics createGenerics(TypeSymbol typeSymbol,
      SymTypeExpression... arguments) {
    return new SymTypeOfGenerics(typeSymbol, Arrays.asList(arguments));
  }

  /**
   * createGenerics: is created using the enclosing Scope to ask for the appropriate symbol.
   */
  public static SymTypeOfGenerics createGenerics(String name, IBasicSymbolsScope enclosingScope) {
    TypeSymbol typeSymbol = new TypeSymbolSurrogate(name);
    typeSymbol.setEnclosingScope(enclosingScope);
    return new SymTypeOfGenerics(typeSymbol);
  }

  public static SymTypeOfGenerics createGenerics(String name, IBasicSymbolsScope enclosingScope,
      List<SymTypeExpression> arguments) {
    TypeSymbol typeSymbol = new TypeSymbolSurrogate(name);
    typeSymbol.setEnclosingScope(enclosingScope);
    return new SymTypeOfGenerics(typeSymbol, arguments);
  }

  public static SymTypeOfGenerics createGenerics(String name, IBasicSymbolsScope enclosingScope,
      SymTypeExpression... arguments) {
    TypeSymbol typeSymbol = new TypeSymbolSurrogate(name);
    typeSymbol.setEnclosingScope(enclosingScope);
    return new SymTypeOfGenerics(typeSymbol,
        Arrays.asList(arguments));
  }

  public static SymTypeOfWildcard createWildcard(boolean isUpper, SymTypeExpression bound){
    return new SymTypeOfWildcard(isUpper,bound);
  }

  public static SymTypeOfWildcard createWildcard(){
    return new SymTypeOfWildcard();
  }
}
