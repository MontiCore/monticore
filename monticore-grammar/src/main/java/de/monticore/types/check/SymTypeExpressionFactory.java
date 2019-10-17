/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbolsScope;
import de.monticore.types.typesymbols._symboltable.TypeVarSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static de.monticore.types.check.DefsTypeBasic.typeConstants;

/**
 * SymTypeExpressionFactory contains static functions that create
 * the various forms of TypeExpressions used for Sym-Types.
 *
 * This factory therefore should be the only source to create SymTypeExpressions.
 * No other source is needed.
 * (That is ok, as the set of SymTypeExpressions is rather fixed and we do not expect
 * many modular extensions that would be needed. Saying this, we know that function types and
 * potentially also union types (A|B) might still be added in the future.)
 */
public class SymTypeExpressionFactory {
  
  @Deprecated  // weil unvollständige Parameter
  public static SymTypeVariable createTypeVariable(String name) {
    SymTypeVariable o = new SymTypeVariable(name);
    return o;
  }
  
  /**
   * createTypeVariable vor Variables
   */
  public static SymTypeVariable createTypeVariable(String name, TypeSymbol typeSymbol) {
    SymTypeVariable o = new SymTypeVariable(name,typeSymbol);
    return o;
  }

  /**
   * for constants, such as "int" (and no other kinds).
   * TypeInfo is not needed (as the Objects are predefined singletons)
   */
  public static SymTypeConstant createTypeConstant(String name) {
    SymTypeConstant stc = typeConstants.get(name);
    if(stc == null) {
      Log.error("0x893F62 Internal Error: Non primitive type " + name + " stored as constant.");
    }
    return stc;
  }
  
  /**
   * for ObjectTypes, as e.g. "Person"
   * @param name  Name of the type
   * @param objTypeSymbol  Symbol behind the Type
   * @return
   */
  public static SymTypeOfObject createTypeObject(String name, TypeSymbol objTypeSymbol) {
    SymTypeOfObject o = new SymTypeOfObject(name,objTypeSymbol);
    return o;
  }

  /**
   * for ObjectTypes, as e.g. "Person"
   * @param name  Name of the type
   * @return
   */
  @Deprecated
  public static SymTypeOfObject createTypeObject(String name) {
    SymTypeOfObject o = new SymTypeOfObject(name);
    o.setObjName(name);
    return o;
  }

  /**
   * creates the "Void"-type, i.e. a pseudotype that represents the absence of a real type
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
   * @param dim   the dimension of the array
   * @param argument the argument type (of the elements)
   * @param typeInfo the Symbol behind this Type
   * @return
   */
  public static SymTypeArray createTypeArray(int dim, SymTypeExpression argument, TypeSymbol typeInfo) {
    SymTypeArray o = new SymTypeArray(dim, argument, typeInfo);
    return o;
  }
  
  /**
   * creates an array-Type Expression
   * @param dim   the dimension of the array
   * @param argument the argument type (of the elements)
   * @return
   */
  @Deprecated
  public static SymTypeArray createTypeArray(int dim, SymTypeExpression argument) {
    SymTypeArray o = new SymTypeArray(dim, argument);
    return o;
  }
  
  
  /**
   * creates a TypeExpression for primitives, such as "int", for "null", "void" and
   * also for object types, such as "Person" from a given symbol
   * @param type
   * @return
   */
  public static SymTypeExpression createTypeExpression(TypeSymbol type){
    return createTypeExpression(type.getName(),type);
  }
  
  /**
   * creates a TypeExpression for primitives, such as "int", for "null", "void" and
   * also for object types, such as "Person" from a given symbol
   * @param name
   * @param type
   * @return
   */
  public static SymTypeExpression createTypeExpression(String name, TypeSymbol type){
    SymTypeExpression o;
    if (typeConstants.containsKey(type.getName())) {
      o = createTypeConstant(name);
    } else if("void".equals(name)){
      o = createTypeVoid();
    } else if("null".equals(name)) {
      o = createTypeOfNull();
    } else {
      o = createTypeObject(name,type);
    }
    return o;
  }
  
  
  // -------------------------------------------------------- GenericTypeExpression
  
  /**
   * createGenerics: for a generic Type
   * @param name    name of the Generic, such as "Map"
   * @param arguments   the SymTypes for the arguments
   * @param objTypeConstructorSymbol  and the symbol-Object the generic type is linked to
   * @return
   */
  public static SymTypeOfGenerics createGenerics(String name, List<SymTypeExpression> arguments,
                                                 TypeSymbol objTypeConstructorSymbol){
    SymTypeOfGenerics o = new SymTypeOfGenerics(name, arguments, objTypeConstructorSymbol);
    return o;
  }
  
  /**
   * createGenerics: is created using the enclosing Scope to ask for the appropriate symbol.
   * @param name    name of the Generic, such as "Map"
   * @param arguments   the SymTypes for the arguments
   * @param enclosingScope  used to derive the Symbol
   */
  public static SymTypeOfGenerics createGenerics(String name, List<SymTypeExpression> arguments,
                                                 TypeSymbolsScope enclosingScope){
    Optional<TypeSymbol> objTypeConstructorSymbol = enclosingScope.resolveType(name);
    // No check, whether the symbol actually exists: Exception may be thrown
    SymTypeOfGenerics o = new SymTypeOfGenerics(name, arguments, objTypeConstructorSymbol.get());
    return o;
  }

  @Deprecated // TODO: delete, because TypeSymbol is not set
  // aktuell nur noch benutzt von DeriveSymTypeOfMCType
  public static SymTypeOfGenerics createGenerics(String fullName, List<SymTypeExpression> arguments){
    SymTypeOfGenerics o = new SymTypeOfGenerics(fullName, arguments);
    // XXX BR: here we also have to add the Symbol
    // being retrieved from somewhere ...
    return o;
  }

}
