/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import java.util.Arrays;
import java.util.List;

public class TypeExpressionBuilder {
  
  
  public static TypeVariable buildTypeVariable(String name) {
    TypeVariable o = new TypeVariable(name);
    return o;
  }
  
  public static TypeConstant buildTypeConstant(String name) {
    TypeConstant o = new TypeConstant(name);
    return o;
  }
  
  public static ObjectType buildObjectType(String name) {
    ObjectType o = new ObjectType(name);
    // XXX BR: here we also have to add the Symbol
    // being retrieved from somewhere ...
    return o;
  }
  
  // --------------------------------------------------------------------------
  
  @Deprecated
  public static GenericTypeExpression buildGenericTypeExpression(String name, List<TypeExpression> superTypes, List<TypeExpression> arguments){
    GenericTypeExpression o = new GenericTypeExpression();
    o.setName(name);
    o.setArguments(arguments);
    o.setSuperTypes(superTypes);
    return o;
  }
  
  
}
