/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolSurrogate;
import de.se_rwth.commons.logging.Log;

import java.util.Arrays;
import java.util.List;

import static de.monticore.types.check.DefsTypeBasic.typeConstants;

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
  public static SymTypeVariable createTypeVariable(String name, IOOSymbolsScope typeSymbol) {
    OOTypeSymbolSurrogate loader = new OOTypeSymbolSurrogate(name);
    loader.setEnclosingScope(typeSymbol);
    return new SymTypeVariable(loader);
  }

  public static SymTypeVariable createTypeVariable(OOTypeSymbolSurrogate typeSymbolSurrogate) {
    return new SymTypeVariable(typeSymbolSurrogate);
  }

  /**
   * for constants, such as "int" (and no other kinds).
   * TypeInfo is not needed (as the Objects are predefined singletons)
   */
  public static SymTypeConstant createTypeConstant(String name) {
    SymTypeConstant stc = typeConstants.get(name);
    if (stc == null) {
      Log.error("0x893F62 Internal Error: Non primitive type " + name + " stored as constant.");
    }
    return stc;
  }

  /**
   * for ObjectTypes, as e.g. "Person"
   */
  public static SymTypeOfObject createTypeObject(OOTypeSymbolSurrogate typeSymbolSurrogate) {
    return new SymTypeOfObject(typeSymbolSurrogate);
  }

  /**
   * for ObjectTypes, as e.g. "Person"
   */
  public static SymTypeOfObject createTypeObject(String name, IOOSymbolsScope enclosingScope) {
    OOTypeSymbolSurrogate loader = new OOTypeSymbolSurrogate(name);
    loader.setEnclosingScope(enclosingScope);
    return new SymTypeOfObject(loader);
  }

  /**
   * creates the "Void"-type, i.e. a pseudotype that represents the absence of a real type
   *
   * @return
   */
  public static SymTypeVoid createTypeVoid() {
    return DefsTypeBasic._voidSymType;
  }

  /**
   * That is the pseudo-type of "null"
   */
  public static SymTypeOfNull createTypeOfNull() {
    return DefsTypeBasic._nullSymType;
  }

  /**
   * creates an array-Type Expression
   *
   * @param typeSymbolSurrogate
   * @param dim              the dimension of the array
   * @param argument         the argument type (of the elements)
   * @return
   */
  public static SymTypeArray createTypeArray(OOTypeSymbolSurrogate typeSymbolSurrogate, int dim,
      SymTypeExpression argument) {
    return new SymTypeArray(typeSymbolSurrogate, dim, argument);
  }

  public static SymTypeArray createTypeArray(String name, IOOSymbolsScope typeSymbolsScope,
      int dim, SymTypeExpression argument) {
    OOTypeSymbolSurrogate loader = new OOTypeSymbolSurrogate(name);
    loader.setEnclosingScope(typeSymbolsScope);
    return new SymTypeArray(loader, dim, argument);
  }

  /**
   * creates a TypeExpression for primitives, such as "int", for "null", "void" and
   * also for object types, such as "Person" from a given symbol
   * Primitives don't need a type symbol, object types need both.
   *
   * @param name
   * @param type
   * @return
   */
  public static SymTypeExpression createTypeExpression(String name, IOOSymbolsScope type) {
    SymTypeExpression o;
    if (typeConstants.containsKey(name)) {
      o = createTypeConstant(name);
    }
    else if ("void".equals(name)) {
      o = createTypeVoid();
    }
    else if ("null".equals(name)) {
      o = createTypeOfNull();
    }
    else {
      o = createTypeObject(name, type);
    }
    return o;
  }

  /**
   * createGenerics: for a generic Type
   *
   * @return
   */
  public static SymTypeOfGenerics createGenerics(OOTypeSymbolSurrogate typeSymbolSurrogate) {
    return new SymTypeOfGenerics(typeSymbolSurrogate);
  }

  public static SymTypeOfGenerics createGenerics(OOTypeSymbolSurrogate typeSymbolSurrogate,
      List<SymTypeExpression> arguments) {
    return new SymTypeOfGenerics(typeSymbolSurrogate, arguments);
  }

  public static SymTypeOfGenerics createGenerics(OOTypeSymbolSurrogate typeSymbolSurrogate,
      SymTypeExpression... arguments) {
    return new SymTypeOfGenerics(typeSymbolSurrogate, Arrays.asList(arguments));
  }

  /**
   * createGenerics: is created using the enclosing Scope to ask for the appropriate symbol.
   */
  public static SymTypeOfGenerics createGenerics(String name, IOOSymbolsScope enclosingScope) {
    OOTypeSymbolSurrogate loader = new OOTypeSymbolSurrogate(name);
    loader.setEnclosingScope(enclosingScope);
    return new SymTypeOfGenerics(loader);
  }

  public static SymTypeOfGenerics createGenerics(String name, IOOSymbolsScope enclosingScope,
      List<SymTypeExpression> arguments) {
    OOTypeSymbolSurrogate loader = new OOTypeSymbolSurrogate(name);
    loader.setEnclosingScope(enclosingScope);
    return new SymTypeOfGenerics(loader, arguments);
  }

  public static SymTypeOfGenerics createGenerics(String name, IOOSymbolsScope enclosingScope,
      SymTypeExpression... arguments) {
    OOTypeSymbolSurrogate loader = new OOTypeSymbolSurrogate(name);
    loader.setEnclosingScope(enclosingScope);
    return new SymTypeOfGenerics(loader,
        Arrays.asList(arguments));
  }

  public static SymTypeOfWildcard createWildcard(boolean isUpper, SymTypeExpression bound){
    return new SymTypeOfWildcard(isUpper,bound);
  }

  public static SymTypeOfWildcard createWildcard(){
    return new SymTypeOfWildcard();
  }
}
