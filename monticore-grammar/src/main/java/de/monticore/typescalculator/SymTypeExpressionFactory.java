/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import java.util.List;

public class SymTypeExpressionFactory {
  
  
  public static SymTypeVariable createTypeVariable(String name) {
    SymTypeVariable o = new SymTypeVariable(name);
    return o;
  }
  
  public static SymTypeConstant createTypeConstant(String name) {
    SymTypeConstant o = new SymTypeConstant(name);
    return o;
  }
  
  public static SymObjectType createObjectType(String name) {
    SymObjectType o = new SymObjectType(name);
    // XXX BR: here we also have to add the Symbol
    // being retrieved from somewhere ...
    return o;
  }
  
  public static SymTypeVoid createTypeVoid() {
    SymTypeVoid o = new SymTypeVoid();
    return o;
  }
  
  public static SymArrayType createArrayType(int dim, SymTypeExpression argument) {
    SymArrayType o = new SymArrayType(dim, argument);
    return o;
  }
  
  public static SymGenericTypeExpression createGenericTypeExpression(String name, List<SymTypeExpression> arguments){
    SymGenericTypeExpression o = new SymGenericTypeExpression(name, arguments);
    // XXX BR: here we also have to add the Symbol
    // being retrieved from somewhere ...
    return o;
  }
  
}
