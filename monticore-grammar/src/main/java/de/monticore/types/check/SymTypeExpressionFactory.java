/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbolsScope;

import java.util.List;
import java.util.Optional;

public class SymTypeExpressionFactory {
  
  
  public static SymTypeVariable createTypeVariable(String name) {
    SymTypeVariable o = new SymTypeVariable(name);
    return o;
  }
  
  public static SymTypeConstant createTypeConstant(String name) {
    SymTypeConstant o = new SymTypeConstant(name);
    return o;
  }
  
  public static SymTypeOfObject createTypeObject(String name, TypeSymbol objTypeSymbol) {
    SymTypeOfObject o = new SymTypeOfObject(name,objTypeSymbol);
    return o;
  }
  
  public static SymTypeVoid createTypeVoid() {
    SymTypeVoid o = new SymTypeVoid();
    return o;
  }
  
  public static SymTypeOfNull createTypeOfNull() {
    SymTypeOfNull o = new SymTypeOfNull();
    return o;
  }
  
  public static SymTypeArray createTypeArray(int dim, SymTypeExpression argument) {
    SymTypeArray o = new SymTypeArray(dim, argument);
    return o;
  }
  
  
  // -------------------------------------------------------- GenericTypeExpression
  
  public static SymTypeOfGenerics createGenerics(String name, List<SymTypeExpression> arguments,
                                                 TypeSymbol objTypeConstructorSymbol){
    SymTypeOfGenerics o = new SymTypeOfGenerics(name, arguments, objTypeConstructorSymbol);
    return o;
  }
  
  /**
   * createGenerics: is created using the enclosing Scope to ask for the appropriate symbol.
   * @param name
   * @param arguments
   * @param enclosingScope  used to derive the Symbol
   */
  public static SymTypeOfGenerics createGenerics(String name, List<SymTypeExpression> arguments,
                                                 TypeSymbolsScope enclosingScope){
    Optional<TypeSymbol> objTypeConstructorSymbol = enclosingScope.resolveType(name);
    // No check, whether the symbol actually exists!
    SymTypeOfGenerics o = new SymTypeOfGenerics(name, arguments, objTypeConstructorSymbol.get());
    return o;
  }
  
  @Deprecated // TODO: delete, because TypeSymbol is not set
  public static SymTypeOfGenerics createGenerics(String fullName, List<SymTypeExpression> arguments){
    SymTypeOfGenerics o = new SymTypeOfGenerics(fullName, arguments);
    // XXX BR: here we also have to add the Symbol
    // being retrieved from somewhere ...
    return o;
  }


  
}
