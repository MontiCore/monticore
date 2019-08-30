/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import java.util.List;

public class TypeExpressionBuilder {
  
  
  public static SymTypeVariable buildTypeVariable(String name) {
    SymTypeVariable o = new SymTypeVariable(name);
    return o;
  }
  
  public static SymTypeConstant buildTypeConstant(String name) {
    SymTypeConstant o = new SymTypeConstant(name);
    return o;
  }
  
  public static SymObjectType buildObjectType(String name) {
    SymObjectType o = new SymObjectType(name);
    // XXX BR: here we also have to add the Symbol
    // being retrieved from somewhere ...
    return o;
  }
  
  public static SymTypeVoid buildTypeConstant() {
    SymTypeVoid o = new SymTypeVoid();
    return o;
  }
  
  public static SymArrayType buildTypeConstant(int dim, SymTypeExpression argument) {
    SymArrayType o = new SymArrayType(dim, argument);
    return o;
  }
  
  public static SymGenericTypeExpression buildGenericTypeExpression(String name, List<SymTypeExpression> arguments){
    SymGenericTypeExpression o = new SymGenericTypeExpression(name, arguments);
    // XXX BR: here we also have to add the Symbol
    // being retrieved from somewhere ...
    return o;
  }
  
  // --------------------------------------------------------------------------
  
  @Deprecated // in use in a test
  public static SymGenericTypeExpression buildGenericTypeExpression(String name, List<SymTypeExpression> superTypes, List<SymTypeExpression> arguments){
    SymGenericTypeExpression o = new SymGenericTypeExpression();
    o.setName(name);
    o.setArguments(arguments);
    o.setSuperTypes(superTypes);
    return o;
  }
  
  
}
